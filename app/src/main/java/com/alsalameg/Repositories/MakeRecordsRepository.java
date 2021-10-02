package com.alsalameg.Repositories;

import android.os.AsyncTask;

import com.alsalameg.BaseClasses.BaseRepository;
import com.alsalameg.Models.IDName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class MakeRecordsRepository extends BaseRepository {

    private MutableLiveData<String> uploadRecordMutableLiveData, insertRecordMasterLiveData, uploadRecordToMasterLiveData;
    private MutableLiveData<List<IDName>> getRegionsLiveData;

    public MakeRecordsRepository() {
        super();

    }

    public MutableLiveData<String> getMutableLiveData(byte[] recordBytes, String fileName) {

        uploadRecordMutableLiveData = new MutableLiveData<>();

        new UploadAsyncClass(recordBytes, fileName).execute();

        return uploadRecordMutableLiveData;
    }

    public MutableLiveData<List<IDName>> getGetRegionsLiveData() {

        getRegionsLiveData = new MutableLiveData<>();

        new GetRegionsAsyncClass().execute();

        return getRegionsLiveData;
    }

    public MutableLiveData<String> getInsertRecordMasterLiveData(String vehicleNumber, String vehicleType, String location,
                                                                 String district, String longitude, String latitude, String idUSER,
                                                                 String idRegions, String notes, boolean type) {

        insertRecordMasterLiveData = new MutableLiveData<>();

        new InsertRecordMasterAsyncClass(vehicleNumber ,vehicleType, location,  district, longitude,  latitude,  idUSER,  idRegions,
                notes, type).execute();

        return insertRecordMasterLiveData;
    }

    public MutableLiveData<String> getUploadRecordToMasterLiveData(String idRecorded, String fileName, String fileExtension,
                                                                   String fileSize, String idUser) {

        uploadRecordToMasterLiveData = new MutableLiveData<>();

        new UploadRecordToMasterAsyncClass(idRecorded, fileName, fileExtension, fileSize, idUser).execute();

        return uploadRecordToMasterLiveData;
    }

    private class UploadAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private byte[] recordBytes;
        private String fileName;

        public UploadAsyncClass(byte[] recordBytes, String fileName) {
            this.recordBytes = recordBytes;
            this.fileName = fileName;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.uploadRecordFile(recordBytes, fileName);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> uploadedResultList) {

            if (uploadedResultList !=null && !uploadedResultList.isEmpty()) {

                HashMap<String,String> uploadedResultMap = uploadedResultList.get(0);

                uploadRecordMutableLiveData.postValue(uploadedResultMap.get("result"));
            }

            else{

                uploadRecordMutableLiveData.postValue("error");
            }

        }

    }


    private class GetRegionsAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getRegions();
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getRegionsList) {

            List<IDName> list = new ArrayList<>();

            if (getRegionsList != null && !getRegionsList.isEmpty()) {

                    IDName idName;

                    for (HashMap<String, String> hashMap : getRegionsList) {

                        idName = new IDName(hashMap.get("ID"), hashMap.get("Regions"));
                        list.add(idName);

                }

                getRegionsLiveData.postValue(list);

            }
        }

    }


    private class InsertRecordMasterAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String vehicleNumber ,vehicleType, location,  district, longitude,  latitude,  idUSER,  idRegions, notes;
        private boolean type;

        public InsertRecordMasterAsyncClass(String vehicleNumber, String vehicleType, String location, String district, String longitude,
                                            String latitude, String idUSER, String idRegions, String notes, boolean type) {
            this.vehicleNumber = vehicleNumber;
            this.vehicleType = vehicleType;
            this.location = location;
            this.district = district;
            this.longitude = longitude;
            this.latitude = latitude;
            this.idUSER = idUSER;
            this.idRegions = idRegions;
            this.notes = notes;
            this.type = type;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.insertRecordMaster(vehicleNumber ,vehicleType, location,  district, longitude,  latitude,  idUSER,
                    idRegions, notes, type);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> insertMasterRecord) {

            if (insertMasterRecord != null) {

                insertRecordMasterLiveData.postValue(insertMasterRecord.get(0).get("ID"));

            }

            else insertRecordMasterLiveData.postValue("error");
        }

    }


    private class UploadRecordToMasterAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String idRecorded, fileName , fileExtension, fileSize, idUser;

        public UploadRecordToMasterAsyncClass(String idRecorded, String fileName, String fileExtension, String fileSize, String idUser) {
            this.idRecorded = idRecorded;
            this.fileName = fileName;
            this.fileExtension = fileExtension;
            this.fileSize = fileSize;
            this.idUser = idUser;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.insertRecordFileToMaster(idRecorded, fileName, fileExtension, fileSize, idUser);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getInsertRecordToMaster) {

            if (getInsertRecordToMaster != null && !getInsertRecordToMaster.isEmpty()){

                uploadRecordToMasterLiveData.postValue(getInsertRecordToMaster.get(0).get("ID"));
            }
            else uploadRecordToMasterLiveData.postValue("error");
        }

    }

}
