package com.alsalamegypt.Repositories;

import android.os.AsyncTask;

import com.alsalamegypt.BaseClasses.BaseRepository;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.RoomDB.RecordHistoryDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MakeRecordsRepository extends BaseRepository {

    private RecordHistoryDao recordHistoryDao;

    private MutableLiveData<String> uploadRecordMutableLiveData, insertRecordMasterLiveData, uploadRecordToMasterLiveData;
    private MutableLiveData<List<IDName>> getRegionsLiveData;
    private MutableLiveData<List<Master>> getRecorderDailyCarsMutableLiveData;
    private MutableLiveData<Long> insertRecordHistoryMutableLiveData;
    private MutableLiveData<Integer> updateRecordHistoryMutableLiveData, deleteAllRecordHistoryMutableLiveData,
            deleteRecordHistoryMutableLiveData;
    private MutableLiveData<List<RecordHistory>> getAllRecordHistoryMutableLiveData;
    private MutableLiveData<RecordHistory> getRecordHistoryMutableLiveData;


    public MakeRecordsRepository() {
        super();

        recordHistoryDao = recordHistoryRoomDatabase.RecordHistoryDao();
    }
    

    public MutableLiveData<String> getMutableLiveData(byte[] recordBytes, String fileName, String userId) {

        uploadRecordMutableLiveData = new MutableLiveData<>();

        new UploadAsyncClass(recordBytes, fileName, userId).execute();

        return uploadRecordMutableLiveData;
    }


    public MutableLiveData<List<IDName>> getGetRegionsLiveData(String userId) {

        getRegionsLiveData = new MutableLiveData<>();

        new GetRegionsAsyncClass(userId).execute();

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

    public MutableLiveData<List<Master>> getGetRecorderDailyCarsMutableLiveData(String id) {

        getRecorderDailyCarsMutableLiveData = new MutableLiveData<>();

        new GetRecorderDailyCarsAsyncClass(id).execute();

        return getRecorderDailyCarsMutableLiveData;
    }


    public MutableLiveData<Long> getInsertRecordHistoryMutableLiveData(RecordHistory recordHistory) {

        insertRecordHistoryMutableLiveData = new MutableLiveData<>();

        new InsertRecordHistoryAsyncTask(recordHistory).execute();

        return insertRecordHistoryMutableLiveData;
    }


    public MutableLiveData<Integer> getDeleteAllRecordHistoryMutableLiveData() {

        deleteAllRecordHistoryMutableLiveData = new MutableLiveData<>();

        new DeleteAllRecordHistoryAsyncTask().execute();

        return deleteAllRecordHistoryMutableLiveData;
    }


    public MutableLiveData<Integer> getDeleteRecordHistoryMutableLiveData(RecordHistory recordHistory) {

        deleteRecordHistoryMutableLiveData = new MutableLiveData<>();

        new DeleteRecordHistoryAsyncTask(recordHistory).execute();

        return deleteRecordHistoryMutableLiveData;
    }


    public MutableLiveData<List<RecordHistory>> getGetAllRecordHistoryMutableLiveData() {

        getAllRecordHistoryMutableLiveData = new MutableLiveData<>();

        new GetAllRecordHistoryAsyncTask().execute();

        return getAllRecordHistoryMutableLiveData;
    }


    public MutableLiveData<Integer> getUpdateRecordHistoryMutableLiveData(RecordHistory recordHistory) {

        updateRecordHistoryMutableLiveData = new MutableLiveData<>();

        new UpdateRecordHistoryAsyncTask(recordHistory).execute();

        return updateRecordHistoryMutableLiveData;
    }


    public MutableLiveData<RecordHistory> getGetRecordHistoryMutableLiveData(int id) {

        getRecordHistoryMutableLiveData = new MutableLiveData<>();

        new GetRecordHistoryAsyncTask(id).execute();

        return getRecordHistoryMutableLiveData;
    }




    private class UploadAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private byte[] recordBytes;
        private String fileName, userId;

        public UploadAsyncClass(byte[] recordBytes, String fileName, String userId) {
            this.recordBytes = recordBytes;
            this.fileName = fileName;
            this.userId = userId;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.uploadRecordFile(recordBytes, fileName, userId);
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

        private String userId;

        public GetRegionsAsyncClass(String userId) {
            this.userId = userId;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getRegions(userId);
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


    private class GetRecorderDailyCarsAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id;

        public GetRecorderDailyCarsAsyncClass(String id) {
            this.id = id;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getMasters(id);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getMasterList) {

            List<Master> list = new ArrayList<>();

            Master master;

            if (getMasterList != null) {

                for (HashMap<String, String> hashMap : getMasterList) {

                    master = new Master(hashMap.get("ID"), hashMap.get("Vehicle_Number"), hashMap.get("Vehicle_Type"), hashMap.get("Location"),
                            hashMap.get("District"), hashMap.get("Longitude"), hashMap.get("Latitude"), hashMap.get("Status"),
                            hashMap.get("Sorting"), hashMap.get("Notes"), hashMap.get("ID_Regions"), hashMap.get("Regions"),
                            hashMap.get("ID_USER"), hashMap.get("DateEdite"), hashMap.get("DateCreate"), hashMap.get("Type"));

                    list.add(master);

                }

                Collections.reverse(list);
                getRecorderDailyCarsMutableLiveData.postValue(list);

            }

            else {

                master = new Master();
                master.setId("-1");
                list.add(master);
                getRecorderDailyCarsMutableLiveData.postValue(list);
            }
        }

    }


    private class InsertRecordHistoryAsyncTask extends AsyncTask<Void, Void, Long> {

        private RecordHistory recordHistory;

        public InsertRecordHistoryAsyncTask(RecordHistory recordHistory) {
            this.recordHistory = recordHistory;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return recordHistoryDao.insert(recordHistory);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            if (aLong != null)
                insertRecordHistoryMutableLiveData.postValue(aLong);

        }
    }


    private class DeleteRecordHistoryAsyncTask extends AsyncTask<Void, Void, Integer> {

        private RecordHistory recordHistory;

        public DeleteRecordHistoryAsyncTask(RecordHistory recordHistory) {
            this.recordHistory = recordHistory;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return recordHistoryDao.delete(recordHistory);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer != null)
                deleteRecordHistoryMutableLiveData.postValue(integer);
        }
    }


    private class DeleteAllRecordHistoryAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return recordHistoryDao.deleteAllRecordHistory();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer != null)
                deleteAllRecordHistoryMutableLiveData.postValue(integer);
        }
    }


    private class UpdateRecordHistoryAsyncTask extends AsyncTask<Void, Void, Integer> {

        private RecordHistory recordHistory;

        public UpdateRecordHistoryAsyncTask(RecordHistory recordHistory) {
            this.recordHistory = recordHistory;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return recordHistoryDao.update(recordHistory);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer != null)
                updateRecordHistoryMutableLiveData.postValue(integer);
        }
    }


    private class GetRecordHistoryAsyncTask extends AsyncTask<Void, Void, LiveData<RecordHistory>> {

        private int id;

        public GetRecordHistoryAsyncTask(int id) {
            this.id = id;
        }

        @Override
        protected LiveData<RecordHistory> doInBackground(Void... voids) {
            return recordHistoryDao.getRecordHistory(id);
        }

        @Override
        protected void onPostExecute(LiveData<RecordHistory> recordHistoryLiveData) {
            super.onPostExecute(recordHistoryLiveData);

            RecordHistory recordHistory;

            if (recordHistoryLiveData != null && recordHistoryLiveData.getValue() != null) {

                recordHistory = recordHistoryLiveData.getValue();
            }

            else {

                recordHistory = new RecordHistory();
                recordHistory.setId(-1);
            }


            getRecordHistoryMutableLiveData.postValue(recordHistory);

        }
    }



    private class GetAllRecordHistoryAsyncTask extends AsyncTask<Void, Void, LiveData<List<RecordHistory>>> {

        @Override
        protected LiveData<List<RecordHistory>> doInBackground(Void... voids) {
            return recordHistoryDao.getAllRecordHistory();
        }

        @Override
        protected void onPostExecute(LiveData<List<RecordHistory>> recordHistoryList) {
            super.onPostExecute(recordHistoryList);

            List<RecordHistory> recordHistories = new ArrayList<>();

            if (recordHistoryList != null && recordHistoryList.getValue() != null){

                recordHistories.addAll(recordHistoryList.getValue());
            }

            else {

                RecordHistory recordHistory = new RecordHistory();
                recordHistory.setId(-1);
                recordHistories.add(recordHistory);
            }

            getAllRecordHistoryMutableLiveData.postValue(recordHistories);
        }
    }

}
