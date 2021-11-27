package com.alsalamegypt.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.R;
import com.alsalamegypt.databinding.FragmentMainRecordsBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainRecordFragment extends BaseFragment {

    private FragmentMainRecordsBinding fragmentMainRecordsBinding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentMainRecordsBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_main_records, container, false);
        return fragmentMainRecordsBinding.getRoot();
    }


    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //appBarConfiguration = new AppBarConfiguration.Builder(R.id.menu_navigation_record, R.id.menu_navigation_my_records).build();
        navController = Navigation.findNavController(getActivity(), R.id.fragment_main_record_fragment_nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController((AppCompatActivity) getActivity(), navController, appBarConfiguration);
        NavigationUI.setupWithNavController(fragmentMainRecordsBinding.fragmentMainRecordFragmentNavView, navController);


    }

}