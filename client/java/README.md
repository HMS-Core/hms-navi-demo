HMS Core Navi Kit Sample Code
===============================

![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

English | [中文](README_ZH.md)

## Contents

* [Introduction](#Introduction)
* [Preparations](#Preparations)
* [Environment Requirements](#Environment-Requirements)
* [Result](#Result)
* [Technical Support](#Technical-Support)
* [License](#License)

Introduction
------------

导航服务（Navi Kit）为您提供了一套导航开发调用的SDK，方便您轻松地在应用中集成导航相关的功能，全方位提升用户体验。

Navi Kit的主要包含如下5大功能：
- 更丰富的导航方式：支持驾车、步行、骑行的智能路线规划，轻松避开拥堵、限行和难走路段，导航中为您实时推荐更快、更准确、更安全的路线。
- 更细节的信息引导：全屏车道级引导和路口放大图，帮助您在复杂路口前提前感知变道，不错过每一个关键路口。
- 更及时的驾驶提醒：实时获取路段的限速值、违章摄像头情况、拥堵情况、道路突发事件，及时作出提醒，为您提供全方位护航。用户也可通过自主上报获得更加实时、精准的导航服务。
- 更精准的路网监测：实时监测平行的主辅路和重叠的桥上桥下道路场景。
- 更直观的3D道路图层：力求将道路及道路周边的建筑1：1还原，帮助您在驾驶过程中更加轻松舒适。

You can also use HMS Toolkit to quickly run the sample code. HMS Toolkit supports one-stop kit integration, and provides functions such as free app debugging on remote real devices. To learn more about HMS Toolkit, please refer to the [HMS Toolkit documentation](https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/getting-started-0000001077381096?ha_source=hms1).

Preparations
---------------

The sample code is built using Gradle to demonstrate how to use the Map SDK for Android.

First, download the sample code by cloning this repository or downloading the compressed package.

In Android Studio, click **Open an existing Android Studio project** and select the directory where the **map-sample** file is located.

You can use the **gradlew build** command to directly build the project.

Then, you need to create an app in AppGallery Connect, obtain the **agconnect-services.json** file, and add it to the project. You also need to generate a signing certificate fingerprint, add the signing certificate file to the project, and add related configurations to the **build.gradle** file. For details, please refer to [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-sdk-config-agc-0000001061560289?ha_source=hms1).

To learn more, refer to the following documents:

- [Development Guide]()
- [API Reference]()

Environment Requirements
-------

+ JDK 1.8及以上
+ 安装Android Studio 3.6.1及以上
+ minSdkVersion 19及以上
+ targetSdkVersion 31（推荐）
+ compileSdkVersion 31（推荐）
+ Gradle 5.6.4及以上（推荐）
+ Android Gradle插件 3.6.0及以上测试应用的设备：EMUI 5.0及以上的华为手机、平板或Android 7.0 ~ 12的非华为手机。

## Result

  <img src="navi.png" width = 30% height = 30%>

## Technical Support
You can visit the [Reddit community](https://www.reddit.com/r/HMSCore/) to obtain the latest information about HMS Core and communicate with other developers.

If you have any questions about the sample code, try the following:
- Visit [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Votes), submit your questions, and tag them with **huawei-mobile-services**. Huawei experts will answer your questions.
- Visit the HMS Core section in the [HUAWEI Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001?ha_source=hms1) and communicate with other developers.

If you encounter any issues when using the sample code, submit your [issues](https://github.com/HMS-Core/hms-navikit-demo-java/issues) or submit a [pull request](https://github.com/HMS-Core/hms-navikit-demo/pulls).

License
-------

The sample code is licensed under [Apache License 2.0](https://github.com/HMS-Core/hms-navikit-demo-java/blob/master/LICENSE).