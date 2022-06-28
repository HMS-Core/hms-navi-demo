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

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.navi.demo.android.util.ToastUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.MapNaviRoutingTip;
import com.huawei.hms.navi.navibase.enums.NaviMode;
import com.huawei.hms.navi.navibase.model.FurnitureInfo;
import com.huawei.hms.navi.navibase.model.Incident;
import com.huawei.hms.navi.navibase.model.IntersectionNotice;
import com.huawei.hms.navi.navibase.model.JamBubble;
import com.huawei.hms.navi.navibase.model.MapModelCross;
import com.huawei.hms.navi.navibase.model.MapNaviStaticInfo;
import com.huawei.hms.navi.navibase.model.MapServiceAreaInfo;
import com.huawei.hms.navi.navibase.model.NaviInfo;
import com.huawei.hms.navi.navibase.model.RouteChangeInfo;
import com.huawei.hms.navi.navibase.model.SpeedInfo;
import com.huawei.hms.navi.navibase.model.TriggerNotice;
import com.huawei.hms.navi.navibase.model.TurnPointInfo;
import com.huawei.hms.navi.navibase.model.ZoomPoint;
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLocation;
import com.huawei.navi.navibase.model.NaviBroadInfo;
import com.huawei.navi.navibase.model.TypeOfTTSInfo;
import com.huawei.navi.navibase.model.voicerequest.VoiceFailedResult;
import com.huawei.navi.navibase.model.voicerequest.VoiceResult;

import java.util.List;

public class NaviActivity extends AppCompatActivity implements MapNaviListener, RadioGroup.OnCheckedChangeListener{
    private static final String TAG = "NaviActivity";

    private MapNavi mapNavi;

    private RadioButton internalLocation;

    private RadioButton externalLocation;

    private RadioGroup locationSetting;

    private Button startEmulatorNavi;

    private Button startGpsNavi;

    private Button stopNavi;

    private Button updateLocation;

    private TextView locationView;

    private TextView distanceView;

    private TextView timeView;

    private boolean isGpsNavigation = false;

    private FusedLocationProviderClient mFusedLocationExProviderClient;

    private LocationRequest mNavLocationRequest;

    private static final int LOCATION_REQUEST_INTERVAL = 1000;

