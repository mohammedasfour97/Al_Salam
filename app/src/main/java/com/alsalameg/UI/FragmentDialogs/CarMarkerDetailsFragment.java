package com.alsalameg.UI.FragmentDialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alsalameg.BaseClasses.BaseDialog;
import com.alsalameg.Models.Car;
import com.alsalameg.Models.IDName;
import com.alsalameg.R;
import com.alsalameg.ViewModels.MakeRecordsViewModel;
import com.alsalameg.ViewModels.MapViewModel;
import com.alsalameg.databinding.FragmentDialogAddCarInfoBinding;
import com.alsalameg.databinding.FragmentDialogCarMarkerDetailsBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class CarMarkerDetailsFragment extends DialogFragment {

    private FragmentDialogCarMarkerDetailsBinding fragmentDialogCarMarkerDetailsBinding;
    private Car car;
    private BaseDialog baseDialog;

    // vars for api
    private MapViewModel mapViewModel;
    private LatLng latLng;

    public CarMarkerDetailsFragment(Car car, LatLng latLng) {
        this.car = car;
        this.latLng = latLng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentDialogCarMarkerDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_car_marker_details, container,
                false);
        return fragmentDialogCarMarkerDetailsBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().getAttributes().windowAnimations = R.style.Sliding_Top_Down_Dialog_Animation;
        }

        setData();
        setListeners();

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        baseDialog = new BaseDialog(getContext());
    }


    private void setData(){

        fragmentDialogCarMarkerDetailsBinding.bankTxt.setText(getResources().getString(R.string.bank) + " " + car.getBank());
        fragmentDialogCarMarkerDetailsBinding.districtTxt.setText(getResources().getString(R.string.district) + " : " + car.getDistrict());
        fragmentDialogCarMarkerDetailsBinding.notesTxt.setText(getResources().getString(R.string.notes) + " : " + car.getNotes());
        fragmentDialogCarMarkerDetailsBinding.regionTxt.setText(getResources().getString(R.string.region) + " : " + car.getRegions());
        fragmentDialogCarMarkerDetailsBinding.statusTxt.setText(getResources().getString(R.string.status) + " : " + car.getStatus());
        fragmentDialogCarMarkerDetailsBinding.vehicleColorTxt.setText(getResources().getString(R.string.car_color) + " " + car.getColor());
        fragmentDialogCarMarkerDetailsBinding.vehicleKindTxt.setText(getResources().getString(R.string.car_kind) + " " + car.getKind());
        fragmentDialogCarMarkerDetailsBinding.vehicleMakerTxt.setText(getResources().getString(R.string.maker) + " " + car.getMaker());
        fragmentDialogCarMarkerDetailsBinding.vehicleNumTxtTxt.setText(getResources().getString(R.string.vichel_num) + " : " +
                car.getPlateNumber());
    }

    private void setListeners(){

        fragmentDialogCarMarkerDetailsBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        fragmentDialogCarMarkerDetailsBinding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                baseDialog.awesomeInfoWithTwoButtonsDialog(getResources().getString(R.string.sure), getResources()
                                .getString(R.string.sure_conf_this_car), getResources().getString(R.string.yes), getResources().getString(R.string.no),
                        new Closure() {
                            @Override
                            public void exec() {

                                baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg),
                                        false).show();

                                mapViewModel.getConfirmCarMutableLiveData(car.getId(), "true", String.valueOf(latLng.latitude),
                                        String.valueOf(latLng.longitude)).observe(getViewLifecycleOwner(), confirmCarObserver());
                            }
                        }, new Closure() {
                            @Override
                            public void exec() {

                                baseDialog.hideInfoDialogWithTwoButton();
                            }
                        }, true).show();

            }
        });
    }

    private Observer<String> confirmCarObserver(){

        return new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        baseDialog.hideProgress();

                        if (!s.equals("error")){

                            baseDialog.awesomeSuccessDialog(getResources().getString(R.string.confir_car_succ), true).show();
                            dismiss();
                        }

                        else
                            baseDialog.awesomeErrorDialog(getResources().getString(R.string.fail_confirm), true).show();
                    }
                }
            }
        };
    }
}

