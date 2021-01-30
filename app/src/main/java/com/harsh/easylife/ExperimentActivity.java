package com.harsh.easylife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.harsh.hkutils.calendar.EventCalendarView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ExperimentActivity extends AppCompatActivity {
	private static final String TAG = "ExperimentActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment);

		EventCalendarView calendarView=findViewById(R.id.calendar);
//		calendarView.setCallback(new EventCalendarView.Callback() {
//			@Override
//			public void onMonthSelect(int month) {
//				calendarView.showYearSelector();
//			}
//			@Override
//			public void onYearSelect(int year) {
//				calendarView.showDateSelector();
//			}
//		});
		findViewById(R.id.root_calender_layout).setOnClickListener(view ->calendarView.showDateSelector());

		ColorStateList colorStateList=new ColorStateList(new int[][]{
				new int[]{android.R.attr.state_selected},
				new int[]{-android.R.attr.state_selected},
				new int[]{R.attr.state_present},
				new int[]{R.attr.state_absent},
				new int[]{R.attr.state_short_day},
				new int[]{R.attr.state_half_day},
				new int[]{R.attr.state_dynamic1_day},
				new int[]{R.attr.state_dynamic2_day}
		},new int[]{
				ContextCompat.getColor(this,R.color.colorPrimary),
				Color.WHITE,
				ContextCompat.getColor(this,R.color.success),
				ContextCompat.getColor(this,R.color.error),
				ContextCompat.getColor(this,R.color.short_day),
				ContextCompat.getColor(this,R.color.half_day),
				ContextCompat.getColor(this,R.color.dynamic1_day),
				ContextCompat.getColor(this,R.color.dynamic2_day)
		});
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);

		Calendar minCalendar = Calendar.getInstance();
		minCalendar.set(Calendar.DAY_OF_MONTH,1);
		try {
			minCalendar.setTime(Objects.requireNonNull(dateFormat.parse(dateFormat.format(minCalendar.getTime()))));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar maxCalendar = Calendar.getInstance();
		maxCalendar.set(Calendar.DAY_OF_MONTH,5);
		maxCalendar.add(Calendar.MONTH,1);
		try {
			maxCalendar.setTime(Objects.requireNonNull(dateFormat.parse(dateFormat.format(maxCalendar.getTime()))));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ArrayList<String>presentDates = new ArrayList<>();
		presentDates.add("01/01/2021");
		presentDates.add("05/01/2021");
		ArrayList<String>absentDates = new ArrayList<>();
		absentDates.add("14/01/2021");

		calendarView.setInflater((calendar, layout, type) -> {
			ImageView v=layout.findViewById(R.id.dot);
			if (type==EventCalendarView.DAY) {
				v.setBackgroundTintList(colorStateList);
				if (calendar.getTimeInMillis() < minCalendar.getTimeInMillis() ||
						calendar.getTimeInMillis() > maxCalendar.getTimeInMillis()){
					layout.setEnabled(false);
					layout.setAlpha(.4f);
				}
				boolean found = false;
				if (presentDates.contains(dateFormat.format(calendar.getTime()))) {
					v.setColorFilter(ContextCompat.getColor(this,R.color.success));
					found = true;
				}
				if (absentDates.contains(dateFormat.format(calendar.getTime()))) {
					v.setColorFilter(ContextCompat.getColor(this,R.color.error));
					found = true;
				}
				if (!found)
					v.setVisibility(View.GONE);
			}else{
				v.setVisibility(View.GONE);
			}
		});
	}
	public void toggle(View view) { }
}