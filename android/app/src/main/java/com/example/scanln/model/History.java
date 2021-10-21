package com.example.scanln.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class History {
    private int id;
    private String pid;
    private String name;
    private int sid;
    private String sessionName;
    private String time;
    private boolean isIn;

    public int getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public int getSid() {
        return sid;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getTime() {
        return time;
    }

    public boolean isIn() {
        return isIn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIn(boolean in) {
        isIn = in;
    }

    public History(int id, String pid, String name, int sid, String sessionName, String time, boolean isIn) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.sid = sid;
        this.sessionName = sessionName;
        this.time = time;
        this.isIn = isIn;
    }

    public History(JSONObject json) throws JSONException {
        this.id=json.getInt("id");
        this.pid=json.getString("pid");
        this.name=json.getString("attendee_name");
        this.sid=json.getInt("sid");
        this.sessionName=json.getString("session_name");
        String iso_time=json.getString("time");
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        sdfOutput.setTimeZone(TimeZone.getTimeZone("CTT"));
        try{
            Date date = sdfInput.parse(iso_time);
            this.time=sdfOutput.format(date);
        }catch(Exception e){
            System.out.println(e.toString());

        }
        this.isIn=json.getBoolean("is_in");
    }
}
