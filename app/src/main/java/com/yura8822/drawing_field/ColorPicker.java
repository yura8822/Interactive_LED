package com.yura8822.drawing_field;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.yura8822.R;

public class ColorPicker extends View {

    private static final String TAG = "ColorPicker";

    public interface ListenerColorPicker{
        void colorSelected(int color);
    }

    private ListenerColorPicker listenerColorPicker;

    private final float COEFFICIENT_STROKE_WIDTH = 0.04f;

    private final float COEFFICIENT_RADIUS_OUTER_CIRCLE = 0.43f;
    private final float COEFFICIENT_RADIUS_INNER_CIRCLE = 0.28f;
    private final float COEFFICIENT_RADIUS_RESULT_CIRCLE = 0.13f;

    private final float COEFFICIENT_ASPECT_RATIO = 0.1f;

    private final int OUTER_CIRCLE = 1;
    private final int INNER_CIRCLE = 2;
    private final int RESULT_CIRCLE = 3;
    private final int BRIGHTNESS_RECT = 4;

    //side size view
    private int width;
    private int height;

    private int centerCircleX;
    private int centerCircleY;

    private float centerCircleOuterMarkerX;
    private float centerCircleOuterMarkerY;
    private float centerCircleInnerMarkerX;
    private float centerCircleInnerMarkerY;
    private float centerCircleRectMarkerX;
    private float centerCircleRectMarkerY;


    private float radiusOuterCircle;
    private float radiusInnerCircle;
    private float radiusResultCircle;

    private float radiusMarkerCircle;
    private float radiusMarkerCircleSecond;


    private RectF rectBrightness;
    private float cornersRadiusBrightnessRect;
    private float leftRect;
    private float topRect;
    private float rightRect;
    private float bottomRect;


    private float strokeWidth;


    private Paint paintOuterCircle;
    private Paint paintInnerCircle;
    private Paint paintResultCircle;
    private Paint paintBrightnessRect;
    private Paint paintMarkerColor;
    private Paint paintMarkerColorSecond;


    private Shader shaderCircle;
    private LinearGradient shaderRect;

    private int[] arrayColorsOuterCircle = new int[]{
            Color.parseColor("#ff0000"), Color.parseColor("#ff8800"),
            Color.parseColor("#ffff00"), Color.parseColor("#88ff00"),
            Color.parseColor("#00ff00"), Color.parseColor("#00ff88"),
            Color.parseColor("#00ffff"), Color.parseColor("#0088ff"),
            Color.parseColor("#0000ff"), Color.parseColor("#8800ff"),
            Color.parseColor("#ff00ff"), Color.parseColor("#ff0088"),
            Color.parseColor("#ff0000")};

    private float[] hsv;

    private float angle;
    private int resultColor;

    private Paint paintPathText;
    private Path pathText;

