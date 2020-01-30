package com.moyck.ncov.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.moyck.ncov.R
import com.moyck.ncov.adapter.DatasAdapter
import com.moyck.ncov.domain.DataItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.moyck.ncov.api.ApiConstant
import com.moyck.ncov.api.ApiConstant.BAIDU
import com.moyck.ncov.api.ApiConstant.DX
import com.moyck.ncov.api.ApiConstant.DXY
import com.moyck.ncov.api.ApiConstant.PEPLE
import com.moyck.ncov.api.ApiConstant.TX
import com.moyck.ncov.api.ApiConstant.WEIBO1
import com.moyck.ncov.api.ApiConstant.WEIBO2
import com.moyck.ncov.api.ApiConstant.WEIBO3
import com.moyck.ncov.api.ApiConstant.ZHIHU3
import com.moyck.ncov.api.ApiConstant.ZHIHU4
import com.moyck.ncov.api.ApiConstant.ZHIHU5
import com.moyck.ncov.api.ApiConstant.ZHIHU6
import com.moyck.ncov.api.ApiConstant.ZHIHU7
import com.moyck.ncov.api.ApiConstant.ZHIHU_KOUZHAO
import com.moyck.ncov.api.ApiConstant.ZHIHU_KOUZHAO2
import com.moyck.ncov.api.ApiConstant.ZHIHU_KOUZHAO3
import com.moyck.ncov.domain.MessageEvent
import com.moyck.ncov.domain.UrlCallback
import com.moyck.ncov.util.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_clear.*
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.fragment_data.webview
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebSettings
import com.moyck.ncov.api.ApiConstant.BAIDU2
import org.greenrobot.eventbus.EventBus


class DataFragment : Fragment(), UrlCallback {


    lateinit var reData: RecyclerView
    lateinit var reMask: RecyclerView
    lateinit var reKnowledge: RecyclerView
    lateinit var reTreatment: RecyclerView
    lateinit var reTest: RecyclerView
    lateinit var reHelp: RecyclerView
    lateinit var webView: WebView

    val dataItems = ArrayList<DataItem>()
    val maskItems = ArrayList<DataItem>()
    val knowledgeItems = ArrayList<DataItem>()
    val treatmentItems = ArrayList<DataItem>()
    val testItems = ArrayList<DataItem>()
    val helpItems = ArrayList<DataItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data, container, false)
        reData = view.findViewById(R.id.re_data)
        reMask = view.findViewById(R.id.re_mask)
        reKnowledge = view.findViewById(R.id.re_knowledge)
        reTreatment = view.findViewById(R.id.re_treatment)
        reTest = view.findViewById(R.id.re_test)
        reHelp = view.findViewById(R.id.re_help)
        webView = view.findViewById(R.id.webview)

        initWebView()
        initData()
        webView.loadUrl("0")
        initRecycleView(dataItems,reData)
        initRecycleView(maskItems,reMask)
        initRecycleView(knowledgeItems,reKnowledge)
        initRecycleView(treatmentItems,reTreatment)
        initRecycleView(testItems,reTest)
        initRecycleView(helpItems,reHelp)
        return view
    }

    fun initWebView(){
        val setting = webView.settings
        setting.javaScriptEnabled = true
        setting.useWideViewPort = true; //将图片调整到适合webview的大小
        setting.loadWithOverviewMode = true; // 缩放至屏幕的大小
        setting.loadsImagesAutomatically = true; //支持自动加载图片
        setting.blockNetworkImage = false;//解决图片不显示
        setting.defaultTextEncodingName = "utf-8";//设置编码格式
        setting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url)
                    }
                    return true
                } catch (e: Exception) {
                    return false
                }
            }
        }
    }

    fun initData() {
        dataItems.add(DataItem("丁香园实时数据", R.drawable.dingxiang, DXY))
        dataItems.add(DataItem("腾讯实时数据", R.drawable.qq, TX))

        maskItems.add(DataItem("口罩正确佩戴", R.drawable.zhihu, ZHIHU_KOUZHAO))
        maskItems.add(DataItem("口罩正确佩戴", R.drawable.zhihu, ZHIHU_KOUZHAO2))
        maskItems.add(DataItem("口罩丢弃", R.drawable.zhihu, ZHIHU_KOUZHAO3))

        knowledgeItems.add(DataItem("丁香园新冠科普", R.drawable.dingxiang, DX))
        knowledgeItems.add(DataItem("百度新冠科普", R.drawable.baidu, BAIDU))

        treatmentItems.add(DataItem("新型肺炎第四版诊疗方案", R.drawable.baidu, BAIDU2))
        treatmentItems.add(DataItem("新型肺炎第三版诊疗方案", R.drawable.weibo, WEIBO2))
        treatmentItems.add(DataItem("各地救治医院", R.drawable.zhihu, ZHIHU4))
        treatmentItems.add(DataItem("新冠防治", R.drawable.zhihu, ZHIHU6))

        testItems.add(DataItem("确诊患者同行程查询", R.drawable.news, PEPLE))
        testItems.add(DataItem("新冠自我评估", R.drawable.weibo, WEIBO3))
        testItems.add(DataItem("新冠症状", R.drawable.zhihu, ZHIHU7))

        helpItems.add(DataItem("支援信息", R.drawable.weibo, WEIBO1))
        helpItems.add(DataItem("物资捐献", R.drawable.zhihu, ZHIHU5))
        helpItems.add(DataItem("春节延长安排", R.drawable.zhihu, ZHIHU3))
    }

    fun initRecycleView(datas: ArrayList<DataItem>,recyclerView :RecyclerView) {
//        val layoutManager = GridLayoutManager(activity, 4)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = DatasAdapter(datas,this)
    }

    override fun onClick(url: String) {
        webview.visibility = View.VISIBLE
        webView.loadUrl(url)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {//可见时
            webView.clearHistory()
            webView.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.code == 1) {
            if (webView.canGoBack()) {
                webview.goBack()
                if (webView.originalUrl == "")
                    webView.visibility = View.GONE
            }else{
                webView.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}