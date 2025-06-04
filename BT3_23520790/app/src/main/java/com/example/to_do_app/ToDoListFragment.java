package com.example.to_do_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.to_do_app.databinding.FragmentTodolistBinding;
import com.example.to_do_app.domain.Work;
import com.example.to_do_app.helper.DBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoListFragment extends Fragment {

    private FragmentTodolistBinding binding;
    private List<Work> WorkList = new ArrayList<>();
    private WorkListAdapter Adapter;
    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(requireContext());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTodolistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Adapter = new WorkListAdapter(requireContext(), R.layout.item_work, WorkList);
        binding.todolist.setAdapter(Adapter);

        LoadDataFromDB();

        // Click to view detail
        binding.todolist.setOnItemClickListener((parent, v, position, id) -> {
            Work clickedWork = (Work) parent.getItemAtPosition(position);
            Bundle bundle = new Bundle();
            bundle.putString("workId", clickedWork.getId());

            NavHostFragment.findNavController(ToDoListFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
        });

        // Long press to delete
        binding.todolist.setOnItemLongClickListener((parent, v, position, id) -> {
            showPopup(v, position);
            return true;
        });

        // Receive result from detail fragment
        getParentFragmentManager().setFragmentResultListener("workUpdateRequest", this,
                (requestKey, bundle) -> {
                    String workId = bundle.getString("workId");
                    boolean isWorkEmpty = bundle.getBoolean("isWorkEmpty");

                    Work updatedWork = dbHelper.getById(workId);
                    int index = -1;
                    for (int i = 0; i < WorkList.size(); i++) {
                        if (WorkList.get(i).getId().equals(workId)) {
                            index = i;
                            break;
                        }
                    }

                    if (index != -1) {
                        if (isWorkEmpty) {
                            dbHelper.delete(workId);
                            WorkList.remove(index);
                            Toast.makeText(requireContext(), "Work deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            WorkList.set(index, updatedWork);
                        }
                    } else {
                        if (!isWorkEmpty && updatedWork != null) {
                            WorkList.add(updatedWork);
                        } else {
                            Toast.makeText(requireContext(), "Empty work discarded", Toast.LENGTH_SHORT).show();
                        }
                    }

                    Adapter.notifyDataSetChanged();
                });

        // FAB to add new
        binding.fab.setOnClickListener(view1 -> {
            Work newWork = new Work("", "", new Date(), false);
            dbHelper.addNew(newWork);

            Bundle bundle = new Bundle();
            bundle.putString("workId", newWork.getId());

            NavHostFragment.findNavController(ToDoListFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
        });
    }

    private void LoadDataFromDB() {
        WorkList.clear();
        WorkList.addAll(dbHelper.loadAll());
    }

    private void showPopup(View anchorView, int position) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.getMenuInflater().inflate(R.menu.item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                dbHelper.delete(WorkList.get(position).getId());
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