    public ColorPicker(Context context) {
        super(context);
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        width = size - (int) (size * COEFFICIENT_ASPECT_RATIO);
        height = size;

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

        calculateSize();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //outer circle
        canvas.drawCircle(centerCircleX, centerCircleY, radiusOuterCircle, paintOuterCircle);
        //inner circle
        canvas.drawCircle(centerCircleX, centerCircleY, radiusInnerCircle, paintInnerCircle);
        //result circle
        canvas.drawCircle(centerCircleX, centerCircleY, radiusResultCircle, paintResultCircle);
        //brightness rectangle
        canvas.drawRoundRect(rectBrightness, cornersRadiusBrightnessRect,
                cornersRadiusBrightnessRect, paintBrightnessRect);

        //outer marker circle
        canvas.drawCircle(centerCircleOuterMarkerX, centerCircleOuterMarkerY,
                radiusMarkerCircle, paintMarkerColor);
        canvas.drawCircle(centerCircleOuterMarkerX, centerCircleOuterMarkerY,
                radiusMarkerCircleSecond, paintMarkerColorSecond);

        //inner marker circle
        canvas.drawCircle(centerCircleInnerMarkerX, centerCircleInnerMarkerY,
                radiusMarkerCircle, paintMarkerColor);
        canvas.drawCircle(centerCircleInnerMarkerX, centerCircleInnerMarkerY,
                radiusMarkerCircleSecond, paintMarkerColorSecond);

        //rect marker circle
        canvas.drawCircle(centerCircleRectMarkerX, centerCircleRectMarkerY,
                radiusMarkerCircle, paintMarkerColor);
        canvas.drawCircle(centerCircleRectMarkerX, centerCircleRectMarkerY,
                radiusMarkerCircleSecond, paintMarkerColorSecond);

        //draw text
        //for result circle
        pathText.reset();
        pathText.addCircle(centerCircleX, centerCircleY, radiusResultCircle, Path.Direction.CW);
        canvas.drawTextOnPath(getResources().getText(R.string.select).toString(), pathText,
                radiusResultCircle * 4, strokeWidth / 1.1f - strokeWidth, paintPathText);

        //for inner circle
        pathText.reset();
        pathText.addCircle(centerCircleX, centerCircleY, radiusInnerCircle, Path.Direction.CW);
        canvas.drawTextOnPath(getResources().getText(R.string.saturation).toString(), pathText,
                radiusInnerCircle * 4,
                strokeWidth / 2.5f - strokeWidth, paintPathText);

        //for outer circle
        pathText.reset();
        pathText.addCircle(centerCircleX, centerCircleY, radiusOuterCircle, Path.Direction.CW);
        canvas.drawTextOnPath(getResources().getText(R.string.hue).toString(), pathText,
                radiusOuterCircle * 4,
                strokeWidth / 2.5f - strokeWidth, paintPathText);

        //for rectangle
        pathText.reset();
        pathText.moveTo(leftRect, topRect);
        pathText.lineTo(rightRect, topRect);
        canvas.drawTextOnPath(getResources().getText(R.string.value).toString(), pathText,
                radiusOuterCircle,
                strokeWidth / 1.25f - strokeWidth, paintPathText);



    }

    private void calculateSize(){
        Log.d(TAG, "calculateSize()");
        //primary angle when starting View;
        angle = 0;

        // calculate the coordinate of the center of the circles
        centerCircleX = width / 2;
        centerCircleY = centerCircleX;

        // calculate circle radius
        radiusOuterCircle = width * (COEFFICIENT_RADIUS_OUTER_CIRCLE - COEFFICIENT_STROKE_WIDTH);
        radiusInnerCircle = width * (COEFFICIENT_RADIUS_INNER_CIRCLE - COEFFICIENT_STROKE_WIDTH);
        radiusResultCircle = width * (COEFFICIENT_RADIUS_RESULT_CIRCLE);

        //calculate the size of the brush spot to draw a circle
        strokeWidth = width * COEFFICIENT_STROKE_WIDTH * 2;

        // Define the corners radius of rounded rectangle
        cornersRadiusBrightnessRect = strokeWidth;

        //calculate the size brightness rect
        leftRect = width - (radiusOuterCircle + strokeWidth / 2) * 2;
        topRect = height - strokeWidth;
        rightRect = width - (width - (radiusOuterCircle + strokeWidth / 2) * 2);
        bottomRect = height;

        //calculation of the central coordinates of the marker circle
        moveCircleMarker(OUTER_CIRCLE);
        moveCircleMarker(INNER_CIRCLE);
        moveRectMarker(width);

        //calculate the radius marker circle
        radiusMarkerCircle = strokeWidth/2;
        radiusMarkerCircleSecond = strokeWidth/2 - (strokeWidth*0.05f);
    }

