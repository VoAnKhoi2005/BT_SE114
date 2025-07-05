package com.example.bt4_23520790;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt4_23520790.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    private final Context context;
    private final List<Employee> employeeList;
    private final List<Employee> selectedEmployees = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Employee employee);
    }

    public EmployeeAdapter(Context context, List<Employee> employeeList, OnItemClickListener listener) {
        this.context = context;
        this.employeeList = employeeList;
        this.listener = listener;
    }

    public List<Employee> getSelectedEmployees() {
        return selectedEmployees;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView idTextView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.employee_name);
            idTextView = itemView.findViewById(R.id.employee_id);
            checkBox = itemView.findViewById(R.id.select_checkbox);
        }

        public void bind(Employee employee, boolean isSelected, OnItemClickListener listener, View.OnClickListener checkboxClickListener) {
            nameTextView.setText(employee.getName());
            idTextView.setText("ID: " + employee.getId());
            checkBox.setChecked(isSelected);
            itemView.setOnClickListener(v -> listener.onItemClick(employee));
            checkBox.setOnClickListener(checkboxClickListener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        boolean isSelected = selectedEmployees.contains(employee);
        holder.bind(employee, isSelected, listener, v -> {
            if (isSelected) {
                selectedEmployees.remove(employee);
            } else {
                selectedEmployees.add(employee);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }
}

