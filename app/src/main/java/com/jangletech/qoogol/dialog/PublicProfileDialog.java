package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.databinding.DialogPublicProfileBinding;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.TinyDB;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.ui.BaseFragment.getUserId;
import static com.jangletech.qoogol.util.Constant.accept_friend_requests;
import static com.jangletech.qoogol.util.Constant.follow;
import static com.jangletech.qoogol.util.Constant.qoogol;
import static com.jangletech.qoogol.util.Constant.reject_friend_requests;
import static com.jangletech.qoogol.util.Constant.remove_connection;
import static com.jangletech.qoogol.util.Constant.sent_friend_req;
import static com.jangletech.qoogol.util.Constant.unfollow;
import static com.jangletech.qoogol.util.UtilHelper.getProfileImageUrl;

public class PublicProfileDialog extends Dialog {

    private static final String TAG = "PublicProfileDialog";
    private DialogPublicProfileBinding mBinding;
    private Activity activity;
    private AppRepository mAppRepository;
    private String userid, profilePicPath = "";
    private PublicProfileClickListener clickListener;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public PublicProfileDialog(@NonNull Activity activity, String userid, PublicProfileClickListener clickListener) {
        super(activity, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.activity = activity;
        this.userid = userid;
        this.clickListener = clickListener;
        mAppRepository = new AppRepository(getContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_public_profile, null, false);
        setContentView(mBinding.getRoot());
        Log.d(TAG, "onCreate UserId : " + userid);
        if (userid != null && userid.equalsIgnoreCase(AppUtils.getUserId())) {
            mBinding.addFriend.setVisibility(View.GONE);
            mBinding.follow.setVisibility(View.GONE);
        }
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        ProgressDialog.getInstance().show(activity);

        Call<UserProfile> call = apiService.fetchOtherUsersInfo(
                getUserId(getContext()),
                getDeviceId(getContext()),
                Constant.APP_NAME,
                Constant.APP_VERSION,
                userid,
                "UP"
        );

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mBinding.setUserProfile(response.body());
                    setPublicProfile(response.body());
                } else {
                    showToast("Something went wrong!!");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void setPublicProfile(UserProfile userProfile) {
        mBinding.shimmerViewContainer.hideShimmer();
        try {
            if (userProfile != null && userProfile.getIsConnected().equalsIgnoreCase("true")) {
                mBinding.addFriend.setText(activity.getResources().getString(R.string.unfriend));
            } else if (userProfile != null && userProfile.getIsInitiated_by_u1().equalsIgnoreCase("true")) {
                mBinding.addFriend.setText(activity.getResources().getString(R.string.cancel));
            } else if (userProfile != null && userProfile.getIsInitiated_by_u2().equalsIgnoreCase("true")) {
                mBinding.addFriend.setText(activity.getResources().getString(R.string.accept));
            } else {
                mBinding.addFriend.setText(activity.getResources().getString(R.string.add_friend));
            }

            if (userProfile != null && userProfile.getU1FollowsU2().equalsIgnoreCase("true")) {
                mBinding.follow.setText(activity.getResources().getString(R.string.unfollow));
            } else {
                mBinding.follow.setText(activity.getResources().getString(R.string.follow));
            }

            //set user badge
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("B"))
                mBinding.imgBadge.setImageDrawable(activity.getDrawable(R.drawable.bronze));
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("S"))
                mBinding.imgBadge.setImageDrawable(activity.getDrawable(R.drawable.silver));
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("G"))
                mBinding.imgBadge.setImageDrawable(activity.getDrawable(R.drawable.gold));
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("P"))
                mBinding.imgBadge.setImageDrawable(activity.getDrawable(R.drawable.platinum));

            mBinding.tvName.setText(AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key1), userProfile.getFirstName()) + " " + AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key2), userProfile.getLastName()));
            mBinding.tvDobs.setText(DateUtils.getFormattedDate(AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key3), userProfile.getDob())));

            if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("M")) {
                mBinding.tvGender.setText("Male");
            } else if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("F")) {
                mBinding.tvGender.setText("Female");
            }
            if (userProfile.getEndPathImage() != null && !userProfile.getEndPathImage().isEmpty()) {
                loadProfilePic(getProfileImageUrl(userProfile.getEndPathImage()));
            } else if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("M")) {
                loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API);
            } else if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("F")) {
                loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API);
            }

            if (userProfile.getU_Nationality() == null || userProfile.getU_Nationality().isEmpty()) {
                mBinding.nationalityLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_State() == null || userProfile.getU_State().isEmpty()) {
                mBinding.stateLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_District() == null || userProfile.getU_District().isEmpty()) {
                mBinding.divisionLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_City() == null || userProfile.getU_City().isEmpty()) {
                mBinding.cityLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_language() == null || userProfile.getU_language().isEmpty()) {
                mBinding.languageLayout.setVisibility(View.GONE);
            }

            mBinding.addFriend.setOnClickListener(v -> {
                if (userProfile.getIsConnected().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), remove_connection);
                } else if (userProfile.getIsInitiated_by_u1().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), reject_friend_requests);
                } else if (userProfile.getIsInitiated_by_u2().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), accept_friend_requests);
                } else {
                    updateConnection(userProfile.getUserId(), sent_friend_req);
                }
            });

            mBinding.follow.setOnClickListener(v -> {
                if (userProfile.getU1FollowsU2().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), unfollow);
                } else {
                    updateConnection(userProfile.getUserId(), follow);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mBinding.shimmerViewContainer.hideShimmer();
        }

        mBinding.userProfilePic.setOnClickListener(v -> {
            clickListener.onViewImage(profilePicPath);
        });
    }

    public void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    private void loadProfilePic(String url) {
        Log.d(TAG, "loadProfilePic : " + url);
        profilePicPath = url;
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.load)
                .error(R.drawable.ic_profile_default);
        Glide.with(activity).load(url)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.userProfilePic);
    }

    private void updateConnection(String user, String Processcase) {
        ApiInterface apiService = ApiClient.getInstance().getApi();
        ProgressDialog.getInstance().show(activity);
        Call<ResponseObj> call = apiService.updateConnections(getUserId(getContext()), Processcase, getDeviceId(getContext()), qoogol, user);
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        fetchUserProfile();
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        if (Processcase.equalsIgnoreCase(reject_friend_requests) || Processcase.equalsIgnoreCase(accept_friend_requests)) {
                            executor.execute(() -> mAppRepository.deleteFriendReq(AppUtils.getUserId(), user));
                        } else if (Processcase.equalsIgnoreCase(unfollow)) {
                            executor.execute(() -> mAppRepository.deleteFollowings(AppUtils.getUserId(), user));
                        } else if (Processcase.equalsIgnoreCase(remove_connection)) {
                            executor.execute(() -> mAppRepository.deleteFriends(AppUtils.getUserId(), user));
                        }
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
                mBinding.shimmerViewContainer.hideShimmer();
            }
        });
    }

    public interface PublicProfileClickListener {
        void onFriendUnFriendClick();

        void onFollowUnfollowClick();

        void onViewImage(String path);
    }
}
