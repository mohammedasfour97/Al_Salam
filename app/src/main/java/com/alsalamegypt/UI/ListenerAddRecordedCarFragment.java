package com.alsalamegypt.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alsalamegypt.Adapters.ListenerAddRecordedCarsAdapter;
import com.alsalamegypt.Adapters.RecordsAdapter;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.Models.Record;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.ListenRecordsViewModel;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.FragmentListenerAddRecordedCarBinding;
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

public class ListenerAddRecordedCarFragment extends BaseFragment {

    private Master master;
    private FragmentListenerAddRecordedCarBinding fragmentListenerAddRecordedCarBinding;
    private Observer<List<Record>> recordsListObserver;
    private Observer<List<IDName>> regionsObserver;
    private Observer<String> deleteRecordObserver, updateHeardRecordObserver;
    private ListenRecordsViewModel listenRecordsViewModel;
    private MakeRecordsViewModel makeRecordsViewModel;

    //vars for spinner
    private ArrayAdapter<IDName> spinnerArrayAdapter;
    private List<IDName> regionList;
    private String selectedRegionID;

    /// vars for recyclerviews ///
    private List<Record> recordList;
    private RecordsAdapter recordsAdapter;
    private List addRecordedCarsList;
    private ListenerAddRecordedCarsAdapter addRecordedCarsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentListenerAddRecordedCarBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_listener_add_recorded_car, container,
                false);
        return fragmentListenerAddRecordedCarBinding.getRoot();
    }


    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTextsFromMaster();

        setListeners();
        initRecyclerViews();
        initRegionSpinner();
        initObservers();

        getRecordsList();

    }


    private void setListeners(){

        /*fragmentListenerAddRecordedCarBinding.sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getTexts()){

                    showInfoDialogWithTwoButtons(getResources().getString(R.string.sure), getResources().getString(R.string.sure_conf_this_car),
                            getResources().getString(R.string.yes), getResources().getString(R.string.no), new Closure() {
                                @Override
                                public void exec() {

                                    showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.add_car_prog_msg),
                                            false);

                                    listenRecordsViewModel.getAddRecordedCarMutableLiveData(fragmentListenerAddRecordedCarBinding.vichelNumberEdt
                                                    .getText().toString(), fragmentListenerAddRecordedCarBinding.vichelTypeEdt.getText().toString(),
                                            fragmentListenerAddRecordedCarBinding.vichelAddressEdt.getText().toString(),
                                            fragmentListenerAddRecordedCarBinding.vichelDistrictEdt.getText().toString(),
                                            master.getLongitude(), master.getLatitude(), MyApplication.getTinyDB().getString(Constants.KEY_USERID),
                                            selectedRegionID, fragmentListenerAddRecordedCarBinding.vichelNotesTxt.getText().toString(), master.getId())
                                            .observe(getViewLifecycleOwner(), addRecordedCarObsever);
                                }
                            }, new Closure() {
                                @Override
                                public void exec() {

                                    hideInfoDialogWithTwoButton();
                                }
                            }, true);

                }
            }
        });

         */

        fragmentListenerAddRecordedCarBinding.regionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedRegionID = ((IDName)parent.getSelectedItem()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fragmentListenerAddRecordedCarBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean someRecordNotHeard = false;

                for (Record record : recordList){

                    if (record.getHeard().equals("false")){

                        someRecordNotHeard = true;
                        break;
                    }

                }

                if (someRecordNotHeard){

                    showInfoDialogWithTwoButtons(getResources().getString(R.string.sure), getResources().getString(R.string.sure_lis_to_all_rec)
                                    + "\n" + getResources().getString(R.string.del_master_lis),
                            getResources().getString(R.string.yes), getResources().getString(R.string.no), new Closure() {
                                @Override
                                public void exec() {

                                    showDefaultProgressDialog();

                                    setHeardToAllRecords();
                                }
                            }, new Closure() {
                                @Override
                                public void exec() {

                                    ((MainActivity)getActivity()).navController.popBackStack();
                                }
                            }, true);
                }

                else {

                    showDefaultProgressDialog();

                    setHeardToAllRecords();

                }

            }
        });


        fragmentListenerAddRecordedCarBinding.standardToolbarRecordedCarsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("lis_rec_car", true);
                bundle.putString("master_id", master.getId());

                ((MainActivity)getActivity()).navController.navigate(R.id.fragment_listener_add_recorded_car_to_fragment_listen_to_records
                ,bundle);
            }
        });


        fragmentListenerAddRecordedCarBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                renewAddRecordedCarsList();
            }
        });
    }


    private void initRecyclerViews(){

        recordList = new ArrayList<>();
        recordsAdapter = new RecordsAdapter(recordList , getContext(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentListenerAddRecordedCarBinding.recordsRecycler.recyclerview.setLayoutManager(mLayoutManager);
        fragmentListenerAddRecordedCarBinding.recordsRecycler.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        recordsAdapter.setHasStableIds(true);
        fragmentListenerAddRecordedCarBinding.recordsRecycler.recyclerview.setAdapter(recordsAdapter);



        addRecordedCarsList = new ArrayList<>();

        addRecordedCarsAdapter = new ListenerAddRecordedCarsAdapter(addRecordedCarsList , master, getContext(), this);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext());
        fragmentListenerAddRecordedCarBinding.addRecordedCarRecycler.recyclerview.setLayoutManager(mLayoutManager2);
        fragmentListenerAddRecordedCarBinding.addRecordedCarRecycler.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        addRecordedCarsAdapter.setHasStableIds(true);
        fragmentListenerAddRecordedCarBinding.addRecordedCarRecycler.recyclerview.setAdapter(addRecordedCarsAdapter);

        renewAddRecordedCarsList();
    }


    private void initRegionSpinner(){

        regionList = new ArrayList<>();
        regionList.add(new IDName("0", getResources().getString(R.string.choose_region)));

        spinnerArrayAdapter = new ArrayAdapter<IDName>(getContext(), android.R.layout.simple_spinner_item, regionList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        fragmentListenerAddRecordedCarBinding.regionsSpinner.setAdapter(spinnerArrayAdapter);
    }


    private void initObservers(){

        listenRecordsViewModel = ViewModelProviders.of(this).get(ListenRecordsViewModel.class);
        makeRecordsViewModel = ViewModelProviders.of(this).get(MakeRecordsViewModel.class);

        regionsObserver = new Observer<List<IDName>>() {
            @Override
            public void onChanged(List<IDName> idNames) {

                if (idNames!=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        hideProgress();

                        if (idNames.isEmpty()) {

                            Toast.makeText(getContext(), getResources().getString(R.string.err_loading), Toast.LENGTH_SHORT).show();

                        } else {

                            initRegionSpinner();
                            regionList.addAll(idNames);
                            spinnerArrayAdapter.notifyDataSetChanged();

                            //set selected item

                            if (Utils.getSelectedIDNameItemInSpinner(idNames, master.getRegions()) != null)
                                fragmentListenerAddRecordedCarBinding.regionsSpinner.setSelection(spinnerArrayAdapter.getPosition
                                        (Utils.getSelectedIDNameItemInSpinner(idNames, master.getRegions())));
                        }
                    }
                }
            }
        };


        recordsListObserver = new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {

                if (records!=null) {

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        hideProgress();

                        if (records.isEmpty())
                            showSnackBar(R.string.no_records);

                        else {

                            if (records.get(0).getId().equals("-1"))
                                showFailedDialog(getResources().getString(R.string.err_loading), true);
                            else {
                                recordList.clear();
                                recordList.addAll(records);
                                recordsAdapter.notifyDataSetChanged();
                            }
                        }

                        getRegions();
                    }
                }
            }
        };


        updateHeardRecordObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s != null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        if (!s.equals("")){

                            if (recordList.get(recordList.size()-1).getFileName().contains("firebase"))
                                deleteRecord();

                            else {

                                if (recordList.size() > 1) {

                                    recordList.remove(recordList.size() - 1);

                                    setHeardToAllRecords();
                                }

                                else {

                                    hideProgress();

                                    ((MainActivity)getActivity()).navController.popBackStack();
                                }
                            }

                        }

                        else {

                            hideProgress();

                            showFailedDialog(getResources().getString(R.string.err_loading), true);
                        }

                    }

                }
            }
        };


        deleteRecordObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        if (!TextUtils.isEmpty(s)){

                            if (recordList.size() > 1){

                                recordList.remove(recordList.size() - 1);

                                setHeardToAllRecords();
                            }

                            else {

                                hideProgress();

                                ((MainActivity)getActivity()).navController.popBackStack();
                            }


                        }

                        else{

                            hideProgress();

                            showFailedDialog(getResources().getString(R.string.fail_delete_rec), true);
                        }

                    }

                }
            }
        };


        /*addRecordedCarObsever = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s != null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        hideProgress();

                        switch (s) {

                            case "error":
                                Toast.makeText(getContext(), getResources().getString(R.string.err_insert),
                                        Toast.LENGTH_SHORT).show();
                                break;

                            case "-1":
                                showInfoDialogWithOneButton(getResources().getString(R.string.err_insert),
                                        getResources().getString(R.string.added_car_before_msg), getResources().getString
                                                (R.string.dialog_ok_button), new Closure() {
                                            @Override
                                            public void exec() {

                                                hideInfoDialogWithTwoButton();
                                            }
                                        }, true);

                                break;

                            case "0":
                                Toast.makeText(getContext(), getResources().getString(R.string.err_insert),
                                        Toast.LENGTH_SHORT).show();
                                break;

                            default:

                                showSuccessDialog(getResources().getString(R.string.succ_add_master), true);

                                setTextsFromMaster();

                                getRecordsList();
                        }
                    }
                }
            }
        };

         */
    }


    private void setTextsFromMaster(){

        master = (Master) getArguments().getSerializable("master");

        /*fragmentListenerAddRecordedCarBinding.vichelAddressEdt.setText(master.getLocation());
        fragmentListenerAddRecordedCarBinding.vichelDistrictEdt.setText(master.getDistrict());
        fragmentListenerAddRecordedCarBinding.vichelTypeEdt.setText(master.getVehicleType());
        fragmentListenerAddRecordedCarBinding.vichelNumberEdt.setText(master.getVehicleNumber());
        fragmentListenerAddRecordedCarBinding.vichelNotesTxt.setText(master.getNotes());
        */
        fragmentListenerAddRecordedCarBinding.latLngTxt.setText(getResources().getString(R.string.latitude) + " : " + master.getLatitude()
                + " || " + getResources().getString(R.string.longitude) + " : " + master.getLongitude());


    }


    private void getRecordsList(){

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.load_records_msg), false);

        listenRecordsViewModel.getMasterRecordListMutableLiveData(master.getId(), MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), recordsListObserver);
    }


    private void getRegions(){

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.load_regions_msg), false);

        makeRecordsViewModel.getGetRegionsLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), regionsObserver);
    }


    private void setHeardToAllRecords(){

        listenRecordsViewModel.getUpdateHeardMutableLiveData(recordList.get(recordList.size()-1).getId(),
              MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(getViewLifecycleOwner(), updateHeardRecordObserver);

    }


    private void deleteRecord(){

            listenRecordsViewModel.getDeleteRecordFirebaseMutableLiveData(recordList.get(recordList.size()-1).getFileName()
                    .substring(recordList.get(recordList.size()-1).getFileName().indexOf("com.alsalameg"),
                            recordList.get(recordList.size()-1).getFileName().indexOf("?alt"))).observe(getViewLifecycleOwner(),
                    deleteRecordObserver);

    }

    private void renewAddRecordedCarsList(){

        addRecordedCarsList.clear();

        for (int a=0; a<5; a++)
            addRecordedCarsList.add("");

        addRecordedCarsAdapter.notifyDataSetChanged();

    }


    public String getSelectedRegionID(){

        return selectedRegionID;
    }

    /*private boolean getTexts(){

        if (TextUtils.isEmpty(fragmentListenerAddRecordedCarBinding.vichelNumberEdt.getText())){

            fragmentListenerAddRecordedCarBinding.vichelNumberEdt.setError(getResources().getString(R.string.enter_vic_num));
            return false;
        }

        else if (TextUtils.isEmpty(fragmentListenerAddRecordedCarBinding.vichelTypeEdt.getText())){

            fragmentListenerAddRecordedCarBinding.vichelTypeEdt.setError(getResources().getString(R.string.enter_vic_type));
            return false;
        }

        else if (TextUtils.isEmpty(fragmentListenerAddRecordedCarBinding.vichelDistrictEdt.getText())){

            fragmentListenerAddRecordedCarBinding.vichelDistrictEdt.setError(getResources().getString(R.string.enter_vic_district));
            return false;
        }

        else if (TextUtils.isEmpty(fragmentListenerAddRecordedCarBinding.vichelAddressEdt.getText())){

            fragmentListenerAddRecordedCarBinding.vichelAddressEdt.setError(getResources().getString(R.string.enter_vic_addr));
            return false;
        }

        else
            return true;


    }

     */

}
