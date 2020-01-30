package com.moyck.ncov.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moyck.ncov.api.ApiService
import com.moyck.ncov.api.RetrofitManager
import com.moyck.ncov.domain.OutBreakInfo
import com.moyck.ncov.domain.ResponseData
import com.rmondjone.locktableview.LockTableView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_record.view.*
import android.graphics.Color.DKGRAY
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.components.Legend
import android.graphics.Color.LTGRAY
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.charts.PieChart
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.moyck.ncov.R
import com.moyck.ncov.api.JsonDayBefore24
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import com.wxy.chinamapview.view.ChinaMapView
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RecordFragment : Fragment(), View.OnClickListener {

    val days = ArrayList<String>()
    var allDatas = ArrayList<OutBreakInfo>()
    var currentDatas = ArrayList<OutBreakInfo>()
    val tableTitle = ArrayList<String>()
    var tableData = ArrayList<ArrayList<String>>()
    var currentPogress = 0
    lateinit var bubbleTextview: TextView
    lateinit var lin_map: LinearLayout
    lateinit var lin_pie: LinearLayout
    lateinit var lin_line: LinearLayout
    lateinit var lin_slide: LinearLayout

    lateinit var table: LinearLayout

    lateinit var record_map: LinearLayout
    lateinit var map: ChinaMapView
    lateinit var seekbar: IndicatorSeekBar
    lateinit var pie_chart: PieChart
    lateinit var record_pie: RelativeLayout
    lateinit var record_bar: RelativeLayout

    lateinit var record_line: RelativeLayout
    lateinit var line_chart: LineChart
    lateinit var bar_chart: BarChart

    var confrim = 0
    var dead = 0
    var heal = 0
    var lastConfrim = 0L
    var lastDead = 0L
    var lastHeal = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        lin_map = view.findViewById(R.id.lin_map)
        lin_pie = view.findViewById(R.id.lin_pie)
        lin_line = view.findViewById(R.id.lin_line)
        lin_slide = view.findViewById(R.id.lin_slide)
        table = view.findViewById(R.id.table)
        record_map = view.findViewById(R.id.record_map)
        map = view.findViewById(R.id.map)
        seekbar = view.findViewById(R.id.seekbar)
        pie_chart = view.findViewById(R.id.pie_chart)
        record_pie = view.findViewById(R.id.record_pie)
        record_line = view.findViewById(R.id.record_line)
        line_chart = view.findViewById(R.id.line_chart)
        bar_chart = view.findViewById(R.id.bar_chart)
        record_bar = view.findViewById(R.id.record_bar)

        bubbleTextview = TextView(activity)
        bubbleTextview.setPadding(10, 10, 10, 10)
        bubbleTextview.setTextColor(Color.WHITE)
        initData()
        initUI()
        return view
    }

    fun initUI() {
        lin_map.setOnClickListener(this)
        lin_pie.setOnClickListener(this)
        lin_line.setOnClickListener(this)
        lin_slide.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.lin_map -> initMap()
            R.id.lin_pie -> initPieChart()
            R.id.lin_line -> initLineChart()
            R.id.lin_slide -> initBarChart()
        }
    }


    fun initBarChart() {
        hideRecord()
        record_bar.visibility = View.VISIBLE
        if (tableData.size == 0)
            return

        bar_chart.setBackgroundColor(Color.WHITE)
        //不显示图表网格
        bar_chart.setDrawGridBackground(false)
        //背景阴影
        bar_chart.setDrawBarShadow(false)
        bar_chart.isHighlightFullBarEnabled = false
        //显示边框
        bar_chart.setDrawBorders(true)
        //设置动画效果
        bar_chart.animateY(1000, Easing.Linear)
        bar_chart.animateX(1000, Easing.Linear)

        bar_chart.setTouchEnabled(true); // 设置是否可以触摸
        bar_chart.isDragEnabled = true;// 是否可以拖拽
        bar_chart.run { setScaleEnabled(true) };// 是否可以缩放
        bar_chart.isScaleXEnabled = true;// 是否可以X方向的缩放


        /***XY轴的设置***/
        //X轴设置显示位置在底部
        val xAxis = bar_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = tableData.size - 2.toFloat()
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)

        val leftAxis = bar_chart.axisLeft
        val rightAxis = bar_chart.axisRight
        //保证Y轴从0开始，不然会上移一点
        leftAxis.axisMinimum = 0f
        rightAxis.axisMinimum = 0f
        rightAxis.isEnabled = false

        val mMatrix = Matrix()
        mMatrix.postScale(1.5f, 1f)
        bar_chart.viewPortHandler.refresh(mMatrix, bar_chart, false);

        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if (value < tableData.size - 2 && value >= 0)
                    return tableData[value.toInt() + 2][0]
                else
                    return ""
            }

        }

        /***折线图例 标签 设置***/
        val legend = bar_chart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 11f
        //显示位置
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val confrimEntries = ArrayList<BarEntry>()
        val deadEntries = ArrayList<BarEntry>()
        val healEntries = ArrayList<BarEntry>()
        for (i in 2 until tableData.size) {
            var barEntry = BarEntry(i - 2.toFloat(), tableData[i][1].toFloat())
            confrimEntries.add(barEntry)
            barEntry = BarEntry(i - 2.toFloat(), tableData[i][2].toFloat())
            deadEntries.add(barEntry)
            barEntry = BarEntry(i - 2.toFloat(), tableData[i][3].toFloat())
            healEntries.add(barEntry)
        }
        // 每一个BarDataSet代表一类柱状图
        val confrimDataSet = BarDataSet(confrimEntries, "");
        initBarDataSet(confrimDataSet, Color.parseColor("#F44336"))
        val deadDataSet = BarDataSet(deadEntries, "");
        initBarDataSet(deadDataSet, Color.BLACK);
        val healDataSet = BarDataSet(healEntries, "");
        initBarDataSet(healDataSet, Color.parseColor("#009688"))

        val barAmount = tableData.size
        val groupSpace = 0.1f; //柱状图组之间的间距
        val barWidth = 0.3f
        val barSpace = 0.02f
        xAxis.labelCount = tableData.size - 2


        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(confrimDataSet)
        dataSets.add(deadDataSet)
        dataSets.add(healDataSet)
        val data = BarData(dataSets)
        data.barWidth = barWidth
        data.groupBars(0f, groupSpace, barSpace);
        bar_chart.data = data

        val m = Matrix();
        m.postScale(5f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
        bar_chart.viewPortHandler.refresh(m, bar_chart, false);//将图表动画显示之前进行缩放
        bar_chart.description.isEnabled = false
        bar_chart.setVisibleXRangeMaximum(10f)
        bar_chart.setVisibleXRangeMinimum(10f)
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {//可见时
            initData()
        }
    }

    fun initBarDataSet(barDataSet: BarDataSet, color: Int) {
        barDataSet.color = color
        barDataSet.formLineWidth = 1f
        barDataSet.formSize = 15f
        barDataSet.setDrawValues(false)
    }

    fun initData() {
        RetrofitManager.getInstance().get().create(ApiService::class.java).getAreaDatas()
            .subscribeOn(Schedulers.io())//IO线程加载数据
            .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
            .subscribe(object : Observer<ResponseData> {

                override fun onNext(t: ResponseData) {
                    if (!t.results.isEmpty()) {
                        val gson = Gson()
                        allDatas = gson.fromJson(
                            JsonDayBefore24.getJson(),
                            object : TypeToken<ArrayList<OutBreakInfo>>() {}.type
                        )
                        val data = t.results
                        for (t in data) {
                            val date = Date(t.updateTime.toLong())
                            val calendar = Calendar.getInstance()
                            calendar.time = date
                            t.updateTime =
                                (calendar.get(Calendar.MONTH) + 1).toString() + "." + calendar.get(
                                    Calendar.DAY_OF_MONTH
                                )
                        }
                        val recordHashMap = HashMap<String, ArrayList<String>>()
                        for (t in data.reversed()) {
                            if (recordHashMap[t.updateTime] != null && recordHashMap[t.updateTime]!!.contains(
                                    t.provinceShortName
                                )
                            ) {
                                data.remove(t)
                            } else if (recordHashMap[t.updateTime] == null) {
                                recordHashMap[t.updateTime] = arrayListOf(t.provinceShortName)
                            } else {
                                recordHashMap[t.updateTime]!!.add(t.provinceShortName)
                            }
                        }
                        allDatas.addAll(data)
                        for (data in allDatas.reversed()) {
                            if (data.confirmedCount == 0L) {
                                allDatas.remove(data)
                            }
                        }
                        allDatas.sortWith(Comparator { p0, p1 -> if (p1.updateTime.toFloat() > p0.updateTime.toFloat()) -1 else 1 })
                    }
                    Toast.makeText(activity, "刷新成功", Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    days.clear()
                    currentDatas.clear()

                    for (data in allDatas) {
                        if (!days.contains(data.updateTime)) {
                            days.add(data.updateTime)
                        }
                    }
                    currentDatas = allDatas.clone() as ArrayList<OutBreakInfo>

                    initTable()
                    initMap()
                    initSeekBar()
                }

                override fun onSubscribe(d: Disposable) {
                    Log.e("getInstance", "onSubscribe ")
                    Toast.makeText(activity, "正在刷新", Toast.LENGTH_LONG).show()
                }

                override fun onError(e: Throwable) {
                    Log.e("getInstance", e.toString())
                    Toast.makeText(activity, "网络错误", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun initTable() {
        kotlin.run {
            val areas = ArrayList<String>()
            confrim = 0
            heal = 0
            dead = 0
            tableTitle.clear()
            tableTitle.add("地区")
            tableTitle.add("确诊")
            tableTitle.add("死亡")
            tableTitle.add("治愈")
            tableData.clear()

            for (data in currentDatas.reversed()) {
                if (!areas.contains(data.provinceShortName) && data.country == "中国" && data.provinceShortName != "") {
                    val rawData = ArrayList<String>()
                    rawData.add(data.provinceShortName)
                    rawData.add(data.confirmedCount.toString())
                    rawData.add(data.deadCount.toString())
                    rawData.add(data.curedCount.toString())
                    tableData.add(rawData)
                    areas.add(data.provinceShortName)
                }
            }

            for (i in 0 until tableData.size) {
                confrim += tableData[i][1].toInt()
                dead += tableData[i][2].toInt()
                heal += tableData[i][3].toInt()
            }
            val rawData = ArrayList<String>()
            rawData.add("合计")
            rawData.add(confrim.toString())
            rawData.add(dead.toString())
            rawData.add(heal.toString())

            if (lastConfrim == 0L){
                lastConfrim = confrim.toLong()
                lastDead = dead.toLong()
                lastHeal = heal.toLong()
            }

            tableData.sortWith(Comparator { p0, p1 -> p1[1].toInt() - p0[1].toInt() })
            val temData = ArrayList<ArrayList<String>>()
            temData.add(tableTitle)
            temData.add(rawData)
            temData.addAll(tableData)
            tableData = temData


            val mLockTableView = LockTableView(activity, table, tableData)
            mLockTableView.setLockFristColumn(true) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMinColumnWidth(65)
                .setMinRowHeight(30)//行最大高度
                .setTextViewSize(16) //单元格字体大小
                .setCellPadding(3)//设置单元格内边距(dp)
                .setOnLoadingListener(null)
                .setFristRowBackGroudColor(R.color.blue)//表头背景色
                .setTableHeadTextColor(R.color.colorPrimary)
                .setTableContentTextColor(R.color.text_color)
                .show()
            mLockTableView.tableScrollView.setPullRefreshEnabled(false)
            mLockTableView.tableScrollView.setLoadingMoreEnabled(false)
        }
    }


    fun initMap() {
        hideRecord()
        record_map.visibility = View.VISIBLE

        val provinces = map.chinaMapModel.provinceslist
        provinces.forEach {
            it.color = ContextCompat.getColor(activity!!.applicationContext, R.color.safe)
        }
        currentDatas.forEach {
            for (province in provinces) {
                if (province.name.contains(it.provinceShortName)) {
                    var color: Int
                    when (it.confirmedCount) {
                        0L -> color =
                            ContextCompat.getColor(activity!!.applicationContext, R.color.safe)
                        in 1L..9L -> color =
                            ContextCompat.getColor(activity!!.applicationContext, R.color.light)
                        in 10L..50L -> color =
                            ContextCompat.getColor(activity!!.applicationContext, R.color.mid)
                        in 50L..100L -> color =
                            ContextCompat.getColor(activity!!.applicationContext, R.color.moderate)
                        in 50L..300L -> color =
                            ContextCompat.getColor(activity!!.applicationContext, R.color.grave)
                        else -> color = ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.serious
                        )
                    }
                    province.color = color
                    break
                }
            }
        }
        map.notifyDataChanged()

    }


    fun initSeekBar() {
        seekbar.max = days.size.toFloat() - 1
        seekbar.min = 0f
        bubbleTextview.text = days.last()
        seekbar.indicator.topContentView = bubbleTextview
        seekbar.customTickTexts(arrayOf(days[0], days.last()))
        seekbar.tickTextsColor(Color.BLACK)
        seekbar.setProgress(days.size.toFloat())
        seekbar.onSeekChangeListener = object : OnSeekChangeListener {

            override fun onSeeking(seekParams: SeekParams) {
                val progress = seekParams.progress
                if (currentPogress != progress) {
                    currentDatas.clear()
                    for (data in allDatas) {
                        if (days.indexOf(data.updateTime) <= progress) {
                            currentDatas.add(data)
                        }
                    }
                    initTable()
                    if (record_map.isVisible)
                        initMap()
                    else if (record_pie.isVisible)
                        initPieChart()
                    else if (record_bar.isVisible)
                        initBarChart()
                    currentPogress = progress
                }
                bubbleTextview.text = days[progress]
//                seekbar.indicator.contentView.findViewById<TextView>(R.id.isb_progress).text = days[progress]
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            }
        }
    }

    private fun hideRecord() {
        record_map.visibility = View.GONE
        record_pie.visibility = View.GONE
        record_line.visibility = View.GONE
        record_bar.visibility = View.GONE
    }

    private fun initPieChart() {
        hideRecord()
        record_pie.visibility = View.VISIBLE
        val pies = ArrayList<PieEntry>()
        val pieChart = pie_chart
        var pieNum = 0
        var pieconfrim = 0
        for (i in 0 until tableData.size) {
            if (i >= 2) {
                if (pieNum++ < 10) {
                    pies.add(
                        PieEntry(
                            tableData[i][1].toFloat() / confrim.toFloat(),
                            tableData[i][0]
                        )
                    )
                    pieconfrim += tableData[i][1].toInt()
                } else {
                    pies.add(PieEntry((confrim - pieconfrim).toFloat() / confrim.toFloat(), "其他"))
                    break
                }
            }
        }

        val dataSet = PieDataSet(pies, "")
        // 设置颜色list，让不同的块显示不同颜色，下面是我觉得不错的颜色集合，比较亮
        val colors = ArrayList<Int>()
        val MATERIAL_COLORS = intArrayOf(Color.rgb(200, 172, 255))
        for (c in MATERIAL_COLORS) {
            colors.add(c)
        }
        for (c in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c)
        }
        dataSet.colors = colors
        val pieData = PieData(dataSet)

        val description = Description()
        description.text = "确诊人数百分比"
        description.setEnabled(true)
        pieChart.description = description
        //设置半透明圆环的半径, 0为透明
        pieChart.transparentCircleRadius = 0f

        //设置初始旋转角度
        pieChart.rotationAngle = -15f

        //数据连接线距图形片内部边界的距离，为百分数
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 1f
        dataSet.valueLinePart2Length = 1f
        dataSet.valueTextSize = 5f
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        //设置连接线的颜色
        dataSet.valueLineColor = Color.LTGRAY
        // 设置饼块之间的间隔
        dataSet.sliceSpace = 1f
        dataSet.isHighlightEnabled = true
        // 不显示图例
        val legend = pieChart.legend
        legend.isEnabled = false

        // 和四周相隔一段距离,显示数据
        pieChart.setExtraOffsets(26f, 5f, 26f, 5f)

        // 设置pieChart图表是否可以手动旋转
        pieChart.isRotationEnabled = false
        // 设置piecahrt图表点击Item高亮是否可用
        pieChart.isHighlightPerTapEnabled = true
        // 设置pieChart图表展示动画效果，动画运行1.4秒结束
        pieChart.animateY(1200, EaseInOutQuad)
        //设置pieChart是否只显示饼图上百分比不显示文字
        pieData.setValueTextSize(10f)
        pieChart.setCenterTextColor(Color.DKGRAY)
        pieData.setValueTextColor(Color.DKGRAY)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.data = pieData
        // 更新 piechart 视图
        pieChart.postInvalidate()
    }

    /**
     * 初始化图表
     */
    fun initLineChart() {
        hideRecord()
        record_line.visibility = View.VISIBLE
        if (allDatas.size == 0)
            return

        line_chart.setDrawGridBackground(false)
        line_chart.setDrawBorders(true)
        line_chart.setDragEnabled(false)
        line_chart.setTouchEnabled(true)
        line_chart.animateY(2500)
        line_chart.animateX(1500)
        val description = Description()
        description.isEnabled = false
        line_chart.description = description

        /***XY轴的设置***/
        val xAxis = line_chart.xAxis
        val leftYAxis = line_chart.axisLeft
        val rightYaxis = line_chart.axisRight
        //X轴设置显示位置在底部
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
        xAxis.granularity = 1f
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.axisMinimum = 0f
        rightYaxis.axisMinimum = 0f

        /***折线图例 标签 设置***/
        val legend = line_chart.legend
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 12f
        //显示位置 左下方
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //是否绘制在图表里面
        legend.setDrawInside(false)

        val confrimEntries = ArrayList<Entry>()
        val deadEntries = ArrayList<Entry>()
        val healEntries = ArrayList<Entry>()
        val confrimLine = LinkedHashMap<String, Long>()
        val deadLine = LinkedHashMap<String, Long>()
        val healLine = LinkedHashMap<String, Long>()


        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return days[value.toInt()]
            }
        }

        for (i in 0 until allDatas.size) {
            if (allDatas[i].country == "中国") {
                val confrim = confrimLine[allDatas[i].updateTime]
                val dead = deadLine[allDatas[i].updateTime]
                val heal = healLine[allDatas[i].updateTime]
                if (confrim != null) {
                    confrimLine[allDatas[i].updateTime] = confrim + allDatas[i].confirmedCount
                } else {
                    confrimLine[allDatas[i].updateTime] = allDatas[i].confirmedCount
                }
                if (dead != null) {
                    deadLine[allDatas[i].updateTime] = dead + allDatas[i].deadCount
                } else {
                    deadLine[allDatas[i].updateTime] = allDatas[i].deadCount
                }
                if (heal != null) {
                    healLine[allDatas[i].updateTime] = heal + allDatas[i].curedCount
                } else {
                    healLine[allDatas[i].updateTime] = allDatas[i].curedCount
                }
            }
        }

        confrimLine[allDatas.last().updateTime] = lastConfrim
        deadLine[allDatas.last().updateTime] = lastDead
        healLine[allDatas.last().updateTime] = lastHeal

        confrimLine.forEach {
            confrimEntries.add(Entry(days.indexOf(it.key).toFloat(), it.value.toFloat()))
        }
        deadLine.forEach {
            deadEntries.add(Entry(days.indexOf(it.key).toFloat(), it.value.toFloat()))
        }
        healLine.forEach {
            healEntries.add(Entry(days.indexOf(it.key).toFloat(), it.value.toFloat()))
        }

        // 每一个LineDataSet代表一条线
        val confrimDataSet = LineDataSet(confrimEntries, "确诊人数")
        val deadDataSet = LineDataSet(deadEntries, "死亡人数")
        val healDataSet = LineDataSet(healEntries, "康复人数")
        initLineDataSet(confrimDataSet, Color.parseColor("#F44336"), null)
        initLineDataSet(deadDataSet, Color.BLACK, null)
        initLineDataSet(healDataSet, Color.parseColor("#009688"), null)
        val lineData = LineData(confrimDataSet, deadDataSet, healDataSet)
        line_chart.data = lineData
    }


    fun initLineDataSet(lineDataSet: LineDataSet, color: Int, mode: LineDataSet.Mode?) {
        lineDataSet.color = color
        lineDataSet.setCircleColor(color)
        lineDataSet.lineWidth = 1f
        lineDataSet.circleRadius = 3f
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.valueTextSize = 0f
        //设置折线图填充
        lineDataSet.setDrawFilled(false)
        lineDataSet.formLineWidth = 1f
        lineDataSet.formSize = 15f
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.mode = LineDataSet.Mode.LINEAR
        } else {
            lineDataSet.mode = mode
        }
    }

}