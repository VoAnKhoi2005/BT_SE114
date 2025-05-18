package com.example.bt2_23520790;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bt2_23520790.databinding.FragmentTodolistBinding;
import com.example.bt2_23520790.domain.Work;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoListFragment extends Fragment {

    private FragmentTodolistBinding binding;
    private List<Work> WorkList = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTodolistBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoadData();

        WorkListAdapter adapter = new WorkListAdapter(requireContext(), R.layout.item_work, WorkList);
        binding.todolist.setAdapter(adapter);
        binding.todolist.setOnItemClickListener((parent, v, position, id) -> {
            Work clickedWork = (Work) parent.getItemAtPosition(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("work", clickedWork);

            NavHostFragment.findNavController(ToDoListFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
        });
    }

    private void LoadData(){
        WorkList.clear();

        Work w1 = new Work();
        w1.Title = "Do homework";
        w1.DeadLine = new Date();
        w1.Status = false;
        WorkList.add(w1);

        Work w2 = new Work();
        w2.Title = "Go fishing";
        w2.DeadLine = new Date();
        w2.Status = true;
        WorkList.add(w2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}