package com.alsalamegypt.UI.FragmentDialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalamegypt.Adapters.SameMultipleMarksAdapter;
import com.alsalamegypt.Models.Car;
import com.alsalamegypt.R;
import com.alsalamegypt.databinding.FragmentDialogCarMarkerDetailsBinding;
import com.alsalamegypt.databinding.FragmentDialogSameMultipleMarksBinding;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class SameMultipleLatlongMarksFragment extends DialogFragment {

    private FragmentDialogSameMultipleMarksBinding fragmentDialogSameMultipleMarksBinding;
    private List<Car> carList;
    private SameMultipleMarksAdapter sameMultipleMarksAdapter;
    private Location location;

    public SameMultipleLatlongMarksFragment(List<Car> carList, Location location) {
        this.carList = carList;
        this.location = location;
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

        sameMultipleMarksAdapter = new SameMultipleMarksAdapter(carList, this, location);
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
