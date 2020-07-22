package com.jangletech.qoogol.ui.usercontacts;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ContactFilterAdapter;
import com.jangletech.qoogol.adapter.ContactListAdapter;
import com.jangletech.qoogol.databinding.ContactListFragmentBinding;
import com.jangletech.qoogol.model.ContactResponse;
import com.jangletech.qoogol.model.Contacts;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.SendInviteResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.NetworkUtility;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.qoogol;

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

public class ContactListFragment extends BaseFragment implements ContactListAdapter.OnContactItemClickListener, ContactFilterAdapter.OnFilterItemClickListener {

    private static final String TAG = "ContactListFragment";

    private ContactListViewModel mViewModel;

    private ContactListFragmentBinding mBinding;
    private FragmentActivity activity;
    private ContactListAdapter mAdapter;
    private ContactFilterAdapter filterAdapter;
    private PreferenceManager mSettings;
    private UtilHelper mUtilHelper;
    private Contacts contacts;
    private int position;
    private Set<Contacts> contactsSet;
    private MenuItem inviteItem;
    private List<Contacts> contactsList;
    private List<String> filterList;
    private boolean isSendingInvite;
    private boolean isSelectedAll;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    LinearLayoutManager filterlinear;
    String letter = "";
    private boolean isContactFetched = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity = requireActivity();
        mViewModel = new ViewModelProvider(this).get(ContactListViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.contact_list_fragment, container, false);

        prepareFilterList();

        initView();

        contactsSet = new HashSet<>();

        mSettings = new PreferenceManager(activity);

        if (checkWriteExternalPermission()) {
            if (!isContactFetched) {
                getContactList();
            } else {
                isContactFetched=true;
                if (letter.equalsIgnoreCase(""))
                    CallFetchAllContactsAPI();
                else
                    callFetchAPI(letter);
            }
        } else {
            askUserPermission();
        }
        updateUI();
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    private void prepareFilterList() {
        filterList = new ArrayList<>();
        filterList.add("All");
        for (char i='A'; i<='Z'; i++)
            filterList.add(String.valueOf(i));
        setFilterRecycler("A", 0, 10);

    }

