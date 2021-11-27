package com.alsalamegypt.UI.FragmentDialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alsalamegypt.BaseClasses.BaseDialog;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.FragmentDialogAddCarInfoBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class AddCarDetailsFragment extends DialogFragment {

    private FragmentDialogAddCarInfoBinding fragmentDialogAddCarInfoBinding;
    private BaseDialog baseDialog;
    private String recordOrForm;

    //vars for spinner
    private ArrayAdapter<IDName> spinnerArrayAdapter;
    private String selectedRegionID;

    //vars for observing data
    private MakeRecordsViewModel makeRecordsViewModel;
    private Observer<List<IDName>> regionsObserver;
    private Observer<String> addMasterObserver;

    private String selectedAddress, selectedSubAdminArea, selectedSubLocality, lonitude, latitude;


    public AddCarDetailsFragment(String selectedAddress, String selectedSubAdminArea, String selectedSubLocality, String lonitude,
                                 String latitude) {
        this.selectedAddress = selectedAddress;
        this.selectedSubAdminArea = selectedSubAdminArea;
        this.selectedSubLocality = selectedSubLocality;
        this.lonitude = lonitude;
        this.latitude = latitude;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentDialogAddCarInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_add_car_info, container,
                false);
        return fragmentDialogAddCarInfoBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().getAttributes().windowAnimations = R.style.Sliding_Right_Left_Dialog_Animation;
        }

        baseDialog = new BaseDialog(getContext());

        initObservers();
        fillRegionsSpinner();
        setListeners();
    }

    private void initObservers(){

        makeRecordsViewModel = ViewModelProviders.of(this).get(MakeRecordsViewModel.class);

        regionsObserver = new Observer<List<IDName>>() {
            @Override
            public void onChanged(List<IDName> idNames) {

                if (idNames!=null){

                    baseDialog.hideProgress();

                    if (idNames.isEmpty()){

                        Toast.makeText(getContext(), getResources().getString(R.string.err_loading), Toast.LENGTH_SHORT).show();
                        AddCarDetailsFragment.this.dismiss();
                    }

                    else {

                        spinnerArrayAdapter = new ArrayAdapter<IDName>(getContext(), android.R.layout.simple_spinner_item, idNames);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
                        fragmentDialogAddCarInfoBinding.regionsSpinner.setAdapter(spinnerArrayAdapter);
                    }

                }
            }
        };

        addMasterObserver = new Observer<String>() {

            @Override
            public void onChanged(String s) {

                if (s != null){

                    baseDialog.hideProgress();

                    switch (s){

                        case "error" :
                            Toast.makeText(getContext(), getResources().getString(R.string.err_insert),
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case "-1" :
                            baseDialog.awesomeInfoWithOneButtonsDialog(getResources().getString(R.string.err_insert),
                                    getResources().getString(R.string.added_before_msg),getResources().getString
                                            (R.string.dialog_ok_button), new Closure() {
                                        @Override
                                        public void exec() {

                                            baseDialog.hideInfoDialogWithTwoButton();
                                        }
                                    }, true).show();

                            MyApplication.getTinyDB().putString("master_id", "");

                            dismiss();
                            break;

                        case "0":
                            Toast.makeText(getContext(), getResources().getString(R.string.err_insert),
                                    Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            baseDialog.awesomeSuccessDialog(getResources().getString(R.string.succ_add_master), true)
                                    .show();

                            if (recordOrForm.equals("record"))
                                MyApplication.getTinyDB().putString("master_id", s);
                            else
                                MyApplication.getTinyDB().putString("master_id", "");
                            dismiss();

                    }

                }
            }
        };
    }


    private void setListeners(){

        fragmentDialogAddCarInfoBinding.locationTxt.setText(getResources().getString(R.string.location) + " " + selectedAddress);

        fragmentDialogAddCarInfoBinding.regionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedRegionID = ((IDName)parent.getSelectedItem()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fragmentDialogAddCarInfoBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApplication.getTinyDB().putString("master_id", "");

                dismiss();
            }
        });

        fragmentDialogAddCarInfoBinding.sendRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recordOrForm = "record";

                insertMasterRecordString(Utils.generateUniqueNumber(), "", "", true);
            }
        });

        fragmentDialogAddCarInfoBinding.sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getTextFromEDTs()){

                    baseDialog.awesomeInfoWithTwoButtonsDialog(getResources().getString(R.string.sure),
                            getResources().getString(R.string.sure_car_data_msg), getResources().getString(R.string.yes),
                            getResources().getString(R.string.no), new Closure() {
                                @Override
                                public void exec() {

                                    recordOrForm = "form";

                                    insertMasterRecordString(fragmentDialogAddCarInfoBinding.vichelNumberEdt.getText().toString(),
                                            fragmentDialogAddCarInfoBinding.vichelTypeEdt.getText().toString(),
                                            fragmentDialogAddCarInfoBinding.notesTxt.getText().toString(), false);

                                }
                            }, new Closure() {
                                @Override
                                public void exec() {

                                    baseDialog.hideInfoDialogWithTwoButton();
                                }
                            }, true).show();

                }
            }
        });
    }


    private void fillRegionsSpinner(){

        baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), getResources().getString
                (R.string.loading_msg),false).show();

        makeRecordsViewModel.getGetRegionsLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), regionsObserver);
    }


    private boolean getTextFromEDTs(){

        if (TextUtils.isEmpty(fragmentDialogAddCarInfoBinding.vichelNumberEdt.getText().toString())){

            fragmentDialogAddCarInfoBinding.vichelNumberEdt.setError(getResources().getString(R.string.enter) + " " +
                    getResources().getString(R.string.vichel_num));

            return false;
        }

        else if (TextUtils.isEmpty(fragmentDialogAddCarInfoBinding.vichelTypeEdt.getText().toString())) {
            fragmentDialogAddCarInfoBinding.vichelTypeEdt.setError(getResources().getString(R.string.enter) + " " +
                    getResources().getString(R.string.vichel_type));
            return false;
        }

        else return true;
    }


    private void insertMasterRecordString(String vichelNum, String vichelType, String notes, boolean type){

        baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), getResources().getString
                (R.string.loading_msg),false).show();

        makeRecordsViewModel.insertMasterRecordString(vichelNum, vichelType, selectedAddress,
                Utils.chooseNonNull(selectedSubLocality, selectedSubAdminArea), lonitude, latitude,
                MyApplication.getTinyDB().getString(Constants.KEY_USERID), selectedRegionID, notes, type)
                .observe(getViewLifecycleOwner(), addMasterObserver);
    }
}
