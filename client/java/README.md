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

Navi Kit provides an SDK for navigation development, empowering you to integrate navigation functions into your app with ease to improve user experience.

Navi Kit mainly provides the following functions:
- Extensive navigation modes: Navi Kit can intelligently plan routes for driving, walking, and cycling and avoid congested, restricted, and rough roads. It can also recommend the faster, safer, and more accurate routes in real time.
- Detailed guidance: Navi Kit can provide full-screen lane-level guidance and enlarged intersection views to help users find the desired lane at complex intersections, preventing them from making a wrong turn.
- Timely driving reminder: Navi Kit can obtain road information such as the speed limit, traffic cameras, traffic conditions, and unexpected incidents in real time and remind users in a timely manner to safeguard driving safety. Users can also report such information to gain access to a more accurate navigation service in real time.
- Accurate road network monitoring: Navi Kit can monitor parallel main and auxiliary roads and overlapped roads on and under bridges in real time.
- Intuitive 3D road map layers: Navi Kit can restore the road and surrounding buildings in 1:1 mode to create an easier and comfortable driving experience for users. 

You can also use HMS Toolkit to quickly run the sample code. HMS Toolkit supports one-stop kit integration, and provides functions such as free app debugging on remote real devices. To learn more about HMS Toolkit, please refer to the [HMS Toolkit documentation](https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/getting-started-0000001077381096?ha_source=hms1).

Preparations
---------------

The sample code is built using Gradle to demonstrate how to use the Navi SDK for Android.

First, download the sample code by cloning this repository or downloading the compressed package.

In Android Studio, click **Open an existing Android Studio project** and select the directory where the sample code file is located.

You can use the **gradlew build** command to directly build the project.

Then, you need to create an app in AppGallery Connect, obtain the **agconnect-services.json** file, and add it to the project. You also need to generate a signing certificate fingerprint, add the signing certificate file to the project, and add related configurations to the **build.gradle** file. For details, please refer to [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/android-sdk-config-agc-0000001214231910).

To learn more, refer to the following documents:

- [Development Guide](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/introduction-0000001185010752)
- [API Reference](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References/api-summary-desc-0000001187429984)

Environment Requirements
-------

+ JDK version: 1.8 or later
+ Android Studio version: 3.6.1 or later
+ minSdkVersion: 19 or later
+ targetSdkVersion: 31 (recommended)
+ compileSdkVersion: 31 (recommended)
+ Gradle version: 5.6.4 or later (recommended)
+ Android Gradle plugin version: 3.6.0 or later
+ Test device: a Huawei phone or tablet running EMUI 5.0 or later, or a non-Huawei phone running Android 7.0 to 12

## Result

  <img src="navi.png" width = 30% height = 30%>

## Technical Support
You can visit the [Reddit community](https://www.reddit.com/r/HMSCore/) to obtain the latest information about HMS Core and communicate with other developers.

If you have any questions about the sample code, try the following:
- Visit [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Votes), submit your questions, and tag them with **huawei-mobile-services**. Huawei experts will answer your questions.
- Visit the HMS Core section in the [HUAWEI Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001?ha_source=hms1) and communicate with other developers.

If you encounter any issues when using the sample code, submit your [issues](https://github.com/HMS-Core/hms-navi-demo/issues) or submit a [pull request](https://github.com/HMS-Core/hms-navi-demo/pulls).

License
-------

The sample code is licensed under [Apache License 2.0](https://github.com/HMS-Core/hms-navi-demo/blob/main/client/java/LICENSE).
