package com.alsalamegypt.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalamegypt.Adapters.RecordsHistoryAdapter;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.Repositories.MakeRecordsRepository;
import com.alsalamegypt.databinding.FragmentListenMastersBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class RecordHistoryFragment extends BaseFragment {

    private FragmentListenMastersBinding fragmentListenMastersBinding;
    private RecordsHistoryAdapter recordsHistoryAdapter;
    private List<RecordHistory> recordHistoryList;
    private MakeRecordsRepository makeRecordsRepository;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentListenMastersBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_listen_masters, container,
                false);
        return fragmentListenMastersBinding.getRoot();
    }


    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        setListeners();
        initRecyclerView();
        requestRecordsList();
    }

    private void initUI(){

        fragmentListenMastersBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.rec_his_list));
    }


    private void setListeners(){

        fragmentListenMastersBinding.standardToolbarLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInfoDialogWithTwoButtons(getResources().getString(R.string.logout), getResources().getString(R.string.sure_logout),
                        getResources().getString(R.string.yes), getResources().getString(R.string.no), new Closure() {
                    @Override
                    public void exec() {

                        ((MainActivity)getActivity()).navController.navigate(R.id.action_fragment_main_records_to_fragment_login);
                        MyApplication.getTinyDB().putString(Constants.KEY_USERID, "");
                        MyApplication.getTinyDB().putString(Constants.KEY_USERNAME, "");
                        MyApplication.getTinyDB().putString(Constants.KEY_USER_PASSWORD, "");
                        MyApplication.getTinyDB().putString(Constants.KEY_USER_TYPE, "");
                    }

                    }, new Closure() {
                    @Override
                    public void exec() {

                        hideInfoDialogWithTwoButton();
                    }
                    }, false);
            }
        });

        fragmentListenMastersBinding.standardToolbarRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestRecordsList();
            }
        });

    }


    private void initRecyclerView(){

        recordsHistoryAdapter = new RecordsHistoryAdapter(recordHistoryList , getContext(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentListenMastersBinding.fragmentListenRecordsrecycler.recyclerview.setLayoutManager(mLayoutManager);
        fragmentListenMastersBinding.fragmentListenRecordsrecycler.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        recordsHistoryAdapter.setHasStableIds(true);
        fragmentListenMastersBinding.fragmentListenRecordsrecycler.recyclerview.setAdapter(recordsHistoryAdapter);
    }


    private void requestRecordsList(){


    }
}
