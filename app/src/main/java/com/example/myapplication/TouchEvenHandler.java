package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * desc：用于处理缩放平移旋转，并返回转换矩阵
 * 单独抽取出来是为了避免当项目中出现大量需要自定义不同样式缩放平移控件时重复写大量代码
 * time:2020/04/29
 * author：ezreal.mei
 */
public class TouchEvenHandler {
    private View view;
    private Bitmap sourceBitmap;
    private float initRatio;
    // Matrix getValues 矩阵参数
    private float[] values=new float[9];
    private static final int MSCALE_X=0;
    private static final int MSKEW_X=1;
    private static final int MTRANS_X=2;
    private static final int MSKEW_Y=3;
    private static final int MSCALE_Y=4;
    private static final int MTRANS_Y=5;
    private static final int MPERSP_0=6;
    private static final int MPERSP_1=7;
    private static final int MPERSP_2=8;
    private float x_down = 0;
    private float y_down = 0;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;              //  平移的距离
    private float oldRotation = 0;          //   手指第一次放上去的角度
    private float rotation;                //    正在旋转中变化的角度
    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    //用于保存matrix
    private Matrix savedMatrix = new Matrix();
    // 检测是否出界
    boolean matrixCheck = false;
    // 控件的大小
    private int widthScreen;
    private int heightScreen;
    // 当前操作的状态
    public int currentStatus;
    public static final int DEFAULT_BITMAP=0;      // 默认状态下
    public static final int SCALE_BITMAP=1;        //  缩放状态下
    public static final int TRANSLATE_BITMAP=2;    //  平移
    public static final int ROTATION_BITMAP=3;    //   旋转
    public static final int NONE_BITMAP=4;        // 不作任何操作
    public static final int SCALE_BITMAP_OUT=5;   //放大
    public static final int SCALE_BITMAP_IN=6;    // 缩小
    private boolean isAutoRefresh=false;
    private boolean canRotate=true;             // 默认可旋转

    public TouchEvenHandler(View view, Bitmap sourceBitmap,boolean isAutoRefresh) {
        this.view=view;
        this.isAutoRefresh=isAutoRefresh;
        widthScreen=view.getWidth();
        heightScreen=view.getHeight();
        this.sourceBitmap=sourceBitmap;
        initBitmap();
        if (!isAutoRefresh){
            view.invalidate();
        }
    }


    /**
     * 获取最终用于绘制的矩阵变量
     * @return
     */
    public Matrix getMatrix(){
        return matrix;
    }

    /**
     * 选择是否可以旋转
     * @param canRotate
     */
    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    /**
     * 获取图片缩放比例
     * @return
     */
    public double getZoomX(){
        return values[MSCALE_X]/getCosA();
    }


    public double getZoomY(){
        return values[MSCALE_Y]/getCosA();
    }

    /**
     * 获取图片左上角在画布中的坐标
     * @return X
     */
    public float getTranslateX(){
        return values[MTRANS_X];
    }

    /**
     * 获取图片左上角在画布中的坐标
     * @return Y
     */
    public float getTranslateY(){
        return values[MTRANS_Y];
    }

