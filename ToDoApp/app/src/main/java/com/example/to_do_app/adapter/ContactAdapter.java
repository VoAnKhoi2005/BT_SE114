package com.example.to_do_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.to_do_app.R;
import com.example.to_do_app.domain.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<Contact> contacts;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public ContactViewHolder(View itemView, OnItemLongClickListener longClickListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view, longClickListener);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.name + " (" + contact.phone + ")");
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void addContact(Contact contact) {
        for (Contact c : contacts) {
            if (c.phone.equals(contact.phone)) return;
        }
        contacts.add(contact);
        notifyItemInserted(contacts.size() - 1);
    }
}

