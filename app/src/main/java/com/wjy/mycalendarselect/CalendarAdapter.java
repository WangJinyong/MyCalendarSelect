package com.wjy.mycalendarselect;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by wjy.
 * Date: 2019/12/26
 * Time: 11:57
 * Describe: 日历adapter
 */
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public ArrayList<DateBean> data = new ArrayList<>();
    private OnRecyclerviewItemClick onRecyclerviewItemClick;

    public OnRecyclerviewItemClick getOnRecyclerviewItemClick() {
        return onRecyclerviewItemClick;
    }

    public void setOnRecyclerviewItemClick(OnRecyclerviewItemClick onRecyclerviewItemClick) {
        this.onRecyclerviewItemClick = onRecyclerviewItemClick;
    }

    public CalendarAdapter(Context context, ArrayList<DateBean> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DateBean.item_type_day){
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day,parent,false);
            final DayViewHolder dayViewHolder = new DayViewHolder(view);
            dayViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerviewItemClick != null){
                        onRecyclerviewItemClick.onItemClick(v,dayViewHolder.getLayoutPosition());
                    }
                }
            });
            return dayViewHolder;
        }else if (viewType == DateBean.item_type_month){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month,parent,false);
            final MonthViewHolder monthViewHolder = new MonthViewHolder(view);
            monthViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerviewItemClick != null){
                        onRecyclerviewItemClick.onItemClick(v,monthViewHolder.getLayoutPosition());
                    }
                }
            });
            return monthViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MonthViewHolder){
            MonthViewHolder viewHolder = (MonthViewHolder) holder;
            viewHolder.tv_month.setText(data.get(position).getMonthStr());
        }else {
            DayViewHolder viewHolder = (DayViewHolder) holder;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date todayDate = new Date();
            String todayStr = format.format(todayDate);//获取今天日期
            String date = data.get(position).getMonthStr()+"-"+data.get(position).getDay();//获取得到的日期
            Date beforeToday = new Date();
            try {
                beforeToday = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date.equals(todayStr)){
                //如果是今天的日期  则把显示的日期号改为“今天”两个字
                viewHolder.tv_day.setText("今天");
                viewHolder.tv_day.setTextColor(Color.parseColor("#2196F3"));
            } else if (beforeToday.getTime() < todayDate.getTime()){
                //今天之前的日期  设置成灰色
                viewHolder.tv_day.setText(data.get(position).getDay());
                viewHolder.tv_day.setTextColor(Color.parseColor("#dadada"));
            } else {
                viewHolder.tv_day.setText(data.get(position).getDay());
                viewHolder.tv_day.setTextColor(Color.BLACK);
            }

            DateBean dateBean = data.get(position);
            //设置item状态
            if (dateBean.getItemState() == DateBean.ITEM_STATE_BEGIN_DATE || dateBean.getItemState() == DateBean.ITEM_STATE_END_DATE){
                //开始日期或结束日期
                viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue));
                viewHolder.tv_day.setTextColor(Color.WHITE);
                viewHolder.tv_check_in_check_out.setVisibility(VISIBLE);
                if (dateBean.getItemState() == DateBean.ITEM_STATE_BEGIN_DATE){
                    viewHolder.tv_check_in_check_out.setText("开始");
                }else {
                    viewHolder.tv_check_in_check_out.setText("结束");
                }
            }else if (dateBean.getItemState() == DateBean.ITEM_STATE_SELECTED){
                //选中状态
                viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue1));
                viewHolder.tv_day.setTextColor(Color.WHITE);
            }else {
                //正常状态
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                viewHolder.tv_check_in_check_out.setVisibility(GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getItemType();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_day;
        public TextView tv_check_in_check_out;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_check_in_check_out = itemView.findViewById(R.id.tv_check_in_check_out);
        }
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_month;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_month = itemView.findViewById(R.id.tv_month);
        }
    }

    public interface OnRecyclerviewItemClick {
        void onItemClick(View v, int position);
    }
}
