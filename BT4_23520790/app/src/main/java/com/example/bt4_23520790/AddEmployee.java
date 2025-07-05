package com.example.bt4_23520790;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bt4_23520790.databinding.FragmentAddEmployeeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEmployee extends Fragment {
    private FragmentAddEmployeeBinding binding;
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEmployeeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://blackntt.net:88")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.inputName.getText().toString().trim();
            String age = binding.inputAge.getText().toString().trim();
            String salary = binding.inputSalary.getText().toString().trim();
            String image = binding.inputImage.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(salary)) {
                Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            Employee newEmp = new Employee();
            newEmp.setName(name);
            newEmp.setAge(age);
            newEmp.setSalary(salary);
            newEmp.setProfileImage(image);

            apiService.createEmployee(newEmp).enqueue(new Callback<Employee>() {
                @Override
                public void onResponse(Call<Employee> call, Response<Employee> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Employee added", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddEmployee.this).popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Add failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}