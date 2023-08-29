/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hms.navi.sample.android

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.navi.sample.android.listener.DefaultMapNaviListener
import com.huawei.hms.navi.sample.android.settings.CommonSetting
import com.huawei.hms.navi.sample.android.util.CommonUtil
import com.huawei.hms.navi.sample.android.util.ToastUtil.showToast
import com.huawei.hms.navi.navibase.MapNavi
import com.huawei.hms.navi.navibase.MapNaviListener
import com.huawei.hms.navi.navibase.enums.MapNaviRoutingTip
import com.huawei.hms.navi.navibase.enums.VehicleType
import com.huawei.hms.navi.navibase.model.*
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLatLng
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class ApiTestActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    companion object {
        private const val TAG = "ApiTestActivity"
    }

    private var routingStartBtn: Button? = null

    private var naviStartBtn: Button? = null

    private var busBtn: Button? = null

    private var spStrategy: Spinner? = null

    private var spMode: Spinner? = null

    private var mVehicleType = VehicleType.DRIVING

    private var mapNavi: MapNavi? = null

    private var routingRequestParam: RoutingRequestParam? = null

    private var hwFrom: NaviLatLng? = null

    private var hwTo: NaviLatLng? = null

    private var mStartPoint: EditText? = null

    private var mEndPoint: EditText? = null

    private var mWayPoint1: EditText? = null

    private var mWayPoint2: EditText? = null

    private var keyValue: EditText? = null

    private var dr1: RadioButton? = null

    private var dr2: RadioButton? = null

    private var dr3: RadioButton? = null

    private var dr4: RadioButton? = null

    private var operationEntity: RadioGroup? = null

    private var mWayPoints = ArrayList<NaviLatLng>()

    private var saveTime = false

    private var avoidFerry = false

    private var avoidHighway = false

    private var avoidToll = false

    private var saveDistance = false

    private var smartRecommend = false

    private var priorityRoad = false

    private var priorityHighway = false

    private var saveMoney = false

    private var avoidCongestion = false

    private var isRouteCalculateSuccess = false

    private val mapNaviListener: MapNaviListener = object : DefaultMapNaviListener() {
        override fun onCalculateRouteFailure(errCode: Int) {
            showToast(this@ApiTestActivity, "drive cal fail, error: $errCode")
        }

        override fun onCalculateRouteSuccess(ints: IntArray, mapNaviRoutingTip: MapNaviRoutingTip) {
            showResultForCalculate()
        }

        override fun onCalculateWalkRouteFailure(errorCode: Int) {
            showToast(this@ApiTestActivity, "walk cal fail, error: $errorCode")
        }

        override fun onCalculateWalkRouteSuccess(ints: IntArray, mapNaviRoutingTip: MapNaviRoutingTip) {
            showResultForCalculate()
        }

        override fun onCalculateCycleRouteFailure(errorCode: Int) {
            showToast(this@ApiTestActivity, "cycle cal fail, error: $errorCode")
        }

        override fun onCalculateCycleRouteSuccess(ints: IntArray, mapNaviRoutingTip: MapNaviRoutingTip) {
            showResultForCalculate()
        }

        override fun onStartNavi(code: Int) {
            showToast(this@ApiTestActivity, "startNavi complete code is :$code")
            if (code == 0) {
                isRouteCalculateSuccess = false
                val intent = Intent(this@ApiTestActivity, NaviActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_test)
        initView()
        initListener()
        initMapNavi()
        initApiTestSite()
    }

    private fun initView() {
        routingStartBtn = findViewById(R.id.btn_routing)
        naviStartBtn = findViewById(R.id.btn_start_navi)
        busBtn = findViewById(R.id.btn_bus_page)
        mStartPoint = findViewById<View>(R.id.m_start_point) as EditText
        mEndPoint = findViewById<View>(R.id.m_end_point) as EditText
        mWayPoint1 = findViewById<View>(R.id.way_one_point) as EditText
        mWayPoint2 = findViewById<View>(R.id.way_two_point) as EditText
        keyValue = findViewById(R.id.user_apikey_var)
        spStrategy = findViewById(R.id.sp_navi_strategy)
        spMode = findViewById(R.id.sp_navi_mode)
        operationEntity = findViewById<View>(R.id.operation_entity) as RadioGroup
        dr1 = findViewById(R.id.dr1)
        dr2 = findViewById(R.id.dr2)
        dr3 = findViewById(R.id.dr3)
        dr4 = findViewById(R.id.dr4)
    }

    private fun initListener() {
        operationEntity!!.setOnCheckedChangeListener(this)
        routingStartBtn!!.setOnClickListener { v: View? ->
            isRouteCalculateSuccess = false
            // collecting input parameters
            getRequestParamForRouting()
            // route planning
            routing()
        }
        naviStartBtn!!.setOnClickListener { v: View? ->
            if (isRouteCalculateSuccess) {
                if (mVehicleType == VehicleType.DRIVING) {
                    mapNavi!!.calculateDriveGuide()
                }
                if (mVehicleType == VehicleType.WALKING) {
                    mapNavi!!.calculateWalkGuide()
                }
                if (mVehicleType == VehicleType.CYCLING) {
                    mapNavi!!.calculateCyclingGuide()
                }
            } else {
                showToast(this, "please calculate route first")
            }
        }
        busBtn!!.setOnClickListener { v: View? ->
            val intent = Intent(this, BusResultActivity::class.java)
            startActivity(intent)
        }
        spStrategy!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                clearNaviStrategy()
                when (position) {
                    0 -> smartRecommend = true
                    1 -> saveTime = true
                    2 -> avoidHighway = true
                    3 -> avoidFerry = true
                    4 -> avoidToll = true
                    5 -> saveDistance = true
                    6 -> {
                        avoidFerry = true
                        avoidToll = true
                    }
                    7 -> priorityRoad = true
                    8 -> priorityHighway = true
                    9 -> saveMoney = true
                    10 -> avoidCongestion = true
                    else -> {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                clearNaviStrategy()
                smartRecommend = true
            }
        }
        spMode!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> mVehicleType = VehicleType.toVehicleType(0)
                    1 -> mVehicleType = VehicleType.toVehicleType(1)
                    2 -> mVehicleType = VehicleType.toVehicleType(2)
                    else -> {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mVehicleType = VehicleType.toVehicleType(0)
            }
        }
    }

    private fun initMapNavi() {
        mapNavi = MapNavi.getInstance(this)
        mapNavi!!.addMapNaviListener(mapNaviListener)
    }

    private fun initApiTestSite() {
        if (mapNavi == null) {
            return
        }
        val site: String? = CommonSetting.getServerSite()
        when (site) {
            DevServerSiteConstant.DR2 -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR2)
                dr2!!.isChecked = true
            }
            DevServerSiteConstant.DR3 -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR3)
                dr3!!.isChecked = true
            }
            DevServerSiteConstant.DR4 -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR4)
                dr4!!.isChecked = true
            }
            else -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR1)
                dr1!!.isChecked = true
            }
        }
    }

    private fun getRequestParamForRouting() {
        routingRequestParam = RoutingRequestParam()
        val startStr = mStartPoint!!.text.toString().trim { it <= ' ' }
        val endStr = mEndPoint!!.text.toString().trim { it <= ' ' }
        val wayPointStrList: MutableList<String> = java.util.ArrayList()
        val wayPointStr1 = mWayPoint1!!.text.toString().trim { it <= ' ' }
        val wayPointStr2 = mWayPoint2!!.text.toString().trim { it <= ' ' }
        wayPointStrList.add(wayPointStr1)
        wayPointStrList.add(wayPointStr2)
        val i = latlngCheck(startStr, endStr, wayPointStrList)
        if (i != 0) {
            showToast(this, "input point is invalids")
            return
        }

        // Start and end points
        val fromPoints: MutableList<NaviRequestPoint> = java.util.ArrayList()
        val fromPnt = NaviRequestPoint()
        fromPnt.point = hwFrom
        fromPoints.add(fromPnt)
        val toPoints: MutableList<NaviRequestPoint> = java.util.ArrayList()
        val toPnt = NaviRequestPoint()
        toPnt.point = hwTo
        toPoints.add(toPnt)

        // Pass-through Point
        val wayPoints: MutableList<NaviRequestPoint> = java.util.ArrayList()
        if (mWayPoints != null) {
            for (naviLatLng in mWayPoints) {
                val requestWayPoint = NaviRequestPoint()
                requestWayPoint.point = naviLatLng
                wayPoints.add(requestWayPoint)
            }
        }

        // User preference setting
        val naviStrategy = NaviStrategy()
        if (mVehicleType == VehicleType.DRIVING) {
            naviStrategy.isSaveTime = saveTime
            naviStrategy.isAvoidFerry = avoidFerry
            naviStrategy.isAvoidHighway = avoidHighway
            naviStrategy.isAvoidToll = avoidToll
            naviStrategy.isSaveDistance = saveDistance
            naviStrategy.isSmartRecommend = smartRecommend
            naviStrategy.isRoadPriority = priorityRoad
            naviStrategy.isHighwayPriority = priorityHighway
            naviStrategy.isSaveMoney = saveMoney
            naviStrategy.isAvoidCrowd = avoidCongestion
        } else {
            naviStrategy.isAvoidFerry = avoidFerry
        }
        Log.i(TAG, "vehicleType: " + mVehicleType.name + " navi strategy: ")
        routingRequestParam!!.setFromPoints(fromPoints)
        routingRequestParam!!.setToPoints(toPoints)
        routingRequestParam!!.setWayPoints(wayPoints)
        routingRequestParam!!.setStrategy(naviStrategy)
    }

    private fun routing() {
        isRouteCalculateSuccess = false
        if ("" == keyValue!!.text.toString()) {
            showToast(this, "please input apiKey")
            return
        } else {
            setApiKey(keyValue!!.text.toString().trim { it <= ' ' })
        }
        if (mapNavi != null) {
            mapNavi!!.vehicleType = mVehicleType
            when (mVehicleType) {
                VehicleType.DRIVING -> mapNavi!!.calculateDriveRoute(routingRequestParam)
                VehicleType.WALKING -> mapNavi!!.calculateWalkRoute(routingRequestParam)
                VehicleType.CYCLING -> mapNavi!!.calculateCycleRoute(routingRequestParam)
                else -> {}
            }
        }
    }

    private fun setApiKey(apiKey: String?) {
        if (apiKey == null || mapNavi == null) {
            return
        }
        try {
            mapNavi!!.setApiKey(URLEncoder.encode(apiKey, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Failed to set api Key: " + e.message)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Failed to set api Key: " + e.message)
        }
    }

    private fun latlngCheck(startStr: String, endStr: String, wayPointStrList: List<String>): Int {
        if (TextUtils.isEmpty(startStr)) {
            showToast(this, "Enter the start point.")
            return 1
        }
        if (TextUtils.isEmpty(endStr)) {
            showToast(this, "Enter the end point.")
            return 1
        }
        if (startStr == endStr) {
            showToast(this, "The start point and end point cannot be the same.")
            return 2
        }
        for (wayPointStr in wayPointStrList) {
            if (wayPointStr == startStr || wayPointStr == endStr) {
                showToast(this, "The way point cannot be the same as the start and end points.")
                return 3
            }
        }
        try {
            // start point
            if (hwFrom != null) {
                hwFrom = null
            }
            val startLat = startStr.split(",".toRegex()).toTypedArray()[0].toDouble()
            val startLng = startStr.split(",".toRegex()).toTypedArray()[1].toDouble()
            hwFrom = NaviLatLng(startLat, startLng)

            // end point
            if (hwTo != null) {
                hwTo = null
            }
            val toLat = endStr.split(",".toRegex()).toTypedArray()[0].toDouble()
            val toLng = endStr.split(",".toRegex()).toTypedArray()[1].toDouble()
            hwTo = NaviLatLng(toLat, toLng)

            // pass-through point
            if (mWayPoints != null && mWayPoints.size > 0) {
                mWayPoints.clear()
            }
            if (mWayPoints == null) {
                mWayPoints = java.util.ArrayList()
            }
            for (pointStr in wayPointStrList) {
                if (!TextUtils.isEmpty(pointStr)) {
                    val wayLat = pointStr.split(",".toRegex()).toTypedArray()[0].toDouble()
                    val wayLng = pointStr.split(",".toRegex()).toTypedArray()[1].toDouble()
                    val wayLatLng = NaviLatLng(wayLat, wayLng)
                    mWayPoints.add(wayLatLng)
                }
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            showToast(this, "Enter the correct longitude and latitude.")
            return -1
        }
        return 0
    }

    private fun clearNaviStrategy() {
        smartRecommend = false
        saveTime = false
        saveDistance = false
        avoidFerry = false
        avoidHighway = false
        avoidToll = false
        priorityRoad = false
        priorityHighway = false
        saveMoney = false
        avoidCongestion = false
    }

    private fun showResultForCalculate() {
        isRouteCalculateSuccess = true
        val naviPaths: Map<Int, MapNaviPath> = mapNavi!!.naviPaths
        var routeInfo = ""
        val iterator = naviPaths.entries.iterator()
        while (iterator.hasNext()) {
            val (routeId, naviPath) = iterator.next()
            routeInfo += (" routeId: " + routeId + " routeInfo: " + "{ distance: " + naviPath.allLength
                    + "m passTime: " + naviPath.allTime + "s trafficNum: " + naviPath.trafficLightNum + "}")
        }
        Log.i(TAG, "route size：" + naviPaths.size + " routeInfo: " + routeInfo)
        showToast(this, "route size：" + naviPaths.size + " " + routeInfo)
    }

    override fun onResume() {
        super.onResume()
        initApiTestSite()
        Log.d(TAG, "============onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "============onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mapNavi != null) {
            mapNavi!!.removeMapNaviListener(mapNaviListener)
            mapNavi!!.destroy()
        }
        Log.d(TAG, "============onDestroy")
    }

    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {
        CommonUtil.changeServerSite(checkedId, mapNavi)
    }
}