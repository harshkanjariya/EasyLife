package com.harsh.easylife;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CirclularProgressButton extends View implements View.OnTouchListener {
	int progressStroke = 50;
	int progress = 180;
	Callback callback;

	public CirclularProgressButton(Context context) {
		super(context);
		commonConstruct(context);
	}
	public CirclularProgressButton(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		commonConstruct(context);
	}
	public void setCallback(Callback callback){
		this.callback=callback;
	}
	Paint paint;
	BlurMaskFilter blurMaskFilter;
	private void commonConstruct(Context context){
		paint = new Paint();
		blurMaskFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);

		setRotation(-90);
		setOnTouchListener(this);
	}
	ValueAnimator animator;
	private void animateProgress(){
		animator = ValueAnimator.ofFloat(0,360);
		animator.addUpdateListener(animation -> {
			float value = (float) animation.getAnimatedValue();
			progress = (int) value;
			setRotationY(progress/2f);
			invalidate();
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) { }
			@Override
			public void onAnimationEnd(Animator animation) {
				if (callback!=null && progress==360)
					callback.onComplete();
			}
			@Override
			public void onAnimationCancel(Animator animation) {
				ValueAnimator valueAnimator = ValueAnimator.ofFloat(progress/2f,0);
				valueAnimator.addUpdateListener(animation1 -> setRotationY((Float) animation1.getAnimatedValue()));
				valueAnimator.start();
				progress = 0;
				invalidate();
				if (callback!=null)
					callback.onCancel();
			}
			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		animator.setDuration(2000);
		animator.start();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				animateProgress();
				break;
			case MotionEvent.ACTION_UP:
				animator.cancel();
				break;
		}
		return true;
	}
	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(progressStroke);

		paint.setMaskFilter(blurMaskFilter);
		paint.setColor(Color.BLACK);
		int offsetX = -5;
		int offsetY = 5;
		canvas.drawCircle(width/2.0f+offsetX,height/2.0f+offsetY,width/2.0f-progressStroke,paint);

		paint.setMaskFilter(null);
		paint.setColor(Color.rgb(200,200,200));
		canvas.drawArc(progressStroke,progressStroke,width-progressStroke,height-progressStroke,progress,360,false,paint);

		paint.setColor(Color.RED);
		canvas.drawArc(progressStroke,progressStroke,width-progressStroke,height-progressStroke,0,progress,false,paint);

		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(width/2.0f,height/2.0f,width/2.0f-progressStroke*2,paint);

		paint.setColor(Color.RED);
		canvas.drawCircle(width/2.0f,height/2.0f,width/2.0f-progressStroke*3,paint);

		super.onDraw(canvas);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	interface Callback{
		void onComplete();
		void onCancel();
	}
}
