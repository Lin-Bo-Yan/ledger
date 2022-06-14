package com.example.appone.Modle;

import java.util.Comparator;

public class accounting {
    String date,serialNumber,expenditure,need,chat,money,newAccountName;

    //一定要有無傳值建構式
    public accounting(){}

    public accounting(String date, String serialNumber, String expenditure, String need, String chat, String money,String newAccountName) {
        this.date = date;
        this.serialNumber = serialNumber;
        this.expenditure = expenditure;
        this.need = need;
        this.chat = chat;
        this.money = money;
        this.newAccountName = newAccountName;
    }

    public String getDate() {
        return date;
    }


    public String getSerialNumber() {
        return serialNumber;
    }


    public String getExpenditure() {
        return expenditure;
    }


    public String getNeed() {
        return need;
    }


    public String getChat() {
        return chat;
    }


    public String getMoney() {
        return money;
    }


    public String getNewAccountName(){return newAccountName;}

    public static Comparator<accounting> mydata = new Comparator<accounting>() {
        @Override
        public int compare(accounting e1, accounting e2) {

            return e2.getDate().compareTo(e1.getDate());
        }
    };
}
