package com.example.scanln;

import android.content.Context;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.scanln.model.Auth;
import org.json.JSONObject;
import java.util.Map;

public class VRequestQueue {
    private String url="http://104.248.151.223:3002/cz3002/";
    public static final String LOGIN="login";
    public static final String GET_VALID_HISTORY_PARAM="get_valid_history_param";
    public static final String GET_HISTORY="get_history";
    public static final String GET_ATTENDEES="get_attendees";
    public static final String ADD_SESSION="add_session";
    public static final String REGISTER_USER="register_user";
    public static final String RECOGNIZE_FACE="recognize_face";
    public static final String GET_LAST_HISTORY="get_last_history";
    public static final String CHECK_IN_OUT="check_in_out";
    public static final String RESULT_PARAM="success";
    public static final String RETURN="return";
    public static final String USERNAME_FIELD="username";
    public static final String PWD_FIELD="password";
    public static final String DETAIL_FIELD="details";
    public static final String REGISTER_PID_FIELD="pid";
    public static final String REGISTER_NAME_FIELD="name";
    public static final String REGISTER_PWD_FIELD="password";
    public static final String REGISTER_IMG_FIELD="front_face";
    public static final String CHECKIN_FACE_FIELD="face";
    public static final String CHECKIN_WANT_SESSION_FIELD="want_session";
    public static final String AUTHENTICATION_OBJECT="auth";

    private static VRequestQueue queue;
    private RequestQueue requestQueue;
    private static Context context;
    private VRequestQueue(Context context){
        this.context=context;
        this.requestQueue= getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue==null){
            return Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VRequestQueue getInstance(Context context){
        if(queue==null) queue=new VRequestQueue(context);
        return queue;
    }

    public void createRequest(String option, Map<String,Object> params,
                              Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        JSONObject post;
        String name="";
        String pwd="";
        if(!option.equals(LOGIN)){
            name=(String)params.getOrDefault("username","");
            pwd=(String)params.getOrDefault("password","");
            params.remove("username");
            params.remove("password");
        }
        post=newPostJSON(name,pwd,option,params);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url,post,listener,errorListener);
        getRequestQueue().add(request);
    }

    public JSONObject newPostJSON(String name,String pwd, String operation, Map<String,Object> params){
        JSONObject json=new JSONObject();
        try{
            JSONObject auth=new JSONObject();
            auth.put("username",name);
            auth.put("hashed_password",pwd);
            json.put("operation",operation);
            JSONObject param=new JSONObject();
            json.put("param",new JSONObject(params));
        }catch (Exception e){
            Log.e("form json post object",e.toString());
        }
        return json;
    }

    public JSONObject newPostJson(Map<String,Object> params, String operation, Auth auth){
        JSONObject jsonAuth=auth.toJSON();
        JSONObject json=new JSONObject();
        try{
            json.put("operation",operation);
            json.put("param",new JSONObject(params));
            json.put("auth",jsonAuth);
        }catch (Exception e){
            Log.e("form json post object",e.toString());
        }
        return json;
    }

    public JSONObject newPostJson(Map<String,Object> params, String operation, JSONObject auth){
        JSONObject json=new JSONObject();
        try{
            json.put("operation",operation);
            json.put("param",new JSONObject(params));
            json.put("auth",auth);
        }catch (Exception e){
            Log.e("form json post object",e.toString());
        }
        return json;
    }

    public void createRequest(Map<String,Object> params, String operation,Auth auth,
                              Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        JSONObject json=newPostJson(params,operation,auth);
        System.out.println(json);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url,json,listener,errorListener);
        getRequestQueue().add(request);
    }

    public void createRequest(Map<String,Object> params, String operation,JSONObject auth,
                              Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        JSONObject json=newPostJson(params,operation,auth);
        System.out.println(json);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url,json,listener,errorListener);
        getRequestQueue().add(request);
    }
}
