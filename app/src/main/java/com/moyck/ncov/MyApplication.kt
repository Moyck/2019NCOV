package com.moyck.ncov

import android.app.Application
import com.moyck.ncov.util.CrashHandler

/**
 * 因为时间和个人精力的关系，代码写得稀烂，也没分层，没写框架，没有学习的必要～
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashHandler.Instance.init(this)
    }


}