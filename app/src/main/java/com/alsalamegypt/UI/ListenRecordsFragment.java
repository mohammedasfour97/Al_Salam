package com.alsalamegypt.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alsalamegypt.Adapters.MastersAdapter;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.ViewModels.ListenRecordsViewModel;
import com.alsalamegypt.databinding.FragmentListenMastersBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ListenRecordsFragment extends BaseFragment {

    private FragmentListenMastersBinding fragmentListenRecordsBinding;
    private Observer<List<Master>> getMasterListObserver, getMastersByPageListObserver;
    private MastersAdapter mastersAdapter;
    private List<Master> masterList;
    private ListenRecordsViewModel listenRecordsViewModel;
    private boolean isRecorder;
    private int pageNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentListenRecordsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_listen_masters, container, false);
        return fragmentListenRecordsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isRecorder = MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Recorded");

        initUI();
        setListeners();
        initObservers();
        initRecyclerView();

    }



    private void initUI(){

        if (!isRecorder){

            if (getArguments().getBoolean("lis_rec_car")){

                fragmentListenRecordsBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.user_records));
                fragmentListenRecordsBinding.standardToolbarLogoutBtn.setImageResource(R.drawable.ic_baseline_arrow_back_white_24);
            }

            else
                fragmentListenRecordsBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.listen_records));
        }

        else
            fragmentListenRecordsBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.user_records));
    }


    private void setListeners(){

        fragmentListenRecordsBinding.standardToolbarLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isRecorder && getArguments().getBoolean("lis_rec_car"))
                    ((MainActivity)getActivity()).navController.popBackStack();

                    else
                        showInfoDialogWithTwoButtons(getResources().getString(R.string.logout), getResources().getString(R.string.sure_logout),
                                getResources().getString(R.string.yes), getResources().getString(R.string.no), new Closure() {
                                    @Override
                                    public void exec() {

                                        if (isRecorder)
                                            ((MainActivity)getActivity()).navController.navigate(R.id.action_fragment_main_records_to_fragment_login);
                                        else
                                            ((MainActivity)getActivity()).navController.navigate(R.id.action_fragment_listen_to_fragment_login);

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

        fragmentListenRecordsBinding.standardToolbarRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initRecyclerView();
            }
        });


        fragmentListenRecordsBinding.fragmentListenMastersScrollView.setOnScrollChangeListener(
                new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (!isRecorder && !getArguments().getBoolean("lis_rec_car")){

                            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {

                                pageNum++;
                                requestMasters();
                            }
                        }

                    }
                });
    }


    private void initRecyclerView(){

        masterList = new ArrayList<>();

        ///// Listener Masters Main Page ////

        if (!isRecorder && !getArguments().getBoolean("lis_rec_car"))
            mastersAdapter = new MastersAdapter(masterList , getContext(), this, false);

        ///// Listener Recorded Cars /////

        else
            mastersAdapter = new MastersAdapter(masterList , getContext(), this, true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentListenRecordsBinding.fragmentListenRecordsrecycler.recyclerview.setLayoutManager(mLayoutManager);
        fragmentListenRecordsBinding.fragmentListenRecordsrecycler.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        mastersAdapter.setHasStableIds(true);
        fragmentListenRecordsBinding.fragmentListenRecordsrecycler.recyclerview.setAdapter(mastersAdapter);

        pageNum = 1;
        requestMasters();
    }


    private void initObservers(){

        listenRecordsViewModel = ViewModelProviders.of(this).get(ListenRecordsViewModel.class);

        getMasterListObserver = new Observer<List<Master>>() {
            @Override
            public void onChanged(List<Master> masters) {

                if (masters!=null){

                    hideProgress();

                    if (masters.isEmpty())
                        showSnackBar(R.string.no_records);

                    else {

                        if (masters.get(0).getId().equals("-1"))
                            showFailedDialog(getResources().getString(R.string.err_loading), true);
                        else {
                            masterList.clear();
                            masterList.addAll(masters);
                            mastersAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };


        getMastersByPageListObserver = new Observer<List<Master>>() {
            @Override
            public void onChanged(List<Master> masters) {

                if (masters != null) {

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        hideProgress();

                        if (!masters.isEmpty()) {

                            if (masters.get(0).getId().equals("-1"))
                                showFailedDialog(getResources().getString(R.string.err_loading), true);

                            else {
                                //masterList.clear();
                                masterList.addAll(masters);
                                mastersAdapter.notifyDataSetChanged();
                            }
                        }

                        else {

                            //// Check if reaches the end of the page //////

                            if (pageNum == 1)
                                showSnackBar(R.string.no_records);

                        }

                        }

                    }
            }
        };
    }


    private void requestMasters(){

        showDefaultProgressDialog();

        if (isRecorder)
            listenRecordsViewModel.getMasterListMutableLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                    .observe(getViewLifecycleOwner(), getMasterListObserver);

        else{

            if (getArguments().getBoolean("lis_rec_car"))
                listenRecordsViewModel.getMasterListenerRecordedCarsMutableLiveData(getArguments().getString("master_id"),
                        MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(getViewLifecycleOwner(),
                        getMasterListObserver);
            else
                listenRecordsViewModel.getMasterListenerListMutableLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID),
                        pageNum, Constants.PAGE_SIZE).observe(getViewLifecycleOwner(), getMastersByPageListObserver);
        }

    }

}
