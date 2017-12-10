package com.udacity.stockhawk.data;

import com.udacity.stockhawk.data.model.Stock;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by SANATA on 12/9/2017.
 */

public interface StockApi {

    @GET("{kode}.json")
    Single<Stock> groupList(@Path("kode") String kode,
                               @Query("column_index") int columnIndex,
                               @Query("start_date") String startDate,
                               @Query("end_date") String endDate);
}
