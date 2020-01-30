package com.moyck.ncov.api

import com.moyck.ncov.domain.ResponseData
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {


    @GET("area?latest=0")
    fun getAreaDatas(): Observable<ResponseData>

    @GET("https://view.inews.qq.com/g2/getOnsInfo?name=wuwei_ww_time_line")
    fun getNews(): Observable<ResponseData>

}