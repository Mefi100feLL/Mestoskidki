package com.popcorp.parser.mestoskidki.parser;

import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalesParser {

    public static Observable<ArrayList<Integer>> loadSales(int cityId, long shopId) {
        return APIFactory.getAPI().getSales(cityId, shopId, 1)
                .flatMap(responseBody -> {
                    ArrayList<Observable<ArrayList<Integer>>> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("Mestoskidki: Page with sales not loaded! Shop: " + shopId + ", city: " + cityId + " page: 1, error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    int countPages = getPagesCount(page);
                    result.add(Observable.just(getSalesIds(page)));
                    if (countPages > 1) {
                        for (int pageIndex = 2; pageIndex < countPages + 1; pageIndex++) {
                            result.add(getIdsForPage(cityId, shopId, pageIndex));
                        }
                    }
                    return Observable.merge(result);
                });
    }

    private static Observable<ArrayList<Integer>> getIdsForPage(int cityId, long shopId, int pageIndex) {
        return APIFactory.getAPI().getSales(cityId, shopId, pageIndex)
                .flatMap(responseBody -> {
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("Mestoskidki: Page with sales not loaded! Shop: " + shopId + ", city: " + cityId + " page: " + pageIndex + ", error: " + e.getMessage());
                        return Observable.empty();
                    }
                    return Observable.just(getSalesIds(page));
                });
    }

    private static int getPagesCount(String page) {
        int result = 1;
        Matcher pageCountMatcher = Pattern.compile("<a href=\"view_shop\\.php\\?city=[0-9]*&shop=[0-9]*&page=[0-9]*\"><b>&#062;&#062;</b>").matcher(page);
        if (pageCountMatcher.find()) {
            String pageCountResult = pageCountMatcher.group();
            Matcher pageMatcher = Pattern.compile("page=[0-9]*\"").matcher(pageCountResult);
            if (pageMatcher.find()) {
                String pageResult = pageMatcher.group();
                result = Integer.valueOf(pageResult.substring(5, pageResult.length() - 1));
            }
        }
        return result;
    }

    private static ArrayList<Integer> getSalesIds(String page) {
        ArrayList<Integer> result = new ArrayList<>();
        Matcher saleIdMatcher = Pattern.compile("href='view_sale\\.php\\?city=[0-9]*&id=[0-9]*'").matcher(page);
        while (saleIdMatcher.find()) {
            String saleIdResult = saleIdMatcher.group();
            Matcher idMatcher = Pattern.compile("id=[0-9]*'").matcher(saleIdResult);
            if (idMatcher.find()) {
                String idResult = idMatcher.group();
                int id = Integer.valueOf(idResult.substring(3, idResult.length() - 1));
                if (!result.contains(id)) {
                    result.add(id);
                }
            }
        }
        return result;
    }
}
