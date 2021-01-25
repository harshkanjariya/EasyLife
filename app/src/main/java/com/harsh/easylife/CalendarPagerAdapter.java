package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.Calendar;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = "CalendarPager";
	public Calendar []calendar;
	Shared shared;

	public CalendarPagerAdapter(@NonNull FragmentManager fm, Shared shared) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		calendar=new Calendar[]{
				Calendar.getInstance(),
				Calendar.getInstance(),
				Calendar.getInstance()
		};
		calendar[0].add(Calendar.MONTH,-1);
		calendar[2].add(Calendar.MONTH,1);
		this.shared = shared;
	}
	public void nextMonth(){
		for (Calendar c:calendar)
			c.add(Calendar.MONTH,1);
	}

	public void previousMonth() {
		for (Calendar c:calendar)
			c.add(Calendar.MONTH,-1);
	}
	public void update()
	{
		calendar[0].setTimeInMillis(shared.selected.getTimeInMillis());
		calendar[1].setTimeInMillis(shared.selected.getTimeInMillis());
		calendar[2].setTimeInMillis(shared.selected.getTimeInMillis());
		calendar[0].add(Calendar.MONTH,-1);
		calendar[2].add(Calendar.MONTH,1);

		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public CalendarPageFragment getItem(int position) {
		return new CalendarPageFragment(calendar[position].getTimeInMillis(),shared);
	}

	@Override
	public int getItemPosition(@NonNull Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return 3;
	}


}
