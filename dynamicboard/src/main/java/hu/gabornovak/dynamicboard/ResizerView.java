package hu.gabornovak.dynamicboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.GridPosition;
import hu.gabornovak.dynamicboard.board.Point;
import hu.gabornovak.dynamicboard.board.Size;
import hu.gabornovak.dynamicboard.api.ResizeLayerRenderer;

/**
 * Created by gnovak on 2/9/2016.
 */
public class ResizerView extends View {
    private Board board;
    private BoardItem currentBoardItem;
    private View currentBoardView;
    private View currentPlaceholderView;

    private GridPosition currentBoardItemPosition;
    private Size currentBoardItemSize;

    private float layerVisibility;

    private Rect draggedSizeRect;
    private Rect lastFitSizeRect;
    private ResizeLayerRenderer.ResizeDirection resizeDirection;

    private ResizeLayerRenderer resizeLayerRenderer;

    public ResizerView(Context context) {
        super(context);
        init();
    }

    public ResizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(40f);
        }

        setOnTouchListener(new ResizerViewTouchListener(this));

        draggedSizeRect = new Rect();
        lastFitSizeRect = new Rect();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }
    }

    public void setResizeLayerRenderer(ResizeLayerRenderer resizeLayerRenderer) {
        this.resizeLayerRenderer = resizeLayerRenderer;
        resizeLayerRenderer.onBindView(this);
    }

    void hideView() {
        layerVisibility = 0f;
        invalidate();
    }

    boolean isViewVisible() {
        return layerVisibility > 0.05f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isViewVisible()) {
            return;
        }
        if (resizeLayerRenderer != null) {
            resizeLayerRenderer.onDraw(canvas, draggedSizeRect, lastFitSizeRect, resizeDirection);
        }
    }

    public void setLayerVisibility(float layerVisibility) {
        this.layerVisibility = layerVisibility;
    }

    void setResizeLayerTopOfBy(float diffY) {
        resizeDirection = ResizeLayerRenderer.ResizeDirection.TOP;
        setResizeLayer(getViewX(), getViewY() + diffY, getViewWidth(), getViewHeight() - diffY);
    }

    void setResizeLayerBottomOfBy(float diffY) {
        resizeDirection = ResizeLayerRenderer.ResizeDirection.BOTTOM;
        setResizeLayer(getViewX(), getViewY(), getViewWidth(), getViewHeight() + diffY);
    }

    void setResizeLayerLeftOfBy(float diffX) {
        resizeDirection = ResizeLayerRenderer.ResizeDirection.LEFT;
        setResizeLayer(getViewX() + diffX, getViewY(), getViewWidth() - diffX, getViewHeight());
    }

    void setResizeLayerRightOfBy(float diffX) {
        resizeDirection = ResizeLayerRenderer.ResizeDirection.RIGHT;
        setResizeLayer(getViewX(), getViewY(), getViewWidth() + diffX, getViewHeight());
    }

    void setResizeLayer(float x, float y, float width, float height) {
        setDraggedSizeRect(x, y, width, height);

        GridPosition position = board.getSnappedGridPositionForPhysicalPosition(x, y);
        Size size = board.getSizeForPhysicalSize(width, height);

        if (board.isBoardItemFit(currentBoardItem, position, size)) {
            Point point = board.getGridPositionPhysicalPosition(position);
            if (size.smallerThanOrEqual(currentBoardItem.getMaxSize()) && size.largerThanOrEqual(currentBoardItem.getMinSize())) {
                setLastFitSizeRect(size, point);
                updateViewsIfNecessary(position, size, point);
            }
        }
        invalidate();
    }

    private void updateViewsIfNecessary(GridPosition position, Size size, Point point) {
        boolean needInvalidate = false;
        if (!currentBoardItemPosition.equals(position)) {
            currentBoardItemPosition = position;
            setViewsPosition(point);
            needInvalidate = true;
        }
        if (!currentBoardItemSize.equals(size)) {
            currentBoardItemSize = size;
            currentBoardItem.onViewResized(currentBoardItem.getCurrentSize(), size);
            setViewsSize(size);
            needInvalidate = true;
        }
        if (needInvalidate) {
            currentBoardView.invalidate();
            currentPlaceholderView.invalidate();
        }
    }

    private void setViewsSize(Size size) {
        currentBoardView.setLayoutParams(new RelativeLayout.LayoutParams((int) board.getSizePhysicalWidth(size), (int) board.getSizePhysicalHeight(size)));
        currentPlaceholderView.setLayoutParams(new RelativeLayout.LayoutParams((int) board.getSizePhysicalWidth(size), (int) board.getSizePhysicalHeight(size)));
    }

    private void setViewsPosition(Point point) {
        currentBoardView.setX(point.getX());
        currentBoardView.setY(point.getY());
        currentPlaceholderView.setX(point.getX());
        currentPlaceholderView.setY(point.getY());
    }

    private void setLastFitSizeRect(Size size, Point point) {
        lastFitSizeRect.left = (int) point.getX();
        lastFitSizeRect.top = (int) point.getY();
        lastFitSizeRect.right = (int) (point.getX() + board.getSizePhysicalWidth(size));
        lastFitSizeRect.bottom = (int) (point.getY() + board.getSizePhysicalHeight(size));
    }

    private void setDraggedSizeRect(float x, float y, float width, float height) {
        draggedSizeRect.left = (int) x;
        draggedSizeRect.top = (int) y;
        draggedSizeRect.right = (int) (x + width);
        draggedSizeRect.bottom = (int) (y + height);
    }

    public void resizeFinished() {
        resizeDirection = ResizeLayerRenderer.ResizeDirection.NONE;
        currentBoardItem.setCurrentSize(currentBoardItemSize);
        currentBoardItem.setCurrentPosition(currentBoardItemPosition);
        initDraggedSizeRect();
        initLastFitSizeRect();
    }

    public void setCurrentBoardItem(Board board, BoardItem currentBoardItem, View view, View placeholderView) {
        this.board = board;
        this.currentBoardItem = currentBoardItem;
        this.currentBoardView = view;
        this.currentPlaceholderView = placeholderView;

        initDraggedSizeRect();
        initLastFitSizeRect();

        resizeDirection = ResizeLayerRenderer.ResizeDirection.NONE;

        currentBoardItemPosition = currentBoardItem.getCurrentPosition();
        currentBoardItemSize = currentBoardItem.getCurrentSize();
        invalidate();
    }

    private void initLastFitSizeRect() {
        lastFitSizeRect.left = draggedSizeRect.left;
        lastFitSizeRect.top = draggedSizeRect.top;
        lastFitSizeRect.right = draggedSizeRect.right;
        lastFitSizeRect.bottom = draggedSizeRect.bottom;
    }

    private void initDraggedSizeRect() {
        setDraggedSizeRect(getViewX(), getViewY(), getViewWidth(), getViewHeight());
    }

    private float getViewX() {
        return board.getGridPositionPhysicalPosition(currentBoardItem.getCurrentPosition()).getX();
    }

    private float getViewY() {
        return board.getGridPositionPhysicalPosition(currentBoardItem.getCurrentPosition()).getY();
    }

    private float getViewWidth() {
        return board.getBoardItemPhysicalWidth(currentBoardItem);
    }

    private float getViewHeight() {
        return board.getBoardItemPhysicalHeight(currentBoardItem);
    }

    public Point getLeftPoint() {
        return new Point(getViewX(), getViewY() + getViewHeight() / 2f);
    }

    public Point getRightPoint() {
        return new Point(getViewX() + getViewWidth(), getViewY() + getViewHeight() / 2f);
    }

    public Point getTopPoint() {
        return new Point(getViewX() + getViewWidth() / 2f, getViewY());
    }

    public Point getBottomPoint() {
        return new Point(getViewX() + getViewWidth() / 2f, getViewY() + getViewHeight());
    }

    public float getDragPointRadius() {
        if (resizeLayerRenderer != null) {
            return resizeLayerRenderer.getDragPointRadius();
        }
        return 0f;
    }
}