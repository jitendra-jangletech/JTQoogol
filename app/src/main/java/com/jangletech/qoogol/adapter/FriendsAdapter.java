package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ConnectionItemBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Friends;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.Gravity.END;
import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.accept_follow_requests;
import static com.jangletech.qoogol.util.Constant.accept_friend_requests;
import static com.jangletech.qoogol.util.Constant.block;
import static com.jangletech.qoogol.util.Constant.connectonId;
import static com.jangletech.qoogol.util.Constant.follow;
import static com.jangletech.qoogol.util.Constant.followers;
import static com.jangletech.qoogol.util.Constant.following;
import static com.jangletech.qoogol.util.Constant.followrequests;
import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.friends;
import static com.jangletech.qoogol.util.Constant.qoogol;
import static com.jangletech.qoogol.util.Constant.reject_follow_requests;
import static com.jangletech.qoogol.util.Constant.reject_friend_requests;
import static com.jangletech.qoogol.util.Constant.remove_connection;
import static com.jangletech.qoogol.util.Constant.unblock;
import static com.jangletech.qoogol.util.Constant.unfollow;

/**
 * Created by Pritali on 5/6/2020.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> implements Filterable {

    private List<Friends> connectionsList;
    private Activity activity;
    private ConnectionItemBinding connectionItemBinding;
    private List<Friends> filteredConnectionsList;
    private String call_from;
    private updateConnectionListener listener;

    public FriendsAdapter(Activity activity, List<Friends> connectionsList, String call_from, updateConnectionListener listener) {
        this.activity = activity;
        this.connectionsList = connectionsList;
        this.filteredConnectionsList = connectionsList;
        this.call_from = call_from;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        connectionItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.connection_item, parent, false);
        return new ViewHolder(connectionItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        Friends connections = connectionsList.get(position);
        holder.connectionItemBinding.tvUserName.setText(connections.getU_first_name() + " " + connections.getU_last_name());
        try {
            if (connections.getProf_pic() != null && !connections.getProf_pic().isEmpty()) {
                Glide.with(activity).load(UtilHelper.getProfileImageUrl(connections.getProf_pic().trim())).circleCrop().placeholder(R.drawable.profile).into(holder.connectionItemBinding.userProfileImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.connectionItemBinding.rlProfile.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.fetch_profile_id, connections.getCn_user_id_2());
            listener.showProfileClick(bundle);
        });

        PopupMenu popup = new PopupMenu(activity, connectionItemBinding.textViewOptions, END);
        popup.setGravity(END);
        popup.inflate(R.menu.connection_options);
        Menu popupMenu = popup.getMenu();
        if (call_from.equalsIgnoreCase(friends)) {
            popupMenu.findItem(R.id.action_remove_connection).setVisible(true);
            if (connections.getCn_blocked_by_u1().equalsIgnoreCase("false"))
                popupMenu.findItem(R.id.action_follow).setVisible(true);
        } else if (call_from.equalsIgnoreCase(followers)) {
            popupMenu.findItem(R.id.action_follow).setVisible(true);
            if (connections.getCn_connected().equalsIgnoreCase("false"))
                popupMenu.findItem(R.id.action_add_friend).setVisible(true);
        } else if (call_from.equalsIgnoreCase(following)) {
            popupMenu.findItem(R.id.action_unfollow).setVisible(true);
            if (connections.getCn_connected().equalsIgnoreCase("false"))
                popupMenu.findItem(R.id.action_add_friend).setVisible(true);
        } else if (call_from.equalsIgnoreCase(friendrequests)) {
            try {
                if (connections.getFriend_req_sent().equalsIgnoreCase("true")) {
                    popupMenu.findItem(R.id.cancel_friend).setVisible(true);
                } else {
                    popupMenu.findItem(R.id.accept_friend).setVisible(true);
                    popupMenu.findItem(R.id.reject_friend).setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (call_from.equalsIgnoreCase(followrequests)) {
            try {
                if (connections.getFollow_req_sent().equalsIgnoreCase("true")) {
                    popupMenu.findItem(R.id.cancel_follow).setVisible(true);
                } else {
                    popupMenu.findItem(R.id.accept_follow).setVisible(true);
                    popupMenu.findItem(R.id.reject_follow).setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_remove_connection:
                    updateConnection(connections.getCn_user_id_2(), remove_connection);
                    break;
                case R.id.action_block:
                    updateConnection(connections.getCn_user_id_2(), block);
                    break;
                case R.id.action_unblock:
                    updateConnection(connections.getCn_user_id_2(), unblock);
                    break;
                case R.id.action_follow:
                    updateConnection(connections.getCn_user_id_2(), follow);
                    break;
                case R.id.action_unfollow:
                    updateConnection(connections.getCn_user_id_2(), unfollow);
                    break;
                case R.id.accept_follow:
                    updateConnection(connections.getCn_user_id_2(), accept_follow_requests);
                    break;
                case R.id.reject_follow:
                case R.id.cancel_follow:
                    updateConnection(connections.getCn_user_id_2(), reject_follow_requests);
                    break;
                case R.id.accept_friend:
                    updateConnection(connections.getCn_user_id_2(), accept_friend_requests);
                    break;
                case R.id.reject_friend:
                case R.id.cancel_friend:
                    updateConnection(connections.getCn_user_id_2(), reject_friend_requests);
                    break;

                case R.id.action_view_profile:
                    Bundle bundle = new Bundle();
                    bundle.putInt(CALL_FROM, connectonId);
                    bundle.putString(Constant.fetch_profile_id, connections.getCn_user_id_2());
                    listener.showProfileClick(bundle);
//                    NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile,bundle);
                    break;

            }
            return false;
        });
        connectionItemBinding.textViewOptions.setOnClickListener(v -> {
            popup.show();
        });

        if (position == connectionsList.size() && connectionsList.size() >= 25) {
            listener.onBottomReached(connectionsList.size());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    connectionsList = filteredConnectionsList;
                } else {
                    List<Friends> filteredList = new ArrayList<>();
                    for (Friends row : filteredConnectionsList) {
                        if (row.getU_first_name().toLowerCase().contains(charString.toLowerCase()) || row.getU_last_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    connectionsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = connectionsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                connectionsList = (ArrayList<Friends>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface updateConnectionListener {
        void onUpdateConnection(String user);

        void onBottomReached(int size);

        void showProfileClick(Bundle bundle);
    }

    private void updateConnection(String user, String Processcase) {
        ApiInterface apiService = ApiClient.getInstance().getApi();
        ProgressDialog.getInstance().show(activity);
        Call<ResponseObj> call = apiService.updateConnections(String.valueOf(new PreferenceManager(activity).getInt(Constant.USER_ID)), Processcase, getDeviceId(), qoogol, user);
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        listener.onUpdateConnection(user);
                    } else {
                        Toast.makeText(activity, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseObj> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    @Override
    public int getItemCount() {
        return connectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConnectionItemBinding connectionItemBinding;

        public ViewHolder(@NonNull ConnectionItemBinding itemView) {
            super(itemView.getRoot());
            this.connectionItemBinding = itemView;
        }
    }
}
