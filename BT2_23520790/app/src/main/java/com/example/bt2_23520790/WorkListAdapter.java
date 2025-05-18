package com.example.bt2_23520790;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bt2_23520790.domain.Work;

import java.util.List;

public class WorkListAdapter extends ArrayAdapter<Work> {
    int resource;
    private List<Work> Works;
    public WorkListAdapter(@NonNull Context context, int resource, @NonNull List<Work> works) {
        super(context, resource, works);
        this.resource = resource;
        this.Works = works;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View v = convertView;

        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource,null);
        }
        Work work = getItem(position);
        if (work != null){
            TextView titleTV = v.findViewById(R.id.titleText);

        }
        return v;
    }
}
