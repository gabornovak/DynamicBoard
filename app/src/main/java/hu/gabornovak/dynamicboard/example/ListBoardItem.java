package hu.gabornovak.dynamicboard.example;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.GridPosition;
import hu.gabornovak.dynamicboard.board.Size;
import hu.gabornovak.dynamicboard.utils.Utils;

/**
 * Created by gnovak on 2/7/2016.
 */
public class ListBoardItem extends BoardItem {
    private Context context;
    private String[] strings;

    private ListView listView;
    private CardView cardView;

    public ListBoardItem(Context context, String[] strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public ViewGroup getView(GridPosition position, Size size) {
        if (cardView == null) {
            cardView = new CardView(context);
            View view = View.inflate(context, R.layout.example_list_layout, cardView);
            listView = (ListView) view.findViewById(R.id.list);

            listView.setLongClickable(false);
            listView.setClickable(false);
            listView.setOnLongClickListener(null);
            listView.setOnItemClickListener(null);
            listView.setOnItemLongClickListener(null);

            cardView.setCardElevation(Utils.pxFromDp(context, 2f));

            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < strings.length; ++i) {
                list.add(strings[i]);
            }
            final StableArrayAdapter adapter = new StableArrayAdapter(context, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
        }

        return cardView;
    }

    @Override
    public View getPlaceholderView(GridPosition position, Size size) {
        CardView cardView = new CardView(context);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        return cardView;
    }

    @Override
    public void onViewResized(Size oldSize, Size newSize) {

    }

    @Override
    public void onViewMoved(GridPosition oldPosition, GridPosition newPosition) {
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {
        private HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
