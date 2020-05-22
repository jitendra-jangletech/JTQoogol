package com.jangletech.qoogol.ui.edit_profile;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddEducationBinding;
import com.jangletech.qoogol.databinding.FragmentEditProfileBinding;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.educational_info.EducationInfoFragment;
import com.jangletech.qoogol.ui.personal_info.PersonalInfoFragment;
import com.jangletech.qoogol.ui.preference.PreferenceFragment;
import java.util.ArrayList;
import java.util.List;
import static com.jangletech.qoogol.util.Constant.add_edu;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    FragmentEditProfileBinding fragmentEditProfileBinding;
    private AddEducationBinding addEducationBinding;
    AlertDialog educationDialog;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private static final String TAG = "EditProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        return fragmentEditProfileBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewPager(fragmentEditProfileBinding.viewpager);
        fragmentEditProfileBinding.resultTabs.setupWithViewPager(fragmentEditProfileBinding.viewpager);
    }

    private void setupViewPager(ViewPager viewPager){
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new PersonalInfoFragment(), getContext().getString(R.string.personal_info_tab));
        adapter.addFragment(new EducationInfoFragment(), getContext().getString(R.string.educational_info_tab));
        //adapter.addFragment(new PreferenceFragment(), getString(R.string.preference_tab));
        viewPager.setAdapter(adapter);
    }


    public static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
