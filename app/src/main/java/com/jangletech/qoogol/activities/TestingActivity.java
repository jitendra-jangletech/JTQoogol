package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ShareAdapter;
import com.jangletech.qoogol.databinding.ActivityTestingBinding;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.model.ShareResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.qoogol;

public class TestingActivity extends AppCompatActivity implements ShareAdapter.OnItemClickListener {

    private static final String TAG = "TestingActivity";
    private ActivityTestingBinding mBinding;
    private String encoded = "";
    private LinearLayoutManager layoutManager;
    //private TestingAdapter mAdapter;
    private ShareAdapter mAdapter;
    private List<String> strings;
    private List<ShareModel> shareModelList;
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private int pageCount = 0;
    private boolean isSearching = false;
    private List<ShareModel> filteredModelList;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        strings = new ArrayList<>();
        shareModelList = new ArrayList<>();
        filteredModelList = new ArrayList<>();
       /* for (int i = 0; i < 10; i++) {
            strings.add(String.valueOf(i));
        }*/
        strings.add("Jitendra");
        strings.add("[keval]");
        strings.add("pritali");
        strings.add("kiran");
        strings.add("test");
        strings.add("user");
        strings.add("(Rajat varma)");
        strings.add("Satish");
        strings.add("(Aditya Kumar)");

        getData(pageCount, "");
        layoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShareAdapter(this, shareModelList, this);
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrolledOutItems = layoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                        isScrolling = false;
                        getData(pageCount, "");
                        //fetchData();
                        //Toast.makeText(TestingActivity.this, "Last Item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filteredModelList.clear();
                query = query.toLowerCase().trim();
                for (ShareModel shareModel : shareModelList) {
                    if (shareModel.getRecordType().equalsIgnoreCase("U")) {
                        if (shareModel.getU_first_name().trim().toLowerCase().contains(query) ||
                                shareModel.getU_last_name().trim().toLowerCase().contains(query)) {
                            filteredModelList.add(shareModel);
                        }
                    } else {
                        if (shareModel.getU_first_name().trim().toLowerCase().contains(query)) {
                            filteredModelList.add(shareModel);
                        }
                    }
                }
                if (filteredModelList.size() > 0) {
                    mAdapter.updateList(filteredModelList);
                } else {
                    //mAdapter.updateList(shareModelList);
                    if (!query.isEmpty() && !isSearching) {
                        isSearching = true;
                        getData(0, query);
                    } else {
                        mAdapter.updateList(shareModelList);
                    }
                }

                return true;
            }


//            @Override
//            public boolean onQueryTextChange(String query) {
//                //Log.d(TAG, "onQueryTextSubmit Query : " + query.toLowerCase());
//                List<String> filteredModelList = new ArrayList<>();
//                query = query.toLowerCase();
//                Log.d(TAG, "onQueryTextChange: " + query);
//                for (String model : strings) {
//
//                    String modelNew = model.toLowerCase().trim().replaceAll("\\(", "")
//                            .replaceAll("\\)", "");
//                    Log.d(TAG, "onQueryTextChange Searching : " + modelNew);
//                    if (modelNew.contains(query)) {
//                        filteredModelList.add(model);
//                    }
//                }
//                Log.d(TAG, "onQueryTextChange FilterList Size : " + filteredModelList.size());
//                mAdapter.updateList(filteredModelList);
//                return true;
//            }
        });
    }


    /*private void fetchData() {
        mBinding.progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    strings.add(String.valueOf(i));
                }
                mBinding.progress.setVisibility(View.GONE);
                mAdapter.updateList(strings);
                //mAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }*/

    private void getData(int pagestart, String text) {
        mBinding.progress.setVisibility(View.VISIBLE);
        Call<ShareResponse> call = apiService.fetchConnectionsforShare("1003", "A", "b002705dfbb1e24d", qoogol, pagestart, text);
        call.enqueue(new Callback<ShareResponse>() {
            @Override
            public void onResponse(Call<ShareResponse> call, retrofit2.Response<ShareResponse> response) {
                try {
                    mBinding.progress.setVisibility(View.GONE);
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        setData(response.body());
                    } else {
                        AppUtils.showToast(getApplicationContext(), "Something went wrong!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isSearching = false;
                    mBinding.progress.setVisibility(View.GONE);
                    AppUtils.showToast(getApplicationContext(), "Something went wrong!!");
                    //ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ShareResponse> call, Throwable t) {
                t.printStackTrace();
                isSearching = false;
                mBinding.progress.setVisibility(View.GONE);
                //ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void setData(ShareResponse shareResponse) {
        Log.d(TAG, "setData PageStart : " + pageCount);
        Log.d(TAG, "setData List Size : " + shareResponse.getConnection_list().size());
        if (!isSearching) {
            if (shareResponse.getConnection_list().size() > 0) {
                pageCount = shareResponse.getRow_count();
                shareModelList.addAll(shareResponse.getConnection_list());
                mAdapter.updateList(shareModelList);
                //mAdapter.notifyDataSetChanged();
            } else {
                AppUtils.showToast(getApplicationContext(), "No more connections available.");
            }
        } else {
            isSearching = false;
            filteredModelList.clear();
            filteredModelList.addAll(shareResponse.getConnection_list());
            if (filteredModelList.size() > 0) {
                mAdapter.updateList(filteredModelList);
            } else {
                AppUtils.showToast(getApplicationContext(), "No search results found.");
                mAdapter.updateList(shareModelList);
            }
        }
    }

    @Override
    public void actionPerformed(ShareModel connections, int position) {

    }

    @Override
    public void onBottomReached(int position) {

    }


//        mBinding.btnEncode.setOnClickListener(v -> {
//            String text = mBinding.text.getText().toString().trim();
//            encoded = StringUtils.stripAccents(Base64.encodeToString(text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
//            Log.d(TAG, "Encoded : " + encoded);
//        });
//
//        mBinding.btnDecode.setOnClickListener(v -> {
//            String decoded = decodedMessage(encoded);
//            Log.d(TAG, "Decoded : " + StringEscapeUtils.unescapeJava(decoded));
//            mBinding.text.setText(StringEscapeUtils.unescapeJava(decoded));
//        });
//    }
//
////    private String check(String s){
////
////        int len = s.length();
////        for (int i = 0; i < len; i++) {
////            // checks whether the character is neither a letter nor a digit
////            // if it is neither a letter nor a digit then it will return false
////            if ((Character.isLetterOrDigit(s.charAt(i)) == false)) {
////                s.replace("")
////            }
////        }
////        return s;
////    }
//
//    public static String decodedMessage(String message) {
//        try {
//            byte[] messageBytes = Base64.decode(message, Base64.DEFAULT);
//            return new String(messageBytes, StandardCharsets.UTF_8);
//        } catch (Exception ex) {
//            Log.e(TAG, "decodedMessage: " + ex.getMessage());
//        }
//        return "";
//    }
}