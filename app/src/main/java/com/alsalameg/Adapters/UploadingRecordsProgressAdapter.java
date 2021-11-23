package com.alsalameg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalameg.Api.WebServices;
import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.Models.Record;
import com.alsalameg.Models.UploadingRecord;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.Utils;
import com.alsalameg.ViewModels.MakeRecordsViewModel;
import com.alsalameg.databinding.ItemRecordPlayerBinding;
import com.alsalameg.databinding.ItemUploadingRecordProcessBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

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

    public class UploadingRecordsProgressViewHolder extends RecyclerView.ViewHolder {

        private ItemUploadingRecordProcessBinding itemUploadingRecordProcessBinding;

        public UploadingRecordsProgressViewHolder(ItemUploadingRecordProcessBinding itemUploadingRecordProcessBinding) {
            super(itemUploadingRecordProcessBinding.getRoot());

            this.itemUploadingRecordProcessBinding = itemUploadingRecordProcessBinding;
        }


        private void uploadRecord(String recordFilePath, String recordFileName, UploadingRecord uploadingRecord, String id){

            if (fragment.hasInternetConnection(context)) {

                try {
                    InputStream iStream = fragment.getActivity().getContentResolver()
                            .openInputStream(Uri.fromFile(new File(recordFilePath)));
                    byte[] voiceBytes = Utils.getBytes(iStream);


                    if (fragment.hasInternetConnection(context)) {

                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                                View.VISIBLE);
                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                fragment.getResources().getString(R.string.uploading_record)
                                        + "\n" + recordFileName);

                        //// request to upload/////
                        new UploadAsyncClass(voiceBytes, recordFileName, MyApplication.getTinyDB().getString
                                (Constants.KEY_USERID), uploadingRecord, id).execute();
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

            }

        }

        // TODO: 11/23/2021 add insert to room rechis when start to upload

        private class UploadAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

            private byte[] recordBytes;
            private String fileName, userId, uploadedRecordId;
            private UploadingRecord uploadingRecord;

            public UploadAsyncClass(byte[] recordBytes, String fileName, String userId, UploadingRecord uploadingRecord,
                                    String uploadedRecordId) {
                this.recordBytes = recordBytes;
                this.fileName = fileName;
                this.userId = userId;
                this.uploadingRecord = uploadingRecord;
                this.uploadedRecordId = uploadedRecordId;
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
                                uploadingRecordList.get(Integer.parseInt(uploadedRecordId))
                                        .getRecordName());
                    }
                }

                else{

                    fragment.showFailedDialog(fragment.
                            getResources().getString(R.string.error_upload_record), true);

                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                            View.GONE);
                    UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                            uploadingRecordList.get(Integer.parseInt(uploadedRecordId))
                                    .getRecordName());
                }

            }

        }


        private void addUploadedRecordToMaster(UploadingRecord uploadingRecord){

            makeRecordsViewModel.insertRecordToMasterLiveDatag(uploadingRecord.getMasterId(), uploadingRecord.getRecordName(),
                    "3gp", String.valueOf(Utils.getImageSizeFromUriInMegaByte(Uri.fromFile(new File
                            (uploadingRecord.getRecordFilePath())), context)), MyApplication.getTinyDB().getString
                            (Constants.KEY_USERID)).observe(fragment.getViewLifecycleOwner(), uploadRecordToMasterObserver(
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

                            if (succUploaded){

                                Toast.makeText(context, context.getResources().getString(R.string.add_rec_to_master_succ_msg)
                                        , Toast.LENGTH_SHORT).show();

                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular
                                        .setVisibility(View.GONE);
                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                        fragment.getResources().getString(R.string.succ_upload_record));
                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.reloadRecordIcon
                                        .setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_baseline_close_prim_24));
                                uploadingRecord.setUploaded(true);
                            }

                            else {

                                Toast.makeText(context, failedMsg, Toast.LENGTH_SHORT).show();

                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular
                                        .setVisibility(View.GONE);
                                UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                        uploadingRecord.getRecordName());
                            }
                        }

                    }
                }
            };
        }

    }

    public UploadingRecordsProgressAdapter(List<UploadingRecord> uploadingRecordList ,Context context,
                                           BaseFragment fragment) {
        this.uploadingRecordList = uploadingRecordList;
        this.context = context;
        this.fragment = fragment;

        makeRecordsViewModel = ViewModelProviders.of(fragment).get(MakeRecordsViewModel.class);
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
                    holder.uploadRecord(uploadingRecord.getRecordFilePath(), uploadingRecord.getRecordName(), uploadingRecord,
                        String.valueOf(position));
            }
        });

        ////// Upload the record ////////

        if (!uploadingRecord.getUploading()) {

            uploadingRecord.setUploading(true);
            holder.uploadRecord(uploadingRecord.getRecordFilePath(), uploadingRecord.getRecordName(), uploadingRecord,
                    String.valueOf(position));
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
