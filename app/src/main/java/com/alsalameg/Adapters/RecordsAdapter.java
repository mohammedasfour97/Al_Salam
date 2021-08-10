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
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.Models.Record;
import com.alsalameg.UI.MainActivity;
import com.alsalameg.UI.MakeRecordsFragment;
import com.alsalameg.ViewModels.ListenRecordsViewModel;
import com.alsalameg.databinding.ItemRecordPlayerBinding;
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

    public class RecordsViewHolder extends RecyclerView.ViewHolder {

        private ItemRecordPlayerBinding itemRecordPlayerBinding;

        public RecordsViewHolder(ItemRecordPlayerBinding itemRecordPlayerBinding) {
            super(itemRecordPlayerBinding.getRoot());

            this.itemRecordPlayerBinding = itemRecordPlayerBinding;
        }

    }

    public RecordsAdapter(List<Record> recordList, Context context, BaseFragment fragment) {
        this.recordList = recordList;
        this.context = context;
        this.fragment = fragment;

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

                deleteRecord(record.getId(), MyApplication.getTinyDB().getString(Constants.KEY_USERID), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordList.size();
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
}
