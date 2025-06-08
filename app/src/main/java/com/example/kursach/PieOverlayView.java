package com.example.kursach;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class PieOverlayView extends View {
    private int holeColor = Color.WHITE;
    private int outlineColor = Color.TRANSPARENT;
    private boolean showOutline = false;
    private float outlineWidth = 8f; // px

    private Paint holePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PieOverlayView(Context context) {
        super(context);
        init();
    }
    public PieOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public PieOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        holePaint.setStyle(Paint.Style.FILL);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(outlineWidth);
        outlinePaint.setColor(outlineColor);
    }
    public void setHoleColor(int color) {
        this.holeColor = color;
        holePaint.setColor(color);
        invalidate();
    }
    public void setOutlineColor(int color) {
        this.outlineColor = color;
        outlinePaint.setColor(color);
        invalidate();
    }
    public void setShowOutline(boolean show) {
        this.showOutline = show;
        invalidate();
    }
    public void setOutlineWidth(float widthPx) {
        this.outlineWidth = widthPx;
        outlinePaint.setStrokeWidth(widthPx);
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2f;
        // Draw hole
        holePaint.setColor(holeColor);
        canvas.drawCircle(cx, cy, radius - outlineWidth/2f, holePaint);
        // Draw outline if needed
        if (showOutline && outlineColor != Color.TRANSPARENT) {
            outlinePaint.setColor(outlineColor);
            canvas.drawCircle(cx, cy, radius - outlineWidth/2f, outlinePaint);
        }
    }
} 