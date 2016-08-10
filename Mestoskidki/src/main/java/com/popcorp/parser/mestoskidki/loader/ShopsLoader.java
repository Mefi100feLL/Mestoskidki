package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.City;
import com.popcorp.parser.mestoskidki.entity.Shop;
import com.popcorp.parser.mestoskidki.parser.ShopsParser;
import com.popcorp.parser.mestoskidki.repository.CityRepository;
import com.popcorp.parser.mestoskidki.repository.ShopsRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class ShopsLoader {

    private ShopsRepository shopsRepository;

    public void loadShops() {
        try {
            shopsRepository = Application.getShopsRepository();
            CityRepository cityRepository = Application.getCityRepository();
            Iterable<City> cities = cityRepository.getAll();
            for (City city : cities) {
                ShopsParser.loadShops(city.getId())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Observer<ArrayList<Shop>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ErrorManager.sendError("Mestoskidki: Error loading shops error: " + e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(ArrayList<Shop> shops) {
                                shopsRepository.save(shops);
                            }
                        });
            }
        } catch (Exception e){
            ErrorManager.sendError("Mestoskidki: Error loading shops error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
