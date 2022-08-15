package com.alsalamegypt.Api;

import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;

@Module
public class WebServices {

    @Singleton
    @Provides
    WebServices webService(){return this;}


    public ArrayList<HashMap<String, String>> getUserInfoService(String username , String Password, String uid) {
        SoupRequest MS = new SoupRequest("GetLogin");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();
        send_params.add("USERNAME");
        send_params.add("USERPASS");
        send_params.add("UID");

        ArrayList<String> send_params_value = new ArrayList<String>();
        send_params_value.add(username);
        send_params_value.add(Password);
        send_params_value.add(uid);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Code");
        request_params.add("FullName");
        request_params.add("USERNAME");
        request_params.add("USERPASS");
        request_params.add("Phone");
        request_params.add("Email");
        request_params.add("TYPE");
        request_params.add("Address");
        request_params.add("Note");
        request_params.add("Img");
        request_params.add("ID_USER");
        request_params.add("ID_Listen");
        request_params.add("DateEdite");
        request_params.add("MODIFICATION_DATE");
        request_params.add("Listen");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> uploadRecordFile(byte[] fileBytes , String fileNmae, String id_user) {
        SoupRequest MS = new SoupRequest("UploadFile", 0);
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();
        send_params.add("fle");
        send_params.add("FileName");
        send_params.add("ID_USER");

        ArrayList<String> send_params_value = new ArrayList<String>();
        String bytes = "";
        for (byte b : fileBytes) bytes+=b;
        send_params_value.add(Base64.encodeToString(fileBytes, Base64.DEFAULT));
        send_params_value.add(fileNmae);
        send_params_value.add(id_user);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("result");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call_result();
    }


    public ArrayList<HashMap<String, String>> getRegions(String id) {
        SoupRequest MS = new SoupRequest("GetRegions");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();
        send_params.add("ID");

        ArrayList<String> send_params_value = new ArrayList<String>();
        send_params_value.add(id);


        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Regions");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getCarTypes(String id) {
        SoupRequest MS = new SoupRequest("GetChit_Words");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();
        send_params.add("ID");

        ArrayList<String> send_params_value = new ArrayList<String>();
        send_params_value.add(id);


        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Word");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> insertRecordMaster(String vehicleNumber, String vehicleType, String location, String district,
                                                                String longitude, String latitude, String idUSER, String idRegions,
                                                                String notes , boolean type) {
        SoupRequest MS = new SoupRequest("ADD_Recorded");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("Vehicle_Number");
        send_params.add("Vehicle_Type");
        send_params.add("Location");
        send_params.add("District");
        send_params.add("Longitude");
        send_params.add("Latitude");
        send_params.add("ID_USER");
        send_params.add("ID_Regions");
        send_params.add("Notes");
        send_params.add("Type");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(vehicleNumber);
        send_params_value.add(vehicleType);
        send_params_value.add(location);
        send_params_value.add(district);
        send_params_value.add(longitude);
        send_params_value.add(latitude);
        send_params_value.add(idUSER);
        send_params_value.add(idRegions);
        send_params_value.add(notes);
        send_params_value.add(String.valueOf(type));

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }

    public ArrayList<HashMap<String, String>> insertRecordFileToMaster(String idRecorded,String fileName ,String fileExtension,
                                                                       String fileSize,String idUser) {
        SoupRequest MS = new SoupRequest("ADD_Recorded_Files");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID_Recorded");
        send_params.add("File_Name");
        send_params.add("File_Extension");
        send_params.add("File_Size");
        send_params.add("ID_User");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(idRecorded);
        send_params_value.add(fileName);
        send_params_value.add(fileExtension);
        send_params_value.add(fileSize);
        send_params_value.add(idUser);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getMasters(String id) {

        SoupRequest MS = new SoupRequest("GETRecorded");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Vehicle_Number");
        request_params.add("Vehicle_Type");
        request_params.add("Location");
        request_params.add("District");
        request_params.add("Longitude");
        request_params.add("Latitude");
        request_params.add("Status");
        request_params.add("Sorting");
        request_params.add("Notes");
        request_params.add("Regions");
        request_params.add("ID_USER");
        request_params.add("DateEdite");
        request_params.add("DateCreate");
        request_params.add("Type");
        request_params.add("heard");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getMasterRecords(String id, String user_id) {

        SoupRequest MS = new SoupRequest("GETRecorded_Files");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("ID_USER");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);
        send_params_value.add(user_id);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("ID_Recorded");
        request_params.add("File_Name");
        request_params.add("File_Extension");
        request_params.add("File_Size");
        request_params.add("heard");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> deleteMasterOrRecord(String masterId, String idUser, String deleted) {

        String fun;
        if (deleted.equals("master"))
            fun = "Delete_Recorded";
        else
            fun = "Delete_Recorded_Files";
        SoupRequest MS = new SoupRequest(fun);
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("ID_USER");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(masterId);
        send_params_value.add(idUser);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getCars(String id) {

        SoupRequest MS = new SoupRequest("GetMapp", 0);
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();

        request_params.add("ID");
        request_params.add("ID_MasterDailyChit");
        request_params.add("PlateNumber");
        request_params.add("Kind");
        request_params.add("ContractNumber");
        request_params.add("ShasNumber");
        request_params.add("Bank");
        request_params.add("DetermineStatus");
        request_params.add("DetermineObserver");
        request_params.add("ID_Vehicles");
        request_params.add("Customer");
        request_params.add("Maker");
        request_params.add("ID_Regions");
        request_params.add("DateCreate");
        request_params.add("DateEdite");
        request_params.add("Original_Number");
        request_params.add("After_Repetition");
        request_params.add("Corresponding_Number");
        request_params.add("Regions");
        request_params.add("Vehicle_Number");
        request_params.add("Vehicle_Type");
        request_params.add("Location");
        request_params.add("District");
        request_params.add("Longitude");
        request_params.add("Latitude");
        request_params.add("Sorting");
        request_params.add("Color");
        request_params.add("Notes");
        request_params.add("Status");
        request_params.add("FullName");
        request_params.add("Colors");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> confirmCar(String id, String confirmation, String latitude, String longitude) {

        SoupRequest MS = new SoupRequest("UpdateMap");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("Confirmation");
        send_params.add("Longitude");
        send_params.add("Latitude");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);
        send_params_value.add(confirmation);
        send_params_value.add(longitude);
        send_params_value.add(latitude);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }

    public ArrayList<HashMap<String, String>> confirmCar(String id, String confirmation, String latitude, String longitude, String regionId,
                                                         String userId, String location, String district, String vehicleType) {

        SoupRequest MS = new SoupRequest("UpdateMapNew");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("Confirmation");
        send_params.add("Longitude");
        send_params.add("Latitude");
        send_params.add("ID_Regions");
        send_params.add("ID_USER");
        send_params.add("Location");
        send_params.add("District");
        send_params.add("Vehicle_Type");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);
        send_params_value.add(confirmation);
        send_params_value.add(longitude);
        send_params_value.add(latitude);
        send_params_value.add(regionId);
        send_params_value.add(userId);
        send_params_value.add(location);
        send_params_value.add(district);
        send_params_value.add(vehicleType);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getListenerMasters(String id) {

        SoupRequest MS = new SoupRequest("Getlistener");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID_Listen");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Vehicle_Number");
        request_params.add("Vehicle_Type");
        request_params.add("Location");
        request_params.add("District");
        request_params.add("Longitude");
        request_params.add("Latitude");
        request_params.add("Status");
        request_params.add("Sorting");
        request_params.add("Notes");
        request_params.add("Regions");
        request_params.add("ID_USER");
        request_params.add("DateEdite");
        request_params.add("DateCreate");
        request_params.add("Type");
        request_params.add("Transfer");
        request_params.add("heard");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getListenerMasters(String id, int pageIndex, int pageSize) {

        SoupRequest MS = new SoupRequest("GetMoblistenerPageWise");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID_Listen");
        send_params.add("PageIndex");
        send_params.add("PageSize");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);
        send_params_value.add(String.valueOf(pageIndex));
        send_params_value.add(String.valueOf(pageSize));

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Vehicle_Number");
        request_params.add("Vehicle_Type");
        request_params.add("Location");
        request_params.add("District");
        request_params.add("Longitude");
        request_params.add("Latitude");
        request_params.add("Status");
        request_params.add("Sorting");
        request_params.add("Notes");
        request_params.add("Regions");
        request_params.add("ID_USER");
        request_params.add("DateEdite");
        request_params.add("DateCreate");
        request_params.add("Type");
        request_params.add("Transfer");
        request_params.add("heard");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> insertRecodedCar(String vehicleNumber, String vehicleType, String location, String district,
                                                                 String longitude, String latitude, String idUSER, String idRegions,
                                                                 String notes, String masterId) {
        SoupRequest MS = new SoupRequest("ADD_listener");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("Vehicle_Number");
        send_params.add("Vehicle_Type");
        send_params.add("Location");
        send_params.add("District");
        send_params.add("Longitude");
        send_params.add("Latitude");
        send_params.add("ID_USER");
        send_params.add("ID_Regions");
        send_params.add("Notes");
        send_params.add("ID_Recorded");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(vehicleNumber);
        send_params_value.add(vehicleType);
        send_params_value.add(location);
        send_params_value.add(district);
        send_params_value.add(longitude);
        send_params_value.add(latitude);
        send_params_value.add(idUSER);
        send_params_value.add(idRegions);
        send_params_value.add(notes);
        send_params_value.add(masterId);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getListenerRecordedCars(String id, String id_user) {

        SoupRequest MS = new SoupRequest("Getlistenerrecordings");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID_Recorded");
        send_params.add("ID_Listen");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);
        send_params_value.add(id_user);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Vehicle_Number");
        request_params.add("Vehicle_Type");
        request_params.add("Location");
        request_params.add("District");
        request_params.add("Longitude");
        request_params.add("Latitude");
        request_params.add("Status");
        request_params.add("Type");
        request_params.add("Sorting");
        request_params.add("Notes");
        request_params.add("ID_Regions");
        request_params.add("Regions");
        request_params.add("ID_USER");
        request_params.add("DateEdite");
        request_params.add("DateCreate");
        request_params.add("ID_Recorded");
        request_params.add("heard");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getUpdateHeard(String id, String id_user) {

        SoupRequest MS = new SoupRequest("Update_heard");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("ID_USER");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);
        send_params_value.add(id_user);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> deleteListenerRecordedCarMaster(String masterId, String idUser) {

        SoupRequest MS = new SoupRequest("Delete_listener");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("ID_USER");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(masterId);
        send_params_value.add(idUser);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> getRecoderDailyCars(String id) {

        SoupRequest MS = new SoupRequest("GetRecordedDay");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(id);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("ID");
        request_params.add("Vehicle_Number");
        request_params.add("Vehicle_Type");
        request_params.add("Location");
        request_params.add("District");
        request_params.add("Longitude");
        request_params.add("Latitude");
        request_params.add("Status");
        request_params.add("Sorting");
        request_params.add("Notes");
        request_params.add("Regions");
        request_params.add("ID_USER");
        request_params.add("DateEdite");
        request_params.add("DateCreate");
        request_params.add("Type");
        request_params.add("Transfer");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> updateFirebaseToken(String userId, String firebaseToken) {

        SoupRequest MS = new SoupRequest("Update_deviceId");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        send_params.add("ID");
        send_params.add("deviceId");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(userId);
        send_params_value.add(firebaseToken);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();
        request_params.add("error");
        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call();
    }


    public ArrayList<HashMap<String, String>> sendFCM(String title, String message, String firebaseToken) {

        SoupRequest MS = new SoupRequest("PushNotification");
        /**1 - any parameter send */
        ArrayList<String> send_params = new ArrayList<String>();

        /// title ///
        send_params.add("TagMsg");
        //// message ////
        send_params.add("Message");
        //// firebase token ////
        send_params.add("deviceId");

        ArrayList<String> send_params_value = new ArrayList<String>();

        send_params_value.add(title);
        send_params_value.add(message);
        send_params_value.add(firebaseToken);

        MS.addsendparam(send_params, send_params_value);

        /**2 - request */
        ArrayList<String> request_params = new ArrayList<String>();

        request_params.add("result");

        MS.setRequest_paramName(request_params);
        /** 3 - any image */
        // MS.setIsImage1("ProductPicture");
        // MS.setIsImage2("Voice");

        return MS.Call_result();
    }


}
