package com.alsalameg.ViewModels;

import com.alsalameg.Models.Master;
import com.alsalameg.Models.Record;
import com.alsalameg.Repositories.ListenRecordsRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListenRecordsViewModel extends ViewModel {

    private ListenRecordsRepository listenRecordsRepository;

    public ListenRecordsViewModel() {

        listenRecordsRepository = new ListenRecordsRepository();
    }

    public MutableLiveData<List<Master>> getMasterListMutableLiveData(String id) {

        return listenRecordsRepository.getMasterListMutableLiveData(id);
    }

    public MutableLiveData<String> getDeleteMasterWithRecordsMutableLiveData(String masterId, String idUser) {

        return listenRecordsRepository.getDeleteMasterWithRecordsMutableLiveData(masterId, idUser);
    }

    public MutableLiveData<String> getDeleteRecordMutableLiveData(String recordId, String idUser) {

        return listenRecordsRepository.getDeleteRecordMutableLiveData(recordId, idUser);
    }

    public MutableLiveData<List<Record>> getMasterRecordListMutableLiveData(String id) {

        return listenRecordsRepository.getMasterRecordsMutableLiveData(id);
    }


}
