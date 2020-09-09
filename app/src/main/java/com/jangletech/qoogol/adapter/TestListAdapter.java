package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestItemBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.ViewHolder> {

    private static final String TAG = "TestListAdapter";
    private Activity activity;
    private HashMap<Integer, TextView> map;
    private List<TestModelNew> testModelList;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private TestItemBinding itemBinding;
    private TestListAdapter.TestClickListener testClickListener;
    private String flag = "";
    private HashMap<String, String> mapDiffLevel = new HashMap<>();
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public TestListAdapter(Activity activity, List<TestModelNew> itemlist, TestListAdapter.TestClickListener testClickListener, String flag) {
        this.activity = activity;
        this.testModelList = itemlist;
        this.testClickListener = testClickListener;
        this.flag = flag;
        setHasStableIds(true);
        setMapValues();
    }

    @Override
    public long getItemId(int position) {
        return testModelList.get(position).getTm_id();
    }

    private void setMapValues() {
        mapDiffLevel.put("M", "Medium");
        mapDiffLevel.put("H", "Hard");
        mapDiffLevel.put("E", "Easy");
        map = new HashMap<>();
    }

    @NonNull
    @Override
    public TestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.test_item, parent, false);
        return new TestListAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TestListAdapter.ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder Position : " + position);
        TestModelNew testModelNew = testModelList.get(position);
        //map.put(position, itemBinding.likeValue);
        holder.itemBinding.tvTestNameSubject.setText(testModelNew.getTm_name() + "(" + testModelNew.getSm_sub_name() + ")");
        holder.itemBinding.tvCategory.setText(BaseFragment.getDefinedTestCategory(testModelNew.getTm_catg()));
        holder.itemBinding.tvDuration.setText(testModelNew.getTm_duration() + " Min.");
        holder.itemBinding.tvTotalMarks.setText(testModelNew.getTm_tot_marks());
        holder.itemBinding.tvDifficultyLevel.setText(mapDiffLevel.get(testModelNew.getTm_diff_level()));
        holder.itemBinding.ratingvalue.setText(testModelNew.getTm_rating_count());
        holder.itemBinding.tvAttendedBy.setText(testModelNew.getTm_attempted_by());
        holder.itemBinding.tvRanking.setText(testModelNew.getTm_ranking());
        holder.itemBinding.tvQuestCount.setText(testModelNew.getQuest_count());

        if (testModelNew.getAttemptedTests() != null && testModelNew.getAttemptedTests().size() > 0) {
            holder.itemBinding.tvNoOfAttempts.setVisibility(View.VISIBLE);
            holder.itemBinding.tvNoOfAttempts.setText(Html.fromHtml("<h>Attempts : " + testModelNew.getAttemptedTests().size() + "</h>"));
        } else {
            holder.itemBinding.tvNoOfAttempts.setVisibility(View.GONE);
        }

        holder.itemBinding.tvNoOfAttempts.setOnClickListener(v -> {
            testClickListener.onAttemptsClick(testModelNew);
        });

        holder.itemBinding.likeValue.setOnClickListener(v -> {
            testClickListener.onLikeCountClick(testModelNew);
        });


        Log.e(TAG, "Like Count : " + testModelNew.getLikeCount());
        holder.itemBinding.likeValue.setText("" + testModelNew.getLikeCount());

        /*if (testModelList.get(holder.getAdapterPosition()).getShareCount() != null) {
            holder.itemBinding.shareValue.setText(testModelNew.getShareCount());
        } else {
            holder.itemBinding.shareValue.setText("0");
        }*/

       /* if (testModelNew.getCommentsCount() != null) {
            holder.itemBinding.commentValue.setText(testModelNew.getCommentsCount());
        } else {
            holder.itemBinding.commentValue.setText("0");
        }*/

        holder.itemBinding.commentValue.setText(testModelNew.getCommentsCount());
        holder.itemBinding.shareValue.setText(testModelNew.getShareCount());

        if (testModelNew.isFavourite()) {
            //itemBinding.favorite.setChecked(true);
            //AppUtils.bounceAnim(activity, holder.itemBinding.favorite);
            holder.itemBinding.favorite.setImageDrawable(activity.getDrawable(R.drawable.ic_favorite_filled));
        } else {
            //AppUtils.bounceAnim(activity, holder.itemBinding.favorite);
            holder.itemBinding.favorite.setImageDrawable(activity.getDrawable(R.drawable.ic_fav));
        }

       /* if (testModelList.get(getAdapterPosition()).isLike()) {
            itemBinding.like.setChecked(true);
        } else {
            holder.itemBinding.like.setChecked(false);
        }*/

        if (testModelNew.isLike()) {
            //AppUtils.bounceAnim(activity, holder.itemBinding.like);
            holder.itemBinding.like.setBackgroundDrawable(activity.getDrawable(R.drawable.ic_like_filled));
        } else {
            //AppUtils.bounceAnim(activity, holder.itemBinding.like);
            holder.itemBinding.like.setBackgroundDrawable(activity.getDrawable(R.drawable.ic_like));
        }

        holder.itemBinding.shareValue.setText(testModelNew.getShareCount());
        holder.itemBinding.commentValue.setText(testModelNew.getCommentsCount());

        if (testModelNew.getPublishedDate() != null)
            holder.itemBinding.tvPublishedDate.setText(DateUtils.getFormattedDate(testModelNew.getPublishedDate().substring(0, 10)));

        holder.itemBinding.tvAuthorName.setText(testModelNew.getAuthor());

        holder.itemBinding.testItemCard.setOnClickListener(v -> {
            testClickListener.onTestItemClick(testModelNew);
        });

        holder.itemBinding.btnStartTest.setOnClickListener(v -> {
            testClickListener.onStartTestClick(testModelNew);
        });

        holder.itemBinding.commentLayout.setOnClickListener(v -> {
            testClickListener.onCommentClick(testModelNew,position);
        });

        holder.itemBinding.shareLayout.setOnClickListener(v -> {
            testClickListener.onShareClick(testModelNew,position);
        });

        holder.itemBinding.favorite.setOnClickListener(v -> {
            AppUtils.bounceAnim(activity, holder.itemBinding.favorite);
            favTest(position, testModelNew);
            //testClickListener.onFavouriteClick(testModelNew, testModelNew.isFavourite(), position);
        });

        holder.itemBinding.like.setOnClickListener(v -> {
            AppUtils.bounceAnim(activity, holder.itemBinding.like);
            doLikeTest(position, testModelNew);
            //testClickListener.onLikeClick(testModelNew, position, testModelNew.isLike());
        });

        setAnimation(holder.itemBinding.getRoot(), position);
    }

    @Override
    public int getItemCount() {
        return testModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TestItemBinding itemBinding;

        public ViewHolder(@NonNull TestItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }

    public interface TestClickListener {
        void onTestItemClick(TestModelNew testModel);

        void onStartTestClick(TestModelNew testModel);

        void onCommentClick(TestModelNew testModel,int pos);

        void onShareClick(TestModelNew testModelNew,int pos);

        void onLikeCountClick(TestModelNew testModel);

        void favClick(TestModelNew testModelNew);

        //void onLikeClick(TestModelNew testModel, int pos, boolean isChecked);

        //void onFavouriteClick(TestModelNew testModel, boolean isChecked, int position);

        void onAttemptsClick(TestModelNew testModel);
    }

    public void setSearchResult(List<TestModelNew> result) {
        testModelList = result;
        notifyDataSetChanged();
    }

    public void deleteFav(int pos) {
        testModelList.remove(pos);
        notifyItemRemoved(pos);
        showToast("Deleted from favourites");
    }

    private void doLikeTest(int pos, TestModelNew testModelNew) {
        ProgressDialog.getInstance().show(activity);
        Call<ProcessQuestion> call = apiService.addTestLike(new PreferenceManager(activity).getInt(Constant.USER_ID),
                testModelNew.getTm_id(), "I", testModelNew.isLike() ? 0 : 1);
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    if (response.body() != null && response.body().getResponse().equals("200")) {
                        int likeCount = testModelNew.getLikeCount();
                        if (testModelNew.isLike()) {
                            Log.e(TAG, "like 0 ");
                            testModelNew.setLike(false);
                            if (likeCount <= 0)
                                testModelNew.setLikeCount(0);
                            else
                                testModelNew.setLikeCount((likeCount - 1));
                        } else {
                            testModelNew.setLike(true);
                            testModelNew.setLikeCount((likeCount + 1));
                        }
                        //testModelList.set(pos,testModelNew);
                        notifyItemChanged(pos, testModelNew);

                    } else {
                        showToast(response.body().getResponse());
                        // showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void favTest(int pos, TestModelNew testModelNew) {
        ProgressDialog.getInstance().show(activity);
        Call<ProcessQuestion> call = apiService.addFavTest(new PreferenceManager(activity).getInt(Constant.USER_ID),
                testModelNew.getTm_id(), "I", testModelNew.isFavourite() ? 0 : 1);
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    if (response.body() != null && response.body().getResponse().equals("200")) {
                        if (!flag.equalsIgnoreCase("FAV")) {
                            if (testModelNew.isFavourite()) {
                                showToast("Removed from favourites");
                                testModelNew.setFavourite(false);
                            } else {
                                showToast("Added to favourites");
                                testModelNew.setFavourite(true);
                            }
                            //testModelList.set(pos,testModelNew);
                            notifyItemChanged(pos, testModelNew);
                            //testClickListener.favClick(testModelNew);
                            //notifyDataSetChanged();
                        } else {
                            deleteFav(pos);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    public void updateList(List<TestModelNew> testList) {
        testModelList = testList;
        notifyDataSetChanged();
    }

    public void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}
