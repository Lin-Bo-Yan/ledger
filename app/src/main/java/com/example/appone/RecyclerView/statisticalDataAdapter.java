package com.example.appone.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appone.Fragment.ledgerFragment;
import com.example.appone.Modle.statisticalDataViewHolder;
import com.example.appone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class statisticalDataAdapter extends RecyclerView.Adapter<statisticalDataViewHolder> {
    ledgerFragment ledgerFragment;
    HashMap<String, String> lumpSum,percnet;
    Set<String> words = new LinkedHashSet<>();
    ArrayList<String>arrayList = new ArrayList<>();

    public statisticalDataAdapter(ledgerFragment ledgerFragment,
                                  HashMap<String, String> lumpSum,
                                  HashMap<String, String> percnet,
                                  Set<String> words) {

        this.ledgerFragment = ledgerFragment;
        this.lumpSum = lumpSum;
        this.percnet = percnet;
        this.words = words;

        //System.out.println("字 "+"1 "+words.size());
    }

    @NonNull
    @Override
    public statisticalDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_list_view,parent,false);
        return new statisticalDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull statisticalDataViewHolder holder, int position) {

        for(String word : words){
            arrayList.add(word);
        }
        // Set 資料抓出來裝進 ArrayList<String> 方變用get抓資料
        holder.classificationName.setText(arrayList.get(position));
        holder.lumpSum.setText(lumpSum.get(arrayList.get(position)));
        holder.proportionMeter.setText(percnet.get(arrayList.get(position)));

        if(holder.classificationName.getText().equals("飲食")){
            holder.picture.setImageResource(R.drawable.breakfast);
        }else if(holder.classificationName.getText().equals("交通")){
            holder.picture.setImageResource(R.drawable.transportation);
        }else if(holder.classificationName.getText().equals("醫藥")){
            holder.picture.setImageResource(R.drawable.medicine);
        }else if(holder.classificationName.getText().equals("購物")){
            holder.picture.setImageResource(R.drawable.shopping);
        }else if(holder.classificationName.getText().equals("居家")){
            holder.picture.setImageResource(R.drawable.supplies);
        }else if(holder.classificationName.getText().equals("娛樂")){
            holder.picture.setImageResource(R.drawable.entertainment);
        }else if(holder.classificationName.getText().equals("其他")){
            holder.picture.setImageResource(R.drawable.other);
        }else if(holder.classificationName.getText().equals("收入")){
            holder.picture.setImageResource(R.drawable.icon_stock);
        }

    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}

