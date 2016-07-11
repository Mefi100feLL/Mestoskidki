package com.popcorp.parser.mestoskidki.net;

import okhttp3.ResponseBody;
import retrofit2.http.*;
import rx.Observable;

public interface API {

    @GET("/pages/osx.php")
    Observable<ResponseBody> getCities();

    @GET("cat_shop.php/")
    Observable<ResponseBody> getShops(@Query("city") int cityId);

    @GET("/cat_sale.php")
    Observable<ResponseBody> getCategories(@Query("city") int cityId);

    @GET("/view_shop.php")
    Observable<ResponseBody> getSales(@Query("city") int cityId, @Query("shop") long shopId, @Query("page") int page);

    @GET("/view_sale.php")
    Observable<ResponseBody> getSale(@Query("city") int cityId, @Query("id") int saleId);

    @FormUrlEncoded
    @POST("/comment.php")
    Observable<ResponseBody> sendComment(@Field("author") String author,
                                         @Field("whom") String whom,
                                         @Field("text") String text,
                                         @Field("city") int city,
                                         @Field("id") int id,
                                         @Field("back") String back,
                                         @Field("check_id") String checkId,
                                         @Field("check") String check,
                                         @Field("sub_com") String subCom


    );
}
