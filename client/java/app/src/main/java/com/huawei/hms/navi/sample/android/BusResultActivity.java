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

package com.huawei.hms.navi.sample.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.VehicleType;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;
import com.huawei.hms.navi.navibase.model.bus.BusNaviPathBean;
import com.huawei.hms.navi.navibase.model.busnavirequest.BusCqlRequest;
import com.huawei.hms.navi.navibase.model.busnavirequest.Destination;
import com.huawei.hms.navi.navibase.model.busnavirequest.Origin;
import com.huawei.hms.navi.sample.android.listener.DefaultMapNaviListener;
import com.huawei.hms.navi.sample.android.setting.CommonSetting;
import com.huawei.hms.navi.sample.android.util.CommonUtil;
import com.huawei.hms.navi.sample.android.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BusResultActivity extends Activity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "BusResultActivity";

    private Button busPlan;

    private EditText busEnd;

    private EditText busStart;

    private EditText alternatives;

    private EditText returnArray;

    private EditText pedestrianMaxDistance;

    private EditText changes;

    private EditText pedestrianSpeed;

    private EditText keyValue;

    private RadioButton dr1;

    private RadioButton dr2;

    private RadioButton dr3;

    private RadioButton dr4;

    private RadioGroup operationEntity;

    private MapNavi mapNavi;

    private MapNaviListener mapNaviListener = new DefaultMapNaviListener() {
        @Override
        public void onCalcuBusDriveRouteSuccess(BusNaviPathBean busNaviPathBean) {
            ToastUtil.showToast(BusResultActivity.this, "bus routing success");
        }

        @Override
        public void onCalcuBusDriveRouteFailed(int i) {
            ToastUtil.showToast(BusResultActivity.this, "bus routing fail: " + i);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_plan_result);
        initNavi();
        initView();
        initBusSite();
    }

    private void initView() {
        busPlan = findViewById(R.id.btn_bus_routing);
        busPlan.setOnClickListener(this);

        busEnd = findViewById(R.id.bus_end_point);
        busStart = findViewById(R.id.bus_start_point);
        alternatives = findViewById(R.id.bus_alternatives);
        returnArray = findViewById(R.id.bus_return_array);
        pedestrianMaxDistance = findViewById(R.id.bus_pedestrianMaxDistance);
        changes = findViewById(R.id.bus_changes);
        pedestrianSpeed = findViewById(R.id.bus_pedestrianSpeed);
        keyValue = findViewById(R.id.user_apikey_var2);

        dr1 = findViewById(R.id.bus_dr1);
        dr2 = findViewById(R.id.bus_dr2);
        dr3 = findViewById(R.id.bus_dr3);
        dr4 = findViewById(R.id.bus_dr4);
        operationEntity = (RadioGroup) findViewById(R.id.bus_operation_entity);
        if (operationEntity != null) {
            operationEntity.setOnCheckedChangeListener(this);
        }
    }

    private void initNavi() {
        mapNavi = MapNavi.getInstance(this);
        mapNavi.addMapNaviListener(mapNaviListener);
    }

    private void initBusSite() {
        if (mapNavi == null) {
            return;
        }

        String busSite = CommonSetting.getServerSite();
        switch (busSite) {
            case DevServerSiteConstant.DR4:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                dr4.setChecked(true);
                break;
            case DevServerSiteConstant.DR3:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                dr3.setChecked(true);
                break;
            case DevServerSiteConstant.DR2:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                dr2.setChecked(true);
                break;
            default:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                dr1.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bus_routing) {
            if ("".equals(keyValue.getText().toString())) {
                ToastUtil.showToast(this, "please input apiKey");
                return;
            } else {
                setApiKey(keyValue.getText().toString().trim());
            }
            // begin point
            String latLngStr = busStart.getText().toString();
            String[] latLngArrayStart = latLngStr.split(",");

            // end point
            String latLngStr2 = busEnd.getText().toString();
            String[] latLngArrayEnd = latLngStr2.split(",");

            // Number of Alternative Routes
            int alternativesValue = Integer.parseInt(alternatives.getText().toString());

            // Maximum number of transfers
            int changeValue = Integer.parseInt(changes.getText().toString());

            // Returned information
            String returnStr = returnArray.getText().toString();
            String[] returnArrayValue = returnStr.split(",");

            // Walking distance
            int pedestrianMaxDistanceValue = Integer.parseInt(pedestrianMaxDistance.getText().toString());

            // Walking speed
            int pedestrianSpeedValue = Integer.parseInt(pedestrianSpeed.getText().toString());

            BusCqlRequest routePlan = new BusCqlRequest();
            Origin or = new Origin();
            if (1 < latLngArrayStart.length) {
                or.setLat(Double.parseDouble(latLngArrayStart[0]));
                or.setLng(Double.parseDouble(latLngArrayStart[1]));
            }

            Destination des = new Destination();
            if (1 < latLngArrayEnd.length) {
                des.setLat(Double.parseDouble(latLngArrayEnd[0]));
                des.setLng(Double.parseDouble(latLngArrayEnd[1]));
            }

            routePlan.setOrigin(or);
            routePlan.setDestination(des);
            routePlan.setReturnMode(returnArrayValue);
            routePlan.setAlternatives(alternativesValue);
            routePlan.setLanguage("en");
            routePlan.setUnits(0);
            routePlan.setChanges(changeValue);
            routePlan.setPedestrianSpeed(pedestrianSpeedValue);
            routePlan.setPedestrianMaxDistance(pedestrianMaxDistanceValue);
            if (mapNavi != null) {
                mapNavi.setVehicleType(VehicleType.BUS);
                mapNavi.calculateBusDriveRoute(routePlan);
            }
        }
    }

    private void setApiKey(String apiKey) {
        if(apiKey == null || mapNavi == null) {
            return;
        }
        try {
            mapNavi.setApiKey(URLEncoder.encode(apiKey, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        CommonUtil.changeServerSite(checkedId, mapNavi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapNavi != null) {
            mapNavi.removeMapNaviListener(mapNaviListener);
        }
    }
}
