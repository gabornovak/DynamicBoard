package hu.gabornovak.dynamicboard.example;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.GridPosition;
import hu.gabornovak.dynamicboard.board.Size;
import hu.gabornovak.dynamicboard.utils.Utils;

/**
 * Created by gnovak on 2/7/2016.
 */
public class DynamicBoardItem extends BoardItem {
    private Context context;
    private String title;
    private String subTitle;
    private int value;
    private int imgResId;

    private TextView titleTextView;
    private TextView subTitleTextView;
    private TextView valueTextView;
    private ImageView imageView;
    private CardView cardView;

    public DynamicBoardItem(Context context, String title, String subTitle, int value, int imgResId) {
        this.context = context;
        this.title = title;
        this.subTitle = subTitle;
        this.value = value;
        this.imgResId = imgResId;
    }

    @Override
    public ViewGroup getView(GridPosition position, Size size) {
        if (cardView == null) {
            cardView = new CardView(context);
            View view = View.inflate(context, R.layout.example_chart_layout, cardView);
            titleTextView = (TextView) view.findViewById(R.id.title);
            subTitleTextView = (TextView) view.findViewById(R.id.subTitle);
            valueTextView = (TextView) view.findViewById(R.id.value);
            imageView = (ImageView) view.findViewById(R.id.image);

            cardView.setCardElevation(Utils.pxFromDp(context, 2f));

            titleTextView.setText(title);
            subTitleTextView.setText(subTitle);
            imageView.setImageResource(imgResId);
            setViewForSize(size);
        }
        return cardView;
    }

    private void setViewForSize(Size size) {
        if (size.getHeight() >= 2) {
            imageView.setVisibility(View.VISIBLE);
        }
        if (size.getHeight() >= 3) {
            subTitleTextView.setVisibility(View.VISIBLE);
        }
        if (size.getHeight() < 2) {
            imageView.setVisibility(View.GONE);
            subTitleTextView.setVisibility(View.GONE);
        }
        if (size.getHeight() < 3) {
            subTitleTextView.setVisibility(View.GONE);
        }

        if (size.getWidth() > 2) {
            valueTextView.setText(String.format("$ %d", value));
        } else {
            valueTextView.setText(String.format("%1.3f M", value / 1000000f));
        }
    }

    @Override
    public View getPlaceholderView(GridPosition position, Size size) {
        CardView cardView = new CardView(context);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        return cardView;
    }

    @Override
    public void onViewResized(Size oldSize, Size newSize) {
        setViewForSize(newSize);
        cardView.invalidate();
    }

    @Override
    public void onViewMoved(GridPosition oldPosition, GridPosition newPosition) {
    }
}