    private void init(){
        Log.d(TAG, "init()");

        //primary color when starting View
        hsv = new float[] {0, 1f, 1f};
        resultColor = Color.HSVToColor(hsv);

        // initialize paint for outer circle
        paintOuterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuterCircle.setStyle(Paint.Style.STROKE);
        paintOuterCircle.setStrokeWidth(strokeWidth);
        shaderCircle = new SweepGradient(centerCircleX, centerCircleY, arrayColorsOuterCircle, null);
        paintOuterCircle.setShader(shaderCircle);

        //initialize paint for inner circle
        paintInnerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintInnerCircle.setStyle(Paint.Style.STROKE);
        paintInnerCircle.setStrokeWidth(strokeWidth);
        shaderCircle = new SweepGradient(centerCircleX, centerCircleY,
                new int[]{
                        Color.HSVToColor(new float[]{angle, 1f, 1f}),
                        Color.HSVToColor(new float[]{angle, 0f, 1f}),
                        Color.HSVToColor(new float[]{angle, 1f, 1f})
                }, null);
        paintInnerCircle.setShader(shaderCircle);

        //initialize paint for result circle
        paintResultCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintResultCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        paintResultCircle.setColor(Color.HSVToColor(hsv));

        //initialize paint for brightness rectangle
        paintBrightnessRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBrightnessRect.setStyle(Paint.Style.FILL_AND_STROKE);
        shaderRect = new LinearGradient(leftRect, topRect, rightRect, bottomRect,
                new int[]{
                        Color.HSVToColor(new float[]{angle, 1f, 0f}),
                        Color.HSVToColor(new float[]{angle, 1f, 1f})
                }, null, Shader.TileMode.CLAMP);
        paintBrightnessRect.setShader(shaderRect);
        rectBrightness = new RectF(leftRect, topRect, rightRect, bottomRect);

        //initialize marker for selected color
        paintMarkerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMarkerColor.setStyle(Paint.Style.STROKE);
        paintMarkerColor.setStrokeWidth(strokeWidth * 0.05f);
        paintMarkerColor.setColor(Color.BLACK);

        paintMarkerColorSecond = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMarkerColorSecond.setStyle(Paint.Style.STROKE);
        paintMarkerColorSecond.setStrokeWidth(strokeWidth * 0.05f);
        paintMarkerColorSecond.setColor(Color.WHITE);

        //initialize objects for drawing text
        paintPathText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPathText.setColor(getResources().getColor(R.color.colorPrimaryDark));
        paintPathText.setTextSize(strokeWidth/2);
        pathText = new Path();
    }


    private int selectedCircle;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x =  event.getX();
        float y =  event.getY();

        //convert coordinates from the calculation that the center of the circle is zero
        float xBias = x - centerCircleX;
        float yBias = y - centerCircleY;

        angle = getAngle(xBias, yBias);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                selectedCircle =  determineSelectedFigure(event.getX(), event.getY());
                if (selectedCircle == RESULT_CIRCLE){
                    if (listenerColorPicker != null){
                        listenerColorPicker.colorSelected(resultColor);
                        Log.d(TAG, "listenerColorPicker.colorSelected(resultColor)");
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                switch (selectedCircle){
                    case OUTER_CIRCLE:
                        getColorOuterCircle(angle);
                        moveCircleMarker(OUTER_CIRCLE);
                        break;

                    case INNER_CIRCLE:
                        getColorInnerCircle(angle);
                        moveCircleMarker(INNER_CIRCLE);
                        break;

                    case BRIGHTNESS_RECT:
                        getColorBrightnessRect(x);
                        moveRectMarker(x);
                        break;
                }

            case MotionEvent.ACTION_UP:
        }

        invalidate();
        return true;
    }

    //check which shape contains touch
    private int determineSelectedFigure(float x, float y){
        double distanceFromCenterToPoint = Math.sqrt(Math.pow(centerCircleX - x, 2)
                + Math.pow(centerCircleY - y ,2));

        if (distanceFromCenterToPoint <= radiusOuterCircle + strokeWidth/2
                && distanceFromCenterToPoint >= radiusOuterCircle - strokeWidth/2){
            Log.d(TAG, "selected outer circle");
            return OUTER_CIRCLE;
        }else
        if (distanceFromCenterToPoint <= radiusInnerCircle + strokeWidth/2
                && distanceFromCenterToPoint >= radiusInnerCircle - strokeWidth/2){
            Log.d(TAG, "selected inner circle");
            return INNER_CIRCLE;
        }else
        if(distanceFromCenterToPoint <= radiusResultCircle){
            Log.d(TAG, "selected result circle");
            return RESULT_CIRCLE;
        }else
        if (rectBrightness.contains(x, y)){
            Log.d(TAG, "selected brightness rectangle");
            return BRIGHTNESS_RECT;
        }
        return 0;
    }

