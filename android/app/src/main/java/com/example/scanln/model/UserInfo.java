package com.example.scanln.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
    private String pid;
    private String name;

    public UserInfo(String pid,String name){
        this.pid=pid;
        this.name=name;
    }

    public UserInfo(JSONObject json) throws JSONException {
        this.name=json.getString("name");
        this.pid=json.getString("pid");
    }
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString(){
        return String.format("%10s %10s",name,pid);
    }
}
