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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hms.location.*
import com.huawei.hms.navi.sample.android.listener.DefaultMapNaviListener
import com.huawei.hms.navi.sample.android.util.ToastUtil.showToast
import com.huawei.hms.navi.navibase.MapNavi
import com.huawei.hms.navi.navibase.MapNaviListener
import com.huawei.hms.navi.navibase.enums.NaviMode
import com.huawei.hms.navi.navibase.model.NaviBroadInfo
import com.huawei.hms.navi.navibase.model.NaviInfo
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLocation

class NaviActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    companion object {
        private const val TAG = "NaviActivity"
        private const val LOCATION_REQUEST_INTERVAL = 1000
    }

    private var mapNavi: MapNavi? = null
    private var internalLocation: RadioButton? = null
    private var externalLocation: RadioButton? = null
    private var locationSetting: RadioGroup? = null
    private var startEmulatorNavi: Button? = null
    private var startGpsNavi: Button? = null
    private var stopNavi: Button? = null
    private var updateLocation: Button? = null
    private var locationView: TextView? = null
    private var distanceView: TextView? = null
    private var timeView: TextView? = null
    private var broadcastView: TextView? = null
    private var isGpsNavigation = false
    private var mFusedLocationExProviderClient: FusedLocationProviderClient? = null
    private var mNavLocationRequest: LocationRequest? = null
    private var mNavLocationCallback: LocationCallback? = null
    private val mMapNaviListener: MapNaviListener = object : DefaultMapNaviListener() {
        override fun onLocationChange(naviLocation: NaviLocation) {
            if (naviLocation != null && naviLocation.coord != null) {
                val text = naviLocation.coord.latitude.toString() + "," + naviLocation.coord.longitude
                locationView!!.text = text
            }
        }

        override fun onNaviInfoUpdate(naviInfo: NaviInfo) {
            if (naviInfo != null) {
                distanceView!!.text = naviInfo.pathRetainDistance.toString() + " m"
                timeView!!.text = naviInfo.pathRetainTime.toString() + " s"
            }
        }

        override fun onGetNavigationText(naviBroadInfo: NaviBroadInfo) {
            if (naviBroadInfo != null) {
                broadcastView!!.text = naviBroadInfo.broadString
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi_test)
        initMapNavi()
        initView()
        initListener()
        initLocationType()
        initLocationPermission()
    }

    // You can visit this website to learn how to integrate LocationKit.
    // https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/location-develop-steps-0000001050746143
    private fun initLocationPermission() {
        // Permissions required for dynamic application
        // Android SDK<=28
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk <= 28 Q")
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val strings = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        } else {
            // Android SDK>28
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                val strings = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION")
                ActivityCompat.requestPermissions(this, strings, 2)
            }
        }
    }

    private fun initListener() {
        locationSetting!!.setOnCheckedChangeListener(this)
        startEmulatorNavi!!.setOnClickListener { v: View? -> startEmulatorNavi() }
        startGpsNavi!!.setOnClickListener { v: View? -> startGpsNavi() }
        stopNavi!!.setOnClickListener { v: View? -> stopNavi() }
        updateLocation!!.setOnClickListener { v: View? -> updateExternalLocation() }
    }

    private fun initView() {
        internalLocation = findViewById(R.id.internal_location)
        externalLocation = findViewById(R.id.external_location)
        locationSetting = findViewById(R.id.location_setting)
        startEmulatorNavi = findViewById(R.id.start_emulator_navi)
        startGpsNavi = findViewById(R.id.start_navi)
        stopNavi = findViewById(R.id.stop_navi)
        updateLocation = findViewById(R.id.update_location)
        locationView = findViewById(R.id.location_view)
        distanceView = findViewById(R.id.distance_view)
        timeView = findViewById(R.id.time_view)
        broadcastView = findViewById(R.id.broadcast_view)
    }

    private fun initMapNavi() {
        mapNavi = MapNavi.getInstance(this)
        mapNavi!!.addMapNaviListener(mMapNaviListener)
        mapNavi!!.setLocationContext(this)
    }

    private fun initLocationType() {
        if (internalLocation!!.isChecked) {
            mapNavi!!.setUseExtraLocationData(false)
        }
        if (externalLocation!!.isChecked) {
            mapNavi!!.setUseExtraLocationData(true)
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.external_location -> mapNavi!!.setUseExtraLocationData(true)
            else -> mapNavi!!.setUseExtraLocationData(false)
        }
    }

    private fun startEmulatorNavi() {
        if (mapNavi != null) {
            if (externalLocation!!.isChecked) {
                showToast(this, "You can't use external locations to simulate navigation.")
            } else {
                val result = mapNavi!!.startNavi(NaviMode.EMULATOR)
                showToast(this, "start emulator navi result: $result")
            }
        }
    }

    private fun startGpsNavi() {
        if (mapNavi != null) {
            val result = mapNavi!!.startNavi(NaviMode.GPS)
            showToast(this, "start navi result: $result")
            if (result) {
                isGpsNavigation = true
            }
        }
    }

    private fun stopNavi() {
        if (mapNavi != null) {
            mapNavi!!.stopNavi()
            mapNavi!!.removeMapNaviListener(mMapNaviListener)
            clearForLocation()
            finish()
        }
    }

    // You can visit this website to learn how to integrate LocationKit.
    // https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/location-develop-steps-0000001050746143
    private fun updateExternalLocation() {
        if (!isGpsNavigation) {
            showToast(this, "Not in gps navigation")
            return
        }
        showToast(this, "start update external location")
        initLocationProviderClient()
        initNavLocationRequest()
        mNavLocationCallback = locationCallback
        updateMyLocationInNav()
    }

    private val locationCallback: LocationCallback
        private get() = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null && locationResult.lastLocation != null) {
                    val location = locationResult.lastLocation
                    if (mapNavi != null) {
                        val isSuccess = mapNavi!!.updateExtraLocationData(location, -1.0)
                        Log.i(TAG, "updated location: $isSuccess")
                    }
                }
            }
        }

    internal class MyFailListener : OnFailureListener {
        override fun onFailure(exception: Exception) {
            Log.e(TAG, "update location fail :" + exception.message)
        }
    }

    private fun updateMyLocationInNav() {
        mFusedLocationExProviderClient!!
                .requestLocationUpdatesEx(mNavLocationRequest, mNavLocationCallback, Looper.getMainLooper())
                .addOnFailureListener(MyFailListener())
    }

    private fun initNavLocationRequest() {
        mNavLocationRequest = LocationRequest()
        mNavLocationRequest!!.interval = LOCATION_REQUEST_INTERVAL.toLong()
        mNavLocationRequest!!.fastestInterval = LOCATION_REQUEST_INTERVAL.toLong()
        mNavLocationRequest!!.priority = LocationRequest.PRIORITY_HD_ACCURACY
    }

    private fun initLocationProviderClient() {
        if (mFusedLocationExProviderClient == null) {
            mFusedLocationExProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }
    }

    private fun clearForLocation() {
        isGpsNavigation = false
        if (mFusedLocationExProviderClient != null) {
            mFusedLocationExProviderClient!!.removeLocationUpdates(mNavLocationCallback)
        }
        mFusedLocationExProviderClient = null
        mNavLocationRequest = null
        mNavLocationCallback = null
    }

    override fun onDestroy() {
        if (mapNavi != null) {
            mapNavi!!.stopNavi()
            mapNavi!!.removeMapNaviListener(mMapNaviListener)
        }
        clearForLocation()
        super.onDestroy()
    }
}