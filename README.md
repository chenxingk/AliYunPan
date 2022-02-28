<a href="https://gitee.com/xingk-code/AliYunPan">
 <img alt="AliYunPan-Logo" src="https://images.gitee.com/uploads/images/2022/0228/182550_33a29e1b_4873209.png">
</a>
  
# 备份助手 
> 一款可以定时备份本地目录到阿里云盘的小工具

[![码云Gitee](https://gitee.com/xingk-code/AliYunPan/badge/star.svg?theme=blue)](https://gitee.com/xingk-code/AliYunPan)


## 支持的平台
Windows • Linux • macOS

#### 环境

Java Swing + FlatLaf + Hutool + Okhttp3

如果觉得不错希望您能留下真贵的Star,您的Star就是我的动力，感谢！

#### 由来

工作中会经常接收到各个客户发来的种种文件，这些文件又比较重要，搞丢了就会很麻烦。

而且公司电脑的文件回家后要改就很麻烦，不能同步，苦于网上没有找到类似的备份软件，咱自己动手写一个！

哪我为什么不用百度云盘？？？因为穷！！！因为限速。。。

#### 截图速览

<p align="center">
  <a href="https://images.gitee.com/uploads/images/2022/0228/184444_0bd7db62_4873209.png">
   <img alt="AliYunPan" src="https://images.gitee.com/uploads/images/2022/0228/184444_0bd7db62_4873209.png">
  </a>
</p>

<p align="center">
  <a href="https://images.gitee.com/uploads/images/2022/0228/184705_4b743edb_4873209.png">
   <img alt="AliYunPan" src="https://images.gitee.com/uploads/images/2022/0228/184705_4b743edb_4873209.png">
  </a>
</p>

<p align="center">
  <a href="https://images.gitee.com/uploads/images/2022/0228/184734_bfa88bd4_4873209.png">
   <img alt="AliYunPan" src="https://images.gitee.com/uploads/images/2022/0228/184734_bfa88bd4_4873209.png">
  </a>
</p>

<p align="center">
  <a href="https://images.gitee.com/uploads/images/2022/0228/184836_e0f63180_4873209.png">
   <img alt="AliYunPan" src="https://images.gitee.com/uploads/images/2022/0228/184836_e0f63180_4873209.png">
  </a>
</p>
 

#### 下载安装
1.  Windows系统  **含JDK安装包，适合没有JDK环境的** 使用：[v1.2点击下载](http://yunpan.xingk.xin/%E5%A4%87%E4%BB%BD%E5%8A%A9%E6%89%8B/%E5%A4%87%E4%BB%BD%E5%8A%A9%E6%89%8B%E5%AE%89%E8%A3%85%E5%8C%85v1.2.exe)
2.  Windows系统  **不含JDK安装包，适合有JDK环境的** 可以直接运行：[v1.2点击下载](https://gitee.com/xingk-code/AliYunPan/attach_files/959151/download/%E5%A4%87%E4%BB%BD%E5%8A%A9%E6%89%8Bv1.2%EF%BC%88%E4%B8%8D%E5%90%ABJDK%EF%BC%89.exe)
2.  Windows、Liunx、Macos、等系统有JDK的可以直接下载jar运行：[v1.2点击下载](https://gitee.com/xingk-code/AliYunPan/attach_files/959145/download/%E5%A4%87%E4%BB%BD%E5%8A%A9%E6%89%8Bv1.2.jar)

#### 更新日志
##### 2022-2-6

- 优化扫描目录逻辑
- 优化目录监听上传
- 修改获取contentType方法

##### 2022-1-16

- 新增微信备份模式(仅对一级目录分类，二级目录不分类)
- 新增记录运行日志
- 优化更新检测
- 优化登录逻辑，修复登录异常
- 优化文件夹、文件获取方式，修复一直获取文件夹

##### 2021-12-5

- 优化用户界面
- 新增检测是否更新
- 优化上传逻辑,新增分片上传
- 支持大文件上传,显示文件上传进度
- 修复开启目录检测不准确bug

##### 2021-11-28

- 新增更多设置界面，配置自动备份时间
- 文件目录监控改为手动开启关闭
-  **重点修复429请求频繁错误** 
- 优化代码结构
##### 2021-09-20


- 自定义设置自动备份时间
- 优化文件监控
- 修复偶尔发生的排版错乱
- 优化代码结构，修复N个BUG
- 去掉账号密码登录和短信验证码登录


##### 2021-06-03


- 新增二维码登录
- 修复阿里云盘更新导致无法登录
- 优化代码结构


#### 内置功能
1.  刷新Token：每小时刷新一次Token，避免token失效。
2.  文件分类：支持按文件类型分类上传，按【文档、图片、视频、音乐等】
3.  上传记忆：记录已经上传的文件，下次执行进行增量上传。
4.  目录监控：软件启动后自动监听当前需要备份的目录，产生新文件或文件修改时自动上传新文件。
5.  定时同步：开启程序后，每晚20:30 自动上传本地目录到【阿里云盘】<br>
PS：如需修改执行时间请到back_config.setting文件中修改backupTime参数，注意格式不要写错哦~如【20:30:00】
6.  登录状态：自动保存登录状态，减少登录次数，提升用户体验
#### 开发计划....
1.生成上传日志文件
2.是否开启压缩后备份
3.按关键字、按类型上传
#### 使用说明

1.  双击打开程序，登录阿里云盘账号
2.  选择要备份的目录
3.  **备份目录则填写想要上传到【阿里云盘】的目录名称**
4.  **备份模式：**
    1. 普通备份：会按本地目录结构上传文件
    2. 分类备份：会将本地文件按文档、压缩包、软件、音乐、图片、视频等分类上传
5.  点击开始备份，即可全量备份所选目录。

#### 常见问题
![输入图片说明](https://images.gitee.com/uploads/images/2021/0524/153117_ebbff9ea_4873209.png "屏幕截图.png")

出现【请您先在电脑网页登录成功后，再登录本软件。】是因为第一次在本设备登录需要进行二次认证。。。暂未兼容

**注意：程序初次运行会在同级目录生成：** 

**back_config.setting（程序配置文件，存放目录，token，备份模式等信息）、**

**uploadLog.json（则存放上传过的文件信息，此文件删除后会重新扫描目录下文件是否已上传）** 

### 特别感谢
[zhoubochina](https://gitee.com/zhoubochina)  
[FlatLaf](https://www.formdev.com/flatlaf/)  
[Hutool](http://hutool.cn/)  

