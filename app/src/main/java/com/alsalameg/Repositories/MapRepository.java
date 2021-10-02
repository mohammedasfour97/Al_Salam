package com.alsalameg.Repositories;

import android.os.AsyncTask;

import com.alsalameg.BaseClasses.BaseRepository;
import com.alsalameg.Models.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class MapRepository extends BaseRepository {

    private MutableLiveData<List<Car>> carsListMutableLiveData;
    private MutableLiveData<String> confirmCarMutableLiveData;

    public MapRepository() {
        super();
    }


    public MutableLiveData<List<Car>> getCarsListMutableLiveData(String id) {

        carsListMutableLiveData = new MutableLiveData<>();

        new GetCarsListAsyncClass(id).execute();

        return carsListMutableLiveData;
    }

    public MutableLiveData<String> getConfirmCarMutableLiveData(String id, String confirmation, String latitude, String longitude) {

        confirmCarMutableLiveData = new MutableLiveData<>();

        new ConfirmCarAsyncClass(id, confirmation, latitude, longitude).execute();

        return confirmCarMutableLiveData;
    }

    private class GetCarsListAsyncClass extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

        private String id;

        public GetCarsListAsyncClass(String id) {
            this.id = id;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(Void... voids) {
            return webServices.getCars(id);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> carsList) {

            Car car;
            List<Car> carList = new ArrayList<>();

            if (carsList != null) {


                for (HashMap<String, String> map : carsList) {

                    car = new Car(map.get("ID"), map.get("ID_MasterDailyChit"), map.get("PlateNumber"), map.get("Kind"),
                            map.get("ContractNumber"), map.get("ShasNumber"), map.get("Color"), map.get("Bank"), map.get("Notes"),
                            map.get("Status"), map.get("DetermineStatus"), map.get("DetermineObserver"), map.get("ID_Vehicles"),
                            map.get("Customer"), map.get("Maker"), map.get("ID_Regions"), map.get("DateCreate"), map.get("DateEdite"),
                            map.get("Original_Number"), map.get("After_Repetition"), map.get("Corresponding_Number"),
                            map.get("FullName"), map.get("Regions"), map.get("Vehicle_Number"), map.get("Vehicle_Type"), map.get("Location"),
                            map.get("District"), map.get("Longitude"), map.get("Latitude"), map.get("Sorting"));

                    carList.add(car);
                }

//                carList.get(0).setLatitude("31.212249");
//                carList.get(0).setLongitude("29.919864");
//                carList.get(0).setStatus("مجمع الشغل");
//
//                carList.get(1).setLatitude("31.212249");
//                carList.get(1).setLongitude("29.919864");
//
//                carList.get(2).setLatitude("31.212249");
//                carList.get(2).setLongitude("29.919864");
//                carList.get(2).setStatus("احاله اليوم");

            } else {

                car = new Car();
                car.setId("");
                carList.add(car);
            }

            carsListMutableLiveData.postValue(carList);

        }
    }


    private class ConfirmCarAsyncClass extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

        private String id, confirmation, latitude, longitude;

        public ConfirmCarAsyncClass(String id, String confirmation, String latitude, String longitude) {
            this.id = id;
            this.confirmation = confirmation;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(Void... voids) {
            return webServices.confirmCar(id, confirmation, latitude, longitude);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> confirmResult) {

            String result;

            if (confirmResult != null && !confirmResult.isEmpty() && !confirmResult.get(0).get("ID").equals("0")) {

                result = confirmResult.get(0).get("ID");
            } else {

                result = "error";
            }

            confirmCarMutableLiveData.postValue(result);
        }

    }
}
