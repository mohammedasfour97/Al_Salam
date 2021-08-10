package com.alsalameg.ViewModels;

import com.alsalameg.Models.Car;
import com.alsalameg.Repositories.MapRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private MapRepository mapRepository;

    public MapViewModel() {

        mapRepository = new MapRepository();
    }

    public MutableLiveData<List<Car>> getCarsList(String id){

        return mapRepository.getCarsListMutableLiveData(id);
    }

    public MutableLiveData<String> getConfirmCarMutableLiveData(String id, String confirmation, String latitude, String longitude) {

        return mapRepository.getConfirmCarMutableLiveData(id, confirmation, latitude, longitude);
    }
}
