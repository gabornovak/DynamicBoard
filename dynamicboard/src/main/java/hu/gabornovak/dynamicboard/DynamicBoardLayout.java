package hu.gabornovak.dynamicboard;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import hu.gabornovak.dynamicboard.api.ResizeLayerRenderer;
import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.GridPosition;
import hu.gabornovak.dynamicboard.board.Point;
import hu.gabornovak.dynamicboard.board.Size;
import hu.gabornovak.dynamicboard.option.Option;
import hu.gabornovak.dynamicboard.utils.Utils;

/**
 * Created by gnovak on 2/7/2016.
 */
public class DynamicBoardLayout extends RelativeLayout {
    private DynamicBoard board;
    private ResizerView resizerView;

    public DynamicBoardLayout(Context context) {
        super(context);
        init();
    }

    public DynamicBoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicBoardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // start region
    // ------------- PUBLIC API ---------------

    public void setResizeLayerRenderer(ResizeLayerRenderer resizeLayerRenderer) {
        this.resizerView.setResizeLayerRenderer(resizeLayerRenderer);
    }

    public void addBoardItem(BoardItem boardItem, GridPosition position, Size size) {
        if (board.isInited()) {
            if (board.isBoardItemFit(boardItem, position, size)) {
                board.addBoardItem(boardItem, position, size);

                View placeholderView = boardItem.getPlaceholderView(position, size);
                initBoardItemPlaceholderView(placeholderView, boardItem);
                addView(placeholderView);

                ViewGroup view = boardItem.getView(position, size);
                initBoardItemView(view, placeholderView, boardItem);
                addView(view);
            } else {
                Log.w("DynamicBoard", "This board item doesn't fit in this area! ");
            }
        } else {
            throw new IllegalAccessError("Please wait till the board is initialized!");
        }
    }

    public void setOnBoardInitializedListener(Board.OnBoardInitializedListener listener) {
        board.setOnBoardInitializedListener(listener);
    }
    //end region

    private void init() {
        board = new DynamicBoard(getContext());
        resizerView = new ResizerView(getContext());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resizerView.isViewVisible()) {
                    resizerView.hideView();
                }
            }
        });
        setResizeLayerRenderer(new DefaultResizeLayerRenderer());
        addView(resizerView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            board.initBoardSize(w, h);
        }
    }

    private void initBoardItemPlaceholderView(View view, BoardItem boardItem) {
        Point point = board.getGridPositionPhysicalPosition(boardItem.getCurrentPosition());
        view.setX(point.getX());
        view.setY(point.getY());
        view.setLayoutParams(new LayoutParams((int) board.getBoardItemPhysicalWidth(boardItem), (int) board.getBoardItemPhysicalHeight(boardItem)));
        view.setVisibility(GONE);
    }

    private void initBoardItemView(ViewGroup view, View placeholderView, BoardItem boardItem) {
        Point point = board.getGridPositionPhysicalPosition(boardItem.getCurrentPosition());
        view.setX(point.getX());
        view.setY(point.getY());
        view.setLayoutParams(new LayoutParams((int) board.getBoardItemPhysicalWidth(boardItem), (int) board.getBoardItemPhysicalHeight(boardItem)));
        view.setOnTouchListener(createOnTouchListenerForBoardItem(boardItem, view, placeholderView));

        setupOptions(view, boardItem);
    }

    private void setupOptions(ViewGroup view, BoardItem boardItem) {
        final List<Option> options = boardItem.getOptions();
        boolean needOptionMenu = false;
        for (final Option option : options) {
            if (option.getOptionVisibility() == Option.OptionVisibility.IN_MENU) {
                needOptionMenu = true;
            }
        }
        if (needOptionMenu) {
            View menuView = createMenuView(view);
            view.addView(menuView);
            final PopupMenu popupMenu = new PopupMenu(getContext(), menuView);
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).getOptionVisibility() == Option.OptionVisibility.IN_MENU) {
                    popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, options.get(i).getOptionName());
                }
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    for (int i = 0; i < options.size(); i++) {
                        if (options.get(i).getOptionVisibility() == Option.OptionVisibility.IN_MENU) {
                            if (i == item.getItemId()) {
                                options.get(i).execute(getContext(), board);
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
            menuView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
        }
    }

    private View createMenuView(ViewGroup view) {
        ImageView optionView = new ImageView(getContext());
        optionView.setImageResource(R.drawable.ic_more_vert_black_24dp);
        optionView.setColorFilter(0xFF333333);

        int size = (int) Utils.pxFromDp(getContext(), 24f);
        int margin = (int) Utils.pxFromDp(getContext(), 4f);

        if (view instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.setMargins(margin, margin, margin, margin);
            params.gravity = Gravity.END;
            optionView.setLayoutParams(params);
        } else if (view instanceof RelativeLayout) {
            LayoutParams params = new LayoutParams(size, size);
            params.setMargins(margin, margin, margin, margin);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            }
            optionView.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, size);
            optionView.setLayoutParams(params);
        }
        return optionView;
    }

    private OnTouchListener createOnTouchListenerForBoardItem(final BoardItem boardItem, View view, View placeholderView) {
        return new DefaultBoardItemTouchListener(resizerView, board, boardItem, view, placeholderView);
    }
}
