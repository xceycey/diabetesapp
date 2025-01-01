package com.example.projem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {
    private ArrayList<Foods> foodsArrayList;
    private Context context;
    private OnItemClickListener listener;

    public FoodAdapter(ArrayList<Foods> foodsArrayList, Context context) {
        this.foodsArrayList = foodsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.fooditem,parent,false);
        return new FoodHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodHolder holder, int position) {
        Foods foods=foodsArrayList.get(position);
        holder.setData(foods);
    }

    @Override
    public int getItemCount() {
        return foodsArrayList.size();
    }

    class FoodHolder extends RecyclerView.ViewHolder{
        TextView name,txt;
        ImageView image;
        public FoodHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.itemtextview);
            image=itemView.findViewById(R.id.itemimageview);
            txt=itemView.findViewById(R.id.itemtextview1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    if(listener!=null &&position!=RecyclerView.NO_POSITION){
                        listener.onItemClick(foodsArrayList.get(position),position);
                    }
                }
            });
        }
        public void setData(Foods foods){
            this.name.setText(foods.getName());
            this.txt.setText(foods.getServing_qty()+" "+foods.getServing_unit()+" "+foods.getGram()+" gram karb:"+foods.getCarb());
            Picasso.with(context.getApplicationContext())
                    .load(foods.getImg())
                    .resize(65,65)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            image.setImageDrawable(imageDrawable);
                        }
                        @Override
                        public void onError() {

                        }
                    });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(Foods foods,int position);

    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
