package com.example.appone.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appone.Fragment.ledgerFragment;
import com.example.appone.Fragment.newAccountFragment;
import com.example.appone.Modle.newAccount;
import com.example.appone.Modle.newAccountViewHolder;
import com.example.appone.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class newAccountAdapter extends RecyclerView.Adapter<newAccountViewHolder> {
    newAccountFragment newAccountFragment;
    ArrayList<newAccount>newAccountArrayList;

    public newAccountAdapter(com.example.appone.Fragment.newAccountFragment newAccountFragment, ArrayList<newAccount>newAccountArrayList) {
        this.newAccountFragment = newAccountFragment;
        this.newAccountArrayList = newAccountArrayList;
    }

    @NonNull
    @Override
    public newAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_account_view,parent,false);
        newAccountViewHolder viewHolder = new newAccountViewHolder(view);
        viewHolder.setOnClickListener(new newAccountViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                newAccount newAccount = newAccountArrayList.get(position);
                String account = newAccount.getAccountName();

                //System.out.println("字 "+"帳戶名稱 "+account);
            }

        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull newAccountViewHolder holder, int position) {
        newAccount newAccount = newAccountArrayList.get(position);
        holder.newAccount.setText(newAccount.getAccountName());
        holder.remark.setText(newAccount.getRemark());
    }

    @Override
    public int getItemCount() {
        return newAccountArrayList.size();
    }
}
