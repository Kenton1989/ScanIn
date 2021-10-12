package com.example.scanln;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminViewModel extends ViewModel {
    MutableLiveData<String> user=new MutableLiveData<>();
    MutableLiveData<String> pwd=new MutableLiveData<>();

    public void setUser(String user){
        this.user.setValue(user);
    }

    public void setPwd(String pwd){
        this.pwd.setValue(pwd);
    }

    public String getUser(){
        return user.getValue();
    }

    public String getPwd(){
        return pwd.getValue();
    }
}
