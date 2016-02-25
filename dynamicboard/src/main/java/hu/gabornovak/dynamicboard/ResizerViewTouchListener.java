package hu.gabornovak.dynamicboard;

import android.view.MotionEvent;
import android.view.View;

import hu.gabornovak.dynamicboard.board.Point;
import hu.gabornovak.dynamicboard.api.ResizeLayerRenderer;

/**
 * Created by gnovak on 2/10/2016.
 */
public class ResizerViewTouchListener implements View.OnTouchListener {
    private Point draggedPoint;
    private Point draggedPointDown;
    public float movedDiffX;
    public float movedDiffY;
    private float downX;
    private float downY;
    private long downTime;
    private boolean isVertical;

    private ResizeLayerRenderer.ResizeDirection direction;
    private ResizerView resizerView;

    public ResizerViewTouchListener(ResizerView resizerView) {
        this.resizerView = resizerView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!resizerView.isViewVisible()) {
            return false;
        }
        float diffX = event.getRawX() - downX;
        float diffY = event.getRawY() - downY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (handleDownEvent(event)) {
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                setMovedDiffs(event);
                if (draggedPoint != null) {
                    handleMove(diffX, diffY);
                }
                break;
            case MotionEvent.ACTION_UP:
                setMovedDiffs(event);
                if (draggedPoint != null) {
                    handleUpEvent(diffX, diffY);
                }
                resizeFinished();
                break;
            default:
                break;

        }
        return false;
    }

    private void setMovedDiffs(MotionEvent event) {
        movedDiffX = Math.max(Math.abs(event.getRawX() - downX), movedDiffX);
        movedDiffY = Math.max(Math.abs(event.getRawY() - downY), movedDiffY);
    }

    private void handleUpEvent(float diffX, float diffY) {
        switch (direction) {
            case TOP:
                resizerView.setResizeLayerTopOfBy(diffY);
                break;
            case BOTTOM:
                resizerView.setResizeLayerBottomOfBy(diffY);
                break;
            case LEFT:
                resizerView.setResizeLayerLeftOfBy(diffX);
                break;
            case RIGHT:
                resizerView.setResizeLayerRightOfBy(diffX);
                break;
        }
        resizerView.resizeFinished();
    }

    private void resizeFinished() {
        if (resizerView.isViewVisible() && isSingleTapEvent()) {
            resizerView.hideView();
        }
        draggedPoint = null;
        draggedPointDown = null;
    }

    private void handleMove(float diffX, float diffY) {
        if (isVertical) {
            draggedPoint.setX(draggedPointDown.getX() + diffX);
        } else {
            draggedPoint.setY(draggedPointDown.getY() + diffY);
        }
        switch (direction) {
            case TOP:
                resizerView.setResizeLayerTopOfBy(diffY);
                break;
            case BOTTOM:
                resizerView.setResizeLayerBottomOfBy(diffY);
                break;
            case LEFT:
                resizerView.setResizeLayerLeftOfBy(diffX);
                break;
            case RIGHT:
                resizerView.setResizeLayerRightOfBy(diffX);
                break;
        }
    }

    private boolean handleDownEvent(MotionEvent event) {
        downX = event.getRawX();
        downY = event.getRawY();
        downTime = System.currentTimeMillis();

        float x = event.getX();
        float y = event.getY();

        if (isCircleContainsPoint(resizerView.getLeftPoint(), x, y)) {
            draggedPoint = resizerView.getLeftPoint();
            draggedPointDown = new Point(draggedPoint);
            direction = ResizeLayerRenderer.ResizeDirection.LEFT;
            isVertical = true;
            return true;
        } else if (isCircleContainsPoint(resizerView.getRightPoint(), x, y)) {
            draggedPoint = resizerView.getRightPoint();
            draggedPointDown = new Point(draggedPoint);
            direction = ResizeLayerRenderer.ResizeDirection.RIGHT;
            isVertical = true;
            return true;
        } else if (isCircleContainsPoint(resizerView.getTopPoint(), x, y)) {
            draggedPoint = resizerView.getTopPoint();
            draggedPointDown = new Point(draggedPoint);
            direction = ResizeLayerRenderer.ResizeDirection.TOP;
            isVertical = false;
            return true;
        } else if (isCircleContainsPoint(resizerView.getBottomPoint(), x, y)) {
            draggedPoint = resizerView.getBottomPoint();
            draggedPointDown = new Point(draggedPoint);
            direction = ResizeLayerRenderer.ResizeDirection.BOTTOM;
            isVertical = false;
            return true;
        }
        return false;
    }

    private boolean isSingleTapEvent() {
        return (movedDiffX < 100 && movedDiffY < 100 && (System.currentTimeMillis() - downTime) < 400);
    }

    private boolean isCircleContainsPoint(Point point, float x, float y) {
        return (resizerView.getDragPointRadius() * resizerView.getDragPointRadius() * 2f) >= (Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - y, 2));
    }
}