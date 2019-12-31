package com.wjy.mycalendarselect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wjy.
 * Date: 2019/12/26
 * Time: 10:21
 * Describe: 类似美团携程选择酒店入住日期和离店日期的日历效果
 */
public class MainActivity extends AppCompatActivity implements CalendarPopupWindow.CalendarListener {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    MyCalendarListView calendarList;
    TextView tv_selectDate;
    CalendarPopupWindow calendarPopupWindow;
    CalendarDialog calendarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarDialog = new CalendarDialog(MainActivity.this);
        initView();
    }

    private void initView(){
        calendarPopupWindow = new CalendarPopupWindow(MainActivity.this);
        calendarPopupWindow.setCalendarListener(this);
        tv_selectDate = findViewById(R.id.tv_selectDate);
        tv_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //以Dialog弹窗形式显示
                calendarDialog.getWindow().setGravity(Gravity.BOTTOM);
                calendarDialog.getWindow().setWindowAnimations(R.style.mystyle);
                calendarDialog.show();

                //以PopupWindow弹窗形式显示
//                calendarPopupWindow.showAtLocation(CalendarActivity.this.findViewById(R.id.ll_parent), Gravity.BOTTOM,0,0);
//                calendarPopupWindow.setAnimationStyle(R.style.mystyle   );
            }
        });

        calendarDialog.setOnDialogCalendarListener(new CalendarDialog.OnDialogCalendarListener() {
            @Override
            public void OnDialogCalendarListener(String startDate, String endDate) {
                Log.e("tag","OnDialogCalendarListener  startDate="+startDate);
                Log.e("tag","OnDialogCalendarListener  endDate="+endDate);
            }
        });

        calendarList = findViewById(R.id.calendarList);
        calendarList.setOnDateSelected(new MyCalendarListView.OnDateSelected() {
            @Override
            public void selected(String startDate, String endDate) {
                Toast.makeText(MainActivity.this,"开始日期:"+startDate+"\n结束日期:"+endDate,Toast.LENGTH_LONG).show();
                try {
                    Date sDate = format.parse(startDate);
                    Date eDate = format.parse(endDate);
                    tv_selectDate.setText(CommonTools.getDateForStandard(startDate).substring(5)+"    "+CommonTools.DateToWeek(sDate)+"——"
                            +CommonTools.getDateForStandard(endDate).substring(5)+"    "+CommonTools.DateToWeek(eDate)+"    共"+ CommonTools.getDayCount(sDate,eDate) +"晚");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void hasSelect(boolean select) {

            }
        });
    }

    @Override
    public void onCalendarListenerResult(String startDate, String endDate) {
        Log.e("tag","onCalendarListenerResult  startDate="+startDate);
        Log.e("tag","onCalendarListenerResult  endDate="+endDate);
    }
}
