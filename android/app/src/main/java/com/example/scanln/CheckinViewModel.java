package com.example.scanln;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.scanln.model.SessionBrief;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckinViewModel extends ViewModel {
    private final MutableLiveData<String> pid=new MutableLiveData<>();
    private final MutableLiveData<String> password=new MutableLiveData<>();
    private final MutableLiveData<String> pname=new MutableLiveData<>();
    private final MutableLiveData<List<SessionBrief>> records=new MutableLiveData<>();
    private final MutableLiveData<JSONObject> auth=new MutableLiveData<>();

    public void setPid(String pid){
        this.pid.setValue(pid);
    }

    public void setPassword(String password){
        this.password.setValue(password);
    }

    public void setPname(String name){
        this.pname.setValue(name);
    }

    public void setRecords(List<SessionBrief> history){
        this.records.setValue(history);
    }

    public String getPid(){
        return pid.getValue();
    }

    public String getPassword(){
        return password.getValue();
    }

    public String getPname(){
        return pname.getValue();
    }

    public List<SessionBrief> getRecords(){
        return records.getValue();
    }

    public JSONObject getAuth(){
        return auth.getValue();
    }

    public void setAuth(JSONObject auth){
        this.auth.setValue(auth);
    }
    public void clear(){
        pid.setValue("");
        password.setValue("");
        pname.setValue("");
        records.setValue(new ArrayList<>());
        auth.setValue(null);
    }
}
