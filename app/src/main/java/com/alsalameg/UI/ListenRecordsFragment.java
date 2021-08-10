package com.alsalameg.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalameg.Adapters.MastersAdapter;
import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.Models.Master;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.ViewModels.ListenRecordsViewModel;
import com.alsalameg.databinding.FragmentListenRecordsBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ListenRecordsFragment extends BaseFragment {

    private FragmentListenRecordsBinding fragmentListenRecordsBinding;
    private Observer<List<Master>> getMasterListObserver;
    private MastersAdapter mastersAdapter;
    private List<Master> masterList;
    private ListenRecordsViewModel listenRecordsViewModel;
    private boolean isRecorderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentListenRecordsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_listen_records, container, false);
        return fragmentListenRecordsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isRecorderListener = MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Recorded");

        initUI();
        setListeners();
        initRecyclerView();
        initObservers();
        requestMasters();
//        Bundle bundle = getArguments();
//        fragmentListenRecordsBinding.recordItem.itemRecordEmployeeNameTxt.setText(getResources().getString(R.string.employee_name) +
//                " محمد عصفور ");
//
//        fragmentListenRecordsBinding.recordItem.itemRecordLocationTxt.setText(getResources().getString(R.string.location) +
//                " " + bundle.getString("address"));
//
//        fragmentListenRecordsBinding.recordItem.itemRecordPlayer.voicePlayerView.setAudio(bundle.getString("path"));

    }



    private void initUI(){

        if (!isRecorderListener)
            fragmentListenRecordsBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.listen_records));
        else
            fragmentListenRecordsBinding.standardToolbarTitleTxt.setText(getResources().getString(R.string.user_records));
    }


    private void setListeners(){

        fragmentListenRecordsBinding.standardToolbarLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInfoDialogWithTwoButtons(getResources().getString(R.string.logout), getResources().getString(R.string.sure_logout),
                        getResources().getString(R.string.yes), getResources().getString(R.string.no), new Closure() {
                            @Override
                            public void exec() {

                                if (isRecorderListener)
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
    }
    private void initRecyclerView(){

        masterList = new ArrayList<>();
        mastersAdapter = new MastersAdapter(masterList , getContext(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentListenRecordsBinding.fragmentListenRecordsrecycler.recyclerview.setLayoutManager(mLayoutManager);
        fragmentListenRecordsBinding.fragmentListenRecordsrecycler.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        fragmentListenRecordsBinding.fragmentListenRecordsrecycler.recyclerview.setAdapter(mastersAdapter);
    }

    private void initObservers(){

        listenRecordsViewModel = ViewModelProviders.of(this).get(ListenRecordsViewModel.class);

        getMasterListObserver = new Observer<List<Master>>() {
            @Override
            public void onChanged(List<Master> masters) {

                if (masters!=null){

                    hideProgress();

                    if (masters.get(0).getId().equals("-1"))
                        showFailedDialog(getResources().getString(R.string.cannot_load_records), true);

                    else {

                        if (masters.isEmpty())
                            showSnackBar(R.string.no_records);
                        else {
                            masterList.addAll(masters);
                            mastersAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
    }


    private void requestMasters(){

        showDefaultProgressDialog();

        listenRecordsViewModel.getMasterListMutableLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(
                getViewLifecycleOwner(), getMasterListObserver);
    }
}
