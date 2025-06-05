package com.example.to_do_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.to_do_app.adapter.WorkListAdapter;
import com.example.to_do_app.databinding.FragmentTodolistBinding;
import com.example.to_do_app.domain.Work;
import com.example.to_do_app.helper.AppDBHelper;
import com.example.to_do_app.helper.WorkDBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ToDoListFragment extends Fragment {

    private FragmentTodolistBinding binding;
    private List<Work> WorkList = new ArrayList<>();
    private WorkListAdapter Adapter;
    private AppDBHelper appDBHelper;
    private Boolean SelectionMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDBHelper = new AppDBHelper(requireContext());
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

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear(); // clear old menu items
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    DeleteWorks();
                    return true;
                }
                if (menuItem.getItemId() == R.id.action_add){
                    AddNewWork();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());

        // Click to view detail
        binding.todolist.setOnItemClickListener((parent, v, position, id) -> {
            Work clickedWork = (Work) parent.getItemAtPosition(position);
            if (!SelectionMode) {
                Bundle bundle = new Bundle();
                bundle.putString("workId", clickedWork.getId());

                NavHostFragment.findNavController(ToDoListFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            } else {
                clickedWork.Selected = !clickedWork.Selected;

                if (WorkList.stream().filter(work -> work.Selected).count() <= 0)
                    exitSelectionMode();

                Adapter.notifyDataSetChanged();
            }
        });

        // Long press
        binding.todolist.setOnItemLongClickListener((parent, v, position, id) -> {
            Work clickedWork = (Work) parent.getItemAtPosition(position);
            clickedWork.Selected = true;
            enterSelectionMode();
            Adapter.notifyDataSetChanged();
            return true;
        });

        // Receive result from detail fragment
        getParentFragmentManager().setFragmentResultListener("workUpdateRequest", this,
                (requestKey, bundle) -> {
                    String workId = bundle.getString("workId");
                    boolean isWorkEmpty = bundle.getBoolean("isWorkEmpty");

                    Work updatedWork = appDBHelper.getWorkById(workId);
                    int index = -1;
                    for (int i = 0; i < WorkList.size(); i++) {
                        if (WorkList.get(i).getId().equals(workId)) {
                            index = i;
                            break;
                        }
                    }

                    if (index != -1) {
                        if (isWorkEmpty) {
                            appDBHelper.deleteWork(workId);
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
            AddNewWork();
        });
    }

    private void LoadDataFromDB() {
        WorkList.clear();
        WorkList.addAll(appDBHelper.getAllWorks());
    }

    private void AddNewWork(){
        Work newWork = new Work(UUID.randomUUID().toString(), "", "", new Date(), false);
        appDBHelper.addWork(newWork);

        Bundle bundle = new Bundle();
        bundle.putString("workId", newWork.getId());

        NavHostFragment.findNavController(ToDoListFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }

    private void DeleteWorks() {
        List<Work> worksToRemove = new ArrayList<>();

        for (Work work : WorkList) {
            if (work.Selected) {
                appDBHelper.deleteWork(work.getId());
                worksToRemove.add(work);
            }
        }

        WorkList.removeAll(worksToRemove);
        Adapter.notifyDataSetChanged();

        exitSelectionMode();
        Toast.makeText(requireContext(), worksToRemove.size() + " items deleted", Toast.LENGTH_SHORT).show();

        // Clear the back stack to ensure clean navigation
        NavHostFragment.findNavController(this).popBackStack(R.id.FirstFragment, false);
    }


    private void enterSelectionMode() {
        SelectionMode = true;
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> exitSelectionMode());
    }

    private void exitSelectionMode() {
        SelectionMode = false;

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
        toolbar.setTitle(R.string.app_name);

        for (Work work: WorkList) {
            work.Selected = false;
        }
        Adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
