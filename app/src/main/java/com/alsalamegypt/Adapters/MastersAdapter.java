package com.alsalamegypt.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalamegypt.Adapters.Interfaces.OnDeleteMaster;
import com.alsalamegypt.Api.WebServices;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.Models.Record;
import com.alsalamegypt.UI.MainActivity;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.ListenRecordsViewModel;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.ItemMasterBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MastersAdapter extends RecyclerView.Adapter<MastersAdapter.MastersViewHolder>{
    private final List<Master> masterList;
    private final Context context;
    private final BaseFragment fragment;
    private final ListenRecordsViewModel listenRecordsViewModel;
    private boolean listenerRecordedCars;

    public MastersAdapter(List<Master> masterList, Context context, BaseFragment fragment) {
        this.masterList = masterList;
        this.context = context;
        this.fragment = fragment;
        listenRecordsViewModel = ViewModelProviders.of(fragment).get(ListenRecordsViewModel.class);
    }


    public MastersAdapter(List<Master> masterList, Context context, BaseFragment fragment, boolean listenerRecordedCars) {
        this.masterList = masterList;
        this.context = context;
        this.fragment = fragment;
        this.listenerRecordedCars = listenerRecordedCars;
        listenRecordsViewModel = ViewModelProviders.of(fragment).get(ListenRecordsViewModel.class);
    }


    public class MastersViewHolder extends RecyclerView.ViewHolder {

        private final ItemMasterBinding itemRecordBinding;
        private final SupportMapFragment mapFragment;
        private final List<View> viewList;
        private final List<String> values;
        private List<Record> recordList;
        private RecordsAdapter recordsAdapter;
        private OnDeleteMaster onDeleteMaster;



        public MastersViewHolder(ItemMasterBinding itemRecordBinding) {
            super(itemRecordBinding.getRoot());
            mapFragment = (SupportMapFragment) fragment.getChildFragmentManager().findFragmentById(R.id.item_record_map);

            viewList = new ArrayList<>();
            values = new ArrayList<>();

            this.itemRecordBinding = itemRecordBinding;
        }


        private void loadRecords(String id){

            MastersViewHolder.this.itemRecordBinding.progress.setVisibility(View.VISIBLE);
            MastersViewHolder.this.itemRecordBinding.loadRecListBtn.setVisibility(View.GONE);

//            fragment.showProgressDialog(context.getResources().getString(R.string.loading),
//                    context.getResources().getString(R.string.loading_msg), false);

            new GetMasterRecordsAsyncClass(id).execute();
        }


        class GetMasterRecordsAsyncClass extends AsyncTask<String, String, List<HashMap<String,String>>> {

            private final String id;

            public GetMasterRecordsAsyncClass(String id) {
                this.id = id;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            protected List<HashMap<String,String>> doInBackground(String... args) {

                return new WebServices().getMasterRecords(id, MyApplication.getTinyDB().getString(Constants.KEY_USERID));
            }
            /**
             * After completing background task Dismiss the progress dialog
             * **/
            protected void onPostExecute(List<HashMap<String,String>> recordsMap) {

                MastersViewHolder.this.itemRecordBinding.progress.setVisibility(View.GONE);

                if (recordsMap!=null){

                    //fragment.hideProgress();

                    Record record;
                    List<Record> recordList = new ArrayList<>();
                    for (HashMap<String,String> hashMap : recordsMap){

                        record = new Record(hashMap.get("ID"), hashMap.get("ID_Recorded"), hashMap.get("File_Name"),
                                hashMap.get("File_Extension"), hashMap.get("File_Size"), hashMap.get("heard"));


                        recordList.add(record);

                    }

                    MastersViewHolder.this.itemRecordBinding.recyclerview.setVisibility(View.VISIBLE);
                    MastersViewHolder.this.recordList.addAll(recordList);
                    MastersViewHolder.this.recordsAdapter.notifyDataSetChanged();
                }
                else {

                    fragment.showFailedDialog(context.getResources().getString(R.string.err_load_rec), true);

                    MastersViewHolder.this.itemRecordBinding.loadRecListBtn.setVisibility(View.VISIBLE);
                }

            }

        }


        private void deleteMaster(String masterId, String idUser, int pos, boolean isRecorder){

            fragment.showInfoDialogWithTwoButtons(context.getResources().getString(R.string.delete), context.getResources().
                    getString(R.string.sure_delete_master), context.getResources().getString(R.string.yes), context.getResources().
                    getString(R.string.no), new Closure() {
                @Override
                public void exec() {

                    fragment.showProgressDialog("", context.getResources().getString(R.string.loading_msg), false);

                    if (isRecorder)
                        listenRecordsViewModel.getDeleteMasterWithRecordsMutableLiveData(masterId, idUser).observe(MastersAdapter.this.fragment
                                .getViewLifecycleOwner(), deleteMasterObserver(masterId, pos, isRecorder));
                    else
                        listenRecordsViewModel.getDeleteListenerRecordedCarMutableLiveData(masterId, idUser).observe(MastersAdapter.this.fragment
                                .getViewLifecycleOwner(), deleteMasterObserver(masterId, pos, isRecorder));
                }
            }, new Closure() {
                @Override
                public void exec() {

                    fragment.hideInfoDialogWithTwoButton();
                }
            }, true);


        }


        private Observer<String> deleteMasterObserver(String masterId, int pos, boolean isRecorder){

            return new Observer<String>() {
                @Override
                public void onChanged(String id) {

                    if (id!=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            fragment.hideProgress();

                            if (!TextUtils.isEmpty(id)){

                                MastersAdapter.this.masterList.remove(pos);
                                MastersAdapter.this.notifyItemRemoved(pos);
                                MastersAdapter.this.notifyDataSetChanged();

                                if (isRecorder)
                                    deleteMasterRecordFromArchieve(masterId);
                            }

                            else {

                                fragment.showFailedDialog(context.getResources().getString(R.string.fail_del), true);
                            }
                        }
                    }
                }
            };
        }


        private void deleteMasterRecordFromArchieve(String masterId){

            ViewModelProviders.of(fragment).get(MakeRecordsViewModel.class).getDeleteRecordHistoryMutableLiveData(masterId)
                    .observe(fragment.getViewLifecycleOwner(), deleteMasterRecordsFromArchiveObserver());
        }


        private Observer<Integer> deleteMasterRecordsFromArchiveObserver(){

            return new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {

                    if (integer !=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            if (integer== -1 || integer== 0){

                                //fragment.showFailedDialog(context.getResources().getString(R.string.fail_delete_rec_arch), false);

                            }

                        }
                    }
                }
            };
        }

    }

    @Override
    public MastersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMasterBinding itemRecordBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_master, parent, false);
        return new MastersViewHolder(itemRecordBinding);

    }

    @Override
    public void onBindViewHolder(MastersViewHolder holder, int position) {

        Master master = masterList.get(holder.getAdapterPosition());

        holder.viewList.add(holder.itemRecordBinding.notesTxt);
        holder.viewList.add(holder.itemRecordBinding.districtTxt);
        holder.viewList.add(holder.itemRecordBinding.vehcileTypeTxt);
        holder.viewList.add(holder.itemRecordBinding.vehicleNumTxt);

        holder.values.add(master.getNotes());
        holder.values.add(master.getDistrict());
        holder.values.add(master.getVehicleType());
        holder.values.add(master.getVehicleNumber());

        Utils.hideIfEmpty(holder.viewList, holder.values);

        holder.itemRecordBinding.createDateTxt.setText(Utils.getStringKeyValue(context, R.string.date_create,
                Utils.formatDate("dd MMM yyyy hh:mm a", master.getDateCreate())));
        holder.itemRecordBinding.editDateTxt.setText(Utils.getStringKeyValue(context, R.string.date_edit,
                Utils.formatDate("dd MMM yyyy hh:mm a", master.getDateEdit())));
        holder.itemRecordBinding.districtTxt.setText(Utils.getStringKeyValue(context, R.string.district,master.getDistrict()));
        holder.itemRecordBinding.locationTxt.setText(Utils.getStringKeyValue(context, R.string.location,master.getLocation()));
        holder.itemRecordBinding.notesTxt.setText(Utils.getStringKeyValue(context, R.string.notes,master.getNotes()));
        holder.itemRecordBinding.regionTxt.setText(Utils.getStringKeyValue(context, R.string.region,master.getRegions()));
        holder.itemRecordBinding.latitudeTxt.setText(Utils.getStringKeyValue(context, R.string.latitude,master.getLatitude()));
        holder.itemRecordBinding.longitudeTxt.setText(Utils.getStringKeyValue(context, R.string.longitude,master.getLongitude()));
        holder.itemRecordBinding.sortingTxt.setText(Utils.getStringKeyValue(context, R.string.status,master.getSorting()));
        holder.itemRecordBinding.vehcileTypeTxt.setText(Utils.getStringKeyValue(context, R.string.vichel_type, master.getVehicleType()));
        holder.itemRecordBinding.vehicleNumTxt.setText(Utils.getStringKeyValue(context, R.string.vichel_num, master.getVehicleNumber()));


        /// hide delete if the listener who is listening and change color if master is heard ////

        if (!MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Recorded")){

            if (!listenerRecordedCars){

                holder.itemRecordBinding.deleteBtn.setVisibility(View.GONE);

//                if (master.getHeard().equals("1"))
//                    holder.itemRecordBinding.topRadiusLayout.setBackground(context.getResources().getDrawable
//                            (R.drawable.orange_solid_top_radius_));
            }
        }



        // init google map
//        FrameLayout frame = new FrameLayout(context);
//        frame.setId(2_525); //you have to set unique id
//
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, context.getResources().getDisplayMetrics());
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        frame.setLayoutParams(layoutParams);
//
//        holder.itemRecordBinding.itemRecordMap.addView(frame);
//
//        GoogleMapOptions options = new GoogleMapOptions();
//        options.liteMode(true);
//        SupportMapFragment mapFrag = SupportMapFragment.newInstance(options);
//        mapFrag.onResume();
//
//        mapFrag.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
//
//                LatLng latLng = new LatLng(Double.parseDouble(master.getLatitude()), Double.parseDouble(master.getLongitude()));
//                googleMap.addMarker(new MarkerOptions().position(latLng));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//
//            }
//        });
//
//        FragmentManager fm = fragment.getChildFragmentManager();
//        fm.beginTransaction().add(frame.getId(), mapFrag).commit();


        // init map
        holder.itemRecordBinding.itemRecordMap.onCreate(null);
        holder.itemRecordBinding.itemRecordMap.onResume();
        holder.itemRecordBinding.itemRecordMap.setClickable(false);
        holder.itemRecordBinding.itemRecordMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

                LatLng latLng = new LatLng(Double.parseDouble(master.getLatitude()), Double.parseDouble(master.getLongitude()));
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                googleMap.getUiSettings().setMapToolbarEnabled(false);

            }
        });



        if (MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Recorded")
                && Boolean.parseBoolean(master.getType())){

            holder.itemRecordBinding.loadRecListBtn.setVisibility(View.VISIBLE);
            holder.itemRecordBinding.recyclerview.setVisibility(View.GONE);

            holder.onDeleteMaster = new OnDeleteMaster() {
                @Override
                public void deleteMaster() {

                    holder.deleteMaster(master.getId(), master.getIdUSER(), position, true);
                }
            };
        }



        holder.itemRecordBinding.loadRecListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    holder.recordList = new ArrayList<>();
                    holder.recordsAdapter = new RecordsAdapter(holder.recordList , holder.onDeleteMaster, context, fragment);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                    holder.itemRecordBinding.recyclerview.setLayoutManager(mLayoutManager);
                    holder.itemRecordBinding.recyclerview.addItemDecoration(new DividerItemDecoration(context,
                            LinearLayoutManager.VERTICAL));
                    holder.recordsAdapter.setHasStableIds(true);
                    holder.itemRecordBinding.recyclerview.setAdapter(holder.recordsAdapter);

                    holder.loadRecords(master.getId());
                }

        });


        holder.itemRecordBinding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.deleteMaster(master.getId(), master.getIdUSER(), position,
                        MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Recorded"));

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!listenerRecordedCars){

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("master", master);
                    ((MainActivity) fragment.getActivity()).navController.navigate(
                            R.id.action_fragment_listen_to_fragment_listener_add_recorded_car, bundle);
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return masterList.size();
    }

//    @Override
//    public long getItemId(int position)
//    {
//
//        return Long.parseLong(masterList.get(position).getId());
//    }
//
//    @Override
//    public int getItemViewType(int position)
//    {
//        return Integer.parseInt(masterList.get(position).getId());
//    }

    /*
    private void loadRecords(String id, MastersViewHolder mastersViewHolder){

        mastersViewHolder.itemRecordBinding.progress.setVisibility(View.VISIBLE);

        listenRecordsViewModel.getMasterRecordListMutableLiveData(id).observe(MastersAdapter.this.fragment
                        .getViewLifecycleOwner(), recordsObserver(mastersViewHolder));
    }

    private Observer<List<Record>> recordsObserver(MastersViewHolder mastersViewHolder){

        return new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {

                if (records!=null){

                    mastersViewHolder.itemRecordBinding.progress.setVisibility(View.GONE);

                    if (!records.get(0).getId().equals("-1")){

                        mastersViewHolder.recordList.addAll(records);
                        mastersViewHolder.recordsAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }


     */


}
