package com.alsalameg.Repositories;

import android.os.AsyncTask;

import com.alsalameg.BaseClasses.BaseRepository;
import com.alsalameg.Models.Master;
import com.alsalameg.Models.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class ListenRecordsRepository extends BaseRepository {

    private MutableLiveData<List<Master>> masterListMutableLiveData, masterListenerListMutableLiveData,
            masterListenerRecordedCarsMutableLiveData;
    private MutableLiveData<List<Record>> masterRecordsMutableLiveData;
    private MutableLiveData<String> deleteMasterWithRecordsMutableLiveData, deleteRecordMutableLiveData, addRecordedCarMutableLiveData,
    deleteListenerRecordedCarMutableLivedata, updateHeardMutableLiveData;

    public ListenRecordsRepository() {

        super();
    }

    public MutableLiveData<List<Master>> getMasterListMutableLiveData(String id) {

        masterListMutableLiveData = new MutableLiveData<>();

        new GetMasterListAsyncClass(id).execute();

        return masterListMutableLiveData;
    }

    public MutableLiveData<List<Master>> getMasterListenerListMutableLiveData(String id) {

        masterListenerListMutableLiveData = new MutableLiveData<>();

        new GetMasterListenerListAsyncClass(id).execute();

        return masterListenerListMutableLiveData;
    }

    public MutableLiveData<List<Record>> getMasterRecordsMutableLiveData(String id, String id_user) {

        masterRecordsMutableLiveData = new MutableLiveData<>();

        new GetMasterRecordsListAsyncClass(id, id_user).execute();

        return masterRecordsMutableLiveData;
    }

    public MutableLiveData<String> getDeleteMasterWithRecordsMutableLiveData(String masterId, String idUser) {

        deleteMasterWithRecordsMutableLiveData = new MutableLiveData<>();

        new DeleteMasterWithRecordsAsyncClass(masterId, idUser).execute();

        return deleteMasterWithRecordsMutableLiveData;
    }

    public MutableLiveData<String> getDeleteRecordMutableLiveData(String recordId, String idUser) {

        deleteRecordMutableLiveData = new MutableLiveData<>();

        new DeleteRecordAsyncClass(recordId, idUser).execute();

        return deleteRecordMutableLiveData;
    }

    public MutableLiveData<List<Master>> getMasterListenerRecordedCarsMutableLiveData(String id, String id_user) {

        masterListenerRecordedCarsMutableLiveData = new MutableLiveData<>();

        new GetMasterListenerRecordedCarsAsyncClass(id, id_user).execute();

        return masterListenerRecordedCarsMutableLiveData;
    }

    public MutableLiveData<String> getAddRecordedCarMutableLiveData(String vehicleNumber, String vehicleType, String location, String district,
                                                                    String longitude, String latitude, String idUSER, String idRegions,
                                                                    String notes, String masterId) {

        addRecordedCarMutableLiveData = new MutableLiveData<>();

        new InsertRecordedCarAsyncClass(vehicleNumber ,vehicleType, location,  district, longitude,  latitude,  idUSER,  idRegions,
                notes, masterId).execute();

        return addRecordedCarMutableLiveData;
    }

    public MutableLiveData<String> getDeleteListenerRecordedCarMutableLivedata(String masterId, String idUser) {

        deleteListenerRecordedCarMutableLivedata = new MutableLiveData<>();

        new DeleteListenerRecordedCarAsyncClass(masterId, idUser).execute();

        return deleteListenerRecordedCarMutableLivedata;
    }

    public MutableLiveData<String> getUpdateHeardMutableLiveData(String recId, String userId) {

        updateHeardMutableLiveData = new MutableLiveData<>();

        new GetUpdateHeardAsyncClass(recId, userId).execute();

        return updateHeardMutableLiveData;
    }

    private class InsertRecordedCarAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String vehicleNumber ,vehicleType, location,  district, longitude,  latitude,  idUSER,  idRegions, notes, masterId;
        private boolean type;

        public InsertRecordedCarAsyncClass(String vehicleNumber, String vehicleType, String location, String district, String longitude,
                                            String latitude, String idUSER, String idRegions, String notes, String masterId) {
            this.vehicleNumber = vehicleNumber;
            this.vehicleType = vehicleType;
            this.location = location;
            this.district = district;
            this.longitude = longitude;
            this.latitude = latitude;
            this.idUSER = idUSER;
            this.idRegions = idRegions;
            this.notes = notes;
            this.masterId = masterId;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.insertRecodedCar(vehicleNumber ,vehicleType, location,  district, longitude,  latitude,  idUSER,
                    idRegions, notes, masterId);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> insertRecordedCar) {

            if (insertRecordedCar != null) {

                addRecordedCarMutableLiveData.postValue(insertRecordedCar.get(0).get("ID"));

            }

            else addRecordedCarMutableLiveData.postValue("error");
        }

    }


    private class GetMasterListAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id;

        public GetMasterListAsyncClass(String id) {
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
                masterListMutableLiveData.postValue(list);

            }

            else {

                master = new Master();
                master.setId("-1");
                list.add(master);
                masterListMutableLiveData.postValue(list);
            }
        }

    }


    private class GetMasterListenerListAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id;

        public GetMasterListenerListAsyncClass(String id) {
            this.id = id;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getListenerMasters(id);
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
            }

            else {

                master = new Master();
                master.setId("-1");
                list.add(master);
            }

            masterListenerListMutableLiveData.postValue(list);
        }

    }


    private class GetMasterRecordsListAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id, id_user;

        public GetMasterRecordsListAsyncClass(String id, String id_user) {
            this.id = id;
            this.id_user = id_user;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getMasterRecords(id, id_user);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getMasterRecordsList) {

            List<Record> list = new ArrayList<>();

            Record record;

            if (getMasterRecordsList != null) {

                for (HashMap<String, String> hashMap : getMasterRecordsList) {

                    record = new Record(hashMap.get("ID"), hashMap.get("ID_Recorded"), hashMap.get("File_Name"), hashMap.get("File_Extension"),
                            hashMap.get("File_Size"), hashMap.get("heard"));

                    list.add(record);

                }
            }

            else {

                record = new Record();
                record.setId("-1");
                list.add(record);
            }
            masterRecordsMutableLiveData.postValue(list);
        }

    }


    private class GetMasterListenerRecordedCarsAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id, id_user;

        public GetMasterListenerRecordedCarsAsyncClass(String id, String id_user) {
            this.id = id;
            this.id_user = id_user;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getListenerRecordedCars(id, id_user);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getMasterList) {

            List<Master> list = new ArrayList<>();

            Master master;

            if (getMasterList != null) {

                for (HashMap<String, String> hashMap : getMasterList) {

                    master = new Master(hashMap.get("ID"), hashMap.get("Vehicle_Number"), hashMap.get("Vehicle_Type"),
                            hashMap.get("Location"), hashMap.get("District"), hashMap.get("Longitude"), hashMap.get("Latitude"),
                            hashMap.get("Status"), hashMap.get("Sorting"), hashMap.get("Notes"), hashMap.get("ID_Regions"),
                            hashMap.get("Regions"), hashMap.get("ID_USER"), hashMap.get("DateEdite"), hashMap.get("DateCreate"),
                            hashMap.get("Type"));

                    list.add(master);

                }

                Collections.reverse(list);
            }

            else {

                master = new Master();
                master.setId("-1");
                list.add(master);
            }

            masterListenerRecordedCarsMutableLiveData.postValue(list);
        }

    }


    private class DeleteMasterWithRecordsAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String masterId, idUser;

        public DeleteMasterWithRecordsAsyncClass(String masterId, String idUser) {
            this.masterId = masterId;
            this.idUser = idUser;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.deleteMasterOrRecord(masterId, idUser, "master");
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getDeleteMasterWithRecords) {

            String result = "";

            if (getDeleteMasterWithRecords != null && !getDeleteMasterWithRecords.get(0).get("ID").equals("0")) {

                result = getDeleteMasterWithRecords.get(0).get("ID");
            }

            deleteMasterWithRecordsMutableLiveData.postValue(result);
        }
    }


    private class DeleteRecordAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String masterId, idUser;

        public DeleteRecordAsyncClass(String masterId, String idUser) {
            this.masterId = masterId;
            this.idUser = idUser;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.deleteMasterOrRecord(masterId, idUser, "record");
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getDeleteMasterWithRecords) {

            String result = "";

            if (getDeleteMasterWithRecords != null && !getDeleteMasterWithRecords.get(0).get("ID").equals("0")) {

                result = getDeleteMasterWithRecords.get(0).get("ID");
            }

            deleteRecordMutableLiveData.postValue(result);
        }
    }


    private class DeleteListenerRecordedCarAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String masterId, idUser;

        public DeleteListenerRecordedCarAsyncClass(String masterId, String idUser) {
            this.masterId = masterId;
            this.idUser = idUser;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.deleteListenerRecordedCarMaster(masterId, idUser);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getDeleteMasterWithRecords) {

            String result = "";

            if (getDeleteMasterWithRecords != null && !getDeleteMasterWithRecords.get(0).get("ID").equals("0")) {

                result = getDeleteMasterWithRecords.get(0).get("ID");
            }

            deleteListenerRecordedCarMutableLivedata.postValue(result);
        }
    }


    private class GetUpdateHeardAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String recId, idUser;

        public GetUpdateHeardAsyncClass(String recId, String idUser) {
            this.recId = recId;
            this.idUser = idUser;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webServices.getUpdateHeard(recId, idUser);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getUpdateHeard) {

            String result = "";

            if (getUpdateHeard != null)
                result = "done";

            updateHeardMutableLiveData.postValue(result);
        }
    }
}
