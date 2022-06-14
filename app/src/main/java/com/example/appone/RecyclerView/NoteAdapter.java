package com.example.appone.RecyclerView;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appone.Fragment.ledgerFragment;
import com.example.appone.Modle.NoteViewHolder;
import com.example.appone.Modle.accounting;
import com.example.appone.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> implements Filterable {
    ledgerFragment ledgerFragment;
    ArrayList<accounting> accountingArrayList,accountingArrayListFilter;
    //建構式
    public NoteAdapter(ArrayList<accounting> accountingArrayList, ledgerFragment ledgerFragment) {
        this.accountingArrayList = accountingArrayList;
        this.ledgerFragment = ledgerFragment;
        accountingArrayListFilter = new ArrayList<>();
        accountingArrayListFilter.addAll(accountingArrayList);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row,parent,false);
        NoteViewHolder viewHolder = new NoteViewHolder(view);
        //handle item clicks here
        viewHolder.setOnClickListener(new NoteViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 這將在用戶單擊項目時調用
                //單擊時在 toast 中顯示數據
                accounting acc = accountingArrayList.get(position);
                String Chat = acc.getChat();
//                System.out.println("字"+" "+position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // 這將在用戶長按項目時調用
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                String[] options = {"修改","刪除"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String serialNumber = accountingArrayList.get(position).getSerialNumber();
                        String chat = accountingArrayList.get(position).getChat();
                        String money = accountingArrayList.get(position).getMoney();
                        String date = accountingArrayList.get(position).getDate();
                        String expenditure = accountingArrayList.get(position).getExpenditure();
                        String need = accountingArrayList.get(position).getNeed();
                        if(which == 0){
                            //修改
                            ledgerFragment.showRevise(position, serialNumber, chat, money,date, expenditure);
                        }
                        if(which == 1){
                            //刪除
                            //System.out.println("字 "+"刪除 "+position);
                            ledgerFragment.deleteData(position,chat,money,date,need,expenditure);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        int number = 0;
        number = position+1;

        accounting acc = accountingArrayList.get(position);
            holder.expenditure.setText(acc.getExpenditure());
            holder.money.setText(acc.getMoney());
            holder.date.setText(acc.getDate());
            holder.chat.setText(acc.getChat());
            holder.need.setText(acc.getNeed());
            holder.serialNumber.setText(String.valueOf(number));
            holder.newAccountName.setText(acc.getNewAccountName());

        //System.out.println("字 "+"1 "+String.valueOf(number));
    }

    @Override
    public int getItemCount() {
        //System.out.println("字 "+"1 "+accountingArrayList.size());
        return accountingArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    //使用Filter濾除方法
    Filter mFilter = new Filter() {
        /**啟動新的線程，根據constraint字符串序列過濾數據，返回FilterResults將作為參數傳遞給 publishResults()方法在UI線程中執行。*/
        /**此處為正在濾除字串時所做的事*/
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
                    /**先將完整陣列複製過去*/
            ArrayList<accounting> filteredList = new ArrayList<accounting>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(accountingArrayListFilter);
            }else {
                String delimeter = "~";  // 指定分割字符
                String[] temp = constraint.toString().split(delimeter);// 分割字符串
                String tempOne = temp[0];
                String tempTwo = temp[1];
                LocalDate startingPoint = LocalDate.parse(tempOne);
                LocalDate endingPoint = LocalDate.parse(tempTwo);

                //如果有輸入，則照順序濾除相關字串
                for (accounting movie: accountingArrayListFilter) {
                    //System.out.println("字 "+"5 "+movie.getDate());
                    LocalDate compareDates = LocalDate.parse(movie.getDate());//找到資料庫裡面所有列表的日期
                    Period period = Period.between(compareDates,startingPoint);
                    Period periodtwo = Period.between(endingPoint,compareDates);
                    if(compareDates.equals(startingPoint) || compareDates.equals(endingPoint)){
                        filteredList.add(movie);
                    }
                    if(period.isNegative() && periodtwo.isNegative()){
                        filteredList.add(movie);
                    }

                }
            }

            /**回傳濾除結果*/
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        /**在UI線程處理這些過濾後的數據，將濾除結果推給原先RecyclerView綁定的陣列，並通知RecyclerView刷新*/
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            accountingArrayList.clear();
            accountingArrayList.addAll((Collection<? extends  accounting>) results.values);
            ledgerFragment.sunMoney(accountingArrayList);//計算支出
            ledgerFragment.statisticalData(accountingArrayList);
            Collections.sort(accountingArrayList,accounting.mydata);//依照日期排序
            notifyDataSetChanged();
        }
    };
}
