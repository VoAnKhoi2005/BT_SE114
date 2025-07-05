package com.example.bt4_23520790;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bt4_23520790.databinding.FragmentEmployeeListBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmployeeList extends Fragment {
    private FragmentEmployeeListBinding binding;
    private EmployeeAdapter adapter;
    private final List<Employee> employeeList = new ArrayList<>();
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMenu();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmployeeAdapter(requireContext(), employeeList, employee -> {
            Bundle bundle = new Bundle();
            bundle.putString("employee_id", employee.getId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_EmployeeList_to_EmployeeDetail, bundle);
        });
        binding.recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://blackntt.net:88")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Load initial data
        loadEmployees();

        // Listen for result after adding a new employee
        getParentFragmentManager().setFragmentResultListener(
                "employee_add_result",
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean shouldRefresh = result.getBoolean("refresh", false);
                    if (shouldRefresh) {
                        loadEmployees();
                    }
                });
    }

    private void loadEmployees() {
        apiService.getAll().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    employeeList.clear();
                    employeeList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.e("API", "Failure: " + t.getMessage());
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_add) {
                    // Navigate to AddEmployee
                    NavHostFragment.findNavController(EmployeeList.this)
                            .navigate(R.id.action_EmployeeList_to_AddEmployeeFragment);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    deleteSelectedEmployees();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void deleteSelectedEmployees() {
        List<Employee> selected = adapter.getSelectedEmployees();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "No employees selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Employee emp : selected) {
            apiService.deleteEmployeeById(emp.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        employeeList.remove(emp);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API", "Delete failed: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
