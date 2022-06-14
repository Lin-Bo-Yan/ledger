package com.example.appone.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appone.Modle.accounting;
import com.example.appone.R;
import java.util.ArrayList;

public class RecyclerAdapter<S extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    private OnItemClickListener mListener;
    ArrayList<accounting>accountingArrayList;
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }
    public interface OnItemClickListener{
        void onReviseClick(int position);
        void onDeleteClick(int position);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
            accounting acc = accountingArrayList.get(position);
            holder.expenditure.setText(acc.getExpenditure());
            holder.money.setText(acc.getMoney());
            holder.date.setText(acc.getDate());
            holder.chat.setText(acc.getChat());
            holder.serialNumber.setText(acc.getSerialNumber());
            holder.need.setText(acc.getNeed());
    }

    @Override
    public int getItemCount() {
        return accountingArrayList.size();
    }

    //建構式，用來接收外部程式傳入的項目資料
    public RecyclerAdapter(Context context, ArrayList<accounting> accountingArrayList) {
        this.context = context;
        this.accountingArrayList = accountingArrayList;
    }
    public void setOnItemClickListenter(OnItemClickListener listenter){mListener=listenter;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date,serialNumber,expenditure,need,chat,money;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date=(TextView)itemView.findViewById(R.id.date);
            serialNumber=(TextView)itemView.findViewById(R.id.serialNumber);
            expenditure=(TextView)itemView.findViewById(R.id.expenditure);
            need=(TextView)itemView.findViewById(R.id.need);
            chat=(TextView)itemView.findViewById(R.id.chat);
            money=(TextView)itemView.findViewById(R.id.money);

//            itemView.findViewById(R.id.delete).setOnClickListener(View ->{
//                if(mListener!=null){
//                    int position = getAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION && mListener != null){
//                        mListener.onDeleteClick(position);
//                    }
//                }
//            });
//            itemView.findViewById(R.id.revise).setOnClickListener(View ->{
//                if(mListener!=null){
//                    int position = getAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION && mListener != null){
//                        mListener.onReviseClick(position);
//                    }
//                }
//            });
        }


//        public ViewHolder linkAdapter(RecyclerAdapter adapter){
//            this.adapter=adapter;
//            return  this;
//        }
    }
}

