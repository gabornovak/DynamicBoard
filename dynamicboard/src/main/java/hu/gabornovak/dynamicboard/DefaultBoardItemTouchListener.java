package hu.gabornovak.dynamicboard;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.GridPosition;
import hu.gabornovak.dynamicboard.board.Point;
import hu.gabornovak.dynamicboard.utils.EmptyAnimatorListener;
import hu.gabornovak.dynamicboard.utils.Utils;

/**
 * Created by gnovak on 2/7/2016.
 */
public class DefaultBoardItemTouchListener extends BoardItemTouchListener {
    private final int DEFAULT_ANIMATION_DURATION = 200;
    private GridPosition lastSnappedPosition;

    private ResizerView resizerView;

    public DefaultBoardItemTouchListener(ResizerView resizerView, Board board, BoardItem boardItem, View view, View placeholderView) {
        super(board, boardItem, view, placeholderView);
        this.resizerView = resizerView;
    }

    protected void onMoveEventInDragMode(float x, float y) {
        GridPosition snappedPosition = board.getSnappedGridPositionForPhysicalPosition(x, y);
        if (board.isBoardItemFit(boardItem, snappedPosition, boardItem.getCurrentSize())) {
            if (!snappedPosition.equals(lastSnappedPosition)) {
                Point positionInPixel = board.getGridPositionPhysicalPosition(snappedPosition);
                lastSnappedPosition = snappedPosition;
                animateViewToPosition(placeholderView, positionInPixel.getX(), positionInPixel.getY(), DEFAULT_ANIMATION_DURATION);
            }
        } else {
            if (lastSnappedPosition != null) {
                Point positionInPixel = board.getGridPositionPhysicalPosition(lastSnappedPosition);
                animateViewToPosition(placeholderView, positionInPixel.getX(), positionInPixel.getY(), DEFAULT_ANIMATION_DURATION);
            }
        }
        view.setX(x);
        view.setY(y);
    }

    protected void onUpEventInDragMode(float x, float y) {
        GridPosition newPosition = lastSnappedPosition;
        if (newPosition == null) {
            newPosition = board.getSnappedGridPositionForPhysicalPosition(x, y);
        }
        Point positionInPixel = board.getGridPositionPhysicalPosition(newPosition);
        animateViewToPosition(view, positionInPixel.getX(), positionInPixel.getY(), DEFAULT_ANIMATION_DURATION);
        boardItem.setCurrentPosition(newPosition);

        resizerView.setCurrentBoardItem(board, boardItem, view, placeholderView);
    }

    protected void onDragStarted() {
        inAnimation = true;
        AnimatorSet set = new AnimatorSet();
        set.setDuration(DEFAULT_ANIMATION_DURATION);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.play(ObjectAnimator.ofFloat(view, "scaleX", 1.02f))
                .with(ObjectAnimator.ofFloat(view, "scaleY", 1.02f))
                .with(ObjectAnimator.ofFloat(view, "cardElevation", Utils.pxFromDp(view.getContext(), 12f)));
        set.addListener(new EmptyAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inAnimation = false;
                placeholderView.setVisibility(View.VISIBLE);
            }
        });
        set.start();
        hideResizerView();
    }

    private void hideResizerView() {
        resizerView.setLayerVisibility(0f);
        resizerView.invalidate();
    }

    private void showResizerView() {
        resizerView.setLayerVisibility(1f);
        resizerView.invalidate();
        resizerView.bringToFront();
        resizerView.getParent().requestLayout();
        ((View) resizerView.getParent()).invalidate();
    }

    protected void onDragFinished() {
        inAnimation = true;
        AnimatorSet set = new AnimatorSet();
        set.setDuration(DEFAULT_ANIMATION_DURATION);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.play(ObjectAnimator.ofFloat(view, "scaleX", 1f))
                .with(ObjectAnimator.ofFloat(view, "scaleY", 1f))
                .with(ObjectAnimator.ofFloat(view, "cardElevation", Utils.pxFromDp(view.getContext(), 2f)));
        set.addListener(new EmptyAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inAnimation = false;
                showResizerView();
            }
        });
        set.start();
        lastSnappedPosition = null;
    }
}