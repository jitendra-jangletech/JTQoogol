package com.jangletech.qoogol.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.LaunchActivity;
import com.jangletech.qoogol.databinding.SettingsFragmentBinding;
import com.jangletech.qoogol.dialog.UniversalDialog;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

public class SettingsFragment extends BaseFragment implements UniversalDialog.DialogButtonClickListener {

    private SettingsViewModel mViewModel;
    private Context mContext;
    private SettingsFragmentBinding mBinding;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        mBinding.tvLogout.setOnClickListener(v -> {
            UniversalDialog universalDialog = new UniversalDialog(mContext, "Confirm Log Out",
                    "you are signing out of your Qoogol app on this device",
                    "Logout", "Cancel", this);
            universalDialog.show();
        });

        mBinding.tvBlockedList.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_blocked_connections);
        });

        mBinding.tvDeactivateAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
            builder.setTitle("Deactivate Account")
                    .setMessage("Are you sure, you want to deactivate account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public void onPositiveButtonClick() {
        new PreferenceManager(mContext).setIsLoggedIn(false);
        new PreferenceManager(mContext).saveString(Constant.MOBILE, "");
        new PreferenceManager(mContext).saveString(Constant.USER_ID, "");
        Intent intent = new Intent(mContext, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}