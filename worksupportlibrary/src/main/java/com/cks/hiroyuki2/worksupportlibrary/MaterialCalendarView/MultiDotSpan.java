/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.MaterialCalendarView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.util.Log;

import java.util.List;

/**
 * 複数のdotSpanを描画するおじさん！
 */

class MultiDotSpan implements LineBackgroundSpan{

    private static final String TAG = "MANUAL_TAG: " + MultiDotSpan.class.getSimpleName();
    private float radius;
    private List<Integer> colorList;
    private final static int DEFAULT_RADIUS = 6;
    private Context context;

    MultiDotSpan(Context context, List<Integer> colorList) {
        this(context, colorList, DEFAULT_RADIUS);
    }

    MultiDotSpan(Context context, List<Integer> colorList, float radius){
        Log.d(TAG, "MultiDotSpan: fire");
        this.colorList = colorList;
        this.radius = radius;
        this.context = context;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNum) {
        Log.d(TAG, "drawBackground: fire");
        int oldColor = paint.getColor();
        final float center_x = (left + right) / 2;
        final float center_y = bottom + radius;
        final float shift_x = radius * 2;//つまり、ひとつの●と両側のスペースを合わせて1単位とすると、| ● |の横幅はradius*4になっている
//        boolean isMax = false;

        int maxNum = (int)getDotMax(canvas);
        if (colorList.size() >= maxNum){
            colorList.subList(maxNum, colorList.size()).clear();
//            isMax = true;
        }

        float totalWidth = shift_x * (colorList.size()*2 -1);
        float firstCircleCenter = center_x - totalWidth/2 + radius;//左端の●のcenter_xを表す
        for (int i = 0; i < colorList.size(); i++) {
//            if (isMax && i==colorList.size()-1){
//                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_add_white_24dp).mutate();
//                int color = ContextCompat.getColor(context, R.color.colorPrimary);
//                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                canvas.drawBitmap(bitmap, firstCircleCenter + shift_x*i*2-radius, center_y+radius, paint);
//            } else {
                paint.setColor(colorList.get(i));
                canvas.drawCircle(firstCircleCenter + shift_x*i*2, center_y, radius, paint);
//            }
        }

        paint.setColor(oldColor);
    }

    float getDotMax(Canvas canvas){
        return canvas.getWidth() / (radius * 4);
    }

    boolean canListDraw(Canvas canvas){
        int width = canvas.getWidth();
        return !(width <= radius*4 ||  width/radius*4 > colorList.size());
    }
}
