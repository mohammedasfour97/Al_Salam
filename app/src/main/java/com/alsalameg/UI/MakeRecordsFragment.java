package com.alsalameg.UI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalameg.AudioRecorder;
import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.UI.FragmentDialogs.AddCarDetailsFragment;
import com.alsalameg.Utils;
import com.alsalameg.ViewModels.MakeRecordsViewModel;
import com.alsalameg.databinding.FragmentMakeRecordsBinding;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MakeRecordsFragment extends BaseFragment {

    public FragmentMakeRecordsBinding fragmentMakeRecordsBinding;

    //Variables for recorder
    private AudioRecorder audioRecorder;
    private String recordFilePath;
    private File parentFile, recordFile;
    private String recordPath, recordFileName;

    //Variables for map
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private OnMapReadyCallback callback;
    private LatLng MylatLng, selectedLatLan;
    private String selectedAdress, selectedSubAdminArea, selectedSubLocality;
    private Marker selectedMark;

    //VariablesForServices
    private MakeRecordsViewModel makeRecordsViewModel;
    private Observer<String> uploadRecordObserver, uploadRecordToMasterObserver;

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
        configureTheRecorder();
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

                if (MyApplication.getTinyDB().getString("master_id") != null &&
                !MyApplication.getTinyDB().getString("master_id").equals(""))
                    fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.VISIBLE);
                else
                    showFailedDialog(getResources().getString(R.string.add_master_fir), true);
            }
        });
    }


    private void configureTheRecorder(){

        fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) ==
                        PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.
                        requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                                0);

                return false;

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkRecordPermission()) {return;} {

            fragmentMakeRecordsBinding.fragmentMakeRecordsRecordButton.setEnabled(true);

            fragmentMakeRecordsBinding.fragmentMakeRecordsRecordButton.setRecordView(fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView);
            fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView.setSlideToCancelText(getResources().getString(R.string.swipe_to_cancel));



            fragmentMakeRecordsBinding.fragmentMakeRecordsRecordView.setOnRecordListener(new OnRecordListener() {
                @Override
                public void onStart() {

                    setRecordPath();

                    if (checkSelectedPoint()){

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
                    }
                    else{

                        showInfoDialogWithTwoButtons(getResources().getString(R.string.sure_make_record_title),
                                getResources().getString(R.string.sure_make_record) + "\n" + getResources().getString(R.string.period)
                                        + recordTime / 1000 + " " + getResources().getString(R.string.second) + "\n" +
                                        getResources().getString(R.string.location) + selectedAdress, getResources().getString(R.string.yes), getResources().getString(R.string.no),
                                new Closure() {
                                    @Override
                                    public void exec() {

                                        uploadRecord();

//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("address", selectedAdress);
//                                        bundle.putString("path", recordFilePath);
//                                        Navigation.findNavController(getActivity(),R.id.fragment_main_record_fragment_nav_host_fragment).navigate(
//                                                R.id.action_fragment_make_records_to_fragment_listen_records, bundle);
                                    }
                                }, new Closure() {
                                    @Override
                                    public void exec() {

                                        hideInfoDialogWithTwoButton();
                                    }
                                }, false);
                    }

                    }

                @Override
                public void onLessThanSecond() {

                    Toast.makeText(getContext(), getResources().getString(R.string.hold_record_button), Toast.LENGTH_SHORT).show();
                    audioRecorder.stop();
                    recordFile.delete();
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

                        showInfoDialogWithTwoButtons(getResources().getString(R.string.record_for_sel_pos),
                                getResources().getString(R.string.record_for_sel_pos_msg), getResources().getString(R.string.yes),
                                getResources().getString(R.string.no), new Closure() {
                                    @Override
                                    public void exec() {

                                        marker.setTitle(getResources().getString(R.string.sel_mark));
                                        marker.showInfoWindow();
                                        if (selectedMark!= null) selectedMark.setIcon(BitmapDescriptorFactory.defaultMarker());
                                        selectedMark = marker;
                                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                        selectedLatLan = selectedMark.getPosition();
                                        selectedAdress = getAdressDetailsFromLatLong(selectedLatLan).getAddressLine(0);
                                        selectedSubAdminArea = getAdressDetailsFromLatLong(selectedLatLan).getSubAdminArea();
                                        selectedSubLocality = getAdressDetailsFromLatLong(selectedLatLan).getSubLocality();

                                        fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.GONE);

                                        //Show dialog to choose write or send record for the selected point
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        AddCarDetailsFragment addCarDetailsFragment = new AddCarDetailsFragment(selectedAdress,
                                                selectedSubAdminArea, selectedSubLocality, String.valueOf(currentLocation.getLongitude()),
                                                String.valueOf(currentLocation.getLatitude()));
                                        addCarDetailsFragment.show(fm, "fragment_new_activity");


                                    }
                                }, new Closure() {
                                    @Override
                                    public void exec() {

                                        hideInfoDialogWithTwoButton();
                                    }
                                }, false);
                        return false;
                    }
                });
            }
        };

        if (mapFragment == null)
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(callback);
    }


    private void fetchCurrentLocation() {

        //showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkLocationPermission()) {return;}
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
                    currentLocation = location;

                    setGoogleMap();
                }
            }
        });
    }


    private void initObservers(){

        uploadRecordObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null) {

                    if (s.equals("DONE")){

                        makeRecordsViewModel.insertRecordToMasterLiveDatag(MyApplication.getTinyDB().getString("master_id"),
                                recordFileName, "3gp", String.valueOf(utils.getImageSizeFromUriInMegaByte
                                                (Uri.fromFile(recordFile), getContext())), MyApplication.getTinyDB().
                                        getString(Constants.KEY_USERID)).observe(getViewLifecycleOwner(), uploadRecordToMasterObserver);

                    }

                    else{

                        hideProgress();
                        showFailedDialog(getResources().getString(R.string.error_upload_record), true);
                    }


                }
            }
        };

        uploadRecordToMasterObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s!=null){

                    hideProgress();

                    switch (s){

                        case "error" :
                            showFailedDialog(getResources().getString(R.string.error_upload_record), true);
                            break;

                        case "-1" :
                            showInfoDialogWithOneButton(getResources().getString(R.string.err_insert),
                                    getResources().getString(R.string.added_record_before),getResources().getString
                                            (R.string.dialog_ok_button), new Closure() {
                                        @Override
                                        public void exec() {

                                            hideInfoDialogWithTwoButton();
                                        }
                                    }, true);
                            break;

                        case "0":
                            showFailedDialog(getResources().getString(R.string.error_upload_record), true);
                            break;

                        default:
                            showSuccessDialog(getResources().getString(R.string.succ_upload_record), true);
                            fragmentMakeRecordsBinding.fragmentMakeRecordsRecorderLayout.setVisibility(View.GONE);

                    }
                }
            }
        };
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

            Toast.makeText(getContext(), getResources().getString(R.string.no_selected_point), Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
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


    private void setRecordPath(){

        audioRecorder = new AudioRecorder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);

        parentFile = new File(Environment.getExternalStorageDirectory(), "com.alsalameg/records");


            if (!parentFile.exists()) parentFile.mkdirs();

        recordFileName = getActivity().getApplicationContext().getPackageName() + "_" +
                dateFormat.format(new Date()) + ".3gp";

        recordFilePath = parentFile.getAbsolutePath() + "/" + recordFileName;

        recordFile = new File(recordFilePath);

    }


    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void uploadRecord(){

        if (hasInternetConnection(getContext())) {

            try {
                InputStream iStream = getActivity().getContentResolver().openInputStream(Uri.fromFile(new File(recordFilePath)));
                byte[] voiceBytes = getBytes(iStream);

                showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.uploading_record), false);

                makeRecordsViewModel.getUploadRecordMutableLiveData(voiceBytes, recordFileName).observe(getViewLifecycleOwner(),
                        uploadRecordObserver);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showFailedDialog(getResources().getString(R.string.error_upload_record), true);
            } catch (IOException e) {
                e.printStackTrace();
                showFailedDialog(getResources().getString(R.string.error_upload_record), true);
            }

        }

    }

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
    public void onStop() {
        super.onStop();

        MyApplication.getTinyDB().putString("master_id", "");
    }


}
