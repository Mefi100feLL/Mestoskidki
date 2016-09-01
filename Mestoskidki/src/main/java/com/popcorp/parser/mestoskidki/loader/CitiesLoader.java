package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.City;
import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.parser.CitiesParser;
import com.popcorp.parser.mestoskidki.repository.CityRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;

public class CitiesLoader {

    private CityRepository cityRepository;

    public void loadCities() {
        try {
            cityRepository = Application.getCityRepository();
            CitiesParser.loadCities()
                    .subscribeOn(APIFactory.getScheduler())
                    .subscribe(new Observer<ArrayList<City>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorManager.sendError("Mestoskidki: Error loading citites error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ArrayList<City> cities) {
                            cityRepository.save(cities);
                        }
                    });
        } catch (Exception e) {
            ErrorManager.sendError("Mestoskidki: Error loading citites error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
