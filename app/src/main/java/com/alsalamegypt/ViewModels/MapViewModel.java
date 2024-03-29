package com.alsalamegypt.ViewModels;

import com.alsalamegypt.Models.Car;
import com.alsalamegypt.Models.IDName;
import com.alsalamegypt.Repositories.MapRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private MapRepository mapRepository;

    public MapViewModel() {

        mapRepository = new MapRepository();
    }

    public MutableLiveData<List<IDName>> getCarTypes(String id){

        return mapRepository.getCarTypesMutableLiveData(id);
    }

    public MutableLiveData<List<Car>> getCarsList(String id){

        return mapRepository.getCarsListMutableLiveData(id);
    }

    public MutableLiveData<String> getConfirmCarMutableLiveData(String id, String confirmation, String latitude, String longitude) {

        return mapRepository.getConfirmCarMutableLiveData(id, confirmation, latitude, longitude);
    }

    public MutableLiveData<String> getConfirmCarMutableLiveData(String id, String confirmation, String latitude, String longitude,
                                                                String regionId, String userId, String location, String district,
                                                                String vehicleType) {

        return mapRepository.getConfirmCarMutableLiveData(id, confirmation, latitude, longitude, regionId, userId, location, district,
                vehicleType);
    }
}
