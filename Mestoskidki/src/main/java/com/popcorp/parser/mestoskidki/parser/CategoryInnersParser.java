package com.popcorp.parser.mestoskidki.parser;

import com.popcorp.parser.mestoskidki.entity.CategoryInner;
import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryInnersParser {

    public static Observable<ArrayList<CategoryInner>> loadCategoriesInner(int cityId) {
        return APIFactory.getAPI().getCategories(cityId)
                .flatMap(responseBody -> {
                    ArrayList<CategoryInner> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("Mestoskidki: Page with categoriesInner not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    int currentParentType = 1;
                    int currentParentId = 1;
                    Matcher categoryMatcher = Pattern.compile("href='view_cat\\.php\\?city=[0-9]+&cat[0-9]*=[0-9]+' class='left_links2'>(<span class=\"ar\">&gt;</span>)?[.[^<]]+</a>").matcher(page);
                    while (categoryMatcher.find()) {
                        int type;
                        int id;
                        String categoryResult = categoryMatcher.group();
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
                            ErrorManager.sendError("Mestoskidki: ParentType for categoryInner not finded! Category: " + categoryResult);
                            continue;
                        }
                        Matcher idMatcher = Pattern.compile("=[0-9]+'").matcher(categoryResult);
                        if (idMatcher.find()) {
                            String idResult = idMatcher.group();
                            id = Integer.valueOf(idResult.substring(1, idResult.length() - 1));
                        } else {
                            ErrorManager.sendError("Mestoskidki: ParentId for categoryInner not finded! Category: " + categoryResult);
                            continue;
                        }
                        if (id < 99) {
                            currentParentType = type;
                            currentParentId = id;
                        } else {
                            CategoryInner categoryInner = new CategoryInner(id, type, currentParentId, currentParentType);
                            if (!result.contains(categoryInner)) {
                                result.add(categoryInner);
                            }
                        }
                    }
                    return Observable.just(result);
                });
    }
}
