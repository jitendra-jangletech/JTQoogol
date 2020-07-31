package com.jangletech.qoogol.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ContactlistItemBinding;
import com.jangletech.qoogol.model.Contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> implements Filterable {

    private List<Contacts> contactsList;
    private List<Contacts> filteredContactList;
    private OnContactItemClickListener itemClickListener;
    private ContactlistItemBinding binding;

    public ContactListAdapter(List<Contacts> contactsList, OnContactItemClickListener itemClickListener) {
        this.contactsList = contactsList;
        this.filteredContactList = contactsList;
        this.itemClickListener = itemClickListener;
    }

    public void updateList (List<Contacts> contactsList) {
        this.contactsList = contactsList;
        this.filteredContactList = contactsList;
        notifyDataSetChanged();
    }

    public void updateResult(Contacts contacts, int position) {
        filteredContactList.add(position, contacts);
        notifyItemChanged(position);
    }

    public void clearAllCheckbox(List<Contacts> contactsList) {
        filteredContactList.clear();
        filteredContactList.addAll(contactsList);
        notifyDataSetChanged();
    }

    public List<Contacts> getContactsList() {
        return filteredContactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.contactlist_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       try {
           Contacts contacts = filteredContactList.get(position);

           holder.binding.setContacts(contacts);

           holder.binding.btnInvite.setOnClickListener(view -> {
               itemClickListener.buttonClicked(contacts, position, false);
           });

           holder.binding.inviteCheckBox.setOnClickListener(view -> {
               itemClickListener.buttonClicked(contacts, position, true);
           });

           if (position == filteredContactList.size() - 1) {
               itemClickListener.onBottomReached(filteredContactList.size());
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public int getItemCount() {
        return filteredContactList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredContactList = contactsList;
                } else if (contactsList != null && contactsList.size() > 0) {
                    List<Contacts> contactList = new ArrayList<>();
                    for (Contacts contacts : contactsList) {
                        // here we are looking for firstName or lastName
                        if (contacts.getUserName().toLowerCase().contains(charString.toLowerCase()) ||
                                contacts.getMobileNum().toLowerCase().contains(charString.toLowerCase())) {
                            contactList.add(contacts);
                        }
                    }
                    filteredContactList = contactList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredContactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredContactList = (List<Contacts>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void removeFromList(Set<Contacts> contactsSet) {
        contactsList.removeAll(contactsSet);
        filteredContactList.removeAll(contactsSet);
        notifyDataSetChanged();
    }

    public interface OnContactItemClickListener {
        void buttonClicked(Contacts contacts, int position, boolean forInvite);
        void onBottomReached(int size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ContactlistItemBinding binding;

        public ViewHolder(ContactlistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