    private LocationCallback mNavLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_test);
        initMapNavi();
        initView();
        initListener();
        initLocationType();
        initLocationPemission();
    }

    // You can visit this website to learn how to integrate LocationKit.
    // https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/location-develop-steps-0000001050746143
    private void initLocationPemission() {
        // Permissions required for dynamic application
        // Android SDK<=28
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk <= 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            // Android SDK>28
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    private void initListener() {
        locationSetting.setOnCheckedChangeListener(this);
        startEmulatorNavi.setOnClickListener(v -> {
            startEmulatorNavi();
        });

        startGpsNavi.setOnClickListener(v -> {
            startGpsNavi();
        });

        stopNavi.setOnClickListener(v -> {
            stopNavi();
        });

        updateLocation.setOnClickListener(v -> {
            updateExternalLocation();
        });
    }

    private void initView() {
        internalLocation = findViewById(R.id.internal_location);
        externalLocation = findViewById(R.id.external_location);
        locationSetting = findViewById(R.id.location_setting);
        startEmulatorNavi = findViewById(R.id.start_emulator_navi);
        startGpsNavi = findViewById(R.id.start_navi);
        stopNavi = findViewById(R.id.stop_navi);
        updateLocation = findViewById(R.id.update_location);
        locationView = findViewById(R.id.location_view);
        distanceView = findViewById(R.id.distance_view);
        timeView = findViewById(R.id.time_view);
    }

    private void initMapNavi() {
        mapNavi = MapNavi.getInstance(this);
        mapNavi.addMapNaviListener(this);
        mapNavi.setLocationContext(this);
    }

    private void initLocationType() {
        if (internalLocation.isChecked()) {
            mapNavi.setUseExtraLocationData(false);
        }
        if (externalLocation.isChecked()) {
            mapNavi.setUseExtraLocationData(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.external_location:
                mapNavi.setUseExtraLocationData(true);
                break;
            default:
                mapNavi.setUseExtraLocationData(false);
                break;
        }
    }

    private void startEmulatorNavi() {
        if (mapNavi != null) {
            if (externalLocation.isChecked()) {
                ToastUtil.showToast(this, "You can't use external locations to simulate navigation.");
            } else {
                boolean result = mapNavi.startNavi(NaviMode.EMULATOR);
                ToastUtil.showToast(this, "start emulator navi result: " + result);
            }
        }
    }

    private void startGpsNavi() {
        if (mapNavi != null) {
            boolean result = mapNavi.startNavi(NaviMode.GPS);
            ToastUtil.showToast(this, "start navi result: " + result);
            if (result) {
                isGpsNavigation = true;
            }
        }
    }

    private void stopNavi() {
        if (mapNavi != null) {
            mapNavi.stopNavi();
            mapNavi.removeMapNaviListener(this);
            clearForLocation();
            this.finish();
        }
    }

    // You can visit this website to learn how to integrate LocationKit.
    // https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/location-develop-steps-0000001050746143
    private void updateExternalLocation() {
        if (!isGpsNavigation) {
            ToastUtil.showToast(this, "Not in gps navigation");
            return;
        }

        ToastUtil.showToast(this, "start update external location");

        initLocationProviderClient();
        initNavLocationRequest();
        mNavLocationCallback = getLocationCallback();
        updateMyLocationInNav();
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    if (mapNavi != null) {
                        boolean isSuccess = mapNavi.updateExtraLocationData(location);
                        Log.i(TAG, "updated location: " + isSuccess);
                    }
                }
            }
        };
    }

    static class MyFailListener implements OnFailureListener {
        @Override
        public void onFailure(Exception exception) {
            Log.e(TAG, "update location fail :" + exception.getMessage());
        }
    }

    private void updateMyLocationInNav() {
        mFusedLocationExProviderClient
                .requestLocationUpdatesEx(mNavLocationRequest, mNavLocationCallback, Looper.getMainLooper())
                .addOnFailureListener(new MyFailListener());
    }

    private void initNavLocationRequest() {
        mNavLocationRequest = new LocationRequest();
        mNavLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mNavLocationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL);
        mNavLocationRequest.setPriority(LocationRequest.PRIORITY_HD_ACCURACY);
    }

    private void initLocationProviderClient() {
        if (mFusedLocationExProviderClient == null) {
            mFusedLocationExProviderClient =
                    LocationServices.getFusedLocationProviderClient(this);
        }
    }

    private void clearForLocation() {
        isGpsNavigation = false;
        if (mFusedLocationExProviderClient != null) {
            mFusedLocationExProviderClient.removeLocationUpdates(mNavLocationCallback);
        }
        mFusedLocationExProviderClient = null;
        mNavLocationRequest = null;
        mNavLocationCallback = null;
    }

    @Override
    public void onArriveDestination(MapNaviStaticInfo mapNaviStaticInfo) {
        ToastUtil.showToast(this, "You have reached your destination.");
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints, MapNaviRoutingTip mapNaviRoutingTip) {

    }

    @Override
    public void onCalBackupGuideSuccess(RouteChangeInfo routeChangeInfo) {

    }

    @Override
    public void onCalBackupGuideFail() {

    }

    @Override
    public void onCalculateWalkRouteFailure(int i) {

    }

    @Override
    public void onCalculateWalkRouteSuccess(int[] ints, MapNaviRoutingTip mapNaviRoutingTip) {

    }

    @Override
    public void onCalculateCycleRouteFailure(int i) {

    }

    @Override
    public void onCalculateCycleRouteSuccess(int[] ints, MapNaviRoutingTip mapNaviRoutingTip) {

    }

    @Override
    public void onGetNavigationText(NaviBroadInfo naviBroadInfo) {

    }

    @Override
    public void onLocationChange(NaviLocation naviLocation) {
        if (naviLocation != null && naviLocation.getCoord() != null) {
            locationView.setText(naviLocation.getCoord().toString());
        }
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        if (naviInfo != null) {
            distanceView.setText(naviInfo.getPathRetainDistance() + " m");
            timeView.setText(naviInfo.getPathRetainTime() + " s");
        }
    }

    @Override
    public void onLineLimitSpeedUpdate(SpeedInfo speedInfo) {

    }

    @Override
    public void onServiceAreaUpdate(MapServiceAreaInfo[] mapServiceAreaInfos) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onDriveRoutesChanged() {

    }

    @Override
    public void onExpiredBackupRoute(List<Integer> list) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onStartNaviSuccess() {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLaneInfoHide() {

    }

    @Override
    public void onLaneInfoShow(Bitmap bitmap) {

    }

    @Override
    public void onCrossHide() {

    }

    @Override
    public void onCrossShow(IntersectionNotice intersectionNotice) {

    }

    @Override
    public void onFullScreenGuideHide() {

    }

    @Override
    public void onFullScreenGuideShow(TriggerNotice triggerNotice) {

    }

    @Override
    public void onModeCrossShow(MapModelCross mapModelCross) {

    }

    @Override
    public void onModeCrossHide() {

    }

    @Override
    public void onNaviArrowUpdate(int i, int i1) {

    }

    @Override
    public void onAutoZoomUpdate(ZoomPoint zoomPoint) {

    }

    @Override
    public void onFurnitureInfoUpdate(FurnitureInfo[] furnitureInfos) {

    }

    @Override
    public void onJamBubbleInfo(JamBubble jamBubble) {

    }

    @Override
    public void onGetMilestoneDisappear(int i) {

    }

    @Override
    public void onSendLocationSuccess() {

    }

    @Override
    public void onSendLocationFailed() {

    }

    @Override
    public void getVoiceByteSuccess(VoiceResult voiceResult) {

    }

    @Override
    public void getVoiceByteFailed(VoiceFailedResult voiceFailedResult) {

    }

    @Override
    public void getAccessTypeOfTTSSuccess(TypeOfTTSInfo typeOfTTSInfo) {

    }

    @Override
    public void getAccessTypeOfTTSFailed(int i) {

    }

    @Override
    public void onIncidentUpdate(Incident incident) {

    }

    @Override
    public void onEnterTunnel() {

    }

    @Override
    public void onLeaveTunnel() {

    }

    @Override
    public void onSpecialTurnPointHide(TurnPointInfo turnPointInfo) {

    }

    @Override
    protected void onDestroy() {
        if (mapNavi != null) {
            mapNavi.stopNavi();
            mapNavi.removeMapNaviListener(this);
        }
        clearForLocation();
        super.onDestroy();
    }
}