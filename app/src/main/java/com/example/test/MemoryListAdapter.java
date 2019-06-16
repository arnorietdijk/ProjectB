package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MemoryListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Food> foodsList;

    public MemoryListAdapter(Context context, int layout, ArrayList<Food> foodsList) {
        this.context = context;
        this.layout = layout;
        this.foodsList = foodsList;
    }

    @Override
    public int getCount() {
        return foodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtTitle, txtDes, txtLoc;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.txtDes = (TextView) row.findViewById(R.id.txtDes);
            holder.txtLoc = (TextView) row.findViewById(R.id.txtLoc);
            holder.imageView = (ImageView) row.findViewById(R.id.imgFood);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Food food = foodsList.get(position);

        holder.txtTitle.setText(food.getTitle());
        holder.txtDes.setText(food.getDescription());
        holder.txtLoc.setText(food.getLocation());

        byte[] foodImage = food.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        row.setBackgroundResource(R.drawable.grid_items_border);
        return row;
    }
}
