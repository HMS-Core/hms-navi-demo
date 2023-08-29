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

package com.huawei.hms.navi.sample.android.listener

import com.huawei.hms.navi.navibase.MapNaviListener
import com.huawei.hms.navi.navibase.enums.MapNaviRoutingTip
import com.huawei.hms.navi.navibase.enums.MapNaviSettingEnums
import com.huawei.hms.navi.navibase.model.*
import com.huawei.hms.navi.navibase.model.bus.BusNaviPathBean
import com.huawei.hms.navi.navibase.model.currenttimebusinfo.CurrentBusInfo
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLocation
import com.huawei.hms.navi.navibase.model.newenergy.ChargeStationInfo
import com.huawei.hms.navi.navibase.model.newenergy.ReachableBoundaryInfo
import com.huawei.hms.navi.navibase.model.searchalongroute.SearchAlongResult

/**
 * Default MapNaviListener inplements.
 */
open class DefaultMapNaviListener : MapNaviListener {
    override fun onArriveDestination(mapNaviStaticInfo: MapNaviStaticInfo) {}
    override fun onArrivedWayPoint(i: Int) {}
    override fun onCalculateRouteFailure(i: Int) {}
    override fun onCalculateRouteSuccess(ints: IntArray, mapNaviRoutingTip: MapNaviRoutingTip) {}
    override fun onCalBackupGuideSuccess(routeChangeInfo: RouteChangeInfo) {}
    override fun onCalBackupGuideFail() {}
    override fun onParallelSwitchFail() {}
    override fun onParallelSwitchSuccess(routeChangeInfo: RouteChangeInfo) {}
    override fun onCalculateWalkRouteFailure(i: Int) {}
    override fun onCalculateWalkRouteSuccess(ints: IntArray, mapNaviRoutingTip: MapNaviRoutingTip) {}
    override fun onCalculateCycleRouteFailure(i: Int) {}
    override fun onCalculateCycleRouteSuccess(ints: IntArray, mapNaviRoutingTip: MapNaviRoutingTip) {}
    override fun onGetNavigationText(naviBroadInfo: NaviBroadInfo) {}
    override fun onLocationChange(naviLocation: NaviLocation) {}
    override fun onNaviInfoUpdate(naviInfo: NaviInfo) {}
    override fun onLineLimitSpeedUpdate(speedInfo: SpeedInfo) {}
    override fun onServiceAreaUpdate(mapServiceAreaInfos: Array<MapServiceAreaInfo>) {}
    override fun onReCalculateRouteForYaw() {}
    override fun onDriveRoutesChanged() {}
    override fun onExpiredBackupRoute(list: List<Int>) {}
    override fun onStartNavi(i: Int) {}
    override fun onStartNaviSuccess() {}
    override fun onTrafficStatusUpdate() {}
    override fun onLaneInfoHide() {}
    override fun onLaneInfoShow(laneInfo: LaneInfo) {}
    override fun onCrossHide() {}
    override fun onCrossShow(intersectionNotice: IntersectionNotice) {}
    override fun onFullScreenGuideHide() {}
    override fun onFullScreenGuideShow(triggerNotice: TriggerNotice) {}
    override fun onModeCrossShow(mapModelCross: MapModelCross) {}
    override fun onModeCrossHide() {}
    override fun onTurnInfoUpdated(mapNaviTurnPoint: Array<out MapNaviTurnPoint>?) {}
    override fun onAutoZoomUpdate(zoomPoint: ZoomPoint) {}
    override fun onFurnitureInfoUpdate(furnitureInfos: Array<FurnitureInfo>) {}
    override fun onCalcuBusDriveRouteSuccess(busNaviPathBean: BusNaviPathBean) {}
    override fun onCalcuBusDriveRouteFailed(i: Int) {}
    override fun onGetRealTimeBusInfoSuccess(currentBusInfo: CurrentBusInfo) {}
    override fun onGetRealTimeBusInfoFailed(i: Int) {}
    override fun onJamBubbleInfo(jamBubble: JamBubble) {}
    override fun onGetMilestoneDisappear(i: Int) {}
    override fun onSendLocationSuccess() {}
    override fun onSendLocationFailed() {}
    override fun getVoiceByteSuccess(voiceResult: VoiceResult) {}
    override fun getVoiceByteFailed(voiceFailedResult: VoiceFailedResult) {}
    override fun getAccessTypeOfTTSSuccess(typeOfTTSInfo: TypeOfTTSInfo) {}
    override fun getAccessTypeOfTTSFailed(i: Int) {}
    override fun onGetFutureEtaSuccess(list: List<FutureEta>) {}
    override fun onGetFutureEtaFailed(i: Int) {}
    override fun onGetHistoryTrafficSuccess(list: List<HistoryTrafficInfo>) {}
    override fun onGetHistoryTrafficFailed(i: Int) {}
    override fun onIncidentUpdate(incident: Incident) {}
    override fun onEnterTunnel() {}
    override fun onLeaveTunnel() {}
    override fun onSpecialTurnPointHide(turnPointInfo: TurnPointInfo) {}
    override fun onBusTakeoff(i: Int) {}
    override fun onBroadcastModeChangeSuccess(i: Int) {}
    override fun onBroadcastModeChangeFail(i: Int) {}
    override fun onHighwayExitPointUpdate(highwayExitInfo: HighwayExitInfo) {}
    override fun onSetNaviSettingSuccess(map: Map<MapNaviSettingEnums, Any>) {}
    override fun onSetNaviSettingFail(map: Map<MapNaviSettingEnums, Any>) {}
    override fun onUpdateWaypointsSuccess(routeChangeInfo: RouteChangeInfo) {}
    override fun onUpdateWaypointsFail() {}
    override fun onUpdateRouteSuccess(routeChangeInfo: RouteChangeInfo) {}
    override fun onUpdateRouteFail() {}
    override fun onAuthenticationFail() {}
    override fun onRecommendBetterRoute(routeRecommendInfo: RouteRecommendInfo) {}
    override fun onGetReachableBoundaryFail(i: Int) {}
    override fun onGetReachableBoundarySuccess(reachableBoundaryInfo: ReachableBoundaryInfo) {}
    override fun onSearchAlongRouteFail(i: Int) {}
    override fun onSearchAlongRouteSuccess(searchAlongResult: SearchAlongResult?) {}
    override fun onBatchQueryFail(i: Int) {}
    override fun onBatchQuerySuccess(list: List<ChargeStationInfo?>?) {}
    override fun onLightInfoUpdate(p0: MutableList<TrafficLightInfo>?) {}
}