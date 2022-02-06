# PixelmonStrengthen
我的世界神奇宝贝重铸 分解/强化 服务器插件 **重制版**

[![Release | 2.0](https://img.shields.io/badge/Release-2.0-orange)](https://github.com/MrXiaoM/PixelmonStrengthen/releases) [![](https://img.shields.io/badge/mcbbs-thread-brightgreen)](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1084748)

## 重构

PixelmonStrength 终于重构啦! 版本号直接加到 2.0， 移除了大量屎山代码，修复了若干bug，而且B格看起来更高了，用上 gradle 了，泪目。

## 构建

我实在没能力找源了，库有点大又不适合放在仓库里。

首先你需要到下面这个链接下载 `8.2.0` 或者你喜欢的宝可梦重铸mod，然后放到 libs 文件夹里
```
https://download.nodecdn.net/containers/reforged/server/release/8.2.0/Pixelmon-1.12.2-8.2.0-server.jar
```
再去[我的网盘](https://pan.baidu.com/s/1WKgNPagXsGTZWsvWw_isCA) (提取码：5bmn) 下载 `forge-1.12.2-14.23.1.2555-srgBin.jar` 一样放在 libs 文件夹里

然后去 [SpigotMC](https://www.spigotmc.org/) 下载 BuildTools，使用 jdk8 执行 `java -jar BuildTools.jar --rev 1.12.2` 构建 1.12.2 的 Spigot，如果不想那么麻烦的话随便丢个 spigot 1.12.2 到 libs 里也没问题

最后执行以下命令即可构建插件

```
gradlew build
```