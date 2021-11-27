package com.alsalamegypt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.R;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.ItemHistoryRecordBinding;

import java.util.List;

import androidx.databinding.DataBindingUtil;
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


        private void deleteRecord(RecordHistory recordHistory){


        }

        private void uploadRecord(RecordHistory recordHistory){


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
