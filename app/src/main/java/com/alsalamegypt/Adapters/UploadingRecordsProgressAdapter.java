package com.alsalamegypt.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalamegypt.Api.WebServices;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.Models.UploadingRecord;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.ItemRecordPlayerBinding;
import com.alsalamegypt.databinding.ItemUploadingRecordProcessBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class UploadingRecordsProgressAdapter extends RecyclerView.Adapter
        <UploadingRecordsProgressAdapter.UploadingRecordsProgressViewHolder> {
    private List<UploadingRecord> uploadingRecordList;
    private Context context;
    private BaseFragment fragment;
    private MakeRecordsViewModel makeRecordsViewModel;

    public UploadingRecordsProgressAdapter(List<UploadingRecord> uploadingRecordList ,Context context,
                                           BaseFragment fragment) {
        this.uploadingRecordList = uploadingRecordList;
        this.context = context;
        this.fragment = fragment;

        makeRecordsViewModel = ViewModelProviders.of(fragment).get(MakeRecordsViewModel.class);
    }


    public class UploadingRecordsProgressViewHolder extends RecyclerView.ViewHolder {

        private ItemUploadingRecordProcessBinding itemUploadingRecordProcessBinding;
        private RecordHistory recordHistory;

        public UploadingRecordsProgressViewHolder(ItemUploadingRecordProcessBinding itemUploadingRecordProcessBinding) {
            super(itemUploadingRecordProcessBinding.getRoot());

            this.itemUploadingRecordProcessBinding = itemUploadingRecordProcessBinding;
        }


        private void insertRecordToArchive(UploadingRecord uploadingRecord){

            Master master = uploadingRecord.getMaster();

            recordHistory = new RecordHistory(Integer.parseInt(master.getId()), master.getIdRegions(),master.getRegions(),
                    master.getVehicleNumber(), master.getLocation(), uploadingRecord.getRecordName(),
                    uploadingRecord.getRecordFileUri().getPath(), String.valueOf(false));

            makeRecordsViewModel.getInsertRecordHistoryMutableLiveData(recordHistory).observe(fragment.getViewLifecycleOwner()
                    ,uploadRecordHistoryObserver(uploadingRecord));
        }



        private Observer<Long> uploadRecordHistoryObserver(UploadingRecord uploadingRecord){

            return new Observer<Long>() {
                @Override
                public void onChanged(Long aLong) {

                    if (aLong !=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            if (aLong!= -1 && aLong!= 0){

                                recordHistory.setId(Integer.parseInt(String.valueOf(aLong)));
                                uploadRecord(uploadingRecord);
                            }

                            else {

                                fragment.showFailedDialog(fragment.
                                        getResources().getString(R.string.error_upload_record), true);

                            }
                        }
                    }
                }
            };
        }



        private void uploadRecord(UploadingRecord uploadingRecord){

            if (fragment.hasInternetConnection(context)) {


                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                        View.VISIBLE);
                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                        fragment.getResources().getString(R.string.uploading_record) + "\n" + uploadingRecord.getRecordName());

                makeRecordsViewModel.getUploadRecordMutableLiveData(uploadingRecord.getRecordFileUri(), uploadingRecord.getRecordName())
                        .observe(fragment.getViewLifecycleOwner(), uploadRecordObserver(uploadingRecord));

                /*try {
                    InputStream iStream = fragment.getActivity().getContentResolver()
                            .openInputStream(Uri.fromFile(new File(uploadingRecord.getRecordFilePath())));
                    byte[] voiceBytes = Utils.getBytes(iStream);


                    if (fragment.hasInternetConnection(context)) {

                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                                View.VISIBLE);
                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                fragment.getResources().getString(R.string.uploading_record) + "\n" + uploadingRecord.getRecordName());

                        //// request to upload/////
                        new UploadAsyncClass(voiceBytes, uploadingRecord.getRecordName(), MyApplication.getTinyDB().getString
                                (Constants.KEY_USERID), uploadingRecord).execute();
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fragment.showFailedDialog(fragment.
                            getResources().getString(R.string.error_upload_record), true);


                } catch (IOException e) {
                    e.printStackTrace();
                    fragment.showFailedDialog(fragment.
                            getResources().getString(R.string.error_upload_record), true);

                }


                 */
            }

        }


        private Observer<Map<String,String>> uploadRecordObserver(UploadingRecord uploadingRecord){

            return new Observer<Map<String, String>>() {
                @Override
                public void onChanged(Map<String, String> stringStringMap) {

                    if (stringStringMap != null) {

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                            switch (stringStringMap.get("success")) {

                                case "": ///// Still trying to upload
                                    break;

                                case "false":

                                    fragment.showFailedDialog(fragment.getResources().getString(R.string.error_upload_record),
                                            true);

                                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular
                                            .setVisibility(View.GONE);
                                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                            uploadingRecord.getRecordName());
                                    break;

                                case "progress":

                                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                            fragment.getResources().getString(R.string.uploading_record) + "\n"
                                                    + uploadingRecord.getRecordName() + "  " + stringStringMap.get("progress"));
                                    break;

                                default:
                                    //// Successed ////

                                    uploadingRecord.setRecordName(stringStringMap.get("success"));
                                    addUploadedRecordToMaster(uploadingRecord);

                            }
                        }
                    }
                }
            };
        }

        /*private class UploadAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

            private byte[] recordBytes;
            private String fileName, userId;
            private UploadingRecord uploadingRecord;

            public UploadAsyncClass(byte[] recordBytes, String fileName, String userId, UploadingRecord uploadingRecord) {
                this.recordBytes = recordBytes;
                this.fileName = fileName;
                this.userId = userId;
                this.uploadingRecord = uploadingRecord;
            }

            @Override
            protected List<HashMap<String,String>> doInBackground(Void... voids) {
                return new WebServices().uploadRecordFile(recordBytes, fileName, userId);
            }

            @Override
            protected void onPostExecute(List<HashMap<String,String>> uploadedResultList) {

                if (uploadedResultList !=null && !uploadedResultList.isEmpty()) {

                    HashMap<String,String> uploadedResultMap = uploadedResultList.get(0);

                    if (uploadedResultMap.get("result").equals("DONE")){


                        addUploadedRecordToMaster(uploadingRecord);


                    }

                    else{

                        fragment.showFailedDialog(fragment.
                                getResources().getString(R.string.error_upload_record), true);

                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                                View.GONE);
                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                uploadingRecord.getRecordName());

                    }
                }

                else{

                    fragment.showFailedDialog(fragment.
                            getResources().getString(R.string.error_upload_record), true);

                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                            View.GONE);
                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText
                            .setText(uploadingRecord.getRecordName());

                }

            }

        }

         */

        private void addUploadedRecordToMaster(UploadingRecord uploadingRecord){

            makeRecordsViewModel.insertRecordToMasterLiveDatag(uploadingRecord.getMaster().getId(), uploadingRecord.getRecordName(),
                    "3gp", String.valueOf(Utils.getImageSizeFromUriInMegaByte(uploadingRecord.getRecordFileUri(), context))
                    , MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(fragment.getViewLifecycleOwner(),
                    uploadRecordToMasterObserver(
                            uploadingRecord));
        }



        private Observer<String> uploadRecordToMasterObserver(UploadingRecord uploadingRecord){

            return new Observer<String>() {
                @Override
                public void onChanged(String s) {

                    if (s!=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            boolean succUploaded;
                            String failedMsg = "";

                            switch (s){

                                case "error" :

                                    succUploaded = false;

                                    failedMsg = context.getResources().getString(R.string.add_rec_to_master_fail_msg);

                                    break;

                                case "-1" :

                                    succUploaded = false;

                                    failedMsg = context.getResources().getString(R.string.added_record_before);

                                    break;

                                case "0":

                                    succUploaded = false;

                                    failedMsg = context.getResources().getString(R.string.add_rec_to_master_fail_msg);

                                    break;

                                default:

                                    succUploaded = true;

                            }

                            UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular
                                    .setVisibility(View.GONE);


                            if (succUploaded){

                                Toast.makeText(context, context.getResources().getString(R.string.add_rec_to_master_succ_msg), Toast.LENGTH_SHORT).show();

                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                        fragment.getResources().getString(R.string.succ_upload_record));
                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.reloadRecordIcon
                                        .setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_baseline_close_prim_24));

                                uploadingRecordList.remove(uploadingRecord);
                                notifyItemRemoved(uploadingRecordList.indexOf(uploadingRecord));
                            }

                            else {

                                Toast.makeText(context, failedMsg, Toast.LENGTH_SHORT).show();

                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                        uploadingRecord.getRecordName());
                            }

                            uploadingRecord.setUploaded(succUploaded);
                            recordHistory.setIsUploaded(String.valueOf(succUploaded));

                            updateRecordHistory();
                        }

                    }
                }
            };
        }



        private void updateRecordHistory(){

            makeRecordsViewModel.getUpdateRecordHistoryMutableLiveData(recordHistory).observe(fragment.getViewLifecycleOwner(),
                    updateRecordHistoryObserver());
        }



        private Observer<Integer> updateRecordHistoryObserver(){

            return new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {

                    /////////////
                }
            };
        }

    }

    @Override
    public UploadingRecordsProgressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemUploadingRecordProcessBinding itemUploadingRecordProcessBinding = DataBindingUtil.inflate
                (LayoutInflater.from(parent.getContext()), R.layout.item_uploading_record_process, parent, false);
        return new UploadingRecordsProgressViewHolder(itemUploadingRecordProcessBinding);

    }

    @Override
    public void onBindViewHolder(UploadingRecordsProgressViewHolder holder, int position) {

        UploadingRecord uploadingRecord = uploadingRecordList.get(position);

        if (!uploadingRecord.getUploading())
            holder.itemUploadingRecordProcessBinding.loadingText.setText(context.getResources().getString(R.string.record_not_upload_msg) +
                "\n" + uploadingRecord.getRecordName());

        holder.itemUploadingRecordProcessBinding.reloadRecordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadingRecord.getUploaded()){

                    uploadingRecordList.remove(uploadingRecord);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                }

                else
                    holder.uploadRecord(uploadingRecord);
            }
        });

        ////// Upload the record ////////

        if (!uploadingRecord.getUploading()) {

            uploadingRecord.setUploading(true);
            holder.insertRecordToArchive(uploadingRecord);
        }

    }

    @Override
    public int getItemCount() {
        return uploadingRecordList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
