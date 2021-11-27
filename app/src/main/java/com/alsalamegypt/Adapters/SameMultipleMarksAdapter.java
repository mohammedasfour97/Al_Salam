package com.alsalamegypt.Adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalamegypt.Models.Car;
import com.alsalamegypt.R;
import com.alsalamegypt.UI.FragmentDialogs.CarMarkerDetailsFragment;
import com.alsalamegypt.databinding.ItemSameMultipleLatlongMarksBinding;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class SameMultipleMarksAdapter extends RecyclerView.Adapter<SameMultipleMarksAdapter.SameMultipleMarksViewHolder> {
    private List<Car> carList;
    private Fragment fragment;
    private LatLng latLng;

    public class SameMultipleMarksViewHolder extends RecyclerView.ViewHolder {

       private ItemSameMultipleLatlongMarksBinding itemSameMultipleLatlongMarksBinding;
       private PorterDuffColorFilter porterDuffColorFilter;

        public SameMultipleMarksViewHolder(ItemSameMultipleLatlongMarksBinding itemSameMultipleLatlongMarksBinding) {
            super(itemSameMultipleLatlongMarksBinding.getRoot());
            this.itemSameMultipleLatlongMarksBinding = itemSameMultipleLatlongMarksBinding;
        }
    }

    public SameMultipleMarksAdapter(List<Car> carList, Fragment fragment, LatLng latLng) {
        this.carList = carList;
        this.fragment = fragment;
        this.latLng = latLng;
    }

    @Override
    public SameMultipleMarksAdapter.SameMultipleMarksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSameMultipleLatlongMarksBinding itemSameMultipleLatlongMarksBinding = DataBindingUtil.inflate(LayoutInflater.from
                        (parent.getContext()), R.layout.item_same_multiple_latlong_marks, parent, false);
        return new SameMultipleMarksAdapter.SameMultipleMarksViewHolder(itemSameMultipleLatlongMarksBinding);

    }

    @Override
    public void onBindViewHolder(SameMultipleMarksAdapter.SameMultipleMarksViewHolder holder, int position) {

        Car car = carList.get(position);
        holder.itemSameMultipleLatlongMarksBinding.bankTxt.setText(car.getBank());
        holder.itemSameMultipleLatlongMarksBinding.kindTxt.setText(car.getKind());
        holder.itemSameMultipleLatlongMarksBinding.plateNumberTxt.setText(car.getPlateNumber());
        holder.itemSameMultipleLatlongMarksBinding.statusTxt.setText(car.getStatus());

        /////  change icon color ///////

        holder.porterDuffColorFilter = new PorterDuffColorFilter(getMarkColor(car.getStatus()),
                PorterDuff.Mode.SRC_ATOP);
        holder.itemSameMultipleLatlongMarksBinding.markIcon.getDrawable().setColorFilter(holder.porterDuffColorFilter);
        holder.itemSameMultipleLatlongMarksBinding.statusTxt.setTextColor(getMarkColor(car.getStatus()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = fragment.getActivity().getSupportFragmentManager();
                CarMarkerDetailsFragment carMarkerDetailsFragment = new CarMarkerDetailsFragment(car, latLng);
                carMarkerDetailsFragment.show(fm, "fragment_new_activity");
            }
        });

    }

    private int getMarkColor(String carStatus){

        switch (carStatus){

            case "مجمع الشغل":
                return Color.HSVToColor(new float[] { 240.0f, 1.0f, 1.0f });

            case "احالات جديده":
                return Color.HSVToColor(new float[] { 120.0f, 1.0f, 1.0f });

            case "جرد جديد":
                return Color.HSVToColor(new float[] { 130.0f, 1.0f, 1.0f });

            case "احاله اليوم":
                return Color.HSVToColor(new float[] { 0.0f, 1.0f, 1.0f });

            case "شغل اليوم":
                return Color.HSVToColor(new float[] { 270.0f, 1.0f, 1.0f });

            case "تثبيت":
                return Color.HSVToColor(new float[] { 60.0f, 1.0f, 1.0f });

            case "ايقافات":
                return Color.HSVToColor(new float[] { 240.0f, 1.0f, 1.0f });

            default:
                return Color.HSVToColor(new float[] { 300.0f, 1.0f, 1.0f });
        }
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }
}
