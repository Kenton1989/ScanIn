package com.example.scanln;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class AdminViewModel extends ViewModel {
    MutableLiveData<String> user=new MutableLiveData<>();
    MutableLiveData<String> pwd=new MutableLiveData<>();
    MutableLiveData<JSONObject> auth=new MutableLiveData<>();
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

    public void setAuth(JSONObject auth){
        this.auth.setValue(auth);
    }

    public JSONObject getAuth(){
        return auth.getValue();
    }
}
