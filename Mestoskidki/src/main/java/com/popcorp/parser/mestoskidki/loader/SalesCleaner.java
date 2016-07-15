package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.City;
import com.popcorp.parser.mestoskidki.entity.Sale;
import com.popcorp.parser.mestoskidki.repository.CityRepository;
import com.popcorp.parser.mestoskidki.repository.SaleRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;

import java.util.Calendar;

public class SalesCleaner {

    private CityRepository cityRepository;

    private SaleRepository saleRepository;

    public void clearOldSales(){
        try {
            cityRepository = Application.getCityRepository();
            saleRepository = Application.getSaleRepository();
            for (City city : cityRepository.getAll()) {
                Calendar cityTime = Calendar.getInstance();
                cityTime.add(Calendar.HOUR_OF_DAY, city.getTimeZone());
                for (Sale sale : saleRepository.getAllForCity(city.getId())) {
                    Calendar saleTime = Calendar.getInstance();
                    saleTime.setTimeInMillis(sale.getPeriodEnd());
                    saleTime.add(Calendar.DAY_OF_YEAR, 1);
                    saleTime.set(Calendar.HOUR_OF_DAY, 0);
                    saleTime.set(Calendar.MINUTE, 30);
                    if (cityTime.getTimeInMillis() > saleTime.getTimeInMillis()) {
                        saleRepository.removeSale(city.getId(), sale.getId());
                    }
                }
            }
        } catch (Exception e){
            ErrorManager.sendError("Mestoskidki: Error clearning sales error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
