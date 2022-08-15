package com.alsalamegypt.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alsalamegypt.Adapters.UploadingRecordsProgressAdapter;
import com.alsalamegypt.AudioRecorder;
import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.Models.Master;
import com.alsalamegypt.Models.UploadingRecord;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.Notifications.NotificationBuilder;
import com.alsalamegypt.Notifications.UploadRecordService;
import com.alsalamegypt.R;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.databinding.FragmentMakeRecordsBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MakeRecordsFragment extends BaseFragment implements Serializable {

    public FragmentMakeRecordsBinding fragmentMakeRecordsBinding;

    //Variables for recorder
    private AudioRecorder audioRecorder;
    private String recordFilePath;
    private File parentFile, recordFile;
    private String recordFileName;
    private Uri recordFileUri;
    private final int PICK_AUD_REQUEST = 71;

    //Variables for map
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private OnMapReadyCallback callback;
    private LatLng MylatLng, selectedLatLan;
    private String selectedAdress, selectedSubAdminArea, selectedSubLocality;
    private Marker selectedMark;
    private List<Marker> markerList;
    private Boolean isEnablingGps;

    //vars for spinner
    private ArrayAdapter<IDName> spinnerArrayAdapter;
    private List<IDName> regionList;
    private IDName selectedRegion;

    // vars for recyclerview///
    private UploadingRecordsProgressAdapter uploadingRecordsProgressAdapter;
    private List<UploadingRecord> uploadingRecordList;

    //VariablesForServices
    private MakeRecordsViewModel makeRecordsViewModel;
    private Observer<String> uploadRecordToMasterObserver, addMasterObserver;
    private Observer<List<IDName>> regionsObserver;
    private Master master;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentMakeRecordsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_make_records, container, false);
        return fragmentMakeRecordsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeRecordsViewModel = ViewModelProviders.of(this).get(MakeRecordsViewModel.class);

        setListeners();
        initRegionSpinner();
        //initRecyclerView();
        fetchCurrentLocation();
        initObservers();

    }


    private void setListeners(){

        fragmentMakeRecordsBinding.fragmentMakeRecordsLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInfoDialogWithTwoButtons(getResources().getString(R.string.logout), getResources().getString(R.string.sure_logout),
                        getResources().getString(R.string.yes), getResources().getString(R.string.no), new Closure() {
                            @Override
                            public void exec() {

                                MyApplication.getTinyDB().putString(Constants.KEY_USERID, "");
                                MyApplication.getTinyDB().putString(Constants.KEY_USERNAME, "");
                                MyApplication.getTinyDB().putString(Constants.KEY_USER_PASSWORD, "");
                                MyApplication.getTinyDB().putString(Constants.KEY_USER_TYPE, "");

                                ((MainActivity) getActivity()).navController.navigate(R.id.action_fragment_main_records_to_fragment_login);
                            }
                        }, new Closure() {
                            @Override
                            public void exec() {

                                hideInfoDialogWithTwoButton();
                            }
                        }, true);

            }
        });

        fragmentMakeRecordsBinding.fragmentMakeRecordsReloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchCurrentLocation();
            }
        });

        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (MyApplication.getTinyDB().getString("master_id") != null &&
//                !MyApplication.getTinyDB().getString("master_id").equals(""))
//                    fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.VISIBLE);
//                else
//                    showFailedDialog(getResources().getString(R.string.add_master_fir), true)
                if (checkBeforeRecord()){

                    /*showInfoDialogWithTwoButtons(getResources().getString(R.string.choose_rec_way),
                            getResources().getString(R.string.choose_rec_eay_msg), getResources().getString(R.string.open_fl_exp),
                            getResources().getString(R.string.rec_from_app), new Closure() {
                                @Override
                                public void exec() {

                                    chooseAud();
                                }
                            }, new Closure() {
                                @Override
                                public void exec() {

                                    fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.VISIBLE);
                                }
                            }, true)
                     */

                    fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.VISIBLE);

                }
            }
        });


        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordFromExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBeforeRecord()){

                    /*showInfoDialogWithTwoButtons(getResources().getString(R.string.choose_rec_way),
                            getResources().getString(R.string.choose_rec_eay_msg), getResources().getString(R.string.open_fl_exp),
                            getResources().getString(R.string.rec_from_app), new Closure() {
                                @Override
                                public void exec() {

                                    chooseAud();
                                }
                            }, new Closure() {
                                @Override
                                public void exec() {

                                    fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.VISIBLE);
                                }
                            }, true);

                     */
                    chooseAud();

                }
            }
        });

        fragmentMakeRecordsBinding.regionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedRegion = ((IDName)parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initRecyclerView(){

        uploadingRecordList = new ArrayList<>();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentMakeRecordsBinding.uploadingRecordsProcessList.recyclerview.setLayoutManager(mLayoutManager);
        fragmentMakeRecordsBinding.uploadingRecordsProcessList.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));

        uploadingRecordsProgressAdapter = new UploadingRecordsProgressAdapter(uploadingRecordList, getContext(), MakeRecordsFragment.this);

        uploadingRecordsProgressAdapter.setHasStableIds(true);
        fragmentMakeRecordsBinding.uploadingRecordsProcessList.recyclerview.setAdapter(uploadingRecordsProgressAdapter);
    }


    private void initRegionSpinner(){

        if(regionList == null)
            regionList = new ArrayList<>();

        regionList.clear();

        regionList.add(new IDName("0", getResources().getString(R.string.choose_region)));

        spinnerArrayAdapter = new ArrayAdapter<IDName>(getContext(), android.R.layout.simple_spinner_item, regionList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        fragmentMakeRecordsBinding.regionsSpinner.setAdapter(spinnerArrayAdapter);
    }


    private void configureTheRecorder(){

        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                return checkRecordPermission();

            }
        });


        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordButton.setEnabled(true);


        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordButton.setRecordView(fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView);


        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView.setSlideToCancelText(getResources().getString(R.string.swipe_to_cancel));



        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView.setOnRecordListener(new OnRecordListener() {
                @Override
                public void onStart() {

                    if (selectedRegion.getId().equals("0")){

                        Toast.makeText(getContext(), getResources().getString(R.string.choose_region_message), Toast.LENGTH_SHORT).show();
                    }

                    if (checkSelectedPoint()){

                        setRecordPath();

                        try {
                            audioRecorder.start(recordFilePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancel() {

                    audioRecorder.stop();
                    recordFile.delete();
                    Toast.makeText(getContext(), getResources().getString(R.string.not_recorded), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFinish(long recordTime, boolean limitReached) {
                    //Stop Recording..
                    //limitReached to determine if the Record was finished when time limit reached.
                    // String time = getHumanTimeText(recordTime);
                    audioRecorder.stop();

                    if (!checkSelectedPoint()){

                        recordFile.delete();

                        showFailedDialog(getResources().getString(R.string.not_recorder_no_selected_point), true);

                        return;
                    }

                    if (selectedRegion.getId().equals("0")){

                        recordFile.delete();
                        Toast.makeText(getContext(), getResources().getString(R.string.choose_region_message), Toast.LENGTH_SHORT).show();

                        return;
                    }



                     showInfoDialogWithTwoButtons(getResources().getString(R.string.sure_make_record_title),
                             getResources().getString(R.string.sure_make_record) + "\n" + getResources().getString(R.string.period)
                                     + recordTime / 1000 + " " + getResources().getString(R.string.second) + "\n" +
                                     getResources().getString(R.string.location) + selectedAdress, getResources().getString(R.string.yes), getResources().getString(R.string.no),
                             new Closure() {
                                 @Override
                                 public void exec() {

                                     insertMasterRecord();

//                                     Bundle bundle = new Bundle();
//                                     bundle.putString("address", selectedAdress);
//                                     bundle.putString("path", recordFilePath);
//                                     Navigation.findNavController(getActivity(),R.id.fragment_main_record_fragment_nav_host_fragment).navigate(
//                                             R.id.action_fragment_make_records_to_fragment_listen_records, bundle);
                                 }
                             }, new Closure() {
                                 @Override
                                 public void exec() {

                                     hideInfoDialogWithTwoButton();
                                 }
                             }, false);
                }

                @Override
                public void onLessThanSecond() {

                    Toast.makeText(getContext(), getResources().getString(R.string.hold_record_button), Toast.LENGTH_SHORT).show();
                    audioRecorder.stop();
                    recordFile.delete();
                }
            });


    }


    private void fetchCurrentLocation() {

        //showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        isEnablingGps = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkLocationPermission()) {return;}

        if (!isGPSEnabled(getContext())){

            showWarningDialog(getResources().getString(R.string.enable_gps), getResources().getString(R.string.enable_gps_msg),
                    getResources().getString(R.string.enable), new Closure() {
                        @Override
                        public void exec() {

                            getActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            isEnablingGps = true;
                        }
                    }, true);
        }

        else {

            showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.detecting_your_loc_msg),
                    false);

            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                    new CancellationToken() {
                        @Override
                        public boolean isCancellationRequested() {
                            return false;
                        }

                        @Override
                        public CancellationToken onCanceledRequested(OnTokenCanceledListener onTokenCanceledListener) {
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        hideProgress();

                        currentLocation = location;

                        setGoogleMap();

                        configureTheRecorder();
                    }
                }
            });
        }

    }


    private void setGoogleMap() {

                 /*if (currentLocation != null) {

                    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("")
                            .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_car_red)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                }
                 */

        callback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //hideProgress();

                if (checkLocationPermission()) googleMap.setMyLocationEnabled(true);
                MylatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(MylatLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MylatLng, 20));
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                ///// Get daily recorded cars ///////

                getRecorderDailyCars(googleMap);


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        ///// Check if this is a point have been just made to record or is a daily previous car

                        if (marker.getTag() ==null){

                            marker.setTitle(getResources().getString(R.string.sel_mark));
                            marker.showInfoWindow();
                            if (selectedMark!= null) selectedMark.setIcon(BitmapDescriptorFactory.defaultMarker());
                            selectedMark = marker;
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                            selectedLatLan = selectedMark.getPosition();
                            selectedAdress = getAdressDetailsFromLatLong(selectedLatLan).getAddressLine(0);
                            selectedSubAdminArea = getAdressDetailsFromLatLong(selectedLatLan).getSubAdminArea();
                            selectedSubLocality = getAdressDetailsFromLatLong(selectedLatLan).getSubLocality();

                            master = null;


                            fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.GONE);

                            //Show dialog to choose write or send record for the selected point
//                            FragmentManager fm = getActivity().getSupportFragmentManager();
//                            AddCarDetailsFragment addCarDetailsFragment = new AddCarDetailsFragment(selectedAdress,
//                                    selectedSubAdminArea, selectedSubLocality, String.valueOf(currentLocation.getLongitude()),
//                                    String.valueOf(currentLocation.getLatitude()));
//                            addCarDetailsFragment.show(fm, "fragment_new_activity");
                        }

                        return false;
                    }
                });
            }
        };

        if (mapFragment == null)
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(callback);
    }


    private void getRecorderDailyCars(GoogleMap googleMap) {

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        makeRecordsViewModel.getRecorderDailyCarsLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), dailyCarsObserver(googleMap));
    }


    private void fillRegionsSpinner(){

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        makeRecordsViewModel.getGetRegionsLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), regionsObserver);
    }


    private void initObservers(){

        regionsObserver = new Observer<List<IDName>>() {
            @Override
            public void onChanged(List<IDName> idNames) {

                if (idNames!=null){

                    hideProgress();

                    if (idNames.isEmpty()){

                        Toast.makeText(getContext(), getResources().getString(R.string.err_loading), Toast.LENGTH_SHORT).show();
                    }

                    else {

                        initRegionSpinner();
                        regionList.addAll(idNames);
                        spinnerArrayAdapter.notifyDataSetChanged();
                    }

                }
            }
        };


        /*uploadRecordObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null) {

                    hideProgress();

                    if (s.equals("DONE")){

                        //// Add the uploaded record to the master

                        addUploadedRecordToMaster();

                    }

                    else{

                        showFailedDialog(getResources().getString(R.string.error_upload_record), true);
                    }


                }
            }
        };

         */


        addMasterObserver = new Observer<String>() {

            @Override
            public void onChanged(String s) {

                if (s != null){

                    hideProgress();

                    switch (s){

                        case "error" :
                            Toast.makeText(getContext(), getResources().getString(R.string.add_master_fail_msg),
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case "-1" :
                            showInfoDialogWithOneButton(getResources().getString(R.string.add_master_fail_msg),
                                    getResources().getString(R.string.added_before_msg),getResources().getString
                                            (R.string.dialog_ok_button), new Closure() {
                                        @Override
                                        public void exec() {

                                            hideInfoDialogWithTwoButton();
                                        }
                                    }, true);

                            break;

                        case "0":
                            Toast.makeText(getContext(), getResources().getString(R.string.add_master_fail_msg),
                                    Toast.LENGTH_SHORT).show();

                            break;

                        default:

                            master.setId(s);

                            //// Upload Record /////
                            insertRecordToArchive();

                    }

                }
            }
        };

        /*uploadRecordToMasterObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null){

                    hideProgress();

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        switch (s){

                            case "error" :
                                showFailedDialog(getResources().getString(R.string.add_rec_to_master_fail_msg), true);
                                break;

                            case "-1" :
                                showInfoDialogWithOneButton(getResources().getString(R.string.add_rec_to_master_fail_msg),
                                        getResources().getString(R.string.added_record_before),getResources().getString
                                                (R.string.dialog_ok_button), new Closure() {
                                            @Override
                                            public void exec() {

                                                hideInfoDialogWithTwoButton();
                                            }
                                        }, true);
                                break;

                            case "0":
                                showFailedDialog(getResources().getString(R.string.add_rec_to_master_fail_msg), true);
                                break;

                            default:
                                showSuccessDialog(getResources().getString(R.string.add_rec_to_master_succ_msg), true);
                                //fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.GONE);

                        }
                    }

                }
            }
        };

         */

    }


    private void insertMasterRecord(){

//        if (selectedRegionID.equals("0")){
//
//            Toast.makeText(getContext(), getResources().getString(R.string.choose_region_message), Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (master!=null){

            insertRecordToArchive();
            return;
        }

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.add_master_loading_msg),
                false);

        master = new Master();

        master.setVehicleNumber(Utils.generateUniqueNumber());
        master.setVehicleType("");
        master.setLocation(selectedAdress);
        master.setDistrict(Utils.chooseNonNull(selectedSubLocality, selectedSubAdminArea));
        master.setLongitude(String.valueOf(selectedLatLan.longitude));
        master.setLatitude(String.valueOf(selectedLatLan.latitude));
        master.setIdRegions(selectedRegion.getId());
        master.setRegions(selectedRegion.getName());
        master.setNotes("");
        master.setType(String.valueOf(true));

        makeRecordsViewModel.insertMasterRecordString(master.getVehicleNumber(), "", selectedAdress,
                Utils.chooseNonNull(selectedSubLocality, selectedSubAdminArea), String.valueOf(selectedLatLan.longitude)
                , String.valueOf(selectedLatLan.latitude),MyApplication.getTinyDB().getString(Constants.KEY_USERID),
                selectedRegion.getId(), "", true).observe(getViewLifecycleOwner(), addMasterObserver);
    }


    private void insertRecordToArchive(){


        RecordHistory recordHistory = new RecordHistory(Integer.parseInt(master.getId()), master.getIdRegions(),master.getRegions(),
                master.getVehicleNumber(), master.getLocation(), recordFileName, recordFileUri.getPath(), String.valueOf(false));

        makeRecordsViewModel.getInsertRecordHistoryMutableLiveData(recordHistory).observe(getViewLifecycleOwner()
                ,uploadRecordToArchiveObserver(recordHistory));
    }


    private void uploadRecord(RecordHistory recordHistory){

        /*if (hasInternetConnection(getContext())) {

            try {
                InputStream iStream = getActivity().getContentResolver().openInputStream(Uri.fromFile(new File(recordFilePath)));
                byte[] voiceBytes = getBytes(iStream);

                showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.uploading_record),
                        false);

                makeRecordsViewModel.getUploadRecordMutableLiveData(voiceBytes, recordFileName, MyApplication.getTinyDB().getString
                        (Constants.KEY_USERID)).observe(getViewLifecycleOwner(), uploadRecordObserver);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showFailedDialog(getResources().getString(R.string.error_upload_record), true);
            } catch (IOException e) {
                e.printStackTrace();
                showFailedDialog(getResources().getString(R.string.error_upload_record), true);
            }

        }

         */

        /*Toast.makeText(getContext(), getResources().getString(R.string.cut_upload_record_warn_msg), Toast.LENGTH_LONG).show();
        uploadingRecordList.add(new UploadingRecord(recordFileUri, recordFileName, master));
        uploadingRecordsProgressAdapter.notifyDataSetChanged();

         */


        ///// Updated to make notification for upload progress /////

        if (hasInternetConnection(getContext())) {


            /*Intent intent = new Intent(((MainActivity)getActivity()),UploadRecordService.class);
            intent.putExtra("record_history", recordHistory);


            ((MainActivity)getActivity()).startService(intent);

             */
            NotificationBuilder notificationBuilder = new NotificationBuilder(((MainActivity)getActivity()))
                    .buildProgressNotification(recordHistory.getRecordName());

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            StorageReference ref = storageReference.child("audios/" + recordFileName);
            UploadTask uploadTask = ref.putFile(recordFileUri);

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

                                recordHistory.setRecordName(task.getResult().toString());
                                addUploadedRecordToMaster(recordHistory);
                                notificationBuilder.hideNotification();

                            } else {

                                notificationBuilder.showProgress(-1);
                            }
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            notificationBuilder.showProgress(-1);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());

                            notificationBuilder.showProgress(progress);


                        }
                    });



            /*makeRecordsViewModel.getUploadRecordMutableLiveData(recordFileUri, recordFileName).observe(getViewLifecycleOwner(),
                    uploadRecordObserver(recordHistory, new NotificationBuilder(((MainActivity)getActivity()))
                            .buildProgressNotification(recordHistory.getRecordName())));

             */

                /*try {
                    InputStream iStream = fragment.getActivity().getContentResolver()
                            .openInputStream(Uri.fromFile(new File(uploadingRecord.getRecordFilePath())));
                    byte[] voiceBytes = Utils.getBytes(iStream);


                    if (fragment.hasInternetConnection(context)) {

                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.progressCircular.setVisibility(
                                View.VISIBLE);
                        UploadingRecordsProgressViewHolder.this.itemUploadingRecordProcessBinding.loadingText.setText(
                                fragment.getResources().getString(R.string.uploading_record) + "\n" + uploadingRecord.getRecordName());

                        //// request to upload/////
                        new UploadAsyncClass(voiceBytes, uploadingRecord.getRecordName(), MyApplication.getTinyDB().getString
                                (Constants.KEY_USERID), uploadingRecord).execute();
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fragment.showFailedDialog(fragment.
                            getResources().getString(R.string.error_upload_record), true);


                } catch (IOException e) {
                    e.printStackTrace();
                    fragment.showFailedDialog(fragment.
                            getResources().getString(R.string.error_upload_record), true);

                }


                 */
        }

    }


    public void addUploadedRecordToMaster(RecordHistory recordHistory){

        makeRecordsViewModel.insertRecordToMasterLiveDatag(String.valueOf(recordHistory.getMasterId()), recordHistory.getRecordName(),
                "3gp", String.valueOf(Utils.getImageSizeFromUriInMegaByte(recordFileUri, getContext()))
                , MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(getViewLifecycleOwner(),
                uploadRecordToMasterObserver(recordHistory));
    }


    private void updateRecordHistory(RecordHistory recordHistory){

        makeRecordsViewModel.getUpdateRecordHistoryMutableLiveData(recordHistory).observe(getViewLifecycleOwner(),
                updateRecordHistoryObserver(recordHistory));
    }


    private Observer<List<Master>> dailyCarsObserver(GoogleMap googleMap) {

        return new Observer<List<Master>>() {
            @Override
            public void onChanged(List<Master> masters) {

                if (masters != null) {

                    hideProgress();

                    markerList = new ArrayList<>();

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        if (masters.isEmpty())
                            showSnackBar(R.string.no_daily_cars);

                        else {

                            if (!masters.get(0).getId().equals("-1")) {

                                Marker marker;

                                for (Master master : masters) {

                                    marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(
                                            master.getLatitude()), Double.parseDouble(master.getLongitude())))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                            //.title(master.getPlateNumber() + " , " + car.getKind() + " , " + car.getBank())
                                    );

                                    marker.setTag(master);
                                    markerList.add(marker);
                                    //marker.showInfoWindow();
                                }

                            } else {

                                showFailedDialog(getResources().getString(R.string.fail_load_daily_cars), true);
                            }
                        }


                        //// Get regions to fill the spinner //////

                        fillRegionsSpinner();
                    }
                }
            }
        };
    }


    private Observer<Long> uploadRecordToArchiveObserver(RecordHistory recordHistory){

        return new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {

                if (aLong !=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        if (aLong!= -1 && aLong!= 0){

                            recordHistory.setId(Integer.parseInt(String.valueOf(aLong)));

                            uploadRecord(recordHistory);
                        }

                        else {

                            showFailedDialog(getResources().getString(R.string.error_upload_record), true);

                        }
                    }
                }
            }
        };
    }


    private Observer<Map<String,String>> uploadRecordObserver(RecordHistory recordHistory, NotificationBuilder notificationBuilder){

        return new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {

                if (stringStringMap != null) {

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        switch (stringStringMap.get("success")) {

                            case "": ///// Still trying to upload

                                notificationBuilder.showProgress(0);

                                break;

                            case "false":

                                //hideProgress();

                                notificationBuilder.showProgress(-1);

                                showFailedDialog(getResources().getString(R.string.error_upload_record),
                                        true);
                                break;

                            case "progress":

                                notificationBuilder.showProgress(Double.parseDouble(stringStringMap.get("progress")));
                                break;

                            default:
                                //// Successed ////

                                recordHistory.setRecordName(stringStringMap.get("success"));
                                addUploadedRecordToMaster(recordHistory);


                        }
                    }
                }
            }
        };
    }


    private Observer<String> uploadRecordToMasterObserver(RecordHistory recordHistory){

        return new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        //hideProgress();

                        boolean succUploaded;
                        String failedMsg = "";

                        switch (s){

                            case "error" :

                                succUploaded = false;

                                failedMsg = getResources().getString(R.string.add_rec_to_master_fail_msg);

                                break;

                            case "-1" :

                                succUploaded = false;

                                failedMsg = getResources().getString(R.string.added_record_before);

                                break;

                            case "0":

                                succUploaded = false;

                                failedMsg = getResources().getString(R.string.add_rec_to_master_fail_msg);

                                break;

                            default:

                                succUploaded = true;

                        }


                        if (succUploaded){

                            Toast.makeText(getContext(), getResources().getString(R.string.add_rec_to_master_succ_msg),
                                    Toast.LENGTH_SHORT).show();

                        }

                        else {

                            Toast.makeText(getContext(), failedMsg, Toast.LENGTH_SHORT).show();


                        }

                        recordHistory.setIsUploaded(String.valueOf(succUploaded));

                        updateRecordHistory(recordHistory);
                    }

                }
            }
        };
    }


    private Observer<Integer> updateRecordHistoryObserver(RecordHistory recordHistory){

        return new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                /////////////
            }
        };
    }


    private Address getAdressDetailsFromLatLong(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = null;
        if (addresses != null && addresses.size() > 0) {


            /*address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            */
            address = addresses.get(0);

        }

        return address;
    }


    private void chooseAud() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, PICK_AUD_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_AUD_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ){

            recordFileName = getActivity().getApplicationContext().getPackageName() + "_" + Utils.getStringRandomCode() + ".3gp";

            recordFileUri = data.getData();

            insertMasterRecord();
        }


    }


    private void setRecordPath(){

        audioRecorder = new AudioRecorder();

        recordFileName = getActivity().getApplicationContext().getPackageName() + "_" + Utils.getStringRandomCode() + ".3gp";

        parentFile = new File(getActivity().getExternalFilesDir("com.alsalameg"), "records");

        if (!parentFile.exists())
            parentFile.mkdirs();


        recordFilePath = parentFile.getAbsolutePath() + "/" + recordFileName;

        recordFile = new File(recordFilePath);

        recordFileUri = Uri.fromFile(recordFile);

    }

    private boolean checkRecordPermission() {

            if (!Constants.checkRecorderPermission(getActivity())) {

                showInfoDialogWithOneButton(getResources().getString(R.string.req_record_and_read_write_perm),
                        getResources().getString(R.string.req_record_and_read_write_perm_message), getResources().getString(R.string.req_loc_perm_pos_btn),
                        new Closure() {
                            @Override
                            public void exec() {

                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        }, false);
                return false;
            }
            else return true;
        }


    private boolean checkLocationPermission() {

        if (!Constants.checkLocationPermission(getActivity())) {

            hideProgress();

            showInfoDialogWithOneButton(getResources().getString(R.string.req_loc_perm),
                    getResources().getString(R.string.req_loc_perm_message), getResources().getString(R.string.req_loc_perm_pos_btn),
                    new Closure() {
                        @Override
                        public void exec() {

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                        }
                    }, false);
            return false;
        } else return true;
    }


    private boolean checkSelectedPoint(){

        if (selectedLatLan==null){

            Toast.makeText(getContext(), getResources().getString(R.string.choose_point_firstly),
                    Toast.LENGTH_SHORT).show();

            return false;
        }
        else return true;
    }


    private boolean checkBeforeRecord(){

        if (selectedRegion.getId().equals("0")){

            Toast.makeText(getContext(), getResources().getString(R.string.choose_region_message),
                    Toast.LENGTH_SHORT).show();

            return false;
        }

        else return checkRecordPermission() && checkSelectedPoint();
    }



    /*private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

     */


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&  grantResults[0] == PERMISSION_GRANTED && grantResults[1] ==PERMISSION_GRANTED &&
                        grantResults[2] == PERMISSION_GRANTED) {

                    configureTheRecorder();

                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchCurrentLocation();

                }

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //// When back from opening GPS page ////
        if (isEnablingGps) fetchCurrentLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
