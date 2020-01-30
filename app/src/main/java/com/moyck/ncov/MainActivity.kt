package com.moyck.ncov

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.moyck.ncov.adapter.SlideBarAdapter
import com.moyck.ncov.domain.SlideItem
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import com.moyck.ncov.util.AppHelper
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.moyck.ncov.api.ApiConstant
import com.moyck.ncov.domain.MessageEvent
import com.moyck.ncov.ui.*
import org.greenrobot.eventbus.EventBus

/**
 * 因为时间和个人精力的关系，代码写得稀烂，也没分层，没写框架，没有学习的必要～
 */
class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    var recordFragment: RecordFragment? = null
    var newsFragment: NewsFragment? = null
    var dataFragment: DataFragment? = null
    var aboutFragment: AboutFragment? = null
    var clearFragment: WebFragment? = null
    private lateinit var fManager: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fManager = supportFragmentManager
        initUI()
    }


    fun initUI() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    or SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT
        }
        listview.setPadding(0, AppHelper.getStatusBarHeight(this), 0, 0)
        framelayout.setPadding(0, AppHelper.getStatusBarHeight(this), 0, 0)

        val list = ArrayList<SlideItem>()
        list.add(SlideItem("数据", R.drawable.record))
        list.add(SlideItem("新闻", R.drawable.news))
        list.add(SlideItem("辟谣", R.drawable.notice))
        list.add(SlideItem("资料", R.drawable.data))
        list.add(SlideItem("关于", R.drawable.about))
        val adapter = SlideBarAdapter(this, list)
        listview.adapter = adapter
        listview.onItemClickListener = this
        switchFrgment(0)
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        switchFrgment(p2)
    }

    private fun switchFrgment(index: Int) {
        val transaction = fManager.beginTransaction()
        hideFragments(transaction)
        when (index) {
            0 -> {
                if (recordFragment == null) {
                    recordFragment = RecordFragment()
                    transaction.add(R.id.framelayout, recordFragment!!)
                }
                transaction.show(recordFragment!!)
            }
            1 -> {
                if (newsFragment == null) {
                    newsFragment = NewsFragment()
                    transaction.add(R.id.framelayout, newsFragment!!)
                }
                transaction.show(newsFragment!!)
            }
            2 -> {
                if (clearFragment == null) {
                    clearFragment = WebFragment(ApiConstant.FACT)
                    transaction.add(R.id.framelayout, clearFragment!!)
                }
                transaction.show(clearFragment!!)
            }
            3 -> {
                if (dataFragment == null) {
                    dataFragment = DataFragment()
                    transaction.add(R.id.framelayout, dataFragment!!)
                }
                transaction.show(dataFragment!!)
            }
            4 -> {
                if (aboutFragment == null) {
                    aboutFragment = AboutFragment()
                    transaction.add(R.id.framelayout, aboutFragment!!)
                }
                transaction.show(aboutFragment!!)
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        if (recordFragment != null) {
            transaction.hide(recordFragment!!)
        }
        if (newsFragment != null) {
            transaction.hide(newsFragment!!)
        }
        if (dataFragment != null) {
            transaction.hide(dataFragment!!)
        }
        if (aboutFragment != null) {
            transaction.hide(aboutFragment!!)
        }
        if (clearFragment != null) {
            transaction.hide(clearFragment!!)
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(MessageEvent(1))
    }

}


