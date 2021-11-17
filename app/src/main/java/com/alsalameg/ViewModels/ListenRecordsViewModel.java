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

    public MutableLiveData<List<Master>> getMasterListenerListMutableLiveData(String id) {

        return listenRecordsRepository.getMasterListenerListMutableLiveData(id);
    }

    public MutableLiveData<List<Master>> getMasterListenerRecordedCarsMutableLiveData(String id, String id_user) {

        return listenRecordsRepository.getMasterListenerRecordedCarsMutableLiveData(id, id_user);
    }

    public MutableLiveData<String> getDeleteMasterWithRecordsMutableLiveData(String masterId, String idUser) {

        return listenRecordsRepository.getDeleteMasterWithRecordsMutableLiveData(masterId, idUser);
    }

    public MutableLiveData<String> getDeleteListenerRecordedCarMutableLiveData(String masterId, String idUser) {

        return listenRecordsRepository.getDeleteListenerRecordedCarMutableLivedata(masterId, idUser);
    }

    public MutableLiveData<String> getDeleteRecordMutableLiveData(String recordId, String idUser) {

        return listenRecordsRepository.getDeleteRecordMutableLiveData(recordId, idUser);
    }

    public MutableLiveData<List<Record>> getMasterRecordListMutableLiveData(String id, String id_user) {

        return listenRecordsRepository.getMasterRecordsMutableLiveData(id, id_user);
    }

    public MutableLiveData<String> getAddRecordedCarMutableLiveData(String vehicleNumber, String vehicleType, String location, String district,
                                                                    String longitude, String latitude, String idUSER, String idRegions,
                                                                    String notes , String masterId){

        return listenRecordsRepository.getAddRecordedCarMutableLiveData(vehicleNumber ,vehicleType, location,  district, longitude,
                latitude,  idUSER,  idRegions, notes, masterId);
    }
}
