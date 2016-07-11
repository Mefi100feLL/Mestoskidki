package com.popcorp.parser.mestoskidki.parser;

import com.popcorp.parser.mestoskidki.entity.Category;
import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoriesParser {

    public static Observable<ArrayList<Category>> loadCategories(int cityId) {
        return APIFactory.getAPI().getCategories(cityId)
                .flatMap(responseBody -> {
                    ArrayList<Category> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("Mestoskidki: Page with categories not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    Matcher categoryMatcher = Pattern.compile("cat[0-9]*=[0-9]+'><img class='img_cat2' align='left' src='[.[^']]*'").matcher(page);
                    while (categoryMatcher.find()) {
                        String categoryResult = categoryMatcher.group();
                        int type;
                        int id;
                        String image;
                        Matcher typeMatcher = Pattern.compile("cat[0-9]*=").matcher(categoryResult);
                        if (typeMatcher.find()) {
                            String typeResult = typeMatcher.group();
                            String tmpType = typeResult.substring(3, typeResult.length() - 1);
                            if (tmpType.isEmpty()) {
                                type = 1;
                            } else {
                                type = Integer.valueOf(tmpType);
                            }
                        } else {
                            ErrorManager.sendError("Mestoskidki: Type for category not finded! Category: " + categoryResult);
                            continue;
                        }
                        Matcher idMatcher = Pattern.compile("=[0-9]+'").matcher(categoryResult);
                        if (idMatcher.find()) {
                            String idResult = idMatcher.group();
                            id = Integer.valueOf(idResult.substring(1, idResult.length() - 1));
                        } else {
                            ErrorManager.sendError("Mestoskidki: Id for category not finded! Category: " + categoryResult);
                            continue;
                        }
                        Matcher imageMatcher = Pattern.compile("src='[.[^']]*'").matcher(categoryResult);
                        if (imageMatcher.find()) {
                            String imageResult = imageMatcher.group();
                            image = imageResult.substring(5, imageResult.length() - 1);
                        } else {
                            ErrorManager.sendError("Mestoskidki: Image for category not finded! Category: " + categoryResult);
                            continue;
                        }
                        Category category = new Category(id, type, "", image);
                        if (!result.contains(category)) {
                            result.add(category);
                        }
                    }
                    int i = 0;
                    Matcher categoryNameMatcher = Pattern.compile("<p class='shop2'>[.[^<]]*</p>").matcher(page);
                    while (categoryNameMatcher.find()) {
                        String categoryNameResult = categoryNameMatcher.group();
                        Matcher nameMatcher = Pattern.compile(">[.[^<]]*<").matcher(categoryNameResult);
                        if (nameMatcher.find()) {
                            String nameResult = nameMatcher.group();
                            String name = nameResult.substring(1, nameResult.length() - 1);
                            result.get(i).setName(name);
                        } else {
                            ErrorManager.sendError("Mestoskidki: Name for category not finded! Category: " + result.get(i));
                            continue;
                        }
                        i++;
                    }
                    return Observable.just(result);
                });
    }
}
