package hu.gabornovak.dynamicboard.board;

import java.util.ArrayList;
import java.util.List;

public abstract class Board {
    public interface OnBoardInitializedListener {
        void onBoardInitialized(Size boardSize);
    }

    private List<BoardItem> boardItems;
    private Size boardSize;
    private int oneBoardItemWidth;
    private int oneBoardItemHeight;
    private int boardWidth;
    private int boardHeight;
    private int xMargin;
    private int yMargin;

    private OnBoardInitializedListener onBoardInitializedListener;

    public Board() {
        boardItems = new ArrayList<>();
    }

    public void initBoardSize(int boardWidth, int boardHeight) {
        initBoardSize(boardWidth, boardHeight, calculateBoardSize(boardWidth, boardHeight));
    }

    public void initBoardSize(int boardWidthInPixel, int boardHeightInPixel, float oneItemSize) {
        boardWidthInPixel = (int) (boardWidthInPixel - getMarginSize());
        boardHeightInPixel = (int) (boardHeightInPixel - getMarginSize());

        boardSize = new Size((int) (boardWidthInPixel / oneItemSize), (int) (boardHeightInPixel / oneItemSize));
        boardWidth = boardWidthInPixel;
        boardHeight = boardHeightInPixel;
        oneBoardItemWidth = (int) ((float) boardWidthInPixel / boardSize.getWidth());
        oneBoardItemHeight = (int) ((float) boardHeightInPixel / boardSize.getHeight());

        xMargin = yMargin = (int) getMarginSize();
        if (onBoardInitializedListener != null) {
            onBoardInitializedListener.onBoardInitialized(boardSize);
        }
    }

    protected abstract float calculateBoardSize(int width, int height);

    protected abstract float getMarginSize();

    public boolean isBoardItemFit(BoardItem boardItem, GridPosition position, Size size) {
        Rectangle rect1 = new Rectangle(position.getX(), position.getY(), size.getWidth(), size.getHeight());
        for (BoardItem item : boardItems) {
            if (!item.equals(boardItem)) {
                int width = item.getCurrentSize().getWidth();
                int height = item.getCurrentSize().getHeight();
                Rectangle rect2 = new Rectangle(item.getCurrentPosition().getX(), item.getCurrentPosition().getY(), width, height);
                if (rect1.intersects(rect2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInited() {
        return boardHeight != 0 && boardWidth != 0;
    }

    public void addBoardItem(BoardItem boardItem, GridPosition position, Size size) {
        if (isBoardItemFit(boardItem, position, size)) {
            boardItem.setCurrentPosition(position);
            boardItem.setCurrentSize(size);
            boardItems.add(boardItem);
        }
    }

    public void removeBoardItem(BoardItem boardItem) {
        boardItems.remove(boardItem);
    }

    public void setOnBoardInitializedListener(OnBoardInitializedListener onBoardInitializedListener) {
        this.onBoardInitializedListener = onBoardInitializedListener;
    }

    public Size getBoardSize() {
        return boardSize;
    }

    public GridPosition getSnappedGridPositionForPhysicalPosition(float x, float y) {
        return new GridPosition(Math.round(x / oneBoardItemWidth), Math.round(y / oneBoardItemHeight));
    }

    public Point getGridPositionPhysicalPosition(GridPosition position) {
        return new Point(position.getX() * oneBoardItemWidth + 1.5f * xMargin, position.getY() * oneBoardItemHeight + 1.5f * yMargin);
    }

    public float getSizePhysicalWidth(Size size) {
        return size.getWidth() * oneBoardItemWidth - 2f * xMargin;
    }

    public float getBoardItemPhysicalWidth(BoardItem boardItem) {
        return getSizePhysicalWidth(boardItem.getCurrentSize());
    }

    public float getSizePhysicalHeight(Size size) {
        return size.getHeight() * oneBoardItemHeight - 2f * yMargin;
    }

    public float getBoardItemPhysicalHeight(BoardItem boardItem) {
        return getSizePhysicalHeight(boardItem.getCurrentSize());
    }

    public int getOneBoardItemWidth() {
        return oneBoardItemWidth;
    }

    public int getOneBoardItemHeight() {
        return oneBoardItemHeight;
    }

    public Size getSizeForPhysicalSize(float width, float height) {
        return new Size(Math.round(width / oneBoardItemWidth), Math.round(height / oneBoardItemHeight));
    }
}
