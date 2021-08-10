package com.alsalameg.ViewModels;

import com.alsalameg.Models.IDName;
import com.alsalameg.Repositories.MakeRecordsRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MakeRecordsViewModel extends ViewModel {

    private MakeRecordsRepository makeRecordsRepository;

    public MakeRecordsViewModel() {

        makeRecordsRepository = new MakeRecordsRepository();
    }

    public MutableLiveData<String> getUploadRecordMutableLiveData(byte[] recordBytes, String fileName){

        return makeRecordsRepository.getMutableLiveData(recordBytes, fileName);
    }

    public MutableLiveData<List<IDName>> getGetRegionsLiveData() {
        return makeRecordsRepository.getGetRegionsLiveData();
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
}
