package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * desc :  通过Matrix实现可缩放、平移、旋转图片的控件
 * time ： 2020/04/29
 * author：EZReal.mei
 */
public class ZoomImageView1 extends View {
    // 绘制的图片
    private Bitmap sourceBitmap;
    // 当前操作的状态
    private int currentStatus;
    private static final int DEFAULT_BITMAP=0;      // 默认状态下
    private Paint pointPaint;
    private TouchEvenHandler touchEvenHandler;


    public ZoomImageView1(Context context) {
        super(context);
        init();
    }

    public ZoomImageView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bkpic);
        currentStatus=DEFAULT_BITMAP;
        pointPaint=new Paint();
        pointPaint.setColor(Color.RED);
        Logger.e("------图片的大小："+sourceBitmap.getWidth()+";"+sourceBitmap.getHeight());
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            // 分别获取到ImageView的宽度和高度
            touchEvenHandler=new TouchEvenHandler(this,sourceBitmap,false);
            Logger.e("控件的宽高----:"+getWidth()+";"+getHeight());

        }
    }

    protected void onDraw(Canvas canvas) {
        if (touchEvenHandler!=null){
            if (currentStatus==DEFAULT_BITMAP){
                canvas.drawBitmap(sourceBitmap,touchEvenHandler.getMatrix(),pointPaint);
                currentStatus=-1;
            }else{
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));
                canvas.drawBitmap(sourceBitmap, touchEvenHandler.getMatrix(), null);
                PointF pointF=touchEvenHandler.coordinatesToCanvas(100,200);
                canvas.drawCircle(pointF.x,pointF.y,5,pointPaint);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        touchEvenHandler.touchEvent(event);
        return true;
    }

}
