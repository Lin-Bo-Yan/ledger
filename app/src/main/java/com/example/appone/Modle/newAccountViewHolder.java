package com.example.appone.Modle;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appone.R;

public class newAccountViewHolder extends RecyclerView.ViewHolder{
    public TextView newAccount,remark;
    public View mView;
    public newAccountViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnClickListener(newAccountViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }


    public newAccountViewHolder(@NonNull View itemView) {
        super(itemView);
        newAccount =  itemView.findViewById(R.id.newAccount);
        remark =  itemView.findViewById(R.id.remark);
        mView = itemView;
        // 監聽器
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }
}
