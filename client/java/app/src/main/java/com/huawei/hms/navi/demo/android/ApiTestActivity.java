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

package com.huawei.hms.navi.demo.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.huawei.hms.navi.demo.android.listener.DefaultMapNaviListener;
import com.huawei.hms.navi.demo.android.util.CommonUtil;
import com.huawei.hms.navi.demo.android.util.ToastUtil;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.MapNaviRoutingTip;
import com.huawei.hms.navi.navibase.enums.VehicleType;
import com.huawei.hms.navi.navibase.model.ClientParas;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;
import com.huawei.hms.navi.navibase.model.MapNaviPath;
import com.huawei.hms.navi.navibase.model.NaviRequestPoint;
import com.huawei.hms.navi.navibase.model.NaviStrategy;
import com.huawei.hms.navi.navibase.model.RoutingRequestParam;
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLatLng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.Nullable;

public class ApiTestActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "ApiTestActivity";

    private Button routingStartBtn;

    private Button naviStartBtn;

    private Button busBtn;

    private Spinner spStrategy;

    private Spinner spMode;

    private VehicleType mVehicleType = VehicleType.DRIVING;

    private MapNavi mapNavi;

    private RoutingRequestParam routingRequestParam;

    private NaviLatLng hwFrom;

    private NaviLatLng hwTo;

    private EditText mStartPoint;

    private EditText mEndPoint;

    private EditText mWayPoint1;

    private EditText mWayPoint2;

    private EditText keyValue;

    private EditText conversationId;

    private RadioButton dr1;

    private RadioButton dr2;

    private RadioButton dr3;

    private RadioButton dr4;

    private RadioGroup operationEntity;

    private ArrayList<NaviLatLng> mWayPoints = new ArrayList<>();

    private boolean saveTime = false;

    private boolean avoidFerry = false;

    private boolean avoidHighway = false;

    private boolean avoidToll = false;

    private boolean saveDistance = false;

    private boolean smartRecommend = false;

    private boolean priorityRoad = false;

    private boolean priorityHighway = false;

    private boolean saveMoney = false;

    private boolean avoidCongestion = false;

    private boolean isRouteCalculateSuccess = false;

    private MapNaviListener mapNaviListener = new DefaultMapNaviListener() {
        @Override
        public void onCalculateRouteFailure(int errCode) {
            ToastUtil.showToast(ApiTestActivity.this, "drive cal fail, error: " + errCode);
        }

        @Override
        public void onCalculateRouteSuccess(int[] routeIds, MapNaviRoutingTip mapNaviRoutingTip) {
            showResultForCalculate();
        }

        @Override
        public void onCalculateWalkRouteFailure(int errorCode) {
            ToastUtil.showToast(ApiTestActivity.this, "walk cal fail, error: " + errorCode);
        }

        @Override
        public void onCalculateWalkRouteSuccess(int[] routeIds, MapNaviRoutingTip mapNaviRoutingTip) {
            showResultForCalculate();
        }

        @Override
        public void onCalculateCycleRouteFailure(int errorCode) {
            ToastUtil.showToast(ApiTestActivity.this, "cycle cal fail, error: " + errorCode);
        }

        @Override
        public void onCalculateCycleRouteSuccess(int[] routeIds, MapNaviRoutingTip mapNaviRoutingTip) {
            showResultForCalculate();
        }

        @Override
        public void onStartNavi(int code) {
            ToastUtil.showToast(ApiTestActivity.this, "startNavi complete code is :" + code);
            if (code == 0) {
                Intent intent = new Intent(ApiTestActivity.this, NaviActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
        initView();
        initListener();
        initServerSite();
        initMapNavi();
    }

    private void initView() {
        routingStartBtn = findViewById(R.id.btn_routing);
        naviStartBtn = findViewById(R.id.btn_start_navi);
        busBtn = findViewById(R.id.btn_bus_page);
        mStartPoint = (EditText) findViewById(R.id.m_start_point);
        mEndPoint = (EditText) findViewById(R.id.m_end_point);
        mWayPoint1 = (EditText) findViewById(R.id.way_one_point);
        mWayPoint2 = (EditText) findViewById(R.id.way_two_point);
        keyValue = findViewById(R.id.user_apikey_var);
        conversationId = findViewById(R.id.conversation_id_var);
        spStrategy = findViewById(R.id.sp_navi_strategy);
        spMode = findViewById(R.id.sp_navi_mode);
        operationEntity = (RadioGroup) findViewById(R.id.operation_entity);
        dr1 = findViewById(R.id.dr1);
        dr2 = findViewById(R.id.dr2);
        dr3 = findViewById(R.id.dr3);
        dr4 = findViewById(R.id.dr4);
    }

    private void initListener() {
        operationEntity.setOnCheckedChangeListener(this);
        routingStartBtn.setOnClickListener(v -> {
            isRouteCalculateSuccess = false;
            // collecting input parameters
            getRequestParamForRouting();
            // route planning
            routing();
        });

        naviStartBtn.setOnClickListener(v -> {
            if (isRouteCalculateSuccess) {
                if (mVehicleType == VehicleType.DRIVING) {
                    mapNavi.calculateDriveGuide();
                }

                if (mVehicleType == VehicleType.WALKING) {
                    mapNavi.calculateWalkGuide();
                }

                if (mVehicleType == VehicleType.CYCLING) {
                    mapNavi.calculateCyclingGuide();
                }
            } else {
                ToastUtil.showToast(this, "please calculate route first");
            }
        });

        busBtn.setOnClickListener(v -> {
            if (mapNavi != null) {
                mapNavi.removeMapNaviListener(mapNaviListener);
            }

            Intent intent = new Intent(this, BusResultActivity.class);
            startActivity(intent);
        });

        spStrategy.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearNaviStrategy();
                switch (position) {
                    case 0:
                        smartRecommend = true;
                        break;
                    case 1:
                        saveTime = true;
                        break;
                    case 2:
                        avoidHighway = true;
                        break;
                    case 3:
                        avoidFerry = true;
                        break;
                    case 4:
                        avoidToll = true;
                        break;
                    case 5:
                        saveDistance = true;
                        break;
                    case 6:
                        avoidFerry = true;
                        avoidToll = true;
                        break;
                    case 7:
                        priorityRoad = true;
                        break;
                    case 8:
                        priorityHighway = true;
                        break;
                    case 9:
                        saveMoney = true;
                        break;
                    case 10:
                        avoidCongestion = true;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearNaviStrategy();
                smartRecommend = true;
            }
        });

        spMode.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mVehicleType = VehicleType.toVehicleType(0);
                        break;
                    case 1:
                        mVehicleType = VehicleType.toVehicleType(1);
                        break;
                    case 2:
                        mVehicleType = VehicleType.toVehicleType(2);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mVehicleType = VehicleType.toVehicleType(0);
            }
        });
    }

    private void initMapNavi() {
        mapNavi = MapNavi.getInstance(this);
        mapNavi.addMapNaviListener(mapNaviListener);
    }

    private void getRequestParamForRouting() {
        routingRequestParam = new RoutingRequestParam();
        String startStr = mStartPoint.getText().toString().trim();
        String endStr = mEndPoint.getText().toString().trim();
        List<String> wayPointStrList = new ArrayList<>();
        String wayPointStr1 = mWayPoint1.getText().toString().trim();
        String wayPointStr2 = mWayPoint2.getText().toString().trim();
        wayPointStrList.add(wayPointStr1);
        wayPointStrList.add(wayPointStr2);
        int i = latlngCheck(startStr, endStr, wayPointStrList);
        if (i != 0) {
            ToastUtil.showToast(this, "input point is invalids");
            return;
        }

        // Start and end points
        List<NaviRequestPoint> fromPoints = new ArrayList<>();
        NaviRequestPoint fromPnt = new NaviRequestPoint();
        fromPnt.setPoint(hwFrom);
        fromPoints.add(fromPnt);
        List<NaviRequestPoint> toPoints = new ArrayList<>();
        NaviRequestPoint toPnt = new NaviRequestPoint();
        toPnt.setPoint(hwTo);
        toPoints.add(toPnt);

        // Pass-through Point
        List<NaviRequestPoint> wayPoints = new ArrayList<>();
        if (mWayPoints != null) {
            for (NaviLatLng naviLatLng : mWayPoints) {
                NaviRequestPoint requestWayPoint = new NaviRequestPoint();
                requestWayPoint.setPoint(naviLatLng);
                wayPoints.add(requestWayPoint);
            }
        }

        // User preference setting
        NaviStrategy naviStrategy = new NaviStrategy();
        if (mVehicleType.equals(VehicleType.DRIVING)) {
            naviStrategy.setSaveTime(saveTime);
            naviStrategy.setAvoidFerry(avoidFerry);
            naviStrategy.setAvoidHighway(avoidHighway);
            naviStrategy.setAvoidToll(avoidToll);
            naviStrategy.setSaveDistance(saveDistance);
            naviStrategy.setSmartRecommend(smartRecommend);
            naviStrategy.setRoadPriority(priorityRoad);
            naviStrategy.setHighwayPriority(priorityHighway);
            naviStrategy.setSaveMoney(saveMoney);
            naviStrategy.setAvoidCrowd(avoidCongestion);
        } else {
            naviStrategy.setAvoidFerry(avoidFerry);
        }

        Log.i(TAG, "vehicleType: " + mVehicleType.name() + " navi strategy: ");

        routingRequestParam.setFromPoints(fromPoints);
        routingRequestParam.setToPoints(toPoints);
        routingRequestParam.setWayPoints(wayPoints);
        routingRequestParam.setStrategy(naviStrategy);
    }

    private void routing() {
        isRouteCalculateSuccess = false;
        if ("".equals(keyValue.getText().toString())) {
            ToastUtil.showToast(this, "please input apiKey");
            return;
        } else {
            setApiKey(keyValue.getText().toString().trim());
        }

        String clientId = conversationId.getText().toString();
        if (clientId.length() != 32) {
            Log.e(TAG, "conversationId must be 32 bit");
            ToastUtil.showToast(this, "conversationId must be 32 bit.");
            return;
        } else {
            ClientParas clientParas = new ClientParas();
            clientParas.setConversationId(clientId);
            MapNavi.initSettings(clientParas);
        }
        if (mapNavi != null) {
            mapNavi.setVehicleType(mVehicleType);
            switch (mVehicleType) {
                case DRIVING:
                    mapNavi.calculateDriveRoute(routingRequestParam);
                    break;
                case WALKING:
                    mapNavi.calculateWalkRoute(routingRequestParam);
                    break;
                case CYCLING:
                    mapNavi.calculateCycleRoute(routingRequestParam);
                    break;
                default:
                    break;
            }
        }
    }

    private void setApiKey(String apiKey) {
        if(apiKey == null) {
            return;
        }
        try {
            MapNavi.setApiKey(URLEncoder.encode(apiKey, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        }
    }

    private void initServerSite() {
        if (dr1.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
        }
        if (dr2.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR2);
        }
        if (dr3.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR3);
        }
        if (dr4.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR4);
        }
    }

    private int latlngCheck(String startStr, String endStr, List<String> wayPointStrList) {
        if (TextUtils.isEmpty(startStr)) {
            ToastUtil.showToast(this, "Enter the start point.");
            return 1;
        }
        if (TextUtils.isEmpty(endStr)) {
            ToastUtil.showToast(this, "Enter the end point.");
            return 1;
        }
        if (startStr.equals(endStr)) {
            ToastUtil.showToast(this, "The start point and end point cannot be the same.");
            return 2;
        }

        for (String wayPointStr : wayPointStrList) {
            if (wayPointStr.equals(startStr) || wayPointStr.equals(endStr)) {
                ToastUtil.showToast(this, "The way point cannot be the same as the start and end points.");
                return 3;
            }
        }

        try {
            // start point
            if (hwFrom != null) {
                hwFrom = null;
            }
            double startLat = Double.parseDouble(startStr.split(",")[0]);
            double startLng = Double.parseDouble(startStr.split(",")[1]);
            hwFrom = new NaviLatLng(startLat, startLng);

            // end point
            if (hwTo != null) {
                hwTo = null;
            }
            double toLat = Double.parseDouble(endStr.split(",")[0]);
            double toLng = Double.parseDouble(endStr.split(",")[1]);
            hwTo = new NaviLatLng(toLat, toLng);

            // pass-through point
            if (mWayPoints != null && mWayPoints.size() > 0) {
                mWayPoints.clear();
            }

            if (mWayPoints == null) {
                mWayPoints = new ArrayList<>();
            }

            for (String pointStr : wayPointStrList) {
                if (!TextUtils.isEmpty(pointStr)) {
                    double wayLat = Double.parseDouble(pointStr.split(",")[0]);
                    double wayLng = Double.parseDouble(pointStr.split(",")[1]);
                    NaviLatLng wayLatLng = new NaviLatLng(wayLat, wayLng);
                    mWayPoints.add(wayLatLng);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ToastUtil.showToast(this, "Enter the correct longitude and latitude.");
            return -1;
        }
        return 0;
    }

    private void clearNaviStrategy() {
        smartRecommend = false;
        saveTime = false;
        saveDistance = false;
        avoidFerry = false;
        avoidHighway = false;
        avoidToll = false;
        priorityRoad = false;
        priorityHighway = false;
        saveMoney = false;
        avoidCongestion = false;
    }

    private void showResultForCalculate() {
        isRouteCalculateSuccess = true;
        Map<Integer, MapNaviPath> naviPaths = mapNavi.getNaviPaths();
        String routeInfo = "";
        Iterator<Map.Entry<Integer, MapNaviPath>> iterator = naviPaths.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, MapNaviPath> entry = iterator.next();
            Integer routeId = entry.getKey();
            MapNaviPath naviPath = entry.getValue();
            routeInfo += " routeId: " + routeId + " routeInfo: " + "{ distance: " + naviPath.getAllLength()
                    + "m passTime: " + naviPath.getAllTime() + "s trafficNum: " + naviPath.getTrafficLightNum() + "}";
        }
        Log.i(TAG, "route size：" + naviPaths.size() + " routeInfo: " + routeInfo);
        ToastUtil.showToast(this, "route size：" + naviPaths.size() + " " + routeInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "============onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "============onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapNavi != null) {
            mapNavi.removeMapNaviListener(mapNaviListener);
            mapNavi.destroy();
        }
        Log.d(TAG, "============onDestroy");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        CommonUtil.changeServerSite(checkedId);
    }
}