    /**
     * 返回图片旋转的弧度
     * @return
     */
    public double getRadians(){
        double radians=Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]);
        return radians;
    }

    /**
     * 返回图片弧度的余弦值
     * @return
     */
    public double getCosA(){
        return Math.cos(getRadians());
    }

    /**
     * 返回图片旋转弧度的正弦值
     * @return
     */
    public double getSinA(){
        return Math.sin(getRadians());
    }

    /**
     * 将values数组变成字符串输出
     * @return
     */
    public String getValuesToString(){
        return Arrays.toString(values);
    }


    /**
     * 已知原始相对于图片的坐标，计算图片平移缩放或绕某点旋转之后相对于整个画布的坐标
     * @return 画布坐标
     */
    public PointF coordinatesToCanvas(float x,float y){
        //Logger.e("-------矩阵:"+getValuesToString());
        double radians=Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]);
        double cosA=Math.cos(radians);
        double sinA=Math.sin(radians);
        //Logger.e("-----cosA:"+cosA+";sinA:"+sinA);
        //Logger.e("-------旋转的角度："+Math.toDegrees(radians));
        double cx=x*getZoomX();
        double cy=y*getZoomY();
        //Logger.e("-------图片缩放值："+getZoomX());
        //Logger.e("-----cx:"+cx+"--cy:"+cy);
        double x1=cx*cosA-cy*sinA;
        double y1=cx*sinA+cy*cosA;
        double x2=getTranslateX()+x1;
        double y2=getTranslateY()+y1;
        //Logger.e("------x1:"+x1+"y1:"+y1+"--x2:"+x2+"--y2:"+y2);
        return new PointF((float) x2,(float) y2);
    }

    /**
     * 已知相对于画布的坐标计算出该点相对于图片左上角的坐标，按照上面的方式反着推
     * @return
     */
    public PointF coordinatesToImage(float x,float y){
        x=widthScreen/2;
        y=heightScreen/2;
        double cosA=getCosA();
        double sinA=getSinA();
        float x1=x-getTranslateX();
        float y1=y-getTranslateY();
        double cx=x1/cosA+sinA/(cosA*cosA+sinA*sinA)*(y1-sinA*x1/cosA);
        double cy=(y1-x1*sinA/cosA)*cosA/(cosA*cosA+sinA*sinA);
        double x2=cx/getZoomX();
        double y2=cy/getZoomY();
        return new PointF((float) x2,(float) y2);
    }

    /**
     * 处理点击触摸事件
     * @param event
     */
    public void touchEvent(@NotNull MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                currentStatus = TRANSLATE_BITMAP;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                currentStatus = SCALE_BITMAP;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentStatus == SCALE_BITMAP) {
                    matrix1.set(savedMatrix);
                    rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    if (newDist>oldDist){
                        currentStatus=SCALE_BITMAP_OUT;
                    }else {
                        currentStatus=SCALE_BITMAP_IN;
                    }
                    float scale = newDist / oldDist;
                    matrix1.postScale(scale, scale, mid.x, mid.y);// 缩放
                    if (canRotate){
                        matrix1.postRotate(rotation, mid.x, mid.y);// 旋转
                    }
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        currentStatus=SCALE_BITMAP;
                        matrix1.getValues(values);
                        matrix.set(matrix1);
                        if (!isAutoRefresh)
                        view.invalidate();
                    }
                } else if (currentStatus == TRANSLATE_BITMAP) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        currentStatus=TRANSLATE_BITMAP;
                        matrix1.getValues(values);
                        matrix.set(matrix1);
                        if (!isAutoRefresh)
                        view.invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                currentStatus = NONE_BITMAP;
                view.invalidate();
                break;
        }

    }


    private void initBitmap(){
        if (sourceBitmap!=null){
            matrix.reset();
            savedMatrix.set(matrix);
            matrix1.set(savedMatrix);
            int bitmapWidth=sourceBitmap.getWidth();
            int bitmapHeight=sourceBitmap.getHeight();
            Logger.d("图片宽高："+bitmapWidth+";"+bitmapHeight+"控件："+widthScreen+";"+heightScreen);
            //当图片的宽或者高大于控件的宽高
            if (bitmapWidth>widthScreen||bitmapHeight>heightScreen){
                //如果图片的宽大于控件的宽
                if (bitmapWidth>widthScreen){
                    Logger.d("图片的宽大于控件的宽");
                    float ratio=widthScreen/(bitmapWidth*1f);
                    float translateY = (heightScreen - (bitmapHeight * ratio)) / 2f;
                    matrix1.postScale(ratio,ratio);
                    // 在纵坐标方向上进行偏移，以保证图片居中显示
                    matrix1.postTranslate(0, translateY);
                    initRatio=ratio;
                }else if(bitmapHeight>heightScreen) {
                    Logger.d("图片的高大于控件的高");
                    float ratio=heightScreen/(bitmapHeight*1f);
                    matrix1.postScale(ratio,ratio);
                    float translateX=(widthScreen-(bitmapWidth*ratio))/2f;
                    //在横坐标上偏移，保证图片居中显示
                    matrix1.postTranslate(translateX,0);
                    Logger.e("------ratio"+ratio+";"+translateX);
                    initRatio=ratio;
                }else {
                    //当图片宽高都大于控件宽高
                    Logger.d("当图片宽高都大于控件宽高");
                    float ratio= Math.max((bitmapWidth*1f)/widthScreen,(bitmapHeight*1f)/heightScreen);
                    matrix1.postScale(ratio,ratio);
                    float translateX=(widthScreen-(bitmapWidth*ratio))/2f;
                    float translateY=(heightScreen-(bitmapHeight*ratio))/2f;
                    matrix1.postTranslate(translateX,translateY);
                    initRatio=ratio;
                }
            }else {
                //当图片的宽高都小于控件的宽高，则按照原图比例居中显示
                Logger.d("图片的宽高都小于控件的宽高");
                float translateX=(widthScreen-bitmapWidth)/2f;
                float translateY=(heightScreen-bitmapHeight)/2f;
                matrix1.postTranslate(translateX,translateY);
                initRatio=1f;
            }
            matrix.set(matrix1);
            matrix.getValues(values);
        }
    }

    /**
     * 边界检测
     * @return
     */
    private boolean matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * sourceBitmap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * sourceBitmap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * sourceBitmap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * sourceBitmap.getHeight() + f[5];
        float x4 = f[0] * sourceBitmap.getWidth() + f[1] * sourceBitmap.getHeight() + f[2];
        float y4 = f[3] * sourceBitmap.getWidth() + f[4] * sourceBitmap.getHeight() + f[5];
        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        // 缩放比率判断
        if (width < sourceBitmap.getWidth()*initRatio-1 || width > widthScreen * 3) {
            return true;
        }
        // 出界判断
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
            return true;
        }
        return false;
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = Math.abs(event.getX(0) - event.getX(1));
        float y = Math.abs(event.getY(0) - event.getY(1));
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        // Logger.e("取弧度："+radians);
        return (float) Math.toDegrees(radians);
    }
}
