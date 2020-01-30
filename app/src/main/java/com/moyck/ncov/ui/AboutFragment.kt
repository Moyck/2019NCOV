package com.moyck.ncov.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.moyck.ncov.R
import android.content.pm.PackageManager
import android.R.attr.versionName
import android.content.pm.PackageInfo




class AboutFragment: Fragment(){

    lateinit var  tvContent :TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val string = """
            本APP1月24前的疫情数据和新闻数据来自腾讯新闻，之后的数据来自
            <a href="https://github.com/BlankerL/DXY-2019-nCoV-Crawler">该Github 项目</a>
            ,并且已取得作者@BlankerL同意。<br>
            <br>
            因时间有限，个人能力有限，若发现该APP存在界面错乱，数据不正确，崩溃等BUG，欢迎通过邮箱联系，或者在我的个人博客下留言。未来得及做自动更新，因此，后面的更新信息我也会发到我的个人博客里。希望这次疫情尽快消灭，天佑中华！
            <br>
            <br>联系邮箱：super@moyck.com
            <br>个人博客： <a href="http://www.moyck.com">http://www.moyck.com</a>
            <br>当前APP版本：""" + getVersionName()
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        tvContent = view.findViewById(R.id.tv_content)
        tvContent.text = Html.fromHtml(string)
        return view
    }

    fun getVersionName(): String? {
        //获取包管理器
        val pm = activity!!.packageManager
        //获取包信息
        try {
            val packageInfo = pm.getPackageInfo(activity!!.packageName, 0)
            //返回版本号
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null

    }

}