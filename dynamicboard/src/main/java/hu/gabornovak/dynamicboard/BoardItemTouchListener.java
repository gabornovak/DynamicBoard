package hu.gabornovak.dynamicboard;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.MotionEvent;
import android.view.View;

import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.Point;
import hu.gabornovak.dynamicboard.utils.EmptyAnimatorListener;

/**
 * Created by gnovak on 2/7/2016.
 */
public abstract class BoardItemTouchListener implements View.OnTouchListener {
    private static final long RUNNABLE_POST_DELAY_TIME = 600;
    protected float positionX;
    protected float positionY;
    protected float downX;
    protected float downY;
    private long downTime;
    protected float movedDiffX;
    protected float movedDiffY;
    protected boolean dragEnabled;
    protected boolean runnableStarted;
    protected boolean inAnimation;
    protected boolean hasUpDuringRunnable;

    protected Board board;
    protected BoardItem boardItem;
    protected View placeholderView;
    protected View view;

    public BoardItemTouchListener(Board board, BoardItem boardItem, View view, View placeholderView) {
        this.board = board;
        this.boardItem = boardItem;
        this.view = view;
        this.placeholderView = placeholderView;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handleDownEvent(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (dragEnabled) {
                handleMoveEventInDraggedMode(event);
            } else {
                handleMoveInNotDraggedMode(event);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((System.currentTimeMillis() - downTime) < RUNNABLE_POST_DELAY_TIME) {
                hasUpDuringRunnable = true;
            }
            if (dragEnabled) {
                handleUpEvent(event);
            }
            dragEnabled = false;
        }
        return true;
    }

    private void handleDownEvent(final MotionEvent event) {
        initVariables(event);
        if (!runnableStarted) {
            runnableStarted = true;
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (movedDiffX < 100 && movedDiffY < 100 && !hasUpDuringRunnable) {
                        dragStarted(event);
                    }
                    runnableStarted = false;
                }
            }, RUNNABLE_POST_DELAY_TIME);
        }
    }

    private void handleMoveInNotDraggedMode(MotionEvent event) {
        movedDiffX = Math.max(Math.abs(event.getRawX() - downX), movedDiffX);
        movedDiffY = Math.max(Math.abs(event.getRawY() - downY), movedDiffY);
    }

    private void initVariables(MotionEvent event) {
        Point point = board.getGridPositionPhysicalPosition(boardItem.getCurrentPosition());
        positionX = point.getX();
        positionY = point.getY();
        downX = event.getRawX();
        downY = event.getRawY();
        downTime = System.currentTimeMillis();
        movedDiffX = 0;
        movedDiffY = 0;
        dragEnabled = false;
        hasUpDuringRunnable = false;
    }

    protected void dragStarted(MotionEvent event) {
        Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
        downX = event.getRawX();
        downY = event.getRawY();
        dragEnabled = true;
        onDragStarted();
    }

    private void handleMoveEventInDraggedMode(MotionEvent event) {
        float newX = positionX + event.getRawX() - downX;
        float newY = positionY + event.getRawY() - downY;
        onMoveEventInDragMode(newX, newY);
    }

    private void handleUpEvent(MotionEvent event) {
        float newX = positionX + event.getRawX() - downX;
        float newY = positionY + event.getRawY() - downY;
        onUpEventInDragMode(newX, newY);
        onDragFinished();
    }

    protected void animateViewToPosition(View v, float x, float y, int duration) {
        inAnimation = true;
        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.play(ObjectAnimator.ofFloat(v, "translationX", x))
                .with(ObjectAnimator.ofFloat(v, "translationY", y));
        set.addListener(new EmptyAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inAnimation = false;
            }
        });
        set.start();
    }

    protected abstract void onMoveEventInDragMode(float x, float y);

    protected abstract void onUpEventInDragMode(float x, float y);

    protected abstract void onDragStarted();

    protected abstract void onDragFinished();
}