package com.harsh.hkutils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView{
	float lastX1,lastX2,lastY1,lastY2;
	double distance=0;
	float scale=1;

	public ZoomableImageView(@NonNull Context context) {
		super(context);
	}
	public ZoomableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}
	public ZoomableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	@Override
	public boolean performClick() {
		return super.performClick();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointers=event.getPointerCount();
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				lastX1=event.getX(0);
				lastY1=event.getY(0);
//				Log.e("first x down",""+event.getPointerCount());
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
//				Log.e("pointer pointer down",""+event.getPointerCount());
				break;
			case MotionEvent.ACTION_POINTER_2_DOWN:
				lastX2=event.getX(1);
				lastY2=event.getY(1);
//				Log.e("second x down",""+event.getPointerCount());
				break;

			case MotionEvent.ACTION_UP:
				lastX1=0;
				lastY1=0;
//				Log.e("first x up",""+event.getPointerCount());
				break;
			case MotionEvent.ACTION_POINTER_UP:
//				Log.e("pointer pointer up",""+event.getPointerCount());
				if (event.getPointerCount()==2){
					distance=0;
				}
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				lastX2=0;
				lastY2=0;
//				Log.e("second x up",""+event.getPointerCount());
				if (event.getPointerCount()==2){
					distance=0;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount()==2){
					int currentX1= (int) event.getX(0);
					int currentY1= (int) event.getY(0);
					int currentX2= (int) event.getX(1);
					int currentY2= (int) event.getY(1);

					Point middle=middlePoint(currentX1,currentY1,currentX2,currentY2);
					if (distance!=0){
						double currentDistance=Math.sqrt((currentX1-currentX2)*(currentX1-currentX2)+(currentY1-currentY2)*(currentY2));
						if (currentDistance-distance>0) {
//							Log.e("zoom in", "" + Math.sqrt(currentDistance - distance));
							scale= (float) Math.min(scale+0.05,4);
						}else{
//							Log.e("zoom out",""+Math.sqrt(Math.abs(currentDistance-distance)));
							scale= (float) Math.max(scale-0.05,0.4);
						}
//						this.setScaleX(scale);
//						this.setScaleY(scale);
//						float scale= (float) Math.sqrt(currentDistance-distance);
//						if (scale>0)
//							scale=(scale+1);
//						else scale=(10+scale)/10;
//						this.setScaleX(scale);
//						this.setScaleX(scale);
					}
					distance=Math.sqrt((currentX1-currentX2)*(currentX1-currentX2)+(currentY1-currentY2)*(currentY2));

//					if(lastX1!=0 && lastY1!=0 && lastX2!=0 && lastY2!=0){
//						Log.e("difX1",""+(currentX1-lastX1));
//						Log.e("difY1",""+(currentY1-lastY1));
//						Log.e("difX2",""+(currentX2-lastX2));
//						Log.e("difY2",""+(currentY2-lastY2));
//					}

					lastX1=currentX1;
					lastY1=currentY1;
					lastX2=currentX2;
					lastY2=currentY2;
				}
//				Log.e("pointer move",""+event.getPointerCount());
				break;
		}
		return true;
	}
	private Point middlePoint(int ax,int ay,int bx, int by){
		return new Point((ax+bx)/2,(ay+by)/2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.
		super.onDraw(canvas);
	}
}