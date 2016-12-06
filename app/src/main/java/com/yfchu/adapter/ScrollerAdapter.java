package com.yfchu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yfchu.entity.HorizontalClass;
import com.yfchu.entity.ScrollerClass;
import com.yfchu.view.customview.HorizontalView;
import com.yfchu.view.customview.R;
import com.yfchu.view.customview.ScrollerLayout;
import com.yfchu.view.customview.TabItem;

import java.util.List;

/**
 * yfchu 2016/11/28 0028.
 */

public class ScrollerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<ScrollerClass> list;

    public ScrollerAdapter(Context mContext, List<ScrollerClass> mList) {
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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.page_contain, null);
            holder = new ViewHolder();
            holder.textAge = (TextView) convertView.findViewById(R.id.txt_Age);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textAge.setId(position);
        holder.textAge.setText(list.get(position).getAge());
        return convertView;
    }

    public void notifyDataSetChanged(List<ScrollerClass> mList, ScrollerLayout scrollerView) {
        this.list=mList;
        scrollerView.setAdapter(this);
    }

    class ViewHolder {
        TextView textAge;
    }
}
