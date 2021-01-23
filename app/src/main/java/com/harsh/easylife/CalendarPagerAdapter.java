package com.harsh.easylife;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.Calendar;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = "CalendarPager";
	public int virtualPosition;
	public Calendar []calendar;
	public Calendar selected;
	private EventCalendarView.DayInflater inflater;
	int day_layout;

	public CalendarPagerAdapter(@NonNull FragmentManager fm,int day_layout) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.day_layout=day_layout;
		virtualPosition=0;
		selected = Calendar.getInstance();
		calendar=new Calendar[]{
				Calendar.getInstance(),
				Calendar.getInstance(),
				Calendar.getInstance()
		};
		calendar[0].add(Calendar.MONTH,-1);
		calendar[2].add(Calendar.MONTH,1);
	}
	public void nextMonth(){
		for (Calendar c:calendar)
			c.add(Calendar.MONTH,1);
	}
	public void previousMonth() {
		for (Calendar c:calendar)
			c.add(Calendar.MONTH,-1);
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		return new CalendarPageFragment(calendar[position].getTimeInMillis(),inflater,day_layout,selected);
	}

	@Override
	public int getItemPosition(@NonNull Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return 3;
	}

	public void setInflater(EventCalendarView.DayInflater inflater) {
		this.inflater=inflater;
		notifyDataSetChanged();
	}
}
