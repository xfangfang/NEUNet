package com.lalala.fangs.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.lalala.fangs.neunet.R;

/**
 * Created by FANGs on 2017/7/28.
 */

public class ShadowView extends View {

    private Paint mPaint;
    private int startColor;
    private int endColor;
    private int radius;
    private int shadowRadius;
    private int padding;
    ScaleAnimation animationUp,animationDown;


    public ShadowView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ShadowView, defStyle, 0);

        endColor = a.getInt(R.styleable.ShadowView_endColor, Color.BLACK);
        startColor = a.getInt(R.styleable.ShadowView_startColor, Color.WHITE);
        radius = a.getDimensionPixelSize(R.styleable.ShadowView_radius,
                (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 8));
        shadowRadius = a.getDimensionPixelSize(R.styleable.ShadowView_shadowRadius,
                (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 15));
        padding = shadowRadius ;



        a.recycle();


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        animationUp =new ScaleAnimation(0.98f, 1.0f, 0.98f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationUp.setDuration(100);
        animationUp.setFillAfter(true);

        animationDown =new ScaleAnimation(1.0f, 0.98f, 1.0f, 0.98f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationDown.setDuration(100);
        animationDown.setFillAfter(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rect;


        mPaint.setShadowLayer(0, 0, 0, Color.parseColor("#00FFFFFF"));
        mPaint.setShader(new LinearGradient(0, getHeight(),
                getWidth(), 0,
                startColor, endColor,
                Shader.TileMode.CLAMP));
        rect = new RectF(padding, padding, getWidth() - padding, getHeight() - padding);
        canvas.drawRoundRect(rect, radius, radius, mPaint);

        mPaint.setShadowLayer(shadowRadius, 0, 10, Color.parseColor("#80FFFFFF"));
        rect = new RectF(padding+10, padding+10, getWidth() - padding-10, getHeight() - padding-10);
        canvas.drawRoundRect(rect, radius, radius, mPaint);
    }

    public void setColor(String startColor,String endColor){
        this.startColor = Color.parseColor(startColor);
        this.endColor = Color.parseColor(endColor);
        invalidate();
    }


    private float getRawSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                this.startAnimation(animationUp);
                break;
            case MotionEvent.ACTION_DOWN:
                this.startAnimation(animationDown);
                break;
        }

        return super.onTouchEvent(event);
    }
}
