package com.example.bt2_23520790;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bt2_23520790.databinding.FragmentDetailBinding;
import com.example.bt2_23520790.domain.Work;
import com.example.bt2_23520790.helper.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private Work MyWork;
    private DBHelper dbHelper;
    private String[] Statuses = {"Not complete", "Completed"};

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(requireContext());

        // Get work ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            String workId = args.getString("workId");
            if (workId != null) {
                MyWork = dbHelper.getById(workId);
                if (MyWork != null) {
                    LoadWork(MyWork);
                }
            }
        }

        // Combo box for status
        ArrayAdapter<String> adapter  = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                Statuses
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        binding.statusComboBox.setAdapter(adapter);

        // Date picker
        binding.deadlineEditView.setOnClickListener(v -> showDatePickerDialog(binding.deadlineEditView));
    }

    private void LoadWork(Work work){
        binding.titleTextBox.setText(work.Title);

        if (work.Status)
            binding.statusComboBox.setSelection(1);
        else
            binding.statusComboBox.setSelection(0);

        if (work.DeadLine != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(work.DeadLine);
            binding.deadlineEditView.setText(formattedDate);
        } else {
            binding.deadlineEditView.setText("");
        }

        binding.descriptionEditText.setText(work.Description);
        binding.descriptionEditText.setMovementMethod(new ScrollingMovementMethod());
    }

    private void sendResultAndGoBack() {
        String title = binding.titleTextBox.getText().toString().trim();
        String description = binding.descriptionEditText.getText().toString().trim();
        String dateStr = binding.deadlineEditView.getText().toString().trim();
        boolean status = binding.statusComboBox.getSelectedItemPosition() == 1;

        Date deadline = null;
        if (!dateStr.isEmpty()) {
            try {
                deadline = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        boolean isWorkEmpty = title.isEmpty() && description.isEmpty() && deadline == null;

        if (!isWorkEmpty) {
            MyWork.Title = title;
            MyWork.Description = description;
            MyWork.DeadLine = deadline;
            MyWork.Status = status;
            dbHelper.update(MyWork);
        } else {
            dbHelper.delete(MyWork.getId());
        }

        Bundle result = new Bundle();
        result.putString("workId", MyWork.getId());
        result.putBoolean("isWorkEmpty", isWorkEmpty);
        getParentFragmentManager().setFragmentResult("workUpdateRequest", result);
    }

    private void showDatePickerDialog(final EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    targetEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    @Override
    public void onDestroyView() {
        sendResultAndGoBack();
        super.onDestroyView();
        binding = null;
    }
}
