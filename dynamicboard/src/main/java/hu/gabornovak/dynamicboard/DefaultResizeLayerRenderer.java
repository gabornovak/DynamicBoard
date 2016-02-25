package hu.gabornovak.dynamicboard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;

import hu.gabornovak.dynamicboard.api.ResizeLayerRenderer;

/**
 * Created by gnovak on 2/11/2016.
 */
public class DefaultResizeLayerRenderer implements ResizeLayerRenderer {
    private Paint pointPaint;
    private Paint rectPaint;

    public DefaultResizeLayerRenderer() {
        pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(0xffffffff);
        pointPaint.setShadowLayer(3f, 1f, 1f, Color.DKGRAY);
        pointPaint.setAntiAlias(true);
        pointPaint.setDither(true);

        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(0xffffffff);
        rectPaint.setShadowLayer(3f, 1f, 1f, Color.DKGRAY);
        rectPaint.setStrokeWidth(6f);
        rectPaint.setAntiAlias(true);
        rectPaint.setDither(true);
    }

    public void onBindView(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, pointPaint);
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, rectPaint);
        }
    }

    @Override
    public float getDragPointRadius() {
        return 35f;
    }

    @Override
    public void onDraw(Canvas canvas, Rect draggedSize, Rect lastFitSize, ResizeDirection direction) {
        canvas.drawRect(draggedSize.left, draggedSize.top, draggedSize.right, draggedSize.bottom, rectPaint);
        switch (direction) {
            case BOTTOM:
                canvas.drawCircle(draggedSize.left + draggedSize.width() / 2f, draggedSize.bottom, getDragPointRadius(), pointPaint);
                break;
            case LEFT:
                canvas.drawCircle(draggedSize.left, draggedSize.top + draggedSize.height() / 2f, getDragPointRadius(), pointPaint);
                break;
            case RIGHT:
                canvas.drawCircle(draggedSize.right, draggedSize.top + draggedSize.height() / 2f, getDragPointRadius(), pointPaint);
                break;
            case TOP:
                canvas.drawCircle(draggedSize.left + draggedSize.width() / 2f, draggedSize.top, getDragPointRadius(), pointPaint);
                break;
            default:
                canvas.drawCircle(draggedSize.left + draggedSize.width() / 2f, draggedSize.top, getDragPointRadius(), pointPaint);
                canvas.drawCircle(draggedSize.right, draggedSize.top + draggedSize.height() / 2f, getDragPointRadius(), pointPaint);
                canvas.drawCircle(draggedSize.left, draggedSize.top + draggedSize.height() / 2f, getDragPointRadius(), pointPaint);
                canvas.drawCircle(draggedSize.left + draggedSize.width() / 2f, draggedSize.bottom, getDragPointRadius(), pointPaint);
                break;
        }
    }
}
