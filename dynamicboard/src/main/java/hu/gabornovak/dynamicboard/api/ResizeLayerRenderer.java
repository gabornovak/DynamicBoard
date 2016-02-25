package hu.gabornovak.dynamicboard.api;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by gnovak on 2/11/2016.
 */
public interface ResizeLayerRenderer {
    enum ResizeDirection {
        LEFT, RIGHT, TOP, BOTTOM, NONE
    }

    void onBindView(View v);
    float getDragPointRadius();
    void onDraw(Canvas canvas, Rect draggedSize, Rect lastFitSize, ResizeDirection direction);
}
