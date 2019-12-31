package com.wjy.mycalendarselect;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wjy.
 * Date: 2019/12/30
 * Time: 8:54
 * Describe: PopupWindow弹窗显示日历
 */
public class CalendarPopupWindow extends PopupWindow {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    ImageView img_close;
    Button btn_ok;
    MyCalendarListView calendarList;
    private CalendarListener calendarListener;
    private String startDates,endDates;

    public CalendarPopupWindow(Context context){
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.calendarpopupwindow,null);
        initView(context,view);

        //设置PopupWindow的View
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.heise_40));
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    private void initView(final Context context, View view){
        img_close = view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        calendarList = view.findViewById(R.id.calendarList);
        calendarList.setOnDateSelected(new MyCalendarListView.OnDateSelected() {
            @Override
            public void selected(String startDate, String endDate) {
                startDates = startDate;
                endDates = endDate;
                try {
                    Date sDate = format.parse(startDate);
                    Date eDate = format.parse(endDate);
                    Toast.makeText(context,"共"+ CommonTools.getDayCount(sDate,eDate) +"晚", Toast.LENGTH_LONG).show();
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

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarListener != null){
                    calendarListener.onCalendarListenerResult(startDates,endDates);
                    dismiss();
                }
            }
        });
    }

    public interface CalendarListener{
        void onCalendarListenerResult(String startDate, String endDate);
    }

    public void setCalendarListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }
}
