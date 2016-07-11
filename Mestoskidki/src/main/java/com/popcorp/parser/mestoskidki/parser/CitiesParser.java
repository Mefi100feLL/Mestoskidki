package com.popcorp.parser.mestoskidki.parser;

import com.popcorp.parser.mestoskidki.entity.City;
import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CitiesParser {

    public static Observable<ArrayList<City>> loadCities() {
        return APIFactory.getAPI().getCities()
                .flatMap(responseBody -> {
                    ArrayList<City> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("Mestoskidki: Page with cities not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    Matcher cityMatcher = Pattern.compile("href=\"http://mestoskidki.ru[.[^\"]]*\">(<strong>)?[.[^<]]*(</strong>)?</a>").matcher(page);
                    while (cityMatcher.find()) {
                        City city = getCity(cityMatcher.group());
                        if (city != null && !result.contains(city)) {
                            result.add(city);
                        }
                    }
                    return Observable.just(result);
                });
    }

    private static City getCity(String cityResult) {
        City result;
        String url;
        int id = 1;
        String name;

        Matcher urlMatcher = Pattern.compile("http://mestoskidki.ru[.[^\"]]*\"").matcher(cityResult);
        if (urlMatcher.find()) {
            url = urlMatcher.group();
        } else {
            ErrorManager.sendError("Mestoskidki: Url for city not finded! City: " + cityResult);
            return null;
        }
        Matcher idMatcher = Pattern.compile("=[0-9]*\"").matcher(url);
        if (idMatcher.find()) {
            String idResult = idMatcher.group();
            id = Integer.valueOf(idResult.substring(1, idResult.length() - 1));
        }
        Matcher nameMatcher = Pattern.compile("\">(<strong>)?[.[^<]]*(</strong>)?</a>").matcher(cityResult);
        if (nameMatcher.find()) {
            String nameResult = nameMatcher.group().replaceAll("<(/)?strong>", "");
            name = nameResult.substring(2, nameResult.length() - 4);
        } else {
            ErrorManager.sendError("Mestoskidki: Name for city not finded! City: " + cityResult);
            return null;
        }
        result = new City(id, name, url, 0);
        return result;
    }
}
