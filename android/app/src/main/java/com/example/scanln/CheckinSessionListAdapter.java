package com.example.scanln;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.scanln.model.History;
import com.example.scanln.model.SessionBrief;

import java.util.List;

public class CheckinSessionListAdapter extends BaseAdapter{
    private List<SessionBrief> sessions;
    private Context context;

    public CheckinSessionListAdapter(List<SessionBrief> sessions, Context context){
        this.sessions=sessions;
        this.context=context;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int position) {
        return sessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(context,R.layout.session_list_item,null);
        TextView name=view.findViewById(R.id.sidtxt);
        TextView num=view.findViewById(R.id.snametxt);
        SessionBrief session=(SessionBrief) getItem(position);
        name.setText(session.getSname());
        num.setText(Integer.toString(session.getSid()));
        return view;
    }
}
