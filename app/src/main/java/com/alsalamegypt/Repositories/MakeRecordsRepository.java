package com.alsalamegypt.Repositories;

import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alsalamegypt.BaseClasses.BaseRepository;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.RoomDB.RecordHistoryDao;
import com.alsalamegypt.UploadRecordFirebase.MainActivity;
import com.alsalamegypt.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MakeRecordsRepository extends BaseRepository {

    private RecordHistoryDao recordHistoryDao;

    private MutableLiveData<String> insertRecordMasterLiveData, uploadRecordToMasterLiveData;
    private MutableLiveData<Map<String, String>> uploadRecordMutableLiveData;
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
    

    /*public MutableLiveData<String> getMutableLiveData(byte[] recordBytes, String fileName, String userId) {

        uploadRecordMutableLiveData = new MutableLiveData<>();

        new UploadAsyncClass(recordBytes, fileName, userId).execute();

        return uploadRecordMutableLiveData;
    }

     */

    public MutableLiveData<Map<String, String>> getUploadRecordMutableLiveData(Uri filePath, String fileName) {

        uploadRecordMutableLiveData = new MutableLiveData<>();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("success", "");
        hashMap.put("progress", "0");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        StorageReference ref = storageReference.child("audios/" + fileName);
        UploadTask uploadTask = ref.putFile(filePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    Uri downloadUri = task.getResult();

                                    String url = downloadUri.toString();

                                    hashMap.put("success", url);
                                    hashMap.put("progress", "100");

                                    uploadRecordMutableLiveData.postValue(hashMap);
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        hashMap.put("success", "false");
                        hashMap.put("progress", "0");

                        uploadRecordMutableLiveData.postValue(hashMap);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());

                        hashMap.put("success", "progress");
                        hashMap.put("progress", String.valueOf(progress));

                        uploadRecordMutableLiveData.postValue(hashMap);

                    }
                });


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


    public MutableLiveData<Integer> getDeleteRecordHistoryMutableLiveData(String masterId) {

        deleteRecordHistoryMutableLiveData = new MutableLiveData<>();

        new DeleteRecordHistoryAsyncTask(masterId).execute();

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


    /*private class UploadAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

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

     */


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

            }

            getRegionsLiveData.postValue(list);
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

                ///// Send FCM to listener if the last send was more than 6 hours ////
                if (TextUtils.isEmpty(MyApplication.getTinyDB().getString(Constants.KEY_REC_TIME)) ||
                        Utils.getSubstractedDateTime(Utils.getCurrentDateTime("dd/MM/yyyy hh:mm:ss"),
                                MyApplication.getTinyDB().getString(Constants.KEY_REC_TIME), "dd/MM/yyyy hh:mm:ss") / 3600000 > 6){

                    new SendFCMToUser("تسجيلات جديدة", "يوجد تسجيلات جديدة من قبل " +
                            MyApplication.getTinyDB().getString(Constants.KEY_USER_FULLNAME), Constants.LISTENER_TOKEN).execute();

                    MyApplication.getTinyDB().putString(Constants.KEY_REC_TIME, Utils.getCurrentDateTime("dd/MM/yyyy hh:mm:ss"));
                }
            }

            else insertRecordMasterLiveData.postValue("error");
        }

    }


    private class SendFCMToUser extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String title, message, token;

        public SendFCMToUser(String title, String message, String token) {
            this.title = title;
            this.message = message;
            this.token = token;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.sendFCM(title, message, token);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getSendFCM) {

            if (getSendFCM != null) {

                Log.d("Fcm_message_status", getSendFCM.get(0).get("result"));

            }
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
            return webServices.getRecoderDailyCars(id);
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
                            hashMap.get("ID_USER"), hashMap.get("DateEdite"), hashMap.get("DateCreate"), hashMap.get("Type"),
                            hashMap.get("heard"));

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
        private String masterId;

        public DeleteRecordHistoryAsyncTask(RecordHistory recordHistory) {
            this.recordHistory = recordHistory;
        }

        public DeleteRecordHistoryAsyncTask(String masterId) {
            this.masterId = masterId;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (recordHistory!=null)
                return recordHistoryDao.delete(recordHistory);

            else
                return recordHistoryDao.delete(Integer.parseInt(masterId));
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


    private class GetRecordHistoryAsyncTask extends AsyncTask<Void, Void, RecordHistory> {

        private int id;

        public GetRecordHistoryAsyncTask(int id) {
            this.id = id;
        }

        @Override
        protected RecordHistory doInBackground(Void... voids) {
            return recordHistoryDao.getRecordHistory(id);
        }

        @Override
        protected void onPostExecute(RecordHistory recordHistory) {
            super.onPostExecute(recordHistory);


            if (recordHistory == null) {

                recordHistory = new RecordHistory();
                recordHistory.setId(-1);
            }


            getRecordHistoryMutableLiveData.postValue(recordHistory);

        }
    }


    private class GetAllRecordHistoryAsyncTask extends AsyncTask<Void, Void, List<RecordHistory>> {

        @Override
        protected List<RecordHistory> doInBackground(Void... voids) {
            return recordHistoryDao.getAllRecordHistory();
        }

        @Override
        protected void onPostExecute(List<RecordHistory> recordHistoryList) {
            super.onPostExecute(recordHistoryList);

            List<RecordHistory> recordHistories = new ArrayList<>();

            if (recordHistoryList != null ){

                recordHistories.addAll(recordHistoryList);
            }

            else {

                RecordHistory recordHistory = new RecordHistory();
                recordHistory.setId(-1);
                recordHistories.add(recordHistory);
            }

            Collections.reverse(recordHistories);

            getAllRecordHistoryMutableLiveData.postValue(recordHistories);
        }
    }

}
