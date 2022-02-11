package com.alsalamegypt.UI;

import android.os.Bundle;
import android.os.Handler;
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
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.FragmentListenMastersBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class RecordHistoryFragment extends BaseFragment {

    private FragmentListenMastersBinding fragmentListenMastersBinding;
    private RecordsHistoryAdapter recordsHistoryAdapter;
    private List<RecordHistory> recordHistoryList;

    /// Vars for apis /////
    private MakeRecordsViewModel makeRecordsViewModel;
    private Observer<List<RecordHistory>> recordHistoryListObserver;
    private Observer<Integer> deleteAllHistoryObserver;

    private final int SPLASH_DISPLAY_LENGTH = 500;



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
        initObservers();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                requestRecordsList();

            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void initUI(){

        fragmentListenMastersBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.rec_his_list));

        fragmentListenMastersBinding.standardToolbarDeleteBtn.setVisibility(View.VISIBLE);
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


        fragmentListenMastersBinding.standardToolbarDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (recordHistoryList.isEmpty()){

                    showFailedDialog(getResources().getString(R.string.no_data_to_remove), true);
                    return;
                }


                showInfoDialogWithTwoButtons(getResources().getString(R.string.sure),
                        getResources().getString(R.string.sure_del_all_arch), getResources().getString(R.string.yes),
                        getResources().getString(R.string.no), new Closure() {
                            @Override
                            public void exec() {

                                ///// Del all archive /////

                                showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.del_all_arch_prog),
                                        false);

                                makeRecordsViewModel.getDeleteAllRecordHistoryMutableLiveData().observe(getViewLifecycleOwner(),
                                        deleteAllHistoryObserver);
                            }
                        }, new Closure() {
                            @Override
                            public void exec() {

                                hideInfoDialogWithTwoButton();
                            }
                        }, true);

            }
        });

    }


    private void initRecyclerView(){

        recordHistoryList = new ArrayList<>();

        recordsHistoryAdapter = new RecordsHistoryAdapter(recordHistoryList , getContext(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentListenMastersBinding.fragmentListenRecordsrecycler.recyclerview.setLayoutManager(mLayoutManager);
        fragmentListenMastersBinding.fragmentListenRecordsrecycler.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        recordsHistoryAdapter.setHasStableIds(true);
        fragmentListenMastersBinding.fragmentListenRecordsrecycler.recyclerview.setAdapter(recordsHistoryAdapter);
    }


    private void requestRecordsList(){

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_rec_his)
                , false);

        makeRecordsViewModel.getGetAllRecordHistoryMutableLiveData().observe(getViewLifecycleOwner(), recordHistoryListObserver);
    }


    private void initObservers(){

        makeRecordsViewModel = ViewModelProviders.of(this).get(MakeRecordsViewModel.class);

        recordHistoryListObserver = new Observer<List<RecordHistory>>() {
            @Override
            public void onChanged(List<RecordHistory> recordHistories) {

                if (recordHistories!= null){

                        hideProgress();

                        if (!recordHistories.isEmpty()){

                            if (recordHistories.get(0).getId()!= -1){

                                recordHistoryList.clear();
                                recordHistoryList.addAll(recordHistories);
                                recordsHistoryAdapter.notifyDataSetChanged();
                            }

                            else {

                                showFailedDialog(getResources().getString(R.string.err_load_rec_his), false);
                            }

                        }

                        else{

                            showSnackBar(R.string.no_record_history);
                        }


                    }
                }

        };


        deleteAllHistoryObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if (integer!=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        hideProgress();

                        if (integer!=0 && integer!=-1){

                            showSuccessDialog(getResources().getString(R.string.succ_del_all_arch_prog), true);

                            int size = recordHistoryList.size();
                            recordHistoryList.clear();
                            recordsHistoryAdapter.notifyItemRangeRemoved(0, size);

                        }

                        else
                            showFailedDialog(getResources().getString(R.string.fail_del_all_arch_prog), false);
                    }
                }
            }
        };
    }
}
