package com.aaron.group.smartmeal.retrofit.service;

import com.aaron.group.smartmeal.base.BaseBean;
import com.aaron.group.smartmeal.bean.BusniessBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 说明:

 */

public interface DiningTableService {

    /**
     *
     * @param params
     * @return
     */
    @GET("ucache/get")
    Call<BusniessBean> obtainDiningTables(@QueryMap Map<String, String> params);
    @GET("ucache/put")
    Call<BaseBean> commitTables(@QueryMap Map<String, String> params);
}
