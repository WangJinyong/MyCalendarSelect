package com.wjy.mycalendarselect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wjy.
 * Date: 2019/12/26
 * Time: 10:50
 * Describe: 生成日历的View
 */
public class MyCalendarListView extends FrameLayout {

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private ArrayList<DateBean> data = new ArrayList<>();
    private DateBean startDate;//开始时间
    private DateBean endDate;//结束时间
    OnDateSelected onDateSelected;//选中监听
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

    public MyCalendarListView(@NonNull Context context) {
        this(context,null);
    }

    public MyCalendarListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyCalendarListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context){
        addView(LayoutInflater.from(context).inflate(R.layout.item_calendar, this, false));

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CalendarAdapter(context,data);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                //这个方法返回的是当前位置的 item 跨度大小
                if (DateBean.item_type_month == data.get(i).getItemType()) {
                    return 7;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
//        data.addAll(days("2019-12-01", "2020-09-08"));
        data.addAll(days(11));

        //设置分割线
//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context,R.drawable.animated_rotate));
//        recyclerView.addItemDecoration(dividerItemDecoration);

        //实现月份标题悬停的效果   测试
        MyItemDecoration myItemDecoration = new MyItemDecoration();
        recyclerView.addItemDecoration(myItemDecoration);

        adapter.setOnRecyclerviewItemClick(new CalendarAdapter.OnRecyclerviewItemClick() {
            @Override
            public void onItemClick(View v, int position) {
                Date todayDate = new Date();//今天
                String date = data.get(position).getMonthStr()+"-"+data.get(position).getDay();//获取得到的日期
                Date beforeToday = new Date();
                try {
                    beforeToday = simpleDateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (beforeToday.getTime() < todayDate.getTime()-1000*60*60*24){//-1000*60*60*24  得到的是昨天的时间  不然今天也不可选
                    //今天之前的日期不可选
                    Toast.makeText(context,"当前日期不可选",Toast.LENGTH_SHORT).show();
                }else {
                    onClick(data.get(position));
                }
                Log.e("tag","date="+date);
            }
        });
    }

    private void onClick(DateBean dateBean){
        if (dateBean.getItemType() == DateBean.item_type_month || TextUtils.isEmpty(dateBean.getDay())) {
            return;
        }

        ////这个是在Dialog显示的情况下会用到，来判断如期是否已选完，来改变Dialog里面确定按钮的选中状态
        if(onDateSelected!=null){
            onDateSelected.hasSelect(false);
        }
        //如果没有选中开始日期则此次操作选中开始日期
        if (startDate == null){
            startDate = dateBean;
            dateBean.setItemState(DateBean.ITEM_STATE_BEGIN_DATE);
        }else if (endDate == null){
            //如果选中了开始日期但没有选中结束日期，本次操作选中结束日期

            //如果当前点击的结束日期跟开始日期一致 则不做操作
            if (startDate == dateBean){

            }else if (dateBean.getDate().getTime() < startDate.getDate().getTime()){
                //如果当前点选的日期小于当前选中的开始日期，则本次操作重新选中开始日期
                startDate.setItemState(DateBean.ITEM_STATE_NORMAL);
                startDate = dateBean;
                startDate.setItemState(DateBean.ITEM_STATE_BEGIN_DATE);
            }else {
                //当前点选的日期大于当前选中的开始日期  此次操作选中结束日期
                endDate = dateBean;
                endDate.setItemState(DateBean.ITEM_STATE_END_DATE);
                setState();//选中中间的日期

                if(onDateSelected!=null){
                    onDateSelected.hasSelect(true);
                    onDateSelected.selected(simpleDateFormat.format(startDate.getDate()),simpleDateFormat.format(endDate.getDate()));
                }
            }
        }else if (startDate != null && endDate != null){
            //结束日期和开始日期都已选中
            clearState();//取消选中状态

            /**
             * 一定要先清除结束日期，再重新选择开始日期，不然会有一个bug，当开始日期和结束日期都选中的时候，如果此次点选开始日期，则选中开始日期，
             * 如果点结束日期，则全都清除了，再点结束日期没有反应，应该是结束日期变为开始日期才对
             * 因此要先清除结束位置，再重新选中开始日期
             */
            //一定要先清除结束日期，再重新选择开始日期
            endDate.setItemState(DateBean.ITEM_STATE_NORMAL);
            endDate = null;
            startDate.setItemState(DateBean.ITEM_STATE_NORMAL);
            startDate = dateBean;
            startDate.setItemState(DateBean.ITEM_STATE_BEGIN_DATE);
        }
        adapter.notifyDataSetChanged();
    }

    //选中中间的日期
    private void setState(){
        if (endDate != null && startDate != null){
            int start = data.indexOf(startDate);
            start += 1;
            int end = data.indexOf(endDate);
            for (; start < end; start++){
                DateBean dateBean = data.get(start);
                if (!TextUtils.isEmpty(dateBean.getDay())) {
                    dateBean.setItemState(DateBean.ITEM_STATE_SELECTED);
                }
            }
        }
    }

    //取消选中状态
    private void clearState(){
        if (endDate != null && startDate != null){
            int start = data.indexOf(startDate);
            start += 1;
            int end = data.indexOf(endDate);
            for (; start < end; start++){
                DateBean dateBean = data.get(start);
                dateBean.setItemState(DateBean.ITEM_STATE_NORMAL);
            }
        }
    }

    //生成日历数据
//    private List<DateBean> days(String startDateStr, String endDateStr)
    private List<DateBean> days(int monthLength){
        List<DateBean> dateBeanList = new ArrayList<>();
        try {
            Calendar calendar = Calendar.getInstance();
            //日期格式化
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatYYYYMM = new SimpleDateFormat("yyyy-MM");

            //=============================== start 动态设置从本月开始   和时间长度（显示多少个月）======================================//
            //起始日期
            Date startDate = new Date();
            calendar.setTime(startDate);

            //结束日期
            calendar.add(Calendar.MONTH, monthLength);//月份是从0开始的  增加6个月  日历显示7个月的长度
            Date endDate = new Date(calendar.getTimeInMillis());//方法返回此Calendar以毫秒为单位的时间

            Log.e("tag", "startDate:" + format.format(startDate) + "----------endDate:" + format.format(endDate));

            //格式化开始日期和结束日期为 yyyy-mm-dd格式
            String endDateStr = format.format(endDate);//把date转成String
            endDate = format.parse(endDateStr);//把String转成date

            String startDateStr = format.format(startDate);
            startDate = format.parse(startDateStr);
            //================================= end ====================================//


            //--------------------------------- start 动态传值方式设置显示的日历日期区间----------------------------------------//
            //起始日期
//            Date startDate = format.parse(startDateStr);//把String转成date
//            //结束日期
//            Date endDate = format.parse(endDateStr);//把String转成date
            //---------------------------------- end ---------------------------------------//

            calendar.setTime(startDate);//上面的calendar.setTime(startDate)是设置了当前时间，但是后面calendar.add(Calendar.MONTH, 5)结束日期加了5个月，日期就延后了5个月，所以要得到当前日期，需要在此处再设置一次

            Log.e("tag", "startDateStr:" + startDateStr + "---------endDate:" + format.format(endDate));

            calendar.set(Calendar.DAY_OF_MONTH, 1);//设置日期为1
            Calendar monthCalendar = Calendar.getInstance();

            //按月生成日历 每行7个 最多6行 42个
            //每一行有七个日期  日 一 二 三 四 五 六 的顺序
            Log.e("tag","calendar.getTimeInMillis()="+calendar.getTimeInMillis()+"----------endDate.getTime()="+endDate.getTime());
            for (calendar.getTimeInMillis(); calendar.getTimeInMillis() <= endDate.getTime();){//从当前时间开始，如果小于等于最后的时间，则增加一个月
                //月份item
                DateBean monthDateBean = new DateBean();
                monthDateBean.setDate(calendar.getTime());
                monthDateBean.setMonthStr(formatYYYYMM.format(monthDateBean.getDate()));
                monthDateBean.setItemType(DateBean.getItem_type_month());
                dateBeanList.add(monthDateBean);

                //获取一个月结束的日期和开始日期
                monthCalendar.setTime(calendar.getTime());
                monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                Date startMonthDay = calendar.getTime();

                monthCalendar.add(Calendar.MONTH, 1);//表示加一个月
                monthCalendar.add(Calendar.DAY_OF_MONTH, -1);//表示对日期进行减一天操作
                //从而得到当前月的最后一天
                Date endMonthDay = monthCalendar.getTime();

                //重置为本月开始
                monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                Log.e("tag", "月份的开始日期:" + format.format(startMonthDay) + "——星期"+getWeekStr(calendar.get(Calendar.DAY_OF_WEEK)+"")+ "---------结束日期:" + format.format(endMonthDay));
                //从月的第一天开始，如果小于等于本月最后一天，则增加一天
                for(monthCalendar.getTimeInMillis();monthCalendar.getTimeInMillis() <= endMonthDay.getTime();){
                    //生成单个月的日历
                    //处理一个月开始的第一天
                    if (monthCalendar.get(Calendar.DAY_OF_MONTH) == 1){
                        //看某个月第一天是周几
                        int weekDay = monthCalendar.get(Calendar.DAY_OF_WEEK);
                        Log.e("tag","dateBeanList="+dateBeanList.size());
                        Log.e("tag","monthDateBean.getMonthStr()="+monthDateBean.getMonthStr());
                        switch (weekDay){
                            case 1://周日  正常顶格显示
                                break;
                            case 2://周一  错后一格显示
                                addDatePlaceholder(dateBeanList, 1, monthDateBean.getMonthStr());
                                break;
                            case 3://周二  错后二格显示
                                addDatePlaceholder(dateBeanList, 2, monthDateBean.getMonthStr());
                                break;
                            case 4://周三  错后三格显示
                                addDatePlaceholder(dateBeanList, 3, monthDateBean.getMonthStr());
                                break;
                            case 5://周四  错后四格显示
                                addDatePlaceholder(dateBeanList, 4, monthDateBean.getMonthStr());
                                break;
                            case 6://周五  错后五格显示
                                addDatePlaceholder(dateBeanList, 5, monthDateBean.getMonthStr());
                                break;
                            case 7://周六  错后六格显示
                                addDatePlaceholder(dateBeanList, 6, monthDateBean.getMonthStr());
                                break;
                        }
                    }

                    //生成某一天日期实体 日item
                    DateBean dayDateBean = new DateBean();
                    dayDateBean.setDate(monthCalendar.getTime());
                    dayDateBean.setMonthStr(monthDateBean.getMonthStr());
                    dayDateBean.setDay(monthCalendar.get(Calendar.DAY_OF_MONTH) + "");
                    dateBeanList.add(dayDateBean);

                    //处理一个月的最后一天
                    if (monthCalendar.getTimeInMillis() == endMonthDay.getTime()){
                        //看某个月最后一天是周几
                        int weekDay = monthCalendar.get(Calendar.DAY_OF_WEEK);
                        switch (weekDay){
                            case 1://周日 添加6个空的日期占位
                                addDatePlaceholder(dateBeanList, 6, monthDateBean.getMonthStr());
                                break;
                            case 2://周一 添加5个空的日期占位
                                addDatePlaceholder(dateBeanList, 5, monthDateBean.getMonthStr());
                                break;
                            case 3://周二 添加4个空的日期占位
                                addDatePlaceholder(dateBeanList, 4, monthDateBean.getMonthStr());
                                break;
                            case 4://周三 添加3个空的日期占位
                                addDatePlaceholder(dateBeanList, 3, monthDateBean.getMonthStr());
                                break;
                            case 5://周四 添加2个空的日期占位
                                addDatePlaceholder(dateBeanList, 2, monthDateBean.getMonthStr());
                                break;
                            case 6://周五 添加1个空的日期占位
                                addDatePlaceholder(dateBeanList, 1, monthDateBean.getMonthStr());
                                break;
                            case 7://周六
                                break;
                        }
                    }
                    //天数加1
                    monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                Log.e("tag", "日期：" + format.format(calendar.getTime()) + "----周" + getWeekStr(calendar.get(Calendar.DAY_OF_WEEK) + ""));
                //月份加1
                calendar.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateBeanList;
    }

    //添加空的日期占位
    private void addDatePlaceholder(List<DateBean> dateBeans, int count, String monthStr) {
        for (int i = 0; i < count; i++) {
            DateBean dateBean = new DateBean();
            dateBean.setMonthStr(monthStr);
            dateBeans.add(dateBean);
        }
    }

    //获取星期几
    private String getWeekStr(String mWay) {
        if ("1".equals(mWay)) {
            mWay = "日";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mWay;
    }

    public interface OnDateSelected{
        void selected(String startDate,String endDate);//将选中的结果回传到Activity页面
        void hasSelect(boolean select);////这个是在Dialog显示的情况下会用到，来判断如期是否已选完，来改变Dialog里面确定按钮的选中状态
    }

    public void setOnDateSelected(OnDateSelected onDateSelected) {
        this.onDateSelected = onDateSelected;
    }
}
