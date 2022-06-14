package com.example.appone.Modle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appone.R;

public class statisticalDataViewHolder extends RecyclerView.ViewHolder{
    public TextView classificationName,lumpSum,proportionMeter;
    public ImageView picture;
    public statisticalDataViewHolder(@NonNull View itemView) {
        super(itemView);
        classificationName =  itemView.findViewById(R.id.classificationName);
        lumpSum = itemView.findViewById(R.id.lumpSum);
        proportionMeter = itemView.findViewById(R.id.proportionMeter);
        picture = itemView.findViewById(R.id.picture);

    }
}
