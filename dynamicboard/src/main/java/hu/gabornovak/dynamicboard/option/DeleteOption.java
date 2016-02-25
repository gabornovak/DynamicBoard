package hu.gabornovak.dynamicboard.option;

import android.content.Context;

import hu.gabornovak.dynamicboard.R;
import hu.gabornovak.dynamicboard.board.Board;

/**
 * Created by gnovak on 2/14/2016.
 */
public class DeleteOption extends Option {
    public DeleteOption() {
        super(R.drawable.ic_delete_black_24dp, "Delete", OptionVisibility.IN_MENU);
    }

    @Override
    public void execute(Context context, Board board) {
        board.removeBoardItem(getBoardItem());
    }
}
