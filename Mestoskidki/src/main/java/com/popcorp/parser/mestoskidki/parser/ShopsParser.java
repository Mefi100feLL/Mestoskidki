package com.popcorp.parser.mestoskidki.parser;

import com.popcorp.parser.mestoskidki.entity.Shop;
import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopsParser {

    public static Observable<ArrayList<Shop>> loadShops(int cityId) {
        return APIFactory.getAPI().getShops(cityId)
                .flatMap(responseBody -> {

                    ArrayList<Shop> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("Mestoskidki: Page with shops not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    Matcher shopMatcher = Pattern.compile("city=[0-9]+&shop=[0-9]+' class='left_links2'>[.[^<]]*</a>").matcher(page);
                    while (shopMatcher.find()) {
                        String shopResult = shopMatcher.group();
                        int id;
                        String name;
                        int count;

                        Matcher idMatcher = Pattern.compile("shop=[0-9]*'").matcher(shopResult);
                        if (idMatcher.find()) {
                            String idResult = idMatcher.group();
                            id = Integer.valueOf(idResult.substring(5, idResult.length() - 1));
                        } else {
                            ErrorManager.sendError("Mestoskidki: Id for shop not finded! Shop: " + shopResult);
                            continue;
                        }
                        Matcher nameMatcher = Pattern.compile(">[.[^\\(]]*\\([0-9]").matcher(shopResult);
                        if (nameMatcher.find()) {
                            String nameResult = nameMatcher.group();
                            name = nameResult.substring(1, nameResult.length() - 3);
                        } else {
                            ErrorManager.sendError("Mestoskidki: Name for shop not finded! Shop: " + shopResult);
                            continue;
                        }
                        Matcher countMatcher = Pattern.compile("\\([0-9]*\\)").matcher(shopResult);
                        if (countMatcher.find()) {
                            String countResult = countMatcher.group();
                            count = Integer.valueOf(countResult.substring(1, countResult.length() - 1));
                        } else {
                            ErrorManager.sendError("Mestoskidki: Count for shop not finded! Shop: " + shopResult);
                            continue;
                        }
                        Shop shop = new Shop(id, name, "", count, cityId);
                        if (!result.contains(shop)) {
                            result.add(shop);
                        }
                    }
                    int i = 0;
                    Matcher shopImageMatcher = Pattern.compile("shop=[0-9]+'><img class='img_shop' align='left' src='[.[^']]*'").matcher(page);
                    while (shopImageMatcher.find()) {
                        String shopImageResult = shopImageMatcher.group();
                        int id = 0;
                        Matcher idMatcher = Pattern.compile("shop=[0-9]+'").matcher(shopImageResult);
                        if (idMatcher.find()) {
                            String idResult = idMatcher.group();
                            id = Integer.valueOf(idResult.substring(5, idResult.length() - 1));
                        }
                        Matcher imageMatcher = Pattern.compile("src='[.[^']]*'").matcher(shopImageResult);
                        if (imageMatcher.find()) {
                            String imageResult = imageMatcher.group();
                            String imageUrl = imageResult.substring(5, imageResult.length() - 1);
                            for (Shop shop : result) {
                                if (shop.getId() == id) {
                                    shop.setImage(imageUrl);
                                }
                            }
                        } else {
                            ErrorManager.sendError("Mestoskidki: Image for shop not finded! Shop: " + result.get(i));
                            continue;
                        }
                        i++;
                    }
                    return Observable.just(result);
                });
    }
}
