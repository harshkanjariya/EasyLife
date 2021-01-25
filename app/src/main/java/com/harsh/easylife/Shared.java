package com.harsh.easylife;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Shared {
  public EventCalendarView.DayInflater inflater;
  public int day_layout;
  public Calendar selected;
  public int height;
  public EventCalendarView.Callback callback;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
    private final SimpleDateFormat dateFormatformonth = new SimpleDateFormat("MMMM", Locale.US);


    public Shared() {
        this.selected = Calendar.getInstance();
    }

    public String getMonthYear() {
        return dateFormat.format(selected.getTime());
    }
    public String getMonth() {
        return dateFormatformonth.format(selected.getTime());
    }
}
