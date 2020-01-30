package com.moyck.ncov.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


class RetrofitManager {

    val domain = "https://lab.isaaclin.cn/nCoV/api/"

    var retrofit: Retrofit? = null

    companion object {
        private var instance: RetrofitManager? = null

        fun getInstance(): RetrofitManager {
            if (instance == null) {
                instance = RetrofitManager()
            }
            return instance!!
        }
    }


    fun get(): Retrofit {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(domain) //设置网络请求的Url地址
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson)) //设置数据解析器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            trustAllHosts()
        }
        return retrofit!!
    }


    fun getHttpClient() : OkHttpClient{
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return arrayOf()
            }
        }), SecureRandom())
        val client = OkHttpClient.Builder().sslSocketFactory(sc.socketFactory).hostnameVerifier { hostname, session -> true }
        return client.build()
    }

    fun trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

        })

        // Install the all-trusting trust manager
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, java.security.SecureRandom())
            HttpsURLConnection
                .setDefaultSSLSocketFactory(sc.getSocketFactory())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}