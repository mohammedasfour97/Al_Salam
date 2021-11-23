package com.alsalameg.UI.FragmentDialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalameg.Adapters.MastersAdapter;
import com.alsalameg.Adapters.SameMultipleMarksAdapter;
import com.alsalameg.BaseClasses.BaseDialog;
import com.alsalameg.Models.Car;
import com.alsalameg.R;
import com.alsalameg.ViewModels.MapViewModel;
import com.alsalameg.databinding.FragmentDialogCarMarkerDetailsBinding;
import com.alsalameg.databinding.FragmentDialogSameMultipleMarksBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class SameMultipleLatlongMarksFragment extends DialogFragment {

    private FragmentDialogSameMultipleMarksBinding fragmentDialogSameMultipleMarksBinding;
    private List<Car> carList;
    private SameMultipleMarksAdapter sameMultipleMarksAdapter;
    private LatLng latLng;

    public SameMultipleLatlongMarksFragment(List<Car> carList, LatLng latLng) {
        this.carList = carList;
        this.latLng = latLng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentDialogSameMultipleMarksBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_same_multiple_marks,
                container, false);
        return fragmentDialogSameMultipleMarksBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().getAttributes().windowAnimations = R.style.Sliding_Top_Down_Dialog_Animation;
        }

        initRecyclerView();
        setListeners();
    }


    private void initRecyclerView(){

        sameMultipleMarksAdapter = new SameMultipleMarksAdapter(carList, this, latLng);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        fragmentDialogSameMultipleMarksBinding.marksRecycler.recyclerview.setLayoutManager(mLayoutManager);
        fragmentDialogSameMultipleMarksBinding.marksRecycler.recyclerview.setAdapter(sameMultipleMarksAdapter);
    }


    private void setListeners(){

        fragmentDialogSameMultipleMarksBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }
}
