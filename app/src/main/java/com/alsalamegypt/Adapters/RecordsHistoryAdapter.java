package com.alsalamegypt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalamegypt.Api.WebServices;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.Record;
import com.alsalamegypt.Models.UploadingRecord;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.R;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.ItemHistoryRecordBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class RecordsHistoryAdapter extends RecyclerView.Adapter<RecordsHistoryAdapter.RecordsHistoryViewHolder> {
    private List<RecordHistory> recordHistoryList;
    private Context context;
    private MakeRecordsViewModel makeRecordsViewModel;
    private BaseFragment fragment;

    public RecordsHistoryAdapter(List<RecordHistory> recordHistoryList, Context context, BaseFragment fragment) {
        this.recordHistoryList = recordHistoryList;
        this.context = context;
        this.fragment = fragment;

        makeRecordsViewModel = ViewModelProviders.of(fragment).get(MakeRecordsViewModel.class);
    }

    public class RecordsHistoryViewHolder extends RecyclerView.ViewHolder {

        private ItemHistoryRecordBinding itemHistoryRecordBinding;

        public RecordsHistoryViewHolder(ItemHistoryRecordBinding itemHistoryRecordBinding) {
            super(itemHistoryRecordBinding.getRoot());

            this.itemHistoryRecordBinding = itemHistoryRecordBinding;
        }

        private void uploadRecord(RecordHistory recordHistory){

            if (fragment.hasInternetConnection(context)) {

                //this.itemHistoryRecordBinding.progress.setVisibility(View.VISIBLE);

                fragment.showProgressDialog(context.getResources().getString(R.string.loading), context.getResources().getString(R.string.loading_msg), false);

//                        UploadAsyncClass uploadAsyncClass = new UploadAsyncClass(voiceBytes,
//                                MyApplication.getTinyDB().getString(Constants.KEY_USERID), recordHistory);
//
//                        uploadAsyncClass.execute();
                makeRecordsViewModel.getUploadRecordMutableLiveData(Uri.fromFile(new File(recordHistory.getRecordFilePath())),
                        recordHistory.getRecordName()).observe(fragment.getViewLifecycleOwner(), uploadRecordObserver(recordHistory));

            }

        }


        private Observer<Map<String,String>> uploadRecordObserver(RecordHistory recordHistory){

            return new Observer<Map<String, String>>() {
                @Override
                public void onChanged(Map<String, String> stringStringMap) {

                    if (stringStringMap != null) {

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                            switch (stringStringMap.get("success")) {

                                case "": ///// Still trying to upload
                                    break;

                                case "false":

                                    fragment.hideProgress();

                                    fragment.showFailedDialog(fragment.getResources().getString(R.string.error_upload_record), true);

                                    break;

                                case "progress":

                                    break;

                                default:
                                    //// Successed ////

                                    recordHistory.setRecordName(stringStringMap.get("success"));
                                    addUploadedRecordToMaster(recordHistory);

                            }
                        }
                    }
                }
            };
        }


        private void addUploadedRecordToMaster(RecordHistory recordHistory){

            makeRecordsViewModel.insertRecordToMasterLiveDatag(String.valueOf(recordHistory.getMasterId()),
                    recordHistory.getRecordName(), "3gp",
                    String.valueOf(Utils.getImageSizeFromUriInMegaByte(Uri.fromFile(new File(recordHistory.getRecordFilePath())),
                            context)), MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                    .observe(fragment.getViewLifecycleOwner(), uploadRecordToMasterObserver(recordHistory));
        }



        private Observer<String> uploadRecordToMasterObserver(RecordHistory recordHistory){

            return new Observer<String>() {
                @Override
                public void onChanged(String s) {

                    if (s!=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            fragment.hideProgress();

                            boolean succUploaded;
                            String msg = "";

                            switch (s){

                                case "error" :

                                    succUploaded = false;

                                    msg = context.getResources().getString(R.string.add_rec_to_master_fail_msg);

                                    break;

                                case "-1" :

                                    succUploaded = true;

                                    msg = context.getResources().getString(R.string.added_record_before);

                                    break;

                                case "0":

                                    succUploaded = false;

                                    msg = context.getResources().getString(R.string.add_rec_to_master_fail_msg);

                                    break;

                                default:

                                    succUploaded = true;

                                    msg = context.getResources().getString(R.string.add_rec_to_master_succ_msg);

                            }

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                            RecordsHistoryViewHolder.this.itemHistoryRecordBinding.progress.setVisibility(View.INVISIBLE);

                            recordHistory.setIsUploaded(String.valueOf(succUploaded));

                            updateRecordHistory(recordHistory);
                        }

                    }
                }
            };
        }



        private void updateRecordHistory(RecordHistory recordHistory){

            makeRecordsViewModel.getUpdateRecordHistoryMutableLiveData(recordHistory).observe(fragment.getViewLifecycleOwner(),
                    updateRecordHistoryObserver(recordHistory));
        }



        private Observer<Integer> updateRecordHistoryObserver(RecordHistory recordHistory){

            return new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {

                   /*asyncClassesList.remove(uploadAsyncClass);

                   if (asyncClassesList.isEmpty())
                        RecordsHistoryAdapter.this.notifyDataSetChanged();

                    */

                    requestDeleteRecordFromDB(recordHistory);

                }
            };
        }



        private void deleteRecord(RecordHistory recordHistory){

            fragment.showInfoDialogWithTwoButtons(context.getResources().getString(R.string.sure_del_rec),
                    context.getResources().getString(R.string.sure_del_rec), context.getResources().getString(R.string.yes),
                    context.getResources().getString(R.string.no), new Closure() {
                        @Override
                        public void exec() {

                            requestDeleteRecordFromDB(recordHistory);

                        }
                    }, new Closure() {
                        @Override
                        public void exec() {

                            fragment.hideInfoDialogWithTwoButton();
                        }
                    }, true);

        }


        private void requestDeleteRecordFromDB(RecordHistory recordHistory){

            RecordsHistoryViewHolder.this.itemHistoryRecordBinding.progress.setVisibility(View.VISIBLE);

            makeRecordsViewModel.getDeleteRecordHistoryMutableLiveData(recordHistory).observe(fragment.getViewLifecycleOwner(),
                    deleteRecordObserver(recordHistory));
        }


        private Observer<Integer> deleteRecordObserver(RecordHistory recordHistory){

            return new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {

                    if (integer !=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            RecordsHistoryViewHolder.this.itemHistoryRecordBinding.progress.setVisibility(View.INVISIBLE);

                            if (integer!= -1 && integer!= 0){

                                //fragment.showSnackBar(R.string.suc_delete_rec);

                                recordHistoryList.remove(recordHistory);
                                RecordsHistoryAdapter.this.notifyItemRemoved(recordHistoryList.indexOf(recordHistory));

                            }

                            else {

                                fragment.showFailedDialog(fragment.
                                        getResources().getString(R.string.fail_delete_rec), true);

                            }
                        }
                    }
                }
            };
        }

    }




    @Override
    public RecordsHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHistoryRecordBinding itemHistoryRecordBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_history_record, parent, false);
        return new RecordsHistoryViewHolder(itemHistoryRecordBinding);

    }

    @Override
    public void onBindViewHolder(RecordsHistoryViewHolder holder, int position) {

        RecordHistory recordHistory = recordHistoryList.get(position);

        holder.itemHistoryRecordBinding.addressTxt.setText(recordHistory.getAddress());
        holder.itemHistoryRecordBinding.carPlateNumTxt.setText(recordHistory.getPlateNumber());
        holder.itemHistoryRecordBinding.regionTxt.setText(recordHistory.getRegion());

        if (Boolean.parseBoolean(recordHistory.getIsUploaded())){

            holder.itemHistoryRecordBinding.shareRecordImg.setVisibility(View.VISIBLE);
            holder.itemHistoryRecordBinding.completeImg.setImageDrawable(context.getResources().getDrawable
                    (R.drawable.ic_round_done_outline_24));
        }

        else
            holder.itemHistoryRecordBinding.completeImg.setImageDrawable(context.getResources().getDrawable
                    (R.drawable.ic_baseline_refresh_24));


        ////// set audio /////

        if (Boolean.parseBoolean(recordHistory.getIsUploaded()))
            holder.itemHistoryRecordBinding.voicePlayerView.setAudio(Constants.ImageURl + recordHistory.getRecordName());
        else
            holder.itemHistoryRecordBinding.voicePlayerView.setAudio(recordHistory.getRecordFilePath());


        ////// Reload record if not uploaded ///////

        holder.itemHistoryRecordBinding.completeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Boolean.parseBoolean(recordHistory.getIsUploaded()))
                    holder.uploadRecord(recordHistory);
            }
        });


        holder.itemHistoryRecordBinding.shareRecordImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constants.ImageURl + recordHistory.getRecordName());
                context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_voice)));
            }
        });


        holder.itemHistoryRecordBinding.deleteRecordImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.deleteRecord(recordHistory);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordHistoryList.size();
    }

    @Override
    public long getItemId(int position) {
        return recordHistoryList.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return recordHistoryList.get(position).getId();
    }

}
