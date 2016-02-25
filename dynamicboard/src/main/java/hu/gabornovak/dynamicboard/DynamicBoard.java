package hu.gabornovak.dynamicboard;

import android.content.Context;

import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.utils.Utils;

/**
 * Created by gnovak on 2/7/2016.
 */
public class DynamicBoard extends Board {
    private static final int ONE_FIELD_SIZE_IN_DP = 80;
    private static final int MARGIN_IN_DP = 4;

    private Context context;

    public DynamicBoard(Context context) {
        this.context = context;
    }

    @Override
    protected float calculateBoardSize(int width, int height) {
        return Utils.pxFromDp(context, ONE_FIELD_SIZE_IN_DP);
    }

    @Override
    protected float getMarginSize() {
        return Utils.pxFromDp(context, MARGIN_IN_DP);
    }
}
