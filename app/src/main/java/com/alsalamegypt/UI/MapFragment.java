package com.alsalamegypt.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.VectorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.StateSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Interfaces.IOnBackPressed;
import com.alsalamegypt.Models.Car;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.UI.FragmentDialogs.CarMarkerDetailsFragment;
import com.alsalamegypt.UI.FragmentDialogs.SameMultipleLatlongMarksFragment;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.MakeRecordsViewModel;
import com.alsalamegypt.ViewModels.MapViewModel;
import com.alsalamegypt.databinding.FragmentUserMapBinding;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;

public class MapFragment extends BaseFragment implements IOnBackPressed {

    // vars for google map
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FragmentUserMapBinding fragmentMapBinding;
    private SupportMapFragment mapFragment;
    private OnMapReadyCallback callback;
    private Location currentLocation;
    private List<Car> carsList, cars, filteredList, bridgeList;
    private Marker selectedMarker;
    private GoogleMap googleMap;

    // vars for spinners ///
    private List<IDName> regionsList, carTypesList;
    private ArrayAdapter<IDName> regionsArrayAdapter, carTypesArrayAdapter;
    private IDName selectedRegion, selectedCarType;

    private Keyboard keyboard;

    // vars for api //
    private MapViewModel mapViewModel;
    private MakeRecordsViewModel makeRecordsViewModel;
    private Observer<List<IDName>> regionsObserver, carTypesObserver;
    private Observer<List<Car>> carsListObserver;

    private String[] filters = new String[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_map, container, false);
        return fragmentMapBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
        initSpinners();
        initObservers();

