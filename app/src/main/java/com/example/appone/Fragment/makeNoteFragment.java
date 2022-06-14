package com.example.appone.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appone.MainActivity;
import com.example.appone.Modle.accounting;
import com.example.appone.Modle.newAccount;
import com.example.appone.R;
import com.example.appone.RecyclerView.NoteAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class makeNoteFragment extends Fragment {
    int uid = 0;
    String collectionPath;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Button newButton;
    Spinner expenditure,accountNameSpinner;
    LinearLayout diet,entertainment,shopping,supplies,other,transportation,medicine,income;
    TextView need;
    Button data;
    EditText money,chat;
    int mYear,mMonth,mDay;
    ArrayList<newAccount>addAccountCategory = new ArrayList<newAccount>();
    List<String> AccountCategoryList = new ArrayList<>();
    String format = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_note_fragment, container, false);
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

        need = view.findViewById(R.id.need);
        money = view.findViewById(R.id.money);
        chat = view.findViewById(R.id.chat);
        expenditure = view.findViewById(R.id.expenditure);
        accountNameSpinner = view.findViewById(R.id.accountName);
        getAccountCategory();
        //FragmentManager fm = getActivity().getSupportFragmentManager();
        //fm.beginTransaction().replace(R.id.frameLayout,new makeNoteFragment()).commit();


        diet = view.findViewById(R.id.diet);
        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    diet.setBackgroundResource(R.drawable.ripple);
                }else{
                    diet.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("飲食");
            }
        });

        transportation = view.findViewById(R.id.transportation);
        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    transportation.setBackgroundResource(R.drawable.ripple);
                }else{
                    transportation.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("交通");
            }
        });

        medicine = view.findViewById(R.id.medicine);
        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    medicine.setBackgroundResource(R.drawable.ripple);
                }else{
                    medicine.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("醫藥");
            }
        });

        entertainment = view.findViewById(R.id.entertainment);
        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entertainment.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.scale));
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("娛樂");
            }
        });

        shopping = view.findViewById(R.id.shopping);
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    shopping.setBackgroundResource(R.drawable.ripple);
                }else{
                    shopping.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("購物");
            }
        });

        supplies = view.findViewById(R.id.supplies);
        supplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    supplies.setBackgroundResource(R.drawable.ripple);
                }else{
                    supplies.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("居家");
            }
        });

        other = view.findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    other.setBackgroundResource(R.drawable.ripple);
                }else{
                    other.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("支出");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("其他");
            }
        });

        income = view.findViewById(R.id.income);
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    income.setBackgroundResource(R.drawable.ripple);
                }else{
                    income.setBackgroundResource(android.R.drawable.btn_default);
                }
                List<String> footballPlayers = new ArrayList<>();
                footballPlayers.add("收入");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, footballPlayers);
                expenditure.setAdapter(arrayAdapter);
                need.setText("收入");
            }
        });

        data = view.findViewById(R.id.data);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        data.setText(format);
                    }
                }, mYear, mMonth, mDay);
                pickerDialog.show();
            }
        });

        newButton = view.findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serialNumber = appendZero4(String.valueOf(uid));
                String needString = need.getText().toString();
                String moneyString = money.getText().toString();
                String chatString = chat.getText().toString();
                String dataString = data.getText().toString();
                String expenditureString = expenditure.getSelectedItem().toString();

                     if(chatString.matches("^[\u4e00-\u9fa5\\w\\.-@]+$")&&
                        moneyString.matches("^[0-9]+$")&&
                        dataString.matches("^[0-9]{4}+\\-+[0-9]{1,2}+\\-+[0-9]{1,2}$")){
                    HashMap<String,String> row = new HashMap<>();
                    row.put("serialNumber",serialNumber);
                    row.put("expenditure",expenditureString);
                    row.put("money",moneyString);
                    row.put("date",dataString);
                    row.put("chat",chatString);
                    row.put("need",needString);
                    row.put("newAccountName",collectionPath);

                    firebaseFirestore.collection(collectionPath).document(serialNumber).set(row);
                    uid+=1;

                    need.setText("");
                    money.setText("");
                    chat.setText("");
                    data.setText("選擇日期");
                    new AlertDialog.Builder(getContext())
                            .setTitle("儲存").setNegativeButton("確定",null).show();
                }else{
                     new AlertDialog.Builder(getContext())
                            .setTitle("要輸入東西喔！").setNegativeButton("確定",null).show();
                }
            }
        });
        return view;
    }

    // 日期轉字串格式
    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
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

    private void getSerialNumber(){
        firebaseFirestore.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                uid = 0;
                for(DocumentSnapshot doc : task.getResult()){
                    doc.getString("serialNumber");
                    int serialNumber = Integer.parseInt(doc.getString("serialNumber"));
                    uid = serialNumber;
                    uid+=1;
                    //System.out.println("字 "+uid+" "+collectionPath);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //取得帳戶名稱
    public void getAccountCategory(){
        firebaseFirestore.collection("帳戶名稱").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        getAccountCategoryDataToSpenner(AccountCategoryList);
    }
    public void getAccountCategoryDataToSpenner(List<String> AccountCategoryList){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), R.layout.myspinner, AccountCategoryList);
        arrayAdapter.setDropDownViewResource(R.layout.myspinner);//改成使用有選紐的
        accountNameSpinner.setAdapter(arrayAdapter);
        accountNameSpinner.setOnItemSelectedListener(spnOnItemSelected);
        collectionPath = accountNameSpinner.getSelectedItem().toString();
    }
    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String getSelectedPosition = String.valueOf(position);
            String getItemSelected = parent.getItemAtPosition(position).toString();
            collectionPath = getItemSelected;
            getSerialNumber();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            System.out.println("字 "+"on Nothing Selected");
        }
    };
}