    private void setFilterRecycler(String letter, int position, int offset) {
        filterlinear = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        filterAdapter = new ContactFilterAdapter(filterList,this);
        mBinding.filterRecycler.setHasFixedSize(true);

        mBinding.filterRecycler.setLayoutManager(filterlinear);
        mBinding.filterRecycler.setAdapter(filterAdapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.contactsList.clear();
    }

    private void initView() {
        contactsList = new ArrayList<>();
        mAdapter = new ContactListAdapter(contactsList, this);
        mBinding.contactRc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.contactRc.setLayoutManager(linearLayoutManager);
        mBinding.contactRc.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_save, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setMaxWidth(Integer.MAX_VALUE);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    try {
                        mAdapter.getFilter().filter(query);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    try {
                        mAdapter.getFilter().filter(query);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                    return false;
                }
            });
        }
        inviteItem = menu.findItem(R.id.action_save);
        inviteItem.setVisible(false);
        inviteItem.setTitle(activity.getResources().getString(R.string.invite));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            callInviteAPI();
        }
        return super.onOptionsItemSelected(item);
    }

    private void callInviteAPI() {
        if (NetworkUtility.isConnected(activity)) {
            if (isSelectedAll) {
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AlertDialogStyle);
                builder.setMessage("Are you sure you want to pause the Test?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    sendInvite();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();

            } else {
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AlertDialogStyle);
                builder.setMessage("Do you want to send invite to selected contacts?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    sendInvite();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();

            }
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

    private void sendInvite() {
        if (!isSendingInvite && contactsSet.size() > 0) {
            String contactListToInvite = TextUtils.join(",", contactsSet);
            isSendingInvite = true;
            contactsSet.clear();

            Call<SendInviteResponse> call = apiService.inviteContacts(String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID)), getDeviceId(getActivity()), qoogol, contactListToInvite);
            call.enqueue(new Callback<SendInviteResponse>() {
                @Override
                public void onResponse(Call<SendInviteResponse> call, retrofit2.Response<SendInviteResponse> response) {
                    try {
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            Toast.makeText(activity, "Invitations sent successfully.", Toast.LENGTH_LONG).show();
                            getVerifyDetails(mViewModel.pageFetch,null);
                            contactsSet.clear();
                        } else {
                            contactsSet.clear();
                            Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                        isSendingInvite = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isSendingInvite = false;
                    }
                }

                @Override
                public void onFailure(Call<SendInviteResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                    isSendingInvite = false;
                }
            });
        } else {
            Toast.makeText(activity, "Selected contact is already invited, try selecting other contacts.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        mBinding.swipeToRefresh.setOnRefreshListener(() -> {
            if (checkWriteExternalPermission()) {
                if (!isContactFetched) {
                    getContactList();
                } else {
                    isContactFetched=true;
                    if (letter.equalsIgnoreCase(""))
                        CallFetchAllContactsAPI();
                    else
                        callFetchAPI(letter);
                }
            } else {
                askUserPermission();
            }
        });

        mBinding.selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    if (contactsList != null && contactsList.size() > 0) {
                        List<Contacts> contactsList1 = new ArrayList<>();
                        for (Contacts contacts : contactsList) {
                            contacts.setSelected(true);
                            contactsList1.add(contacts);
                            if (contacts.getNameOnSpotmeetCircle().isEmpty() && contacts.isSelected() && contacts.getOtpSent() == 0) {
                                contactsSet.add(contacts);
                            }
                        }
                        isSelectedAll = true;
                        inviteItem.setVisible(true);
                        mAdapter.clearAllCheckbox(contactsList1);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            } else {
                try {
                    if (contactsList != null && contactsList.size() > 0 && contactsSet.size() > 0) {
                        contactsSet.clear();
                        List<Contacts> contactsList1 = new ArrayList<>();
                        for (Contacts contacts : contactsList) {
                            contacts.setSelected(false);
                            contactsList1.add(contacts);
                        }
                        isSelectedAll = false;
                        inviteItem.setVisible(false);
                        mAdapter.clearAllCheckbox(contactsList1);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });
    }



    private boolean checkWriteExternalPermission() {
        String permission = android.Manifest.permission.READ_CONTACTS;
        int res = requireContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    private void askUserPermission() {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_layout);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tvDesc = dialog.findViewById(R.id.txt_dia);
        tvDesc.setText(requireContext().getResources().getString(R.string.import_permission_desc));
        btnCancel.setText(getString(R.string.cancel));
        btnCancel.setVisibility(View.GONE);
        btnCancel.setOnClickListener(view -> dialog.dismiss());
        TextView btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setText(getString(R.string.ok));
        btnExit.setOnClickListener(view -> {
            enableRuntimePermission();
            dialog.dismiss();
        });
        if (!dialog.isShowing() && isAdded()) {
            dialog.show();
        }
    }



        private void enableRuntimePermission() {
            Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.READ_CONTACTS)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            mBinding.swipeToRefresh.setRefreshing(true);
                            if (!isContactFetched) {
                                getContactList();
                            } else {
                                isContactFetched=true;
                                if (letter.equalsIgnoreCase(""))
                                    CallFetchAllContactsAPI();
                                else
                                    callFetchAPI(letter);
                            }

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            mBinding.swipeToRefresh.setRefreshing(false);
                            Toast.makeText(activity, "CONTACTS permission denied.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(error ->
                            Toast.makeText(activity, "Error occurred! ", Toast.LENGTH_SHORT).show())
                    .onSameThread()
                    .check();
        }

    private void getContactList() {
        try {
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    , projection
                    , null
                    , null
                    , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Contacts contacts = new Contacts();
                    Log.i(TAG, String.format("%s, %s", cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)),
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                    contacts.setUserName(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
                    contacts.setMobileNum(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    mViewModel.contactsList.add(contacts);
                }
                cursor.close();
                mViewModel.setContactListLiveData();

                CallFetchAllContactsAPI();
//                callFetchAPI("A");


            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void CallFetchAllContactsAPI() {
        mViewModel.firstIndex = 0;
        mBinding.selectCheckBox.setChecked(false);
        mViewModel.lastIndex = 100;
        mViewModel.pageFetch="0";
        letter = "";
        mViewModel.filteredList.clear();
        contactsList.clear();

        mViewModel.filteredList.addAll(mViewModel.contactsList);

        if (mViewModel.filteredList.size() >= mViewModel.lastIndex) {
            getVerifyDetails("0", mViewModel.filteredList.subList(mViewModel.firstIndex, mViewModel.lastIndex));
        } else {
            getVerifyDetails("0", mViewModel.filteredList.subList(mViewModel.firstIndex, mViewModel.filteredList.size()));
        }
    }

    private void callFetchAPI(String selected_letter) {
        mViewModel.firstIndex = 0;
        mBinding.selectCheckBox.setChecked(false);
        mViewModel.lastIndex = 100;
        mViewModel.pageFetch="0";
        letter = selected_letter;
        mViewModel.filteredList.clear();
        contactsList.clear();
        for (Contacts contacts : mViewModel.contactsList) {
            if (contacts.getUserName().startsWith(selected_letter))
                mViewModel.filteredList.add(contacts);
        }

        if (mViewModel.filteredList.size() >= mViewModel.lastIndex) {
            getVerifyDetails("0", mViewModel.filteredList.subList(mViewModel.firstIndex, mViewModel.lastIndex));
        } else {
            getVerifyDetails("0", mViewModel.filteredList.subList(mViewModel.firstIndex, mViewModel.filteredList.size()));
        }

    }

    public void manageEmptyView() {
        if (contactsList.size()==0) {
            mBinding.selectCheckBox.setVisibility(View.GONE);
            mBinding.tvNoContacts.setVisibility(View.VISIBLE);
        } else {
            mBinding.selectCheckBox.setVisibility(View.VISIBLE);
            mBinding.tvNoContacts.setVisibility(View.GONE);
        }
    }

    private void getVerifyDetails(String pagestart, List<Contacts> contacts) {
        String modelAction = "";

        if (!mBinding.swipeToRefresh.isRefreshing())
        mBinding.swipeToRefresh.setRefreshing(true);

        if (contacts != null)
            modelAction = TextUtils.join(",", contacts);

        Call<ContactResponse> call = apiService.fetchVerifiedContacts(String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID)), getDeviceId(getActivity()), qoogol, pagestart, modelAction, "N", letter,"100");
        call.enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, retrofit2.Response<ContactResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        contactsList.addAll(response.body().getContactList());
                        mViewModel.pageFetch = response.body().getPagefetch();
                        mViewModel.firstIndex = mViewModel.lastIndex+1;
                        mViewModel.lastIndex = mViewModel.lastIndex + 100;
                        mAdapter.updateList(contactsList);
                        manageEmptyView();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                    if (mBinding.swipeToRefresh.isRefreshing())
                        mBinding.swipeToRefresh.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mBinding.swipeToRefresh.isRefreshing())
                        mBinding.swipeToRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                t.printStackTrace();
                if (mBinding.swipeToRefresh.isRefreshing())
                    mBinding.swipeToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void buttonClicked(Contacts contacts, int position, boolean forInvite) {
        if (NetworkUtility.isConnected(activity)) {
            if (forInvite) {
                if (contactsSet != null && contactsSet.contains(contacts)) {
                    contactsSet.remove(contacts);
                    Log.i(TAG + " 1", contacts.toString());
                } else if (contacts.getOtpSent() == 0 && contacts.getNameOnSpotmeetCircle().isEmpty() && contacts.isSelected()) {
                    Log.i(TAG, contacts.toString());
                    contactsSet.add(contacts);
                }
            } else if (!contacts.getNameOnSpotmeetCircle().isEmpty() && contacts.getUserId() != 0 && !contacts.isAlreadyConnected() && !contacts.isRequestActive()) {
                this.contacts = contacts;
                this.position = position;
                Toast.makeText(activity, contacts.getUserName() + " " + contacts.getMobileNum(), Toast.LENGTH_LONG).show();

                updateConnection(String.valueOf(contacts.getUserId()));
            } else {
//                if (appInstalledOrNot()) {
//                    shareAction(contacts.getMobileNum());
//                }
            }
            inviteItem.setVisible(!contactsSet.isEmpty());
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }

    }

    private void updateConnection(String user) {
        ApiInterface apiService = ApiClient.getInstance().getApi();
        com.jangletech.qoogol.dialog.ProgressDialog.getInstance().show(getActivity());
        Call<ResponseObj> call = apiService.updateConnections(String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID)), "I", getDeviceId(getActivity()), qoogol, user);
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    com.jangletech.qoogol.dialog.ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        contacts.setAlreadyConnected(true);
                        mAdapter.updateResult(contacts, position);
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.jangletech.qoogol.dialog.ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseObj> call, Throwable t) {
                t.printStackTrace();
                com.jangletech.qoogol.dialog.ProgressDialog.getInstance().dismiss();
            }
        });
    }


    @Override
    public void onBottomReached(int size) {
//        getVerifyDetails(String.valueOf(size), mViewModel.contactsList.subList(mViewModel.firstIndex, mViewModel.lastIndex));
        try {
            if (NetworkUtility.isConnected(requireActivity())) {
//                if (contactsSet.size() > 0) {
//                    if (isSelectedAll) {
//                        callInviteAPI();
//                    } else {
//                        new AlertDialog.Builder(activity)
//                                .setMessage("Do you want to send invite to selected contacts?")
//                                .setPositiveButton("Yes", (dialog, which) -> {
//                                    dialog.dismiss();
//                                    sendInvite();
//                                })
//                                .setNeutralButton("No", (dialog, which) -> dialog.dismiss())
//                                .create().show();
//                    }
//                } else
                if (mViewModel.filteredList.size() >= mViewModel.lastIndex) {
                    getVerifyDetails(mViewModel.pageFetch, mViewModel.filteredList.subList(mViewModel.firstIndex, mViewModel.lastIndex));
                } else {
                    if (mViewModel.filteredList.size()>=mViewModel.firstIndex)
                    getVerifyDetails(mViewModel.pageFetch,mViewModel.filteredList.subList(mViewModel.firstIndex, mViewModel.lastIndex));
                }
            } else {
                Toast.makeText(requireActivity(),
                        requireActivity().getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            getVerifyDetails(mViewModel.pageFetch, null);
        }
    }

    @Override
    public void onFilterClick(String letter, int position) {
        if (checkWriteExternalPermission()) {
            if (!isContactFetched) {
                getContactList();
            } else {
                isContactFetched=true;
                if (position==0)
                    CallFetchAllContactsAPI();
                else
                    callFetchAPI(letter);
            }
        } else {
            askUserPermission();
        }

    }
}