    private void getColorOuterCircle(float angle){
        hsv[0] = angle;

        resultColor = Color.HSVToColor(hsv);

        shaderCircle = new SweepGradient(centerCircleX, centerCircleY,
                new int[]{
                        Color.HSVToColor(new float[]{angle, 1f, 1f}),
                        Color.HSVToColor(new float[]{angle, 0f, 1f}),
                        Color.HSVToColor(new float[]{angle, 1f, 1f})
                }, null);

        //redraw the inner circle depending on the color choice of the outer circle
        paintInnerCircle.setShader(shaderCircle);

        //redraw the brightness rectangle depending on the color choice of the outer circle
        shaderRect = new LinearGradient(leftRect, topRect, rightRect, bottomRect,
                new int[]{
                        Color.HSVToColor(new float[]{hsv[0], hsv[1], 0f}),
                        Color.HSVToColor(new float[]{hsv[0], hsv[1], 1f})
                }, null, Shader.TileMode.CLAMP);
        paintBrightnessRect.setShader(shaderRect);

        paintResultCircle.setColor(resultColor);
    }

    private void getColorInnerCircle(float angle){
        if (angle < 180) hsv[1] = 1 - angle * 1 / 180;
        else hsv[1] = angle * 1 / 180 - 1;
        resultColor = Color.HSVToColor(hsv);

        //redraw the brightness rectangle depending on the color choice of the inner circle
        shaderRect = new LinearGradient(leftRect, topRect, rightRect, bottomRect,
                new int[]{
                        Color.HSVToColor(new float[]{hsv[0], hsv[1], 0f}),
                        Color.HSVToColor(new float[]{hsv[0], hsv[1], 1f})
                }, null, Shader.TileMode.CLAMP);
        paintBrightnessRect.setShader(shaderRect);

        paintResultCircle.setColor(resultColor);
    }

    private void getColorBrightnessRect(float x){
        float brightness;
        float xBias = x - width * 0.1f;
        brightness = xBias / (width - (width * 0.2f));

        hsv[2] = brightness;
        resultColor = Color.HSVToColor(hsv);

        paintResultCircle.setColor(resultColor);
    }

    private float getAngle(float x, float y) {
        float angle = 0;
        if (x != 0) angle = y / x;
        angle = (float) Math.toDegrees(Math.atan(angle));
        if (x < 0) angle += 180;
        else if (x > 0 && y < 0) angle += 360;
        return angle;
    }

    private void moveCircleMarker(int selectedCircle){
        if (selectedCircle == OUTER_CIRCLE){
            centerCircleOuterMarkerX =
                    centerCircleX + (float)(radiusOuterCircle * Math.cos(Math.toRadians(angle)));
            centerCircleOuterMarkerY =
                    centerCircleY + (float)(radiusOuterCircle * Math.sin(Math.toRadians(angle)));
        }else if (selectedCircle == INNER_CIRCLE){
            centerCircleInnerMarkerX =
                    centerCircleX + (float)(radiusInnerCircle * Math.cos(Math.toRadians(angle)));
            centerCircleInnerMarkerY =
                    centerCircleY + (float)(radiusInnerCircle * Math.sin(Math.toRadians(angle)));
        }

    }

    private void moveRectMarker(float x){
        if (x < leftRect + strokeWidth / 2){
            centerCircleRectMarkerX = leftRect + strokeWidth/2;
            centerCircleRectMarkerY = height - strokeWidth/2;
        }else if (x > rightRect - strokeWidth / 2) {
            centerCircleRectMarkerX = rightRect - strokeWidth/2;
            centerCircleRectMarkerY = height - strokeWidth/2;
        }else {
            centerCircleRectMarkerX = x;
            centerCircleRectMarkerY = height - strokeWidth/2;
        }
    }

    public void setListenerColorPicker(ListenerColorPicker listenerColorPicker) {
        this.listenerColorPicker = listenerColorPicker;
    }

    public int getResultColor() {
        return resultColor;
    }
}

