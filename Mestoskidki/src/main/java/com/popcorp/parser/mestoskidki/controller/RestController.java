package com.popcorp.parser.mestoskidki.controller;

import com.popcorp.parser.mestoskidki.dto.UniversalDTO;
import com.popcorp.parser.mestoskidki.entity.*;
import com.popcorp.parser.mestoskidki.parser.SaleParser;
import com.popcorp.parser.mestoskidki.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rx.schedulers.Schedulers;

import java.util.Calendar;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    @Qualifier(City.REPOSITORY)
    private CityRepository cityRepository;

    @Autowired
    @Qualifier(Category.REPOSITORY)
    private CategoryRepository categoryRepository;

    @Autowired
    @Qualifier(CategoryInner.REPOSITORY)
    private CategoryInnerRepository categoryInnerRepository;

    @Autowired
    @Qualifier(Shop.REPOSITORY)
    private ShopsRepository shopRepository;

    @Autowired
    @Qualifier(Sale.REPOSITORY)
    private SaleRepository saleRepository;

    @Autowired
    @Qualifier(SaleComment.REPOSITORY)
    private SaleCommentRepository saleCommentRepository;


    @RequestMapping("/cities")
    public UniversalDTO<Iterable<City>> getCities() {
        UniversalDTO<Iterable<City>> result = new UniversalDTO<>(true, "Ошибка при поиске регионов", null);
        Iterable<City> cities = cityRepository.getAll();
        if (cities != null) {
            result = new UniversalDTO<>(false, "", cities);
        }
        return result;
    }

    @RequestMapping("/categories")
    public UniversalDTO<Iterable<Category>> getCategories() {
        UniversalDTO<Iterable<Category>> result = new UniversalDTO<>(true, "Ошибка при поиске категорий", null);
        Iterable<Category> categories = categoryRepository.getAll();
        if (categories != null) {
            result = new UniversalDTO<>(false, "", categories);
        }
        return result;
    }

    @RequestMapping("/shops")
    public UniversalDTO<Iterable<Shop>> getShops(@RequestParam(value = "city", defaultValue = "-1") int city) {
        if (city == -1) {
            return new UniversalDTO<>(true, "Не указан регион", null);
        }
        UniversalDTO<Iterable<Shop>> result = new UniversalDTO<>(true, "Ошибка при поиске магазинов", null);
        Iterable<Shop> shops = shopRepository.getForCity(city);
        if (shops != null) {
            result = new UniversalDTO<>(false, "", shops);
        }
        return result;
    }

    @RequestMapping("/sales")
    public UniversalDTO<Iterable<Sale>> getSales(
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "shops", defaultValue = "") String shops,
            @RequestParam(value = "categs", defaultValue = "") String categs,
            @RequestParam(value = "categs_types", defaultValue = "") String categsTypes) {
        if (city == -1 || shops.isEmpty() || categs.isEmpty() || categsTypes.isEmpty()) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        UniversalDTO<Iterable<Sale>> result = new UniversalDTO<>(true, "Ошибка при поиске акций", null);
        Iterable<Sale> sales = saleRepository.getForShopAndCategories(city, shops, categs, categsTypes);
        if (sales != null) {
            result = new UniversalDTO<>(false, "", sales);
        }
        return result;
    }

    @RequestMapping("/sale")
    public UniversalDTO<Sale> getSale(
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "id", defaultValue = "-1") int id) {
        if (city == -1 || id == -1) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        UniversalDTO<Sale> result;
        Sale sale = saleRepository.getWithId(city, id);
        if (sale == null) {
            sale = new SaleParser().getSale(city, id, categoryInnerRepository)
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn(throwable -> null)
                    .toBlocking()
                    .first();
        }
        if (sale == null) {
            result = new UniversalDTO<>(true, "Акция не найдена", null);
        } else {
            result = new UniversalDTO<>(false, "", sale);
        }
        return result;
    }

    @RequestMapping("/comments")
    public UniversalDTO<Iterable<SaleComment>> getComments(
            @RequestParam(value = "sale", defaultValue = "-1") int saleId,
            @RequestParam(value = "city", defaultValue = "-1") int cityId) {
        if (saleId == -1 || cityId == -1) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        UniversalDTO<Iterable<SaleComment>> result = new UniversalDTO<>(true, "Ошибка при поиске комментариев", null);
        Iterable<SaleComment> comments = saleCommentRepository.getForSaleId(saleId);
        int timeZone = cityRepository.getWithId(cityId).getTimeZone();
        if (comments != null) {
            for (SaleComment comment : comments) {
                Calendar dateTime = Calendar.getInstance();
                dateTime.setTimeInMillis(comment.getDateTime());
                dateTime.add(Calendar.HOUR_OF_DAY, timeZone);
                comment.setDateTime(dateTime.getTimeInMillis());
            }
            result = new UniversalDTO<>(false, "", comments);
        }
        return result;
    }

    @RequestMapping("/comments/new")
    public UniversalDTO<SaleComment> sendComment(
            @RequestParam(value = "author", defaultValue = "") String author,
            @RequestParam(value = "whom", defaultValue = "") String whom,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "id", defaultValue = "-1") int id) {
        if (city == -1 || id == -1 || author.isEmpty() || text.isEmpty()) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        Calendar currentDate = Calendar.getInstance();
        SaleComment saleComment = new SaleComment(id, author, whom, text, currentDate.getTimeInMillis());
        saleCommentRepository.save(saleComment);

        currentDate.add(Calendar.HOUR_OF_DAY, cityRepository.getWithId(city).getTimeZone());
        saleComment.setDateTime(currentDate.getTimeInMillis());

        return new UniversalDTO<>(false, "", saleComment);
        /*return APIFactory.getAPI().sendComment(author, whom, text, city, id, "", "4", "8", "Комментировать")
                .map(responseBody -> {
                    String result;
                    try {
                        result = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new CommentResult(false, e.getMessage(), "", "", 0);
                    }
                    if (result.contains("Refresh")) {
                        saleCommentRepository.save(saleComment);
                        return new CommentResult(true, "", date, time, dateTime);
                    }
                    return new CommentResult(false, "Неизвестная ошибка", "", "", 0);
                })
                .onErrorReturn(throwable -> new CommentResult(false, throwable.getMessage(), "", "", 0))
                .toBlocking()
                .first();*/
    }
}
