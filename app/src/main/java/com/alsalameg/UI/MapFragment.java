package com.alsalameg.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.Models.Car;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.UI.FragmentDialogs.AddCarDetailsFragment;
import com.alsalameg.UI.FragmentDialogs.CarMarkerDetailsFragment;
import com.alsalameg.ViewModels.MapViewModel;
import com.alsalameg.databinding.FragmentUserMapBinding;
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

import java.util.List;

public class MapFragment extends BaseFragment {

    // vars for google map
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FragmentUserMapBinding fragmentMapBinding;
    private SupportMapFragment mapFragment;
    private OnMapReadyCallback callback;
    private LatLng latLng;

    // vars for api
    private MapViewModel mapViewModel;

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

        mapViewModel = ViewModelProviders.of(MapFragment.this).get(MapViewModel.class);

        fetchCurrentLocation();
    }

    private void setListeners(){

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

                                ((MainActivity)getActivity()).navController.navigate(R.id.action_fragment_map_to_fragment_login);
                            }
                        }, new Closure() {
                            @Override
                            public void exec() {

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

                hideProgress();

                if (checkPermission()){

                    googleMap.setMyLocationEnabled(true);
                    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                }

                requestCarsLocations(googleMap);

                final int[] click = {0};

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        click[0]++;

                        if (click[0]==2){

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            CarMarkerDetailsFragment carMarkerDetailsFragment = new CarMarkerDetailsFragment((Car)marker.getTag(), latLng);
                            carMarkerDetailsFragment.show(fm, "fragment_new_activity");

                            click[0] = 0;
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

    private void fetchCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            return;
        }
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


    private void requestCarsLocations(GoogleMap googleMap){

        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false);

        mapViewModel.getCarsList(MyApplication.getTinyDB().getString(Constants.KEY_USERID)).observe(getViewLifecycleOwner(),
                carMarkObserver(googleMap));
    }

    private Observer<List<Car>> carMarkObserver(GoogleMap googleMap) {

        return new Observer<List<Car>>() {
            @Override
            public void onChanged(List<Car> cars) {

                if (cars != null) {

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {

                        hideProgress();

                        if (!cars.isEmpty()) {

                            if (!cars.get(0).getId().equals("")) {

                                for (Car car : cars) {

                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(
                                            car.getLatitude()), Double.parseDouble(car.getLongitude()))).icon(getMark(car.getStatus()))
                                            .title(car.getPlateNumber() + " , " + car.getKind() + " , " + car.getBank()));

                                    marker.setTag(car);
                                    marker.showInfoWindow();

                                }
                            }

                            else {

                                showFailedDialog(getResources().getString(R.string.fail_load_cars), true);
                            }
                        }

                        else
                            showFailedDialog(getResources().getString(R.string.no_cars), true);
                    }
                }
            }
        };
    }


    private BitmapDescriptor getMark(String carStatus){

        switch (carStatus){

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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
