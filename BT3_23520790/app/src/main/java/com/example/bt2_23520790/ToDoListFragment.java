package com.example.bt2_23520790;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;
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
    private WorkListAdapter Adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LoadData();
    }

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

        Adapter = new WorkListAdapter(requireContext(), R.layout.item_work, WorkList);
        binding.todolist.setAdapter(Adapter);

        //Click to load detail
        binding.todolist.setOnItemClickListener((parent, v, position, id) -> {
            Work clickedWork = (Work) parent.getItemAtPosition(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("work", clickedWork);

            NavHostFragment.findNavController(ToDoListFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
        });

        //Long press for delete
        binding.todolist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopup(view, position);
                return true;
            }
        });

        //Receive update from detail
        getParentFragmentManager().setFragmentResultListener("workUpdateRequest", this,
                (requestKey, bundle) -> {
                    Work updatedWork = (Work) bundle.getSerializable("updatedWork");
                    boolean isWorkEmpty = bundle.getBoolean("isWorkEmpty");

                    int index = WorkList.indexOf(updatedWork);
                    if (index != -1) {
                        if (isWorkEmpty) {
                            WorkList.remove(index);
                            Toast.makeText(requireContext(), "Empty work discard", Toast.LENGTH_SHORT).show();
                        }
                        else
                            WorkList.set(index, updatedWork);
                    } else {
                        if (!isWorkEmpty)
                            WorkList.add(updatedWork);
                        else
                            Toast.makeText(requireContext(), "Empty work discard", Toast.LENGTH_SHORT).show();
                    }

                    ((WorkListAdapter) binding.todolist.getAdapter()).notifyDataSetChanged();
                });


        //Add button
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Work newWork = new Work("", "", null, false);
                bundle.putSerializable("work", newWork);

                NavHostFragment.findNavController(ToDoListFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            }
        });
    }

    private void LoadData() {
        WorkList.clear();

        Work w1 = new Work("Do homework", "", new Date(), false);
        WorkList.add(w1);

        Work w2 = new Work("Go fishing", "", new Date(), true);
        WorkList.add(w2);
    }

    private void showPopup(View anchorView, int position) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.getMenuInflater().inflate(R.menu.item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                WorkList.remove(position);
                Adapter.notifyDataSetChanged();
                return true;
            }
            return false;
        });

        popup.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}