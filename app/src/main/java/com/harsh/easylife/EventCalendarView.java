package com.harsh.easylife;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.harsh.hkutils.list.HKList;
import com.harsh.hkutils.list.HKListHelper;
import com.harsh.hkutils.list.HKViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class EventCalendarView extends LinearLayout{

	private static final String TAG = "EventCalendarView";
	private ViewPager viewPager;
	CalendarPagerAdapter adapter;
	private TextView monthTitle;

	Shared shared;

	public boolean isMonthView = false;
	public boolean isYearView = false;


	public int getSelectedTextcolor() {
		return selectedTextcolor;
	}

	public void setSelectedTextcolor(int selectedTextcolor) {
		this.selectedTextcolor = selectedTextcolor;
	}

	private int selectedTextcolor ;


	public static int DAY=0;
	public static int BLANK=-1;
	public static int WEEK=1;
	HKList monthList;

	public EventCalendarView(Context context) {
		super(context);
		shared = new Shared();
		shared.day_layout = R.layout.item_calendar_day;
		commonInit(context);
	}
	public EventCalendarView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		shared = new Shared();
		TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.EventCalendarView);
		shared.day_layout=typedArray.getResourceId(R.styleable.EventCalendarView_dayLayout, R.layout.item_calendar_day);
		typedArray.recycle();
		commonInit(context);
	}
	private void commonInit(Context context){
		inflate(context,R.layout.layout_event_calendar_view,this);
		selectedTextcolor = ContextCompat.getColor(context,R.color.colorPrimary);


		AppCompatActivity activity= (AppCompatActivity) context;
		adapter = new CalendarPagerAdapter(activity.getSupportFragmentManager(),shared);

		monthTitle = findViewById(R.id.month_title);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
		monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
		monthList = findViewById(R.id.month_list);

		ImageButton nextButton = findViewById(R.id.next_month_button);
		ImageButton previousButton = findViewById(R.id.previous_month_button);

		viewPager=findViewById(R.id.calendar_pager);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(1);


		findViewById(R.id.month_year_title).setOnClickListener(view -> {
			nextButton.setVisibility(GONE);
			previousButton.setVisibility(GONE);
			MonthYearSelector();
		});

		nextButton.setOnClickListener(v -> viewPager.setCurrentItem(2));
		previousButton.setOnClickListener(v -> viewPager.setCurrentItem(0));

		ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (position == 1 && positionOffset == 0){
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
			public void onPageScrollStateChanged(int state) {
				if (state==ViewPager.SCROLL_STATE_IDLE && viewPager.getCurrentItem()!=1) {
					if (viewPager.getCurrentItem() == 2) {
						adapter.nextMonth();
					} else {
						adapter.previousMonth();
					}
					viewPager.setCurrentItem(1, false);
					adapter.notifyDataSetChanged();
				}
			}
		};
		viewPager.addOnPageChangeListener(pageChangeListener);
	}

	private void MonthSelector() {
		monthList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,shared.height));
		monthTitle.setText(""+shared.selected.get(Calendar.YEAR));
		ArrayList<String> Months=new ArrayList<String>(Arrays.asList("Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"));
		monthList.layoutManager(new GridLayoutManager(getContext(),4)).init(R.layout.month_layout, Months, new HKListHelper<String>() {
			@Override
			public void bind(HKViewHolder holder, String object, int position) {

				TextView tv = holder.textView(R.id.txt);
				tv.setText(""+object);
				if(shared.selected.get(Calendar.MONTH)==position) {
					GridViewSquareItem gqi = (GridViewSquareItem) holder.view(R.id.month_layout_particular);
					tv.setBackgroundResource(R.drawable.selectedbackground);
					tv.setTextColor(selectedTextcolor);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
				}
				else
				{
					tv.setTextColor(Color.BLACK);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
				}

				holder.click(R.id.month_layout_particular, view -> {
					if (shared.callback!=null)
						shared.callback.onMonthSelect(position);
					else
						ShowDateSelector();
					shared.selected.set(Calendar.MONTH,position);
					adapter.update();
				});
			}
		});

	}

	public void MonthYearSelector() {
		if(!isMonthView)
			ShowMonthSelector();
		else if(!isYearView)
			ShowYearSelector();
		else
			ShowDateSelector();
	}
	public void ShowMonthSelector() {
		MonthSelector();
		isMonthView = true;
		viewPager.setVisibility(GONE);
		findViewById(R.id.year_list).setVisibility(GONE);
		monthList.setVisibility(VISIBLE);
	}
	public void ShowYearSelector() {
		isMonthView = false;
		YearSelector();
		isYearView = true;
		viewPager.setVisibility(GONE);
		findViewById(R.id.year_list).setVisibility(VISIBLE);
		monthList.setVisibility(GONE);
	}
	public void ShowDateSelector() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
		monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
			isMonthView = false;
			isYearView = false;
			findViewById(R.id.next_month_button).setVisibility(VISIBLE);
			findViewById(R.id.previous_month_button).setVisibility(VISIBLE);
			viewPager.setVisibility(VISIBLE);
			findViewById(R.id.year_list).setVisibility(GONE);
			monthList.setVisibility(GONE);
	}


	public void YearSelector()
	{
		HKList listView = findViewById(R.id.year_list);
		listView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,shared.height));
		monthList.setVisibility(GONE);
		listView.setVisibility(VISIBLE);
		ArrayList<Integer> list = new ArrayList<>();
		for(int i = 1970;i<2100;i++) list.add(i);
		listView.init(R.layout.listitem_layout, list, new HKListHelper<Integer>() {
					@Override
					public void bind(HKViewHolder holder, Integer object, int position) {

						TextView tv = holder.textView(R.id.year_txt);
						tv.setText(""+object);
						if(object+1 == shared.selected.get(Calendar.YEAR))
						{
							tv.setBackground(null);
						}
						if(shared.selected.get(Calendar.YEAR)==object) {
							tv.setBackgroundResource(R.drawable.selectedbackground);
							tv.setTextColor(selectedTextcolor);
							tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
						}else{
							tv.setTextColor(Color.BLACK);
							tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
						}
						holder.click(R.id.year_layout, view -> {

							shared.selected.set(Calendar.YEAR,object);
							adapter.update();
						listView.update();
						if(shared.callback!=null)
							shared.callback.onYearSelect(object);
						else
							MonthYearSelector();
						});
					}
				});

		listView.recyclerView.getLayoutManager().scrollToPosition(shared.selected.get(Calendar.YEAR)-1973);
	}

	public void setDayResource(int day_resource){
		this.shared.day_layout=day_resource;
		adapter.notifyDataSetChanged();
	}

	public void setInflater(DayInflater inflater){
		shared.inflater = inflater;
		adapter.notifyDataSetChanged();
	}
	public void setCallback(Callback callback){
		shared.callback = callback;
		adapter.notifyDataSetChanged();
	}


	public interface Callback{
		void onMonthSelect(int month);
		void onYearSelect(int year);
	}
	public interface DayInflater{
		void inflate(Calendar calendar, View layout,int type);
	}
}
