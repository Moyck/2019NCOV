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
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.SpannableStringBuilder
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.util.Log


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
            
            该APP只有网络权限，可以放心使用。因时间有限，个人能力有限，若发现该APP存在界面错乱，数据不正确，崩溃等BUG，欢迎通过邮箱联系，或者在我的个人博客下留言。未来得及做自动更新，因此，后面的更新信息我也会发到我的个人博客里。希望这次疫情尽快消灭，天佑中华！
            <br>
            <br>本人联系邮箱：super@moyck.com
            <br>BlankerL联系邮箱：me@isaaclin.cn
            <br>个人博客： <a href="http://www.moyck.com">http://www.moyck.com</a>
            <br>当前APP版本：""" + getVersionName()
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        tvContent = view.findViewById(R.id.tv_content)
        tvContent.text = getClickableHtml(string)
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        return view
    }

    /**
     * 格式化超链接文本内容并设置点击处理
     */
    private fun getClickableHtml(html: String): CharSequence {
        val spannedHtml = Html.fromHtml(html)
        val clickableHtmlBuilder = SpannableStringBuilder(spannedHtml)
        val urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length, URLSpan::class.java)
        for (span in urls) {
            setLinkClickable(clickableHtmlBuilder, span)
        }
        return clickableHtmlBuilder
    }

    /**
     * 设置点击超链接对应的处理内容
     */
    private fun setLinkClickable(clickableHtmlBuilder: SpannableStringBuilder, urlSpan: URLSpan) {
        val start = clickableHtmlBuilder.getSpanStart(urlSpan)
        val end = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val flags = clickableHtmlBuilder.getSpanFlags(urlSpan)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Log.e("onClickonClick","onClick" + urlSpan.url)
                val uri = Uri.parse(urlSpan.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags)
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