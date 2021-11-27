package com.alsalamegypt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsalamegypt.Adapters.Interfaces.OnDeleteMaster;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.Models.Record;
import com.alsalamegypt.ViewModels.ListenRecordsViewModel;
import com.alsalamegypt.databinding.ItemRecordPlayerBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder> {
    private List<Record> recordList;
    private Context context;
    private ListenRecordsViewModel listenRecordsViewModel;
    private BaseFragment fragment;
    private OnDeleteMaster onDeleteMaster;

    public class RecordsViewHolder extends RecyclerView.ViewHolder {

        private ItemRecordPlayerBinding itemRecordPlayerBinding;

        public RecordsViewHolder(ItemRecordPlayerBinding itemRecordPlayerBinding) {
            super(itemRecordPlayerBinding.getRoot());

            this.itemRecordPlayerBinding = itemRecordPlayerBinding;
        }


        private void deleteRecord(String recordId, String idUser, int pos){

            fragment.showInfoDialogWithTwoButtons(context.getResources().getString(R.string.delete), context.getResources().
                    getString(R.string.sure_delete_record), context.getResources().getString(R.string.yes), context.getResources().
                    getString(R.string.no), new Closure() {
                @Override
                public void exec() {

                    fragment.showProgressDialog("", context.getResources().getString(R.string.loading_msg), false);

                    listenRecordsViewModel.getDeleteRecordMutableLiveData(recordId, idUser).observe(fragment.getViewLifecycleOwner(),
                            deleteRecordObserver(pos));
                }
            }, new Closure() {
                @Override
                public void exec() {

                    fragment.hideInfoDialogWithTwoButton();
                }
            }, true);


        }


        private void updateHeard(String recId){

            listenRecordsViewModel.getUpdateHeardMutableLiveData(recId, MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                    .observe(fragment.getViewLifecycleOwner(), updateHeardObserver());
        }

        private Observer<String> deleteRecordObserver(int pos){

            return new Observer<String>() {
                @Override
                public void onChanged(String id) {

                    if (id!=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            fragment.hideProgress();

                            if (!TextUtils.isEmpty(id)){

                                RecordsAdapter.this.recordList.remove(pos);
                                RecordsAdapter.this.notifyItemRemoved(pos);
                                RecordsAdapter.this.notifyDataSetChanged();
                            }
                        }
                    }
                }
            };
        }


        private Observer<String> updateHeardObserver(){

            return new Observer<String>() {
                @Override
                public void onChanged(String msg) {

                    if (msg!=null){

                        if (fragment.getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                            //////
                        }
                    }
                }
            };
        }

    }

    public RecordsAdapter(List<Record> recordList, Context context, BaseFragment fragment) {
        this.recordList = recordList;
        this.context = context;
        this.fragment = fragment;

        listenRecordsViewModel = ViewModelProviders.of(fragment).get(ListenRecordsViewModel.class);
    }

    public RecordsAdapter(List<Record> recordList,OnDeleteMaster onDeleteMaster, Context context, BaseFragment fragment) {
        this.recordList = recordList;
        this.context = context;
        this.fragment = fragment;
        this.onDeleteMaster = onDeleteMaster;

        listenRecordsViewModel = ViewModelProviders.of(fragment).get(ListenRecordsViewModel.class);
    }

    @Override
    public RecordsAdapter.RecordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRecordPlayerBinding itemRecordPlayerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_record_player, parent, false);
        return new RecordsAdapter.RecordsViewHolder(itemRecordPlayerBinding);

    }

    @Override
    public void onBindViewHolder(RecordsAdapter.RecordsViewHolder holder, int position) {

        Record record = recordList.get(position);

        holder.itemRecordPlayerBinding.voicePlayerView.setAudio(Constants.ImageURl + record.getFileName());

        /// hide delete if the listener who is listening

        if (!MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Recorded"))
            holder.itemRecordPlayerBinding.deleteRecord.setVisibility(View.GONE);


        holder.itemRecordPlayerBinding.voicePlayerView.setImgPlayClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Listener"))
                    holder.updateHeard(record.getId());
            }
        });

        //if (MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE).equals("Listener") && r)

        holder.itemRecordPlayerBinding.shareRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constants.ImageURl + record.getFileName());
                context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_voice)));
            }
        });


        holder.itemRecordPlayerBinding.deleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recordList.size() == 1)
                    onDeleteMaster.deleteMaster();
                else
                    holder.deleteRecord(record.getId(), MyApplication.getTinyDB().getString(Constants.KEY_USERID), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(recordList.get(position).getId());
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(recordList.get(position).getId());
    }

}
