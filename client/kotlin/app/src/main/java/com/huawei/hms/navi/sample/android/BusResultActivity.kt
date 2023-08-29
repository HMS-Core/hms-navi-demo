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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.navi.sample.android.listener.DefaultMapNaviListener
import com.huawei.hms.navi.sample.android.settings.CommonSetting
import com.huawei.hms.navi.sample.android.util.CommonUtil.changeServerSite
import com.huawei.hms.navi.sample.android.util.ToastUtil.showToast
import com.huawei.hms.navi.navibase.MapNavi
import com.huawei.hms.navi.navibase.MapNaviListener
import com.huawei.hms.navi.navibase.enums.VehicleType
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant
import com.huawei.hms.navi.navibase.model.bus.BusNaviPathBean
import com.huawei.hms.navi.navibase.model.busnavirequest.BusCqlRequest
import com.huawei.hms.navi.navibase.model.busnavirequest.Destination
import com.huawei.hms.navi.navibase.model.busnavirequest.Origin
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class BusResultActivity : AppCompatActivity(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    companion object {
        private const val TAG = "BusResultActivity"
    }

    private var busPlan: Button? = null

    private var busEnd: EditText? = null

    private var busStart: EditText? = null

    private var alternatives: EditText? = null

    private var returnArray: EditText? = null

    private var pedestrianMaxDistance: EditText? = null

    private var changes: EditText? = null

    private var pedestrianSpeed: EditText? = null

    private var keyValue: EditText? = null

    private var dr1: RadioButton? = null

    private var dr2: RadioButton? = null

    private var dr3: RadioButton? = null

    private var dr4: RadioButton? = null

    private var operationEntity: RadioGroup? = null

    private var mapNavi: MapNavi? = null

    private val mapNaviListener: MapNaviListener = object : DefaultMapNaviListener() {
        override fun onCalcuBusDriveRouteSuccess(busNaviPathBean: BusNaviPathBean) {
            showToast(this@BusResultActivity, "bus routing success")
        }

        override fun onCalcuBusDriveRouteFailed(i: Int) {
            showToast(this@BusResultActivity, "bus routing fail: $i")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_plan_result)
        initNavi()
        initView()
        initBusSite()
    }

    private fun initView() {
        busPlan = findViewById(R.id.btn_bus_routing)
        if (busPlan != null) {
            busPlan!!.setOnClickListener(this)
        }
        busEnd = findViewById(R.id.bus_end_point)
        busStart = findViewById(R.id.bus_start_point)
        alternatives = findViewById(R.id.bus_alternatives)
        returnArray = findViewById(R.id.bus_return_array)
        pedestrianMaxDistance = findViewById(R.id.bus_pedestrianMaxDistance)
        changes = findViewById(R.id.bus_changes)
        pedestrianSpeed = findViewById(R.id.bus_pedestrianSpeed)
        keyValue = findViewById(R.id.user_apikey_var2)
        dr1 = findViewById(R.id.bus_dr1)
        dr2 = findViewById(R.id.bus_dr2)
        dr3 = findViewById(R.id.bus_dr3)
        dr4 = findViewById(R.id.bus_dr4)
        operationEntity = findViewById<View>(R.id.bus_operation_entity) as RadioGroup
        if (operationEntity != null) {
            operationEntity!!.setOnCheckedChangeListener(this)
        }
    }

    private fun initNavi() {
        mapNavi = MapNavi.getInstance(this)
        if (mapNavi != null) {
            mapNavi!!.addMapNaviListener(mapNaviListener)
        }
    }

    private fun initBusSite() {
        if (mapNavi == null) {
            return
        }
        val busSite: String? = CommonSetting.getServerSite()
        when (busSite) {
            DevServerSiteConstant.DR4 -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR4)
                dr4!!.isChecked = true
            }
            DevServerSiteConstant.DR3 -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR3)
                dr3!!.isChecked = true
            }
            DevServerSiteConstant.DR2 -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR2)
                dr2!!.isChecked = true
            }
            else -> {
                mapNavi!!.setDevServerSite(DevServerSiteConstant.DR1)
                dr1!!.isChecked = true
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_bus_routing) {
            if ("" == keyValue!!.text.toString()) {
                showToast(this, "please input apiKey")
                return
            } else {
                setApiKey(keyValue!!.text.toString().trim { it <= ' ' })
            }
            // begin point
            val latLngStr = busStart!!.text.toString()
            val latLngArrayStart = latLngStr.split(",".toRegex()).toTypedArray()

            // end point
            val latLngStr2 = busEnd!!.text.toString()
            val latLngArrayEnd = latLngStr2.split(",".toRegex()).toTypedArray()

            // Number of Alternative Routes
            val alternativesValue = alternatives!!.text.toString().toInt()

            // Maximum number of transfers
            val changeValue = changes!!.text.toString().toInt()

            // Returned information
            val returnStr = returnArray!!.text.toString()
            val returnArrayValue = returnStr.split(",".toRegex()).toTypedArray()

            // Walking distance
            val pedestrianMaxDistanceValue = pedestrianMaxDistance!!.text.toString().toInt()

            // Walking speed
            val pedestrianSpeedValue = pedestrianSpeed!!.text.toString().toInt()
            val routePlan = BusCqlRequest()
            val or = Origin()
            if (1 < latLngArrayStart.size) {
                or.lat = latLngArrayStart[0].toDouble()
                or.lng = latLngArrayStart[1].toDouble()
            }
            val des = Destination()
            if (1 < latLngArrayEnd.size) {
                des.lat = latLngArrayEnd[0].toDouble()
                des.lng = latLngArrayEnd[1].toDouble()
            }
            routePlan.origin = or
            routePlan.destination = des
            routePlan.returnMode = returnArrayValue
            routePlan.alternatives = alternativesValue
            routePlan.language = "en"
            routePlan.units = 0
            routePlan.changes = changeValue
            routePlan.pedestrianSpeed = pedestrianSpeedValue.toDouble()
            routePlan.pedestrianMaxDistance = pedestrianMaxDistanceValue
            if (mapNavi != null) {
                mapNavi!!.vehicleType = VehicleType.BUS
                mapNavi!!.calculateBusDriveRoute(routePlan)
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

    override fun onCheckedChanged(radioGroup: RadioGroup?, checkedId: Int) {
        changeServerSite(checkedId, mapNavi)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mapNavi != null) {
            mapNavi!!.removeMapNaviListener(mapNaviListener)
        }
    }
}