package com.popcorp.parser.mestoskidki.controller;

import com.popcorp.parser.mestoskidki.entity.*;
import com.popcorp.parser.mestoskidki.net.API;
import com.popcorp.parser.mestoskidki.parser.SaleParser;
import com.popcorp.parser.mestoskidki.repository.*;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    public Iterable<City> getCities() {
        return cityRepository.getAll();
    }

    @RequestMapping("/categories")
    public Iterable<Category> getCategories() {
        return categoryRepository.getAll();
    }

    @RequestMapping("/shops")
    public Iterable<Shop> getShops(@RequestParam(value = "city", defaultValue = "1") int city) {
        return shopRepository.getForCity(city);
    }

    @RequestMapping("/sales")
    public Iterable<Sale> getSales(
            @RequestParam(value = "city", defaultValue = "1") int city,
            @RequestParam(value = "shops", defaultValue = "") String shops,
            @RequestParam(value = "categs", defaultValue = "") String categs,
            @RequestParam(value = "categs_types", defaultValue = "") String categsTypes) {
        return saleRepository.getForShopAndCategories(city, shops, categs, categsTypes);
    }

    @RequestMapping("/sale")
    public Sale getSale(
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "id", defaultValue = "-1") int id) {
        Sale result = null;
        if (id != -1 && city != -1) {
            result = saleRepository.getWithId(city, id);
            if (result == null) {
                return new SaleParser().getSale(city, id, categoryInnerRepository).subscribeOn(Schedulers.newThread()).toBlocking().first();
            }
        }
        return result;
    }

    @RequestMapping("/comments/new")
    public Result sendComment(
            @RequestParam(value = "author", defaultValue = "") String author,
            @RequestParam(value = "whom", defaultValue = "") String whom,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "id", defaultValue = "-1") int id) {
        if (city == -1 || id == -1 || author.isEmpty() || text.isEmpty()) {
            return new CommentResult(false, "Empty field", "", "", 0);
        }
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("ru"));
        String date = dateFormat.format(currentDate.getTime());
        String time = timeFormat.format(currentDate.getTime());
        long dateTime = currentDate.getTimeInMillis();
        SaleComment saleComment = new SaleComment(id, author, whom, date, time, text, dateTime);
        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("http://mestoskidki.ru/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        API api = retrofit.create(API.class);
        return api.sendComment(author, whom, text, city, id, "", "4", "8", "Комментировать")
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
                    return new CommentResult(false, "Any error", "", "", 0);
                }).toBlocking().first();
    }
}
