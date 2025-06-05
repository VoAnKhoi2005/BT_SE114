package com.example.to_do_app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_do_app.adapter.ContactAdapter;
import com.example.to_do_app.databinding.FragmentDetailBinding;
import com.example.to_do_app.domain.Contact;
import com.example.to_do_app.domain.Work;
import com.example.to_do_app.helper.AppDBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private Work MyWork;
    private AppDBHelper appDBHelper;
    private String[] Statuses = {"Not complete", "Completed"};

    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList = new ArrayList<>();

    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        appDBHelper = new AppDBHelper(requireContext());

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openContactPicker();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        contactRecyclerView = binding.contactRecyclerView;
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        contactAdapter = new ContactAdapter(contactList);
        contactRecyclerView.setAdapter(contactAdapter);

        //Click event
        binding.addContactButton.setOnClickListener(v -> requestContactPermissionIfNeeded());

        //Long press contact
        contactAdapter.setOnItemLongClickListener(position -> {
            Contact contact = contactList.get(position);
            showDeleteContactDialog(contact, position);
        });

        Bundle args = getArguments();
        if (args != null) {
            String workId = args.getString("workId");
            if (workId != null) {
                MyWork = appDBHelper.getWorkById(workId);
                if (MyWork != null) {
                    LoadWork(MyWork);
                }
            }
        }

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> {
                SaveAndGoBack();
                NavHostFragment.findNavController(this).popBackStack();
            });
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        SaveAndGoBack();
                        NavHostFragment.findNavController(DetailFragment.this).popBackStack();
                    }
                }
        );

        ArrayAdapter<String> adapter  = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                Statuses
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        binding.statusComboBox.setAdapter(adapter);

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

        List<Contact> contacts = appDBHelper.getContactsForWork(MyWork.getId());
        contactList.clear();
        contactList.addAll(contacts);
        contactAdapter.notifyDataSetChanged();
    }

    private void SaveAndGoBack() {
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

            appDBHelper.updateWork(MyWork);

            appDBHelper.deleteContactsForWork(MyWork.getId());
            for (Contact contact : contactList) {
                appDBHelper.addContact(contact, MyWork.getId());
            }
        } else {
            appDBHelper.deleteWork(MyWork.getId());
        }

        Bundle result = new Bundle();
        result.putString("workId", MyWork.getId());
        result.putBoolean("isWorkEmpty", isWorkEmpty);
        getParentFragmentManager().setFragmentResult("workUpdateRequest", result);
    }

    private void requestContactPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            openContactPicker();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
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

    private void openContactPicker() {
        List<Contact> contacts = new ArrayList<>();

        List<Contact> savedContacts = appDBHelper.getContactsForWork(MyWork.getId());

        Cursor cursor = requireContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            if (nameIndex == -1 || numberIndex == -1) {
                cursor.close();
                return;
            }

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(numberIndex);

                if (name != null && phone != null) {
                    boolean alreadyInDb = false;
                    for (Contact saved : savedContacts) {
                        if (saved.phone.equals(phone)) {
                            alreadyInDb = true;
                            break;
                        }
                    }

                    if (!alreadyInDb) {
                        boolean alreadyInList = false;
                        for (Contact c : contacts) {
                            if (c.phone.equals(phone)) {
                                alreadyInList = true;
                                break;
                            }
                        }
                        if (!alreadyInList) {
                            contacts.add(new Contact(name, phone));
                        }
                    }
                }
            }
            cursor.close();
        }

        List<String> contactDisplayList = new ArrayList<>();
        for (Contact c : contacts) {
            contactDisplayList.add(c.name + " (" + c.phone + ")");
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_contact_search, null);
        SearchView searchView = dialogView.findViewById(R.id.searchView);
        ListView listView = dialogView.findViewById(R.id.listViewContacts);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                contactDisplayList
        );
        listView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Select a contact")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapter.getItem(position);
            if (selected != null) {
                for (Contact c : contacts) {
                    String display = c.name + " (" + c.phone + ")";
                    if (display.equals(selected)) {
                        contactAdapter.addContact(c);
                        contactList.add(c);
                        appDBHelper.addContact(c, MyWork.getId());
                        break;
                    }
                }
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        dialog.show();
    }


    private void showDeleteContactDialog(Contact contact, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + contact.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    appDBHelper.deleteContact(contact.phone);
                    contactList.remove(position);
                    contactAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        SaveAndGoBack();
        super.onDestroyView();
        binding = null;
    }
}