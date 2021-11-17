package com.alsalameg.Adapters;

import android.content.Context;
import android.content.Intent;
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
        private List<View> editTexts;

        public ListenerAddRecordedCarsViewHolder(ItemListenerAddRecordedCarBinding itemListenerAddRecordedCarBinding) {
            super(itemListenerAddRecordedCarBinding.getRoot());

            this.itemListenerAddRecordedCarBinding = itemListenerAddRecordedCarBinding;

            editTexts = new ArrayList<>();
            editTexts.add(itemListenerAddRecordedCarBinding.vichelNumberEdt);
            editTexts.add(itemListenerAddRecordedCarBinding.vichelTypeEdt);
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

                            Boolean completed;

                            switch (s) {

                                case "error":

                                    Toast.makeText(context, context.getResources().getString(R.string.err_insert),
                                            Toast.LENGTH_SHORT).show();

                                    completed = false;

                                    break;

                                case "-1":

                                    Toast.makeText(context, context.getResources().getString(R.string.added_car_before_msg),
                                            Toast.LENGTH_SHORT).show();

                                    completed = false;

                                    break;

                                case "0":

                                    Toast.makeText(context, context.getResources().getString(R.string.err_insert),
                                            Toast.LENGTH_SHORT).show();

                                    completed = false;

                                default:

                                    completed = true;
                            }

                            Utils.setEnableOrNot(editTexts, !completed);
                            itemListenerAddRecordedCarBinding.loadingBtn.setEnabled(!completed);

                            ListenerAddRecordedCarsViewHolder.this.itemListenerAddRecordedCarBinding.loadingBtn.complete(completed);
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

        holder.itemListenerAddRecordedCarBinding.loadingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.setEnableOrNot(holder.editTexts, false);

                holder.itemListenerAddRecordedCarBinding.loadingBtn.start();

                holder.insertAddRecordedCar();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
