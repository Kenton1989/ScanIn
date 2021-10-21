package com.example.scanln.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionBrief {
    private int sid;
    private String sname;
    private String beg_time;
    private String end_time;

    public SessionBrief(){}
    public SessionBrief(int sid, String sname, String beg_time, String end_time) {
        this.sid = sid;
        this.sname = sname;
        this.beg_time = beg_time;
        this.end_time = end_time;
    }

    public SessionBrief(JSONObject json) throws JSONException {
        this.sid = json.getInt("sid");
        this.sname = json.getString("name");
        this.beg_time = json.getString("beg_time");
        this.end_time = json.getString("end_time");
    }
    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getBeg_time() {
        return beg_time;
    }

    public void setBeg_time(String beg_time) {
        this.beg_time = beg_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    @Override
    public String toString(){
        return (sname!=null)?sname:"-";
    }
}
