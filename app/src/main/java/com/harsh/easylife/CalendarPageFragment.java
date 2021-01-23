package com.harsh.easylife;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CalendarPageFragment extends Fragment {

	private static final String TAG = "CalendarPageFragment";
	Calendar calendar,selected;
	DayAdapter adapter;
	EventCalendarView.DayInflater dayInflater;
	int day_layout;
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

	public CalendarPageFragment(long millis, EventCalendarView.DayInflater inflater,int day_layout,Calendar selected) {
		this.calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		this.dayInflater = inflater;
		this.day_layout = day_layout;
		this.selected=selected;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar_page, container, false);

		GridView gridView = view.findViewById(R.id.calendar_grid);

		adapter=new DayAdapter(Objects.requireNonNull(getContext()));
		gridView.setAdapter(adapter);

		return view;
	}
	class DayAdapter extends BaseAdapter {

		LayoutInflater inflater;
		int firstDayOfWeek, maxDay;
		String[] weeks=new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

		public DayAdapter(Context context) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
		}
		@Override
		public int getCount() {
			return 42;
		}
		@Override
		public Object getItem(int position) { return position; }

		@Override
		public long getItemId(int position) { return position; }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(day_layout,parent,false);

			TextView txt = convertView.findViewById(R.id.number);

			int day = position - 6 - firstDayOfWeek;
			if (29+position-firstDayOfWeek <= maxDay)
				day = 29+position-firstDayOfWeek;
			calendar.set(Calendar.DAY_OF_MONTH,day);
			if (dayInflater!=null){
				if (position < 7) {
					dayInflater.inflate(null, convertView, EventCalendarView.WEEK);
					txt.setText(weeks[position]);
				}else if (day>0 && day<=maxDay){
					dayInflater.inflate(calendar,convertView,EventCalendarView.DAY);
					txt.setText(""+(day));
					if (dateFormat.format(selected.getTime()).equals(dateFormat.format(calendar.getTime())))
						convertView.findViewById(R.id.dot).setSelected(true);

					int finalDay = day;
					convertView.setOnClickListener(v -> {
						calendar.set(Calendar.DAY_OF_MONTH, finalDay);
						selected.setTimeInMillis(calendar.getTimeInMillis());
						notifyDataSetChanged();
					});
				}else{
					dayInflater.inflate(null,convertView,EventCalendarView.BLANK);
					txt.setText("");
				}
			}
			return convertView;
		}
	}
}