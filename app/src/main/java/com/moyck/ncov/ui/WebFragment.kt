package com.moyck.ncov.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.moyck.ncov.R
import com.moyck.ncov.domain.MessageEvent
import kotlinx.android.synthetic.main.fragment_clear.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe


class WebFragment(val domain :String) : Fragment() {

    lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clear, container, false)
        webView = view.findViewById(R.id.webview)
        initWebView()
        webView.loadUrl(domain)
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.code == 1) {
            if (webView.canGoBack()) {
                webview.goBack()
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