package com.wjy.mycalendarselect;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wjy.
 * Date: 2019/12/30
 * Time: 12:06
 * Describe: Dialog弹窗显示日历
 */
public class CalendarDialog extends Dialog {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    ImageView img_close;
    Button btn_ok;
    MyCalendarListView calendarList;
    private OnDialogCalendarListener calendarListener;
    private String startDates,endDates;

    public static DisplayMetrics metrics;
    public static int screenWidth;//屏幕宽
    public static int screenHeigh;//屏幕高

    public CalendarDialog(@NonNull Context context) {
        super(context,R.style.CalendarDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.calendarpopupwindow);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View viewDialog = inflater.inflate(R.layout.calendarpopupwindow, null);
        metrics = getContext().getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;//屏幕宽
        screenHeigh = metrics.heightPixels;//屏幕高
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(screenWidth, screenHeigh-100);
        setContentView(viewDialog, layoutParams);
        initView();
    }

    @Override
    public void show() {
        super.show();
    }

    private void initView(){
        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        calendarList = findViewById(R.id.calendarList);
        calendarList.setOnDateSelected(new MyCalendarListView.OnDateSelected() {
            @Override
            public void selected(String startDate, String endDate) {
                startDates = startDate;
                endDates = endDate;
                try {
                    Date sDate = format.parse(startDate);
                    Date eDate = format.parse(endDate);
                    Toast.makeText(getContext(),"共"+ CommonTools.getDayCount(sDate,eDate) +"晚", Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void hasSelect(boolean select) {
                if (select){
                    btn_ok.setSelected(true);
                }else {
                    btn_ok.setSelected(false);
                }
            }
        });

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarListener != null){
                    calendarListener.OnDialogCalendarListener(startDates,endDates);
                    dismiss();
                }
            }
        });
    }

    public interface OnDialogCalendarListener{
        void OnDialogCalendarListener(String startDate, String endDate);
    }

    public void setOnDialogCalendarListener(OnDialogCalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }
}
