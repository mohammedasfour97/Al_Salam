package com.alsalamegypt.ViewModels;

import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.Repositories.MakeRecordsRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MakeRecordsViewModel extends ViewModel {

    private MakeRecordsRepository makeRecordsRepository;

    public MakeRecordsViewModel() {

        makeRecordsRepository = new MakeRecordsRepository();
    }

    public MutableLiveData<String> getUploadRecordMutableLiveData(byte[] recordBytes, String fileName, String userId){

        return makeRecordsRepository.getMutableLiveData(recordBytes, fileName, userId);
    }

    public MutableLiveData<List<IDName>> getGetRegionsLiveData(String userId) {
        return makeRecordsRepository.getGetRegionsLiveData(userId);
    }

    public MutableLiveData<String> insertMasterRecordString (String vehicleNumber, String vehicleType, String location, String district, String longitude,
    String latitude, String idUSER, String idRegions, String notes, boolean type){

        return makeRecordsRepository.getInsertRecordMasterLiveData(vehicleNumber ,vehicleType, location,  district, longitude,  latitude,
                idUSER,  idRegions, notes, type);
    }

    public MutableLiveData<String> insertRecordToMasterLiveDatag (String idRecorded, String fileName, String fileExtension,
                                                                  String fileSize, String idUser){

        return makeRecordsRepository.getUploadRecordToMasterLiveData(idRecorded, fileName, fileExtension, fileSize, idUser);
    }

    public MutableLiveData<List<Master>> getRecorderDailyCarsLiveData(String id){

        return makeRecordsRepository.getGetRecorderDailyCarsMutableLiveData(id);
    }

    public MutableLiveData<Long> getInsertRecordHistoryMutableLiveData(RecordHistory recordHistory) {

        return makeRecordsRepository.getInsertRecordHistoryMutableLiveData(recordHistory);
    }


    public MutableLiveData<Integer> getDeleteAllRecordHistoryMutableLiveData() {

        return makeRecordsRepository.getDeleteAllRecordHistoryMutableLiveData();
    }


    public MutableLiveData<Integer> getDeleteRecordHistoryMutableLiveData(RecordHistory recordHistory) {

        return makeRecordsRepository.getDeleteRecordHistoryMutableLiveData(recordHistory);
    }


    public MutableLiveData<List<RecordHistory>> getGetAllRecordHistoryMutableLiveData() {

        return makeRecordsRepository.getGetAllRecordHistoryMutableLiveData();
    }


    public MutableLiveData<Integer> getUpdateRecordHistoryMutableLiveData(RecordHistory recordHistory) {

        return makeRecordsRepository.getUpdateRecordHistoryMutableLiveData(recordHistory);
    }


    public MutableLiveData<RecordHistory> getGetRecordHistoryMutableLiveData(int id) {

        return makeRecordsRepository.getGetRecordHistoryMutableLiveData(id);
    }
}
