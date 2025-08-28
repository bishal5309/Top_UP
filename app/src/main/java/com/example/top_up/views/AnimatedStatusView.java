package com.example.top_up.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class AnimatedStatusView extends View {

    public enum State { EMPTY, LOADING, DONE }

    private State currentState = State.EMPTY;
    private Paint paint;
    private ValueAnimator spinnerAnimator;
    private float animatedAngle = 0f;

    public AnimatedStatusView(Context context) {
        super(context);
        init();
    }

    public AnimatedStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(4f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
    }

    public void setState(State state) {
        currentState = state;
        if (state == State.LOADING) {
            startSpinner();
        } else {
            stopSpinner();
        }
        invalidate();
    }

    private void startSpinner() {
        spinnerAnimator = ValueAnimator.ofFloat(0, 360);
        spinnerAnimator.setDuration(1000);
        spinnerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        spinnerAnimator.addUpdateListener(anim -> {
            animatedAngle = (float) anim.getAnimatedValue();
            invalidate();
        });
        spinnerAnimator.start();
    }

    private void stopSpinner() {
        if (spinnerAnimator != null) {
            spinnerAnimator.cancel();
            spinnerAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom();

        float cx = (left + right) / 2f;
        float cy = (top + bottom) / 2f;
        float radius = Math.min(right - left, bottom - top) / 2f - 2;

        switch (currentState) {
            case LOADING:
                paint.setColor(Color.parseColor("#1d334a"));
                canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
                        animatedAngle, 270, false, paint);
                break;

            case DONE:
                paint.setColor(Color.parseColor("#1d334a"));
                drawCheckmark(canvas, cx, cy, radius);
                break;

            case EMPTY:
                // Do nothing — no drawing
                break;
        }
    }

    private void drawCheckmark(Canvas canvas, float cx, float cy, float radius) {
        float scale = radius / 12f;
        float boost = 1.05f;

        Path path = new Path();
        path.moveTo(cx - 5 * scale * boost, cy);
        path.lineTo(cx - 1 * scale * boost, cy + 4 * scale * boost);
        path.lineTo(cx + 6 * scale * boost, cy - 4 * scale * boost);
        canvas.drawPath(path, paint);
    }
}