        fetchCurrentLocation();
    }


    private void setListeners() {

        fragmentMapBinding.searchBar.setInputType(InputType.TYPE_NULL);
        keyboard = new Keyboard(getContext(), R.xml.qwerty);
        fragmentMapBinding.fragmentMapKeyboardView.setKeyboard(keyboard);
        fragmentMapBinding.fragmentMapKeyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int i) {

            }

            @Override
            public void onRelease(int i) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {

                if (fragmentMapBinding.searchBar.getText() == null) return;

                int start = fragmentMapBinding.searchBar.getSelectionStart();

                switch (primaryCode) {
                    case -5:
                        if(fragmentMapBinding.searchBar.getText()!=null && start>0 )
                            fragmentMapBinding.searchBar.getText().delete(start - 1, start);
                        break;
                    case -4:

                        drawFilteredMarksOnMap(fragmentMapBinding.searchBar.getText().toString());
                        break;
                    default:
                        fragmentMapBinding.searchBar.getText().insert(start, Character.toString((char) primaryCode));
                }
            }

            @Override
            public void onText(CharSequence charSequence) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });

        fragmentMapBinding.searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) showCustomKeyboard(v, fragmentMapBinding.fragmentMapKeyboardView);
                else hideCustomKeyboard(fragmentMapBinding.fragmentMapKeyboardView);
            }
        });

        fragmentMapBinding.searchBar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showCustomKeyboard(v, fragmentMapBinding.fragmentMapKeyboardView);
            }
        });


        fragmentMapBinding.fragmentMapLogoutBtn.setOnClickListener(new View.OnClickListener() {
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

                                ((MainActivity) getActivity()).navController.navigate(R.id.action_fragment_map_to_fragment_login);
                            }
                        }, new Closure() {
                            @Override
                            public void exec() {/////////

                                hideInfoDialogWithTwoButton();
                            }
                        }, false);
            }
        });

        fragmentMapBinding.fragmentMapReloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchCurrentLocation();
            }
        });
    }


    private void fetchCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            return;
        }

        if (!isGPSEnabled(getContext())){

            showWarningDialog(getResources().getString(R.string.enable_gps), getResources().getString(R.string.enable_gps_msg),
                    getResources().getString(R.string.enable), new Closure() {
                        @Override
                        public void exec() {

                            getActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    }, true);
        }

        else {

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

                        setGoogleMap(location);
                    }
                }
            });
        }

    }


    private void setGoogleMap(Location currentLocation) {

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

                hideProgress();

                if (checkPermission()) {

                    googleMap.setMyLocationEnabled(true);

                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
                    //googleMap.getUiSettings().setMapToolbarEnabled(false);


                }

                MapFragment.this.googleMap = googleMap;

                setSpinnersListener();
                requestCarsLocations();

                final int[] click = {0};

                cars = new ArrayList<>();

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                        if (fragmentMapBinding.searchBar.hasFocus())
                            fragmentMapBinding.searchBar.clearFocus();
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        cars.clear();

                        for (Car car : carsList) {

                            if (marker.getPosition().longitude + marker.getPosition().latitude == Double.parseDouble(car.getLongitude())
                                    + Double.parseDouble(car.getLatitude()))
                                cars.add(car);

                        }

                        if (cars.size() > 1) {

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            SameMultipleLatlongMarksFragment sameMultipleLatlongMarksFragment =
                                    new SameMultipleLatlongMarksFragment(cars, currentLocation);
                            sameMultipleLatlongMarksFragment.show(fm, "fragment_new_activity");

                        } else {

                            if (selectedMarker == null)
                                selectedMarker = marker;

                            if (selectedMarker.getPosition().longitude + selectedMarker.getPosition().latitude ==
                                    marker.getPosition().longitude + marker.getPosition().latitude) {

                                click[0]++;

                                if (click[0] == 2) {

                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    CarMarkerDetailsFragment carMarkerDetailsFragment = new CarMarkerDetailsFragment(
                                            (Car) marker.getTag(), currentLocation);
                                    carMarkerDetailsFragment.show(fm, "fragment_new_activity");

                                    click[0] = 0;

                                }
                            } else {

                                selectedMarker = marker;
                                click[0] = 1;
                            }

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


    private void initSpinners(){

        regionsList = new ArrayList<>();
        carTypesList = new ArrayList<>();

        resetSpinners();

        regionsArrayAdapter = new ArrayAdapter<IDName>(getContext(), android.R.layout.simple_spinner_item, regionsList);
        regionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        fragmentMapBinding.regionSpinner.setAdapter(regionsArrayAdapter);

        carTypesArrayAdapter = new ArrayAdapter<IDName>(getContext(), android.R.layout.simple_spinner_item, carTypesList);
        carTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        fragmentMapBinding.carTypesSpinner.setAdapter(carTypesArrayAdapter);
    }


    private void setSpinnersListener(){

        fragmentMapBinding.regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedRegion = ((IDName)parent.getSelectedItem());
                drawFilteredMarksOnMap(fragmentMapBinding.searchBar.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fragmentMapBinding.carTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedCarType = ((IDName)parent.getSelectedItem());

                drawFilteredMarksOnMap(fragmentMapBinding.searchBar.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void configureSearchBar(){

        fragmentMapBinding.searchBar.setVisibility(View.VISIBLE);
//        fragmentMapBinding.searchBar.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//
////                if (!drawFilteredMarksOnMap(cs.toString()))
////                    Toast.makeText(getContext(), getResources().getString(R.string.no_cars), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//
////                if (!drawFilteredMarksOnMap(arg0.toString()))
////                    Toast.makeText(getContext(), getResources().getString(R.string.no_cars), Toast.LENGTH_SHORT).show();
//            }
//        });
        fragmentMapBinding.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (!drawFilteredMarksOnMap(v.getText().toString()))
                        Toast.makeText(getContext(), getResources().getString(R.string.no_cars), Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });
//        fragmentMapBinding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                if (!drawFilteredMarksOnMap(query))
//                    Toast.makeText(getContext(), getResources().getString(R.string.no_cars), Toast.LENGTH_SHORT).show();
//
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                drawFilteredMarksOnMap(newText);
//
//                return false;
//            }
//        });

    }


    private void resetSpinners(){

        regionsList.clear();
        regionsList.add(new IDName("-1", getResources().getString(R.string.choose_region)));
        selectedRegion = regionsList.get(0);

        carTypesList.clear();
        carTypesList.add(new IDName("-1", getResources().getString(R.string.choose_car_type)));
        selectedCarType = carTypesList.get(0);

    }


    private void requestCarsLocations() {

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        mapViewModel.getCarsList(MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(getViewLifecycleOwner(),
                carMarkObserver());
    }


    private void fillRegionsSpinner(){

        makeRecordsViewModel.getGetRegionsLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), regionsObserver);
    }


    private void fillCarTypesSpinner(){

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        mapViewModel.getCarTypes(MyApplication.getTinyDB().getString(Constants.KEY_USERID))
                .observe(getViewLifecycleOwner(), carTypesObserver);
    }


    private boolean drawFilteredMarksOnMap(String query) {

        if (filteredList == null)
            filteredList = new ArrayList<>();

        if (bridgeList == null)
            bridgeList = new ArrayList<>();

        filteredList.clear();
        bridgeList.clear();

        googleMap.clear();

        if(TextUtils.isEmpty(query) && selectedRegion.getId().equals("-1") && selectedCarType.getId().equals("-1")){

            filteredList.addAll(carsList);
        }

        else{

            filteredList.addAll(carsList);

            if(!TextUtils.isEmpty(query)){

                for (Car car: filteredList){

                    if ((car.getPlateNumber().contains(query) || car.getKind().contains(query) || car.getNotes().contains(query)
                            || car.getBank().contains(query))){

                        bridgeList.add(car);
                    }
                }

                filteredList.clear();
                filteredList.addAll(bridgeList);
                bridgeList.clear();

            }

            if (!selectedRegion.getId().equals("-1")){

                for (Car car: filteredList){

                    if (car.getRegions().equals(selectedRegion.getName())){

                        bridgeList.add(car);
                    }
                }

                filteredList.clear();
                filteredList.addAll(bridgeList);
                bridgeList.clear();
            }

            if (!selectedCarType.getId().equals("-1")){

                for (Car car: filteredList){

                    if (car.getVehicleType().equals(selectedCarType.getName())){

                        bridgeList.add(car);
                    }
                }

                filteredList.clear();
                filteredList.addAll(bridgeList);
                bridgeList.clear();
            }

        }

        Marker marker;
        boolean isExist = false;

        for(Car car : filteredList){

            marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(
                            car.getLatitude()), Double.parseDouble(car.getLongitude()))).icon(getMark(car.getColors()))
                    .title(car.getPlateNumber() + " , " + car.getKind() + " , " + car.getNotes()
                            + " , " + car.getBank()));

            marker.showInfoWindow();
            marker.setTag(car);

            if (filteredList.indexOf(car) == filteredList.size()-1){

                LatLng markLatLng = new LatLng(Double.parseDouble(car.getLatitude()),Double.parseDouble(car.getLongitude()));

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(markLatLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markLatLng, 13));
            }



            isExist = true;
        }

        return isExist;
    }


    private void initObservers(){

        mapViewModel = ViewModelProviders.of(MapFragment.this).get(MapViewModel.class);
        makeRecordsViewModel = ViewModelProviders.of(MapFragment.this).get(MakeRecordsViewModel.class);

        regionsObserver = new Observer<List<IDName>>() {
            @Override
            public void onChanged(List<IDName> idNames) {

                if (idNames!=null){

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        if (idNames.isEmpty()) {

                            Toast.makeText(getContext(), getResources().getString(R.string.err_loading), Toast.LENGTH_SHORT).show();
                        } else {

                            resetSpinners();
                            regionsList.addAll(idNames);
                            regionsArrayAdapter.notifyDataSetChanged();

                            fillCarTypesSpinner();
                        }
                    }

                }
            }
        };

        carTypesObserver = new Observer<List<IDName>>() {
            @Override
            public void onChanged(List<IDName> idNames) {

                if (idNames!=null){

                    hideProgress();

                    if (idNames.isEmpty()){

                        Toast.makeText(getContext(), getResources().getString(R.string.err_loading), Toast.LENGTH_SHORT).show();
                    }

                    else {

                        carTypesList.addAll(idNames);
                        carTypesArrayAdapter.notifyDataSetChanged();
                    }

                }
            }
        };
    }


    private Observer<List<Car>> carMarkObserver() {

        if (carsListObserver == null) {

            carsListObserver = new Observer<List<Car>>() {
                @Override
                public void onChanged(List<Car> cars) {

                    if (cars != null) {

                        if (carsList == null)
                            carsList = new ArrayList<>();

                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                            hideProgress();

                            if (!cars.isEmpty()) {

                                if (!cars.get(0).getId().equals("")) {

                                    carsList.clear();
                                    carsList.addAll(cars);

                                    drawFilteredMarksOnMap("");

                                    configureSearchBar();

                                } else {

                                    showFailedDialog(getResources().getString(R.string.fail_load_cars), true);
                                }
                            } else
                                showFailedDialog(getResources().getString(R.string.no_cars), true);
                        }

                        mapViewModel.getCarsList(MyApplication.getTinyDB().getString(Constants.KEY_USERID)).removeObserver(this);

                        fillRegionsSpinner();
                    }
                }
            };
        }

        return carsListObserver;
    }


    private BitmapDescriptor getMark(String hexColor) {

        int color = Color.parseColor(hexColor);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        float[] hslColor = new float[3];

        ColorUtils.RGBToHSL(r,g,b,hslColor);

        return BitmapDescriptorFactory.fromBitmap(
                Utils.changeBitmapColor(Utils.getBitmapFromVectorDrawable(getContext(),R.drawable.ic__665633_location_pin_icon), color));

    }
    /*private BitmapDescriptor getMark(String carStatus) {

        switch (carStatus) {

            case "مجمع الشغل":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);

            case "احالات جديده":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

            case "جرد جديد":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);

            case "احاله اليوم":
                return BitmapDescriptorFactory.defaultMarker();

            case "شغل اليوم":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET); // penafsegy

            case "تثبيت":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

            case "ايقافات":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA); // fushia

            default:
                return BitmapDescriptorFactory.defaultMarker();
        }
    }

     */


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private boolean checkPermission() {

        if (!Constants.checkLocationPermission(getActivity())) {

            showInfoDialogWithOneButton(getResources().getString(R.string.req_loc_perm),
                    getResources().getString(R.string.req_loc_perm_message), getResources().getString(R.string.req_loc_perm_pos_btn),
                    new Closure() {
                        @Override
                        public void exec() {

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        }
                    }, false);
            return false;
        } else return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchCurrentLocation();
                }
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        return fragmentMapBinding.fragmentMapKeyboardView.getVisibility() == View.VISIBLE;
    }


}
