package com.example.scanln.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Auth {
    public String username;
    public String password;

    public Auth(String username,String password){
        this.password=password;
        this.username=username;
    }

    public JSONObject toJSON(){
        JSONObject json=new JSONObject();
        try {
            json.put("username",username);
            json.put("hashed_password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
