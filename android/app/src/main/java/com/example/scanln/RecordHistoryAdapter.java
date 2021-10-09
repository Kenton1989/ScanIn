package com.example.scanln;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.scanln.databinding.RecordListItemBinding;

import java.util.List;

public class RecordHistoryAdapter extends BaseAdapter{
    private List<Record> records;
    private Context context;


    public RecordHistoryAdapter(List<Record> records,Context context) {
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

        Record record=(Record) getItem(position);
        sid.setText(record.getSession_id());
        pid.setText(record.getStudent_id());
        time.setText(record.getTime().toString());
        return view;
    }
}
