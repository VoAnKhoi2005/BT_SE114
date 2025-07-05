package com.example.bt4_23520790;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bt4_23520790.databinding.FragmentEmployeeDetailBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmployeeDetail extends Fragment {
    private FragmentEmployeeDetailBinding binding;
    private Employee originalEmployee;
    private boolean updateAttempted = false;
    private boolean updateSucceeded = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEmployeeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey("employee_id")) {
            int id = args.getInt("employee_id");
            loadEmployeeById(id);
        }
    }

    private void loadEmployeeById(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://blackntt.net:88")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        service.getEmployeeById(id).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalEmployee = response.body();
                    showEmployee(originalEmployee);
                } else {
                    Log.e("API", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.e("API", "Failure: " + t.getMessage());
            }
        });
    }

    private void showEmployee(Employee emp) {
        binding.MNVTextbox.setText(String.valueOf(emp.getId()));
        binding.NameTextbox.setText(emp.getName());
        binding.AgeTextbox.setText(String.valueOf(emp.getAge()));
        binding.SalaryTextbox.setText(String.valueOf(emp.getSalary()));

        if (emp.getProfileImage() != null && !emp.getProfileImage().isEmpty()) {
            Glide.with(requireContext())
                    .load(emp.getProfileImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imageView);
        }
    }

    @Override
    public void onDestroyView() {
        if (!updateAttempted) {
            attemptUpdate();
        }

        if (!updateSucceeded && isVisible() && requireActivity() != null) {
            revertAndWarn();
        }

        super.onDestroyView();
        binding = null;
    }

    private void attemptUpdate() {
        if (originalEmployee == null) return;

        updateAttempted = true;

        Employee updated = new Employee();
        updated.setId(originalEmployee.getId());
        updated.setName(binding.NameTextbox.getText().toString());
        updated.setAge(binding.AgeTextbox.getText().toString());
        updated.setSalary(binding.SalaryTextbox.getText().toString());
        updated.setProfileImage(originalEmployee.getProfileImage());

        if (updated.equals(originalEmployee)) {
            updateSucceeded = true;
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://blackntt.net:88")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        service.updateEmployee(updated.getId(), updated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                updateSucceeded = response.isSuccessful();
                if (!updateSucceeded) revertAndWarn();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                updateSucceeded = false;
                revertAndWarn();
            }
        });
    }

    private void revertAndWarn() {
        if (originalEmployee == null || binding == null) return;

        requireActivity().runOnUiThread(() -> {
            showEmployee(originalEmployee);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Lỗi cập nhật")
                    .setMessage("Không thể cập nhật thông tin. Dữ liệu đã được khôi phục.")
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
        });
    }
}
