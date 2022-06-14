package com.example.appone.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.appone.Modle.newAccount;
import com.example.appone.R;
import com.example.appone.RecyclerView.newAccountAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class newAccountFragment extends Fragment {
    EditText accountName,remark;
    Button newButton;
    RecyclerView addItem;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    newAccountAdapter newAccountAdapter;
    String[]words;
    static boolean notRepeating = false;
    ArrayList<newAccount>addAccountCategory = new ArrayList<newAccount>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataSelect();
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
        View view = inflater.inflate(R.layout.new_account_fragment, container, false);
        accountName = view.findViewById(R.id.accountName);
        remark = view.findViewById(R.id.remark);
        addItem = view.findViewById(R.id.addItem);

        newButton = view.findViewById(R.id.newButton);
        newButton.setOnClickListener(v -> {
            String accountNameString = accountName.getText().toString();
            String remarkString = remark.getText().toString();
            //判斷即將輸入的用戶名稱是否已經存在，如果存在就不能新建
            //先判斷輸入是否有值，條件成立再跑迴圈抓帳戶名稱是否重複
            if(accountNameString.matches("^[\u4e00-\u9fa5\\w\\.-@]+$")){

                for(String word : words) {
                    boolean wordRepeat = accountNameString.matches(word);
                    //System.out.println("字 " + accountNameString+" "+word+" "+wordRepeat);
                    if(wordRepeat){
                        notRepeating = true;
                        break;
                    }
                    if (notRepeating){break;}
                }

                if(notRepeating){
                    //System.out.println("字 "+"帳戶重複");
                    new AlertDialog.Builder(getContext())
                            .setTitle("帳戶存在！").setNegativeButton("確定",null).show();
                }else {
                    storeCollection(accountNameString,remarkString);
                    dataSelect();
                }
                notRepeating = false;

            }else {
                new AlertDialog.Builder(getContext())
                            .setTitle("要輸入東西喔！").setNegativeButton("確定",null).show();}
                    accountName.setText("");
                    remark.setText("");
        });

        return view;
    }
    //顯示data
    public void dataSelect(){
        //重新呼叫他時，要把之前的ArrayList 清除掉，否則會有舊資料出現
        addAccountCategory.clear();
        firebaseFirestore.collection("帳戶名稱").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    addAccountCategory.add(doc.toObject(newAccount.class));
                }
                showAccount(addAccountCategory);
                getAccount(addAccountCategory);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("字 "+e.getMessage());
            }
        });
    }
    //顯示data
    public void showAccount(ArrayList<newAccount>addAccountCategory){
        newAccountAdapter = new newAccountAdapter(newAccountFragment.this,addAccountCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        addItem.setHasFixedSize(true);
        addItem.setLayoutManager(layoutManager);
        addItem.setAdapter(newAccountAdapter);
    }
    //產生流水號
    public static String appendZero4(String res){
        if(res.length() ==1)
            return "000" + res;
        else if(res.length() ==2)
            return "00" + res;
        else if(res.length() ==3)
            return "0" + res;
        else
            return res;
    }
    //開個資料庫儲存collection的名稱
    public void storeCollection(String accountName, String remark){
        HashMap<String,String> row = new HashMap<>();
        row.put("accountName",accountName);
        row.put("remark",remark);
        firebaseFirestore.collection("帳戶名稱").document(accountName).set(row)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("新增成功").setNegativeButton("確定",null).show();
                    }
                });
    }
    //抓取資料庫裡面已存在的用戶名稱
    public void getAccount(ArrayList<newAccount>addAccountCategory){
        words = new String[addAccountCategory.size()];
        for (int index=0; index<addAccountCategory.size();index++){
            String Account = addAccountCategory.get(index).getAccountName();
            words[index] = Account;
        }
        //System.out.println("字 "+ Arrays.toString(words));
    }
}

/*
newButton.setOnClickListener(v -> {
            String accountNameString = accountName.getText().toString();
            String remarkString = remark.getText().toString();
            //判斷即將輸入的用戶名稱是否已經存在，如果存在就不能新建
            //先判斷輸入是否有值，條件成立再跑迴圈抓帳戶名稱是否重複
            if(accountNameString.matches("^[\u4e00-\u9fa5\\w\\.-@]+$")&&
                    remarkString.matches("^[\u4e00-\u9fa5\\w\\.-@]+$")){
                System.out.println("字 "+"if 有動作");
                for(String word : words) {
                    System.out.println("字 "+"for 有動作");
                    if(accountNameString.matches(word)){
                        new AlertDialog.Builder(getContext())
                                .setTitle("已經使用過！").setNegativeButton("確定",null).show();
                    }else{
                        System.out.println("字 "+"else 有動作");
                        //儲存進資料庫
                        String serialNumber = appendZero4(String.valueOf(uid));
                        HashMap<String,String> row = new HashMap<>();
                        row.put("serialNumber",serialNumber);
                        row.put("expenditure","");
                        row.put("money","");
                        row.put("date","");
                        row.put("chat","");
                        row.put("need","");
                        firebaseFirestore.collection(accountNameString).document(serialNumber).set(row)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        storeCollection(accountNameString,remarkString);
                                        dataSelect();
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("新增成功").setNegativeButton("確定",null).show();
                                    }
                                });
                    }

                }
            }else {
                new AlertDialog.Builder(getContext())
                            .setTitle("要輸入東西喔！").setNegativeButton("確定",null).show();}
                    accountName.setText("");
                    remark.setText("");
        });
*/