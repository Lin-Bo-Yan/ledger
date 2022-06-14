package com.example.appone.Modle;

public class newAccount {
    String accountName,remark;

    //一定要有無傳值建構式
    public newAccount(){}

    public newAccount(String accountName, String remark) {
        this.accountName = accountName;
        this.remark = remark;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getRemark() {
        return remark;
    }
}
