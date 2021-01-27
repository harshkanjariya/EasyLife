package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harsh.hkutils.ExpandableLayout;
import com.harsh.hkutils.HttpProcess;
import com.harsh.hkutils.list.HKList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ExperimentActivity extends AppCompatActivity {
	private static final String TAG = "ExperimentActivity";
	ArrayList<Map<String, String>> spinnerlist = new ArrayList<>();

	String[] randomSuggestions = {"a", "aa", "ab", "aab", "abc", "abcd", "abcde", "abcdef"};
	String[] tags = {"Java", "JavaScript", "Spring", "Java EE", "Java 8", "Java 9", "Java 10", "SQL", "SQLite"};
	ArrayList<String> mOriginalValues = new ArrayList<>();

	ArrayList<String> permissionsList = new ArrayList<>();
	int expanded = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment);
		mOriginalValues.add("abc");
		mOriginalValues.add("xyz");
		mOriginalValues.add("xbz");

		EventCalendarView calendarView=findViewById(R.id.calendar);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String current = dateFormat.format(System.currentTimeMillis());
		calendarView.setCallback(new EventCalendarView.Callback() {
			@Override
			public void onMonthSelect(int month) {
				calendarView.ShowYearSelector();
			}

			@Override
			public void onYearSelect(int year) {
				calendarView.ShowDateSelector();

			}
		});

		findViewById(R.id.root_calender_layout).setOnClickListener(view ->calendarView.ShowDateSelector());

		ColorStateList colorStateList=new ColorStateList(new int[][]{
				new int[]{android.R.attr.state_selected},
				new int[]{-android.R.attr.state_selected}
		},new int[]{
				Color.RED,
				Color.WHITE
		});
		calendarView.setInflater((calendar, layout, type) -> {
			View v=layout.findViewById(R.id.dot);
			if (type==EventCalendarView.DAY) {
				v.setBackgroundTintList(colorStateList);
			}else{
				v.setVisibility(View.GONE);
			}
		});

		HttpProcess process = new HttpProcess("http://harsh.netwebdevelopers.com/add_tutorial.php");
//		HttpProcess process=new HttpProcess("http://192.168.43.228/check.php");

		Map<String, String> map = new HashMap<>();
		map.put("test", "vvaall");
//		Map<String,String>head=new HashMap<>();
//		head.put("myhead","myval");

		process.post(new HttpProcess.Callback() {
			@Override
			public void onError(IOException exception) {
				Log.e(TAG, "onError: " + exception.getMessage());
			}

			@Override
			public void onResponse(JSONObject json) throws JSONException {
				Log.e(TAG, "onResponse: " + json);
			}
		}, map);
	}
	public void toggle(View view) {

	}
}