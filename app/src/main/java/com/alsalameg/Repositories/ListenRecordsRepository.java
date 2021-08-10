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

    private MutableLiveData<List<Master>> masterListMutableLiveData;
    private MutableLiveData<List<Record>> masterRecordsMutableLiveData;
    private MutableLiveData<String> deleteMasterWithRecordsMutableLiveData;
    private MutableLiveData<String> deleteRecordMutableLiveData;

    public ListenRecordsRepository() {

        super();
    }

    public MutableLiveData<List<Master>> getMasterListMutableLiveData(String id) {

        masterListMutableLiveData = new MutableLiveData<>();

        new GetMasterListAsyncClass(id).execute();

        return masterListMutableLiveData;
    }

    public MutableLiveData<List<Record>> getMasterRecordsMutableLiveData(String id) {

        masterRecordsMutableLiveData = new MutableLiveData<>();

        new GetMasterRecordsListAsyncClass(id).execute();

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

    private class GetMasterListAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id;

        public GetMasterListAsyncClass(String id) {
            this.id = id;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webService.getMasters(id);
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

    private class GetMasterRecordsListAsyncClass extends AsyncTask<Void, Void, List<HashMap<String,String>>> {

        private String id;

        public GetMasterRecordsListAsyncClass(String id) {
            this.id = id;
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(Void... voids) {
            return webService.getMasterRecords(id);
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> getMasterRecordsList) {

            List<Record> list = new ArrayList<>();

            Record record;

            if (getMasterRecordsList != null) {

                for (HashMap<String, String> hashMap : getMasterRecordsList) {

                    record = new Record(hashMap.get("ID"), hashMap.get("ID_Recorded"), hashMap.get("File_Name"), hashMap.get("File_Extension"),
                            hashMap.get("File_Size"));

                    list.add(record);

                }

                masterRecordsMutableLiveData.postValue(list);

            }

            else {

                record = new Record();
                record.setId("-1");
                list.add(record);
                masterRecordsMutableLiveData.postValue(list);
            }
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
            return webService.deleteMasterOrRecord(masterId, idUser, "master");
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
            return webService.deleteMasterOrRecord(masterId, idUser, "record");
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
}
