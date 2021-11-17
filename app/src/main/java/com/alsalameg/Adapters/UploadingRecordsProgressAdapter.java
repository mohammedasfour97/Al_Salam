package com.alsalameg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalameg.Api.WebServices;
import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.Models.Record;
import com.alsalameg.Models.UploadingRecord;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class UploadingRecordsProgressAdapter extends RecyclerView.Adapter
        <UploadingRecordsProgressAdapter.UploadingRecordsProgressViewHolder> {
    private List<UploadingRecord> uploadingRecordList;
    private Context context;
    private BaseFragment fragment;
    private MakeRecordsViewModel makeRecordsViewModel;
    private OnFinish onFinish;

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
                    byte[] voiceBytes = getBytes(iStream);


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

        private byte[] getBytes(InputStream inputStream) throws IOException {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }


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


                        onFinish.finish(uploadingRecord);

                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                                View.GONE);
                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                fragment.getResources().getString(R.string.succ_upload_record));
                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.reloadRecordIcon.setImageDrawable(
                                fragment.getResources().getDrawable
                                        (R.drawable.ic_baseline_close_prim_24));
                        uploadingRecord.setUploaded(true);

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

    }

    public UploadingRecordsProgressAdapter(List<UploadingRecord> uploadingRecordList, OnFinish onFinish ,Context context,
                                           BaseFragment fragment) {
        this.uploadingRecordList = uploadingRecordList;
        this.context = context;
        this.fragment = fragment;
        this.onFinish = onFinish;

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