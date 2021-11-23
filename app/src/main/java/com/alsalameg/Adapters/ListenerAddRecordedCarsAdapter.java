package com.alsalameg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.Models.Master;
import com.alsalameg.Models.Record;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.UI.ListenerAddRecordedCarFragment;
import com.alsalameg.Utils;
import com.alsalameg.ViewModels.ListenRecordsViewModel;
import com.alsalameg.databinding.ItemListenerAddRecordedCarBinding;
import com.alsalameg.databinding.ItemRecordPlayerBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.flod.drawabletextview.DrawableTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class ListenerAddRecordedCarsAdapter extends RecyclerView.Adapter<ListenerAddRecordedCarsAdapter
        .ListenerAddRecordedCarsViewHolder> {
    private List list;
    private Context context;
    private Master master;
    private ListenRecordsViewModel listenRecordsViewModel;
    private Fragment fragment;

    public class ListenerAddRecordedCarsViewHolder extends RecyclerView.ViewHolder {

        private ItemListenerAddRecordedCarBinding itemListenerAddRecordedCarBinding;
        private List<View> allViews;

        public ListenerAddRecordedCarsViewHolder(ItemListenerAddRecordedCarBinding itemListenerAddRecordedCarBinding) {
            super(itemListenerAddRecordedCarBinding.getRoot());

            this.itemListenerAddRecordedCarBinding = itemListenerAddRecordedCarBinding;

            allViews = new ArrayList<>();
            allViews.add(itemListenerAddRecordedCarBinding.vichelNumberEdt);
            allViews.add(itemListenerAddRecordedCarBinding.vichelTypeEdt);
            allViews.add(itemListenerAddRecordedCarBinding.loadingBtn);
        }

        private boolean getTextFromEDTs(){

            if (TextUtils.isEmpty(this.itemListenerAddRecordedCarBinding.vichelNumberEdt.getText().toString())){

                this.itemListenerAddRecordedCarBinding.vichelNumberEdt.setError(context.getResources().getString(R.string.enter) +
                        " " + context.getResources().getString(R.string.vichel_num));

                return false;
            }

            else if (TextUtils.isEmpty(this.itemListenerAddRecordedCarBinding.vichelTypeEdt.getText().toString())) {
                this.itemListenerAddRecordedCarBinding.vichelTypeEdt.setError(context.getResources().getString(R.string.enter) + " " +
                        context.getResources().getString(R.string.vichel_type));
                return false;
            }
            else if (((ListenerAddRecordedCarFragment)fragment).getSelectedRegionID().equals("0")){

                Toast.makeText(context, R.string.choose_region_message, Toast.LENGTH_SHORT).show();
                return false;
            }

            else return true;
        }

        private void insertAddRecordedCar(){

            listenRecordsViewModel.getAddRecordedCarMutableLiveData(itemListenerAddRecordedCarBinding.vichelNumberEdt
                            .getText().toString(), itemListenerAddRecordedCarBinding.vichelTypeEdt.getText().toString(),
                    master.getLocation(), master.getDistrict(), master.getLongitude(), master.getLatitude(),
                    MyApplication.getTinyDB().getString(Constants.KEY_USERID),
                    ((ListenerAddRecordedCarFragment)fragment).getSelectedRegionID(), "", master.getId())
                    .observe(fragment.getViewLifecycleOwner(), addRecordedCarObserver());
        }


        private Observer<String> addRecordedCarObserver(){

            return new Observer<String>() {
                @Override
                public void onChanged(String s) {

                    if (s != null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                            Boolean completed = false;
                            float alpha = 1;

                            switch (s) {

                                case "error":

                                    Toast.makeText(context, context.getResources().getString(R.string.err_insert),
                                            Toast.LENGTH_SHORT).show();

                                    break;

                                case "-1":

                                    Toast.makeText(context, context.getResources().getString(R.string.added_car_before_msg),
                                            Toast.LENGTH_SHORT).show();

                                    break;

                                case "0":

                                    Toast.makeText(context, context.getResources().getString(R.string.err_insert),
                                            Toast.LENGTH_SHORT).show();

                                default:

                                    completed = true;
                                    alpha = .5f;
                            }

                            Utils.setEnableOrNot(allViews, !completed);
                            itemListenerAddRecordedCarBinding.mainLayout.setAlpha(alpha);


                            itemListenerAddRecordedCarBinding.loadingBtn.complete(completed);
                        }
                    }
                }
            };

        }

    }

    public ListenerAddRecordedCarsAdapter(List list, Master master, Context context, Fragment fragment) {
        this.list = list;
        this.master = master;
        this.context = context;
        this.fragment = fragment;

        listenRecordsViewModel = ViewModelProviders.of(fragment).get(ListenRecordsViewModel.class);
    }


    @Override
    public ListenerAddRecordedCarsAdapter.ListenerAddRecordedCarsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemListenerAddRecordedCarBinding itemListenerAddRecordedCarBinding = DataBindingUtil.inflate
                (LayoutInflater.from(parent.getContext()), R.layout.item_listener_add_recorded_car, parent, false);
        return new ListenerAddRecordedCarsAdapter.ListenerAddRecordedCarsViewHolder(itemListenerAddRecordedCarBinding);

    }

    @Override
    public void onBindViewHolder(ListenerAddRecordedCarsAdapter.ListenerAddRecordedCarsViewHolder holder, int position) {

        holder.itemListenerAddRecordedCarBinding.mainLayout.setAlpha(1);
        holder.itemListenerAddRecordedCarBinding.vichelNumberEdt.setText("");
        holder.itemListenerAddRecordedCarBinding.vichelTypeEdt.setText("");

        Utils.setEnableOrNot(holder.allViews, true);

        //// init loading btn /////

        holder.itemListenerAddRecordedCarBinding.loadingBtn.setEnableShrink(true)
                .setEnableRestore(false)
                .setDisableClickOnLoading(true)
                .setShrinkDuration(450)
                .setLoadingPosition(DrawableTextView.POSITION.START)
                .setSuccessDrawable(R.drawable.ic_successful)
                .setFailDrawable(R.drawable.ic_fail)
                .setEndDrawableKeepDuration(900)
                .setLoadingEndDrawableSize((int) (holder.itemListenerAddRecordedCarBinding.loadingBtn.getTextSize() * 2));
        holder.itemListenerAddRecordedCarBinding.loadingBtn.getLoadingDrawable()
                .setStrokeWidth(holder.itemListenerAddRecordedCarBinding.loadingBtn.getTextSize() * 0.14f);
        holder.itemListenerAddRecordedCarBinding.loadingBtn.getLoadingDrawable()
                .setColorSchemeColors(holder.itemListenerAddRecordedCarBinding.loadingBtn.getTextColors().getDefaultColor());

        holder.itemListenerAddRecordedCarBinding.loadingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getTextFromEDTs()){

                    Utils.setEnableOrNot(holder.allViews, false);

                    holder.itemListenerAddRecordedCarBinding.loadingBtn.start();

                    holder.insertAddRecordedCar();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
