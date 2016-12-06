package com.yfchu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yfchu.entity.HorizontalClass;
import com.yfchu.view.customview.HorizontalView;
import com.yfchu.view.customview.R;
import com.yfchu.view.customview.TabItem;

import java.util.List;

/**
 * yfchu 2016/11/28 0028.
 */

public class HorizontalAdapter{
    private Context context;
    private LayoutInflater inflater;
    private List<HorizontalClass> list;

    public HorizontalAdapter(Context mContext, List<HorizontalClass> mList) {
        context = mContext;
        list = mList;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent,int selectIndex) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.horizontal_contain, null);
            holder = new ViewHolder();
            holder.textAge = (TabItem) convertView.findViewById(R.id.tabTxt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textAge.setId(position);
        holder.textAge.setTextValue(list.get(position).getAge());
        holder.textAge.setTextColorSelect(context.getResources().getColor(R.color.textSelectColor));
        holder.textAge.setTextColorNormal(context.getResources().getColor(R.color.textColor));
        if (position == selectIndex) {
            holder.textAge.setScaleX(1.1f);
            holder.textAge.setScaleY(1.1f);
            holder.textAge.setTabAlpha(1.0f);
        } else {
            holder.textAge.setScaleX(0.95f);
            holder.textAge.setScaleY(0.95f);
        }
        return convertView;
    }

    public void notifyDataSetChanged(List<HorizontalClass> mList, HorizontalView horizontalView) {
        this.list=mList;
        horizontalView.setAdapter(this);
    }

    class ViewHolder {
        TabItem textAge;
    }
}
