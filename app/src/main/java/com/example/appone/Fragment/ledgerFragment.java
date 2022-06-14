package com.example.appone.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appone.Modle.accounting;
import com.example.appone.Modle.newAccount;
import com.example.appone.R;
import com.example.appone.RecyclerView.NoteAdapter;
import com.example.appone.RecyclerView.statisticalDataAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ledgerFragment extends Fragment {
    String collectionPath;
    Spinner accountNameSpinner;
    RecyclerView addItem,statistics;
    TextView totalExpenses,totalRevenue,dateRangTxt;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //recyclerView 的佈局管理器
    NoteAdapter adapter;
    Button clearDate,dateRang;
    LocalDate startingPoint,endingPoint;
    int mYear,mMonth,mDay;
    int sumOfExpenses = 0;//支出總和
    int sumOfRevenue = 0;//收入總和
    String format = "";
    String startDateFormat = "選擇日期";
    String endDateFormat = "選擇日期";
    HashMap<String, String> lumpSum,percnet;//統計資料 R.drawable.other R.drawable.medicine
    ArrayList<accounting> accountingArrayList = new ArrayList<accounting>();
    ArrayList<newAccount>addAccountCategory = new ArrayList<newAccount>();
    List<String> AccountCategoryList = new ArrayList<>();
    ProgressDialog pd;
    SharedPreferences sp;
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ledger_fragment, container, false);
        //-------鍵盤收縮
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null){
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        addItem = view.findViewById(R.id.addItem);
        statistics = view.findViewById(R.id.statistics);
        dateRangTxt = view.findViewById(R.id.dateRangTxt);
        accountNameSpinner = view.findViewById(R.id.accountName);

        totalExpenses = view.findViewById(R.id.totalExpenses);
        totalRevenue = view.findViewById(R.id.totalRevenue);

        clearDate = view.findViewById(R.id.clearDate);
        clearDate.setOnClickListener(v -> {
            adapter.getFilter().filter("");
            dateRangTxt.setText("");
        });
        dateRang = view.findViewById(R.id.dateRang);
        dateRang.setOnClickListener(v -> {
            dateRangDialog();
        });

        getAccountCategory();
        return view;
    }

    //dateRang 的 Dialog
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dateRangDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.date_rang);

        Button startDate = dialog.findViewById(R.id.startDate);
        startDate.setText(startDateFormat);
        startDate.setOnClickListener((view)->{
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    startingPoint = LocalDate.of(year, month+1, day); // month+1 因為不知為何 月份會少1個月
                    startDateFormat = startingPoint.format(formatter);
                    startDate.setText(startDateFormat);

                }
            }, mYear, mMonth, mDay);
            pickerDialog.show();
        });

        Button endDate = dialog.findViewById(R.id.endDate);
        endDate.setText(endDateFormat);
        endDate.setOnClickListener((view)->{
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    endingPoint = LocalDate.of(year, month+1, day);
                    endDateFormat = endingPoint.format(formatter);
                    endDate.setText(endDateFormat);
                }
            }, mYear, mMonth, mDay);
            pickerDialog.show();
        });

        Button selected = dialog.findViewById(R.id.selected);
        selected.setOnClickListener((view)->{
            if(startDate.getText().toString().equals("選擇日期") && endDate.getText().toString().equals("選擇日期")){
                dialog.show();
            }else if(startingPoint.isBefore(endingPoint) || startDate.getText().toString().equals(endDate.getText().toString())){
                String sumString = startDateFormat+"~"+endDateFormat;
                adapter.getFilter().filter(sumString);
                dateRangTxt.setText(sumString);
                dialog.dismiss();
            }else {
                new AlertDialog.Builder(getContext())
                        .setTitle("請依照日期順序").setNegativeButton("確定",null).show();
            }

        });
        dialog.show();
    }
    //顯示data orderBy("date") 排序
    public void dataSelect(){
        try {
            accountingArrayList.clear();
            db.collection(collectionPath).orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc : task.getResult()){
                        accountingArrayList.add(doc.toObject(accounting.class));
                    }

                    sunMoney(accountingArrayList);
                    statisticalData(accountingArrayList);
                    adapter = new NoteAdapter(accountingArrayList,ledgerFragment.this);
                    Collections.sort(accountingArrayList,accounting.mydata);//依照日期排序
                    addItem.setAdapter(adapter);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("字 "+"dataSelect  "+e);
                }
            });

            //設置回收站視圖屬性
            addItem.setHasFixedSize(true);
            /**RecyclerView的尺寸在每次改變時，比如你加任何些東西。setHasFixedSize 的作用就是確保尺寸是通過用戶輸入從而確保RecyclerView的尺寸是一個常數。
             * RecyclerView 的Item寬或者高不會變。每一個Item添加或者刪除都不會變。*/

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true); //先添加的item會被頂上去，最新添加的item每次都會顯示在最下面
            //layoutManager.setReverseLayout(false);
            addItem.setLayoutManager(layoutManager);
        }catch (Exception e){System.out.println("字 "+"collectionPathCanNotFount  "+e);}

    }
    //刪除data
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteData(int position, String chat, String money, String date, String need, String expenditure){
        if(collectionPath.matches("全部明細")){
            new AlertDialog.Builder(getContext())
                    .setTitle("請到該帳戶進行刪除！").setNegativeButton("確定",null).show();
        }else {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.delete_screen);

            TextView dateDelete = dialog.findViewById(R.id.date);
            TextView expenditureDelete = dialog.findViewById(R.id.expenditure);//收入支出
            TextView serialNumberDelete= dialog.findViewById(R.id.serialNumber);//流水號
            TextView needDelete= dialog.findViewById(R.id.need);
            TextView chatDelete= dialog.findViewById(R.id.chat);
            TextView moneyDelete= dialog.findViewById(R.id.money);
            Button sureButton = dialog.findViewById(R.id.sureButton);
            Button cancelButton = dialog.findViewById(R.id.cancelButton);
            int number = 0;
            number = position+1;
            dateDelete.setText(date);
            expenditureDelete.setText(expenditure);
            serialNumberDelete.setText(String.valueOf(number));
            needDelete.setText(need);
            chatDelete.setText(chat);
            moneyDelete.setText(money);
            dialog.show();
            //確定
            sureButton.setOnClickListener((view)->{

                db.collection(collectionPath).document(accountingArrayList.get(position).getSerialNumber())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                switch (expenditure){
                                    case "收入":
                                        //重新計算 總收入
                                        sumOfRevenue = sumOfRevenue-Integer.parseInt(money);
                                        if(sumOfRevenue<0)sumOfRevenue=0;
                                        totalRevenue.setText(String.valueOf(sumOfRevenue));
                                        break;
                                    case "支出":
                                        //重新計算 總支出
                                        sumOfExpenses = sumOfExpenses-Integer.parseInt(money);
                                        if(sumOfExpenses<0)sumOfExpenses=0;
                                        totalExpenses.setText(String.valueOf(sumOfExpenses));
                                        break;
                                }
                                new AlertDialog.Builder(getContext())
                                        .setTitle("刪除成功").setNegativeButton("確定",null).show();
                                accountingArrayList.remove(position);//從列表中移除數據
                                adapter.notifyItemRemoved(position);//通知移除item，notifyItemRemoved方法，是只是局部ui的刷新，被刪除掉的item之後的數據都沒有變化
                                adapter.notifyItemRangeChanged(position,accountingArrayList.size());
                                dataSelect();//順序在 刪除 adapter 之後才重新刷新畫面，不然會閃退
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("字 "+"deleteData "+e);
                            }
                        });
            });
            //取消
            cancelButton.setOnClickListener((view)->{
                dialog.dismiss();
            });
        }

    }
    // 顯示修改視窗
    public void showRevise(int position, String serialNumber, String chat, String money, String date, String expenditure){


        if(collectionPath.matches("全部明細")){
            new AlertDialog.Builder(getContext())
                    .setTitle("請到該帳戶進行修改").setNegativeButton("確定",null).show();
        }else {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.revise);
            //System.out.println("字"+" "+"serialNumber  "+serialNumber);
            Spinner expenditureRevise = dialog.findViewById(R.id.expenditure);
            EditText moneyRevise = dialog.findViewById(R.id.money);
            EditText chatRevise = dialog.findViewById(R.id.chat);
            Button dateButtonRevise = dialog.findViewById(R.id.date);
            moneyRevise.setText(money);
            chatRevise.setText(chat);
            dateButtonRevise.setText(date);
            //expenditureRevise.setSelected(true);

            List<String> footballPlayers = new ArrayList<>();
            switch (expenditure){
                case "收入":
                    footballPlayers.add(expenditure);
                    //footballPlayers.add("支出");
                    break;
                case "支出":
                    footballPlayers.add(expenditure);
                    //footballPlayers.add("收入");
                    break;
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
            expenditureRevise.setAdapter(arrayAdapter);



            format = date;
            dateButtonRevise.setOnClickListener((view)->{
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate myLocalDate = LocalDate.of(year, month+1, day);
                        format = myLocalDate.format(formatter);
                        dateButtonRevise.setText(format);
                    }
                }, mYear, mMonth, mDay);
                pickerDialog.show();
            });

            Button sureButtonRevise = dialog.findViewById(R.id.sureButton);
            sureButtonRevise.setOnClickListener((view)->{
                String expenditureRe = expenditureRevise.getSelectedItem().toString();
                String moneyRe = moneyRevise.getText().toString();
                String chatRe = chatRevise.getText().toString();
                String dateButtonRe = dateButtonRevise.getText().toString();
                if(chatRe.matches("^[\u4e00-\u9fa5\\w\\.-@]+$")&&
                        moneyRe.matches("^[0-9]+$")&&
                        dateButtonRe.matches("^[0-9]{4}+\\-+[0-9]{1,2}+\\-+[0-9]{1,2}$")){
                    db.collection(collectionPath).document(serialNumber)
                            .update("expenditure",expenditureRe,"money",moneyRe,"date",format,"chat",chatRe);
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("要輸入東西喔！").setNegativeButton("確定",null).show();
                }
                dataSelect();
                //adapter.notifyDataSetChanged();
                dialog.dismiss();
            });
            dialog.show();
        }
    }
    //轉換日期格式
    public String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }
    //取出金額算總和
    public void sunMoney(ArrayList<accounting> accountingArrayList){
        //把支出總和計數清除
        sumOfExpenses = 0;
        int moneyOfExpensesInt = 0;
        //把收入總和計數清除
        sumOfRevenue = 0;
        int moneyOfRevenueInt = 0;

        for (int index=0; index<accountingArrayList.size();index++){
            String expenditure = accountingArrayList.get(index).getExpenditure();
            switch (expenditure){
                case "收入":
                    String moneyOfRevenue = accountingArrayList.get(index).getMoney();
                    moneyOfRevenueInt = Integer.parseInt(moneyOfRevenue);
                    sumOfRevenue = sumOfRevenue + moneyOfRevenueInt;
                    //System.out.println("字"+" "+"收入 "+sumOfRevenue);
                    break;
                case "支出":
                    String moneyOfExpenses = accountingArrayList.get(index).getMoney();
                    moneyOfExpensesInt = Integer.parseInt(moneyOfExpenses);
                    sumOfExpenses = sumOfExpenses+moneyOfExpensesInt;
                    //System.out.println("字"+" "+"收入 "+sumOfExpenses);
                    break;
            }

        }
        if(sumOfExpenses<0)sumOfExpenses=0;
        totalExpenses.setText(String.valueOf(sumOfExpenses));
        //System.out.println("字"+" "+"總和  "+sumOfRevenue);
        if(sumOfRevenue<0)sumOfRevenue=0;
        totalRevenue.setText(String.valueOf(sumOfRevenue));
    }
    //統計數據
    public void statisticalData(ArrayList<accounting> accountingArrayList){
        //如何活動式增加類別名稱
        lumpSum = new HashMap<String, String>();
        percnet = new HashMap<String, String>();
        Set<String> words = new LinkedHashSet<>(); //保證裡面元素存放的順序與新增時相同。最適合拿來存放由資料庫中存取的資料集。

        //總和
        int sumOfDiet = 0;
        int sumOfTransportation = 0;
        int sumOfMedicine = 0;
        int sumOfShopping = 0;
        int totalHouseholdMoney = 0;
        int totalEntertainmentMoney = 0;
        int sumOfOtherMoney = 0;
        int income = 0;

         for (int index=0; index<accountingArrayList.size();index++){
             String need = accountingArrayList.get(index).getNeed();
             words.add(need);
             switch (need){
                 case "飲食":
                     String moneyOfDiet = accountingArrayList.get(index).getMoney();
                     sumOfDiet = sumOfDiet + Integer.parseInt(moneyOfDiet);
                     break;
                 case "交通":
                     String moneyOfTransportation = accountingArrayList.get(index).getMoney();
                     sumOfTransportation = sumOfTransportation + Integer.parseInt(moneyOfTransportation);
                     break;
                 case "醫藥":
                     String moneyOfMedicine = accountingArrayList.get(index).getMoney();
                     sumOfMedicine = sumOfMedicine + Integer.parseInt(moneyOfMedicine);
                     break;
                 case "購物":
                     String moneyOfShopping = accountingArrayList.get(index).getMoney();
                     sumOfShopping = sumOfShopping + Integer.parseInt(moneyOfShopping);
                     break;
                 case "居家":
                     String moneyAtHousehold = accountingArrayList.get(index).getMoney();
                     totalHouseholdMoney = totalHouseholdMoney + Integer.parseInt(moneyAtHousehold);
                     break;
                 case "娛樂":
                     String entertainmentMoney = accountingArrayList.get(index).getMoney();
                     totalEntertainmentMoney = totalEntertainmentMoney + Integer.parseInt(entertainmentMoney);
                     break;
                 case "其他":
                     String otherMoney = accountingArrayList.get(index).getMoney();
                     sumOfOtherMoney = sumOfOtherMoney + Integer.parseInt(otherMoney);
                     break;
                 case  "收入":
                     String incomeMoney = accountingArrayList.get(index).getMoney();
                     income = income + Integer.parseInt(incomeMoney);
                     break;
             }
         }

         //總金額
         lumpSum.put("飲食",String.valueOf(sumOfDiet));
         lumpSum.put("交通",String.valueOf(sumOfTransportation));
         lumpSum.put("醫藥",String.valueOf(sumOfMedicine));
         lumpSum.put("購物",String.valueOf(sumOfShopping));
         lumpSum.put("居家",String.valueOf(totalHouseholdMoney));
         lumpSum.put("娛樂",String.valueOf(totalEntertainmentMoney));
         lumpSum.put("其他",String.valueOf(sumOfOtherMoney));
         lumpSum.put("收入",String.valueOf(income));

         //佔比數據
         percnet.put("飲食",percnet(sumOfDiet,sumOfExpenses));
         percnet.put("交通",percnet(sumOfTransportation,sumOfExpenses));
         percnet.put("醫藥",percnet(sumOfMedicine,sumOfExpenses));
         percnet.put("購物",percnet(sumOfShopping,sumOfExpenses));
         percnet.put("居家",percnet(totalHouseholdMoney,sumOfExpenses));
         percnet.put("娛樂",percnet(totalEntertainmentMoney,sumOfExpenses));
         percnet.put("其他",percnet(sumOfOtherMoney,sumOfExpenses));

         percnet.put("收入",percnet(income,sumOfRevenue));

         statisticalDataAdapter statisticalDataAdapter = new statisticalDataAdapter(ledgerFragment.this,lumpSum,percnet,words);
         statistics.setHasFixedSize(true);
         statistics.setAdapter(statisticalDataAdapter);
         GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
         statistics.setLayoutManager(layoutManager);
         //System.out.println("字 "+"有值");
    }
    //計算百分比值
    public String percnet(double quantity,double total){
        double doubl = quantity/total;
        DecimalFormat nf = (DecimalFormat) NumberFormat.getPercentInstance();
        nf.applyPattern("0%"); //00表示小數點2位
        nf.setMaximumFractionDigits(2); //2表示精確到小數點後2位
        return nf.format(doubl);
    }
    // LocalDate或者LocalDateTime判断是否在本月之内
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isInThisMonth(LocalDateTime time){
        LocalDate localDate = time.toLocalDate();
        LocalDate now = LocalDate.now();
        return localDate.isAfter(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())) &&
                localDate.isBefore(now.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
    }

    //取得帳戶名稱
    public void getAccountCategory(){
        db.collection("帳戶名稱").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    addAccountCategory.add(doc.toObject(newAccount.class));
                }
                getAccountCategoryData(addAccountCategory);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("字 "+"getAccountCategory "+e);
            }
        });
    }
    public void getAccountCategoryData(ArrayList<newAccount>addAccountCategory){
        for (int index = 0; index<addAccountCategory.size();index++){
            String AccountCategory = addAccountCategory.get(index).getAccountName();
            AccountCategoryList.add(AccountCategory);
        }
        AccountCategoryList.add("全部明細");
        getAccountCategoryDataToSpenner(AccountCategoryList);
    }
    public void getAccountCategoryDataToSpenner(List<String> AccountCategoryList){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), R.layout.myspinner, AccountCategoryList);
        arrayAdapter.setDropDownViewResource(R.layout.myspinner);
        accountNameSpinner.setAdapter(arrayAdapter);
        String accountPosition = getCollectionPath();
        int position = arrayAdapter.getPosition(accountPosition);
        accountNameSpinner.setSelection(position,false);//預設選項
        accountNameSpinner.setOnItemSelectedListener(spnOnItemSelected);
        collectionPath = accountPosition;
        dataSelect();
    }
    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String getSelectedPosition = String.valueOf(position);
            String getItemSelected = parent.getItemAtPosition(position).toString();
            collectionPath = getItemSelected;
            saveCollectionPath(collectionPath);
            if(collectionPath.matches("全部明細")){
                getAllAccountNameDetails(position);
            }
            dataSelect();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            System.out.println("字 "+"on Nothing Selected");
        }
    };
    public void saveCollectionPath(String account){
        sp = getContext().getSharedPreferences("Account",Context.MODE_PRIVATE);
        sp.edit().putString("account", account).apply();
    }
    public String getCollectionPath(){
        sp = getContext().getSharedPreferences("Account",Context.MODE_PRIVATE);
        String accountPosition = sp.getString("account","帳戶名稱");
        return accountPosition;
    }
    public void getAllAccountNameDetails(int position){
        List<String> getAllAccountNameDetalsList = new ArrayList<>();
        getAllAccountNameDetalsList.addAll(AccountCategoryList);
        getAllAccountNameDetalsList.remove(position);
        //System.out.println("字 "+"全部明細 "+getAllAccountNameDetalsList+" "+position);
        for(String AllAccountNameDeta : getAllAccountNameDetalsList){
            db.collection(AllAccountNameDeta).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc : task.getResult()){
                        accountingArrayList.add(doc.toObject(accounting.class));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("字 "+"getAllAccountNameDetails "+e);
                }
            });
        }
    }
}