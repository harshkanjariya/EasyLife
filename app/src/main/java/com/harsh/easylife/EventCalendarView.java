package com.harsh.easylife;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventCalendarView extends LinearLayout{

	private static final String TAG = "EventCalendarView";
	private ViewPager viewPager;
	CalendarPagerAdapter adapter;
	private TextView monthTitle;

	public static int DAY=0;
	public static int BLANK=-1;
	public static int WEEK=1;

	private int day_resource;

	public EventCalendarView(Context context) {
		super(context);
		day_resource = R.layout.item_calendar_day;
		commonInit(context);
	}
	private void commonInit(Context context){
		inflate(context,R.layout.layout_event_calendar_view,this);

		AppCompatActivity activity= (AppCompatActivity) context;
		adapter = new CalendarPagerAdapter(activity.getSupportFragmentManager(),day_resource);

		monthTitle = findViewById(R.id.month_title);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
		monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));

		ImageButton nextButton = findViewById(R.id.next_month_button);
		ImageButton previousButton = findViewById(R.id.previous_month_button);

		viewPager=findViewById(R.id.calendar_pager);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(1);

		nextButton.setOnClickListener(v -> viewPager.setCurrentItem(2));
		previousButton.setOnClickListener(v -> viewPager.setCurrentItem(0));

		ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if ( ((viewPager.getCurrentItem()==2 && 2==position) ||
						(viewPager.getCurrentItem()==0 && 0==position)) &&
						positionOffset==0) {
					Log.e(TAG+" "+position, "onPageScrolled: current="+viewPager.getCurrentItem()+" "+positionOffset);
					if (viewPager.getCurrentItem()==2){
						adapter.nextMonth();
					}else{
						adapter.previousMonth();
					}
					viewPager.setCurrentItem(1,false);
					adapter.notifyDataSetChanged();
				}else if (position == 1 && positionOffset == 0){
					monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
				}else if (position == 1 && positionOffset > 0.5){
					monthTitle.setText(dateFormat.format(adapter.calendar[2].getTime()));
				}else if (position == 0 && positionOffset < 0.5){
					monthTitle.setText(dateFormat.format(adapter.calendar[0].getTime()));
				}else if (position!=2){
					monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
				}
			}
			@Override
			public void onPageSelected(int position) { }
			@Override
			public void onPageScrollStateChanged(int state) { }
		};
		viewPager.addOnPageChangeListener(pageChangeListener);
	}
	public EventCalendarView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.EventCalendarView);

		day_resource=typedArray.getResourceId(R.styleable.EventCalendarView_dayLayout, R.layout.item_calendar_day);

		typedArray.recycle();
		commonInit(context);
	}
	public void setDayResource(int day_resource){
		this.day_resource=day_resource;
		adapter.notifyDataSetChanged();
	}
	public void setInflater(DayInflater inflater){
		adapter.setInflater(inflater);
	}
	public interface DayInflater{
		void inflate(Calendar calendar, View layout,int type);
	}
}
