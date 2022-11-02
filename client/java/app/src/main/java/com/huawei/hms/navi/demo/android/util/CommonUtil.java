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

package com.huawei.hms.navi.demo.android.util;

import com.huawei.hms.navi.demo.android.R;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;

public class CommonUtil {
    public static void changeBusServerSite(int checkedId) {
        switch (checkedId) {
            case R.id.bus_dr1:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
            case R.id.bus_dr2:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                break;
            case R.id.bus_dr3:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                break;
            case R.id.bus_dr4:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                break;
            default:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
        }
    }

    public static void changeServerSite(int checkedId) {
        switch (checkedId) {
            case R.id.dr1:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
            case R.id.dr2:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                break;
            case R.id.dr3:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                break;
            case R.id.dr4:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                break;
            default:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
        }
    }
}
