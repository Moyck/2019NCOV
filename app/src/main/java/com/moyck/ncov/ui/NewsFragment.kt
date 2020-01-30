package com.moyck.ncov.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moyck.ncov.R
import com.moyck.ncov.adapter.NewsAdapter
import com.moyck.ncov.api.ApiService
import com.moyck.ncov.api.JsonDayBefore24
import com.moyck.ncov.api.RetrofitManager
import com.moyck.ncov.domain.New
import com.moyck.ncov.domain.OutBreakInfo
import com.moyck.ncov.domain.ResponseData
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import java.lang.Exception
import java.text.SimpleDateFormat



class NewsFragment : Fragment() {

    val gson = Gson()
    lateinit var allNews: ArrayList<New>
    lateinit var listNews: RecyclerView
    val dateFormat = SimpleDateFormat("MM-dd HH:mm")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        listNews = view.findViewById(R.id.ls_news)
        initData()
        return view
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {//可见时
            initData()
        }
    }

    fun initData() {
        RetrofitManager.getInstance().get().create(ApiService::class.java).getNews()
            .subscribeOn(Schedulers.io())//IO线程加载数据
            .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
            .subscribe(object : Observer<ResponseData> {
                override fun onComplete() {
                    Log.e("initData", "onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    Log.e("initData", "onSubscribe")
                    Toast.makeText(activity, "正在刷新", Toast.LENGTH_LONG).show()
                }

                override fun onNext(t: ResponseData) {
                    allNews = gson.fromJson(
                        t.data,
                        object : TypeToken<ArrayList<New>>() {}.type
                    )
                    allNews.sortWith(object : Comparator<New> {

                        override fun compare(p0: New, p1: New): Int {
                            try {
                                var time0 = p0.time
                                var time1 = p1.time
                                if (time0.length <= 10)
                                    time0 = "0$time0"
                                if (time1.length <= 10)
                                    time1 = "0$time1"
                                if (dateFormat.parse(time0) < dateFormat.parse(time1))
                                    return -1
                                else
                                    return 1
                            } catch (e: Exception) {
                                return  0
                            }

                        }

                    })
                    val layoutManager = LinearLayoutManager(activity!!)
                    listNews.layoutManager = layoutManager
                    layoutManager.orientation = VERTICAL
                    listNews.adapter = NewsAdapter(allNews.reversed())
                    Log.e("initData", "onNext" + allNews.size)
                    Toast.makeText(activity, "刷新成功", Toast.LENGTH_LONG).show()
                }

                override fun onError(e: Throwable) {
                    Log.e("initData", "onNext")
                    Toast.makeText(activity, "网络错误", Toast.LENGTH_LONG).show()
                }
            })
    }


}