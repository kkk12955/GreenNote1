package com.im.tku.greennote;

/**
 * Created by B310 on 2017/9/4.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.im.tku.greennote.Editpage.listIvParams;

public class MyImageView extends android.support.v7.widget.AppCompatImageView implements Serializable {
    private static final long serialVersionUID = 1L;



    /** 使用者動作 */
    private enum Action {
        /** 沒動作 */
        None,
        /** 縮放 */
        Zoom,
        /** 拖曳 */
        Drag
    };

    /** 動作 */
    private Action action = Action.None;

    /** 矩形大小 */
    private RectF rectF;

    /** 畫筆 */
    private Paint paint = new Paint();

    /**縮放大小*/
    private float scale;

    public static int moveX;
    public static int moveY;

    {
        // 空心
        paint.setStyle(Paint.Style.STROKE);
        // 藍色
        paint.setColor(Color.BLUE);
    };

    /** 自訂ImageView */
    public MyImageView(Context context, AttributeSet attrs,Editpage.ImageViewParams params) {
        super(context, attrs);

        this.rectF = new RectF(0, 0, 480 - 50, 400 - 5);
        this.setOnTouchListener(new OnTouchListener() {

            /** 使用者點擊位置 */
            private float x, y;

            /** 距離 */
            private float distance = 1f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyImageView imageView = (MyImageView) v;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        action = Action.Drag;
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        action = Action.None;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        distance = getDistance(event);
                        // 螢幕觸碰為2個觸控點時
                        if (event.getPointerCount() == 2) {
                            action = Action.Zoom;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (action == Action.Drag) {
                            moveX = (int) (event.getRawX() - x);
                            moveY = (int) (event.getRawY() - y);
                            // 設定layout位置
                            v.layout(moveX, moveY, moveX + v.getWidth(), moveY + v.getHeight());

                        } else if (action == Action.Zoom) {
                            // 距離
                            float newDistance = getDistance(event);

                            // 縮放比例
                            scale = newDistance / this.distance;

                            // 設定當前距離
                            this.distance = newDistance;

                            // 將ImageView放大
                            ViewGroup.LayoutParams params = v.getLayoutParams();
                            params.width = (int) (v.getWidth() * scale);
                            params.height = (int) (v.getHeight() * scale);

                            // 重新定義矩形大小
                            rectF = new RectF(0, 0, params.width, params.height );
                            v.setLayoutParams(params);
                        }
                        Editpage.updateImageViewParams(imageView,scale);
                        break;
                }

                return true;
            }



        });
        if(params==null){
            params = new Editpage.ImageViewParams();
            setTag(System.currentTimeMillis());
            Editpage.saveImageViewparams(this);
        }else{
            setTag(params.getTag());
            ViewGroup.LayoutParams params_tmp = new ViewGroup.LayoutParams(params.getWidth(),params.getHeight());
            this.setLayoutParams(params_tmp);
            this.layout((int)params.getX(),(int)params.getX(),(int)params.getX()+params.getWidth(),(int)params.getY()+params.getHeight());

        }


    }
    protected void setLoc(int x , int y,int w , int h){

    }


    // onDraw會持續渲染
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 畫矩形
        canvas.drawRect(rectF, paint);
    }

    /** 取距離公式 */
    private float getDistance(MotionEvent event) {
        // x點距離
        float x = event.getX(0) - event.getX(1);

        // y點距離
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }

    /**更新ivParams*/






    public class Point extends android.graphics.Point implements Serializable {
        private static final long serialVersionUID = 1L;

        public int x;
        public int y;

        Point() {

        }

        Point(Point point) {
            this.x = point.x;
            this.y = point.y;
        }

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    public class Paint extends android.graphics.Paint implements Serializable {
        private static final long serialVersionUID = 1L;

    }
    public class RectF extends android.graphics.RectF implements Serializable {
        private static final long serialVersionUID = 1L;
        public RectF(float left, float top, float right, float bottom) {
            super.left = left;
            super.top = top;
            super.right = right;
            super.bottom = bottom;
        }
    }
}
