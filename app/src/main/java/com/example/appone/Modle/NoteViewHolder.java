package com.example.appone.Modle;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appone.R;
public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView date,serialNumber,expenditure,need,chat,money,newAccountName;
    public View mView;
    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);
        date = (TextView)itemView.findViewById(R.id.date);
        serialNumber = (TextView)itemView.findViewById(R.id.serialNumber);
        expenditure = (TextView)itemView.findViewById(R.id.expenditure);
        need = (TextView)itemView.findViewById(R.id.need);
        chat = (TextView)itemView.findViewById(R.id.chat);
        money = (TextView)itemView.findViewById(R.id.money);
        newAccountName = (TextView)itemView.findViewById(R.id.newAccountName);

        //對View設計一個監聽器
        mView = itemView;
        // 監聽器
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
        // 長按監聽器
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });
    }
    private NoteViewHolder.ClickListener mClickListener;
    // iterface for click listener
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(NoteViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
