package com.example.to_do_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.to_do_app.R;
import com.example.to_do_app.databinding.ItemWorkBinding;
import com.example.to_do_app.domain.Work;

import java.text.SimpleDateFormat;
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
        ItemWorkBinding binding;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            binding = ItemWorkBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemWorkBinding) convertView.getTag();
        }

        Work work = getItem(position);
        if (work != null) {
            if (work.Title == null || work.Title.isEmpty())
                binding.titleText.setText("{No title}");
            else
                binding.titleText.setText(work.Title);

            if (work.DeadLine != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf.format(work.DeadLine);
                binding.deadLineText.setText("Deadline:" + formattedDate);
            }
            else
                binding.deadLineText.setText("Deadline: no deadline");

            binding.completeCheckBox.setChecked(work.Status);
            binding.completeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> work.Status = isChecked);

            if (work.Selected) {
                binding.rootLayout.setBackgroundResource(R.drawable.selected_border);
            } else {
                binding.rootLayout.setBackgroundResource(R.drawable.default_border);
            }
        }

        return convertView;
    }
}
