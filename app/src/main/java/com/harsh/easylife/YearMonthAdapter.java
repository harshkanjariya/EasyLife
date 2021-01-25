package com.harsh.easylife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;

public class YearMonthAdapter extends BaseAdapter {

    LayoutInflater inflater;
    String[] Months=new String[]{"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};
    String SelectedMonth = null;
    int Currentyear = Calendar.YEAR;

    public YearMonthAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return 12;
    }
    @Override
    public Object getItem(int position) { return position; }

    @Override
    public long getItemId(int position) { return position; }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.month_layout,parent,false);
        TextView txt = convertView.findViewById(R.id.txt);
         txt.setText(Months[position]);
         txt.setOnClickListener(view ->{ SelectedMonth = Months[position];
         EventCalendarView calendarView;});


        return convertView;
    }
}
