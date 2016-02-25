package hu.gabornovak.dynamicboard.board;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hu.gabornovak.dynamicboard.option.Option;

/**
 * Created by gnovak on 2/7/2016.
 */
public abstract class BoardItem {
    private final Size DEFAULT_MIN_SIZE = new Size(1, 1);

    private Size minSize = DEFAULT_MIN_SIZE;
    private Size maxSize;
    private Size currentSize;
    private boolean isResizeable;
    private GridPosition currentPosition;
    private List<Option> options = new ArrayList<>();

    public abstract ViewGroup getView(GridPosition position, Size size);

    public abstract View getPlaceholderView(GridPosition position, Size size);

    public abstract void onViewResized(Size oldSize, Size newSize);

    public abstract void onViewMoved(GridPosition oldPosition, GridPosition newPosition);

    public Size getMinSize() {
        return minSize;
    }

    public void setMinSize(Size minSize) {
        this.minSize = minSize;
    }

    public Size getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Size maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isResizeable() {
        return isResizeable;
    }

    public void setResizeable(boolean resizeable) {
        isResizeable = resizeable;
    }

    public void addOption(Option option) {
        options.add(option);
        option.setBoardItem(this);
    }

    public void setCurrentPosition(GridPosition currentPosition) {
        if (!currentPosition.equals(this.currentPosition) && this.currentPosition != null) {
            onViewMoved(this.currentPosition, currentPosition);
        }
        this.currentPosition = currentPosition;
    }

    public GridPosition getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentSize(Size currentSize) {
        if (!currentSize.equals(this.currentSize) && this.currentSize != null) {
            onViewResized(this.currentSize, currentSize);
        }
        this.currentSize = currentSize;
    }

    public Size getCurrentSize() {
        return currentSize;
    }

    public List<Option> getOptions() {
        return options;
    }
}
