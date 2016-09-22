package com.popcorp.parser.mestoskidki.util;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.Error;
import com.popcorp.parser.mestoskidki.loader.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class ScheduledTasks {

    private static final long SECOND = 1000;

    private static final long MINUTE = 60 * SECOND;

    private static final long HALF_HOUR = 30 * MINUTE;
    private static final long HOUR = 60 * MINUTE;

    private static final long DAY = 24 * HOUR;

    @Scheduled(fixedRate = DAY, initialDelay = 5 * SECOND)
    public void loadCities() {
        new CitiesLoader().loadCities();
    }

    @Scheduled(fixedRate = DAY, initialDelay = 10 * SECOND)
    public void loadCategories() {
        new CategoriesLoader().loadCategories();
    }

    @Scheduled(fixedRate = DAY, initialDelay = 15 * SECOND)
    public void loadCategoriesInners() {
        new CategoriesInnerLoader().loadCategories();
    }

    @Scheduled(fixedRate = 10 * MINUTE, initialDelay = 20 * SECOND)
    public void loadShops() {
        new ShopsLoader().loadShops();
    }

    @Scheduled(fixedRate = HOUR, initialDelay = 25 * SECOND)
    public void clearOldSales() {
        new SalesCleaner().clearOldSales();
    }

    @Scheduled(fixedRate = HOUR, initialDelay = 30 * SECOND)
    public void loadSales() {
        new SalesLoader().loadSales();
    }

    @Scheduled(fixedRate = HALF_HOUR, initialDelay = 40 * SECOND)
    public void sendErrors() {
        for (Error error : Application.getErrorRepository().getAll()) {
            ErrorManager.sendError(error.getSubject(), error.getBody());
        }
    }
}
