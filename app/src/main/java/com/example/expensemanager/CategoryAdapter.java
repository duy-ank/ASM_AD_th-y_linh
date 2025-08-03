package com.example.expensemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CategoryItem> categories;
    private int selectedPosition = -1;

    public CategoryAdapter(Context context, ArrayList<CategoryItem> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.icon);
            holder.name = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CategoryItem item = categories.get(position);
        holder.icon.setImageResource(item.getIconRes());
        holder.name.setText(item.getName());

        // Nếu danh mục được chọn thì đổi màu nền hoặc chữ
        if (position == selectedPosition) {
            convertView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_category_selected));
            holder.name.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            holder.name.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
