package hu.gabornovak.dynamicboard.option;

import android.content.Context;

import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.board.BoardItem;

/**
 * Created by gnovak on 2/14/2016.
 */
public abstract class Option {
    public enum OptionVisibility {
        ALWAYS, IF_ROOM, IN_MENU
    }

    private int optionIcon;
    private String optionName;
    private boolean alwaysVisible;
    private OptionVisibility optionVisibility;

    private BoardItem boardItem;

    public Option(int optionIcon, String optionName, OptionVisibility optionVisibility) {
        this.optionIcon = optionIcon;
        this.optionName = optionName;
        this.optionVisibility = optionVisibility;
    }

    public abstract void execute(Context context, Board board);

    public int getOptionIcon() {
        return optionIcon;
    }

    public void setOptionIcon(int optionIcon) {
        this.optionIcon = optionIcon;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public BoardItem getBoardItem() {
        return boardItem;
    }

    public void setBoardItem(BoardItem boardItem) {
        this.boardItem = boardItem;
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }

    public OptionVisibility getOptionVisibility() {
        return optionVisibility;
    }

    public void setOptionVisibility(OptionVisibility optionVisibility) {
        this.optionVisibility = optionVisibility;
    }
}
