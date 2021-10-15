package com.example.scanln;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.scanln.model.History;
import com.example.scanln.model.Record;

import java.util.List;

public class HistoryAdapter extends BaseAdapter{
    private List<History> records;
    private Context context;


    public HistoryAdapter(List<History> records, Context context) {
        this.records = records;
        this.context=context;

    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(context,R.layout.record_list_item,null);
        TextView sid=view.findViewById(R.id.sid);
        TextView pid=view.findViewById(R.id.pid);
        TextView time=view.findViewById(R.id.time);

        History record=(History) getItem(position);
        sid.setText(record.getSid());
        pid.setText(record.getPid());
        time.setText(record.getTime());
        return view;
    }
}
