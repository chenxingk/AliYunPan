<div align="center">
<br/>

  <h1 align="center">
    自动备份文件到网盘
  </h1>
  <h4 align="center">
    一款可以定时备份本地目录到阿里云盘的小工具
  </h4> 

<div align="center">
  <img  width="92%" style="border-radius:10px;margin-top:20px;margin-bottom:20px;box-shadow: 2px 0 6px gray;" src="https://images.gitee.com/uploads/images/2021/0520/180245_14051ef1_4873209.png" />
</div>
</div>
#### 介绍
Java Swing + Hutool + Okhttp3 自动备份文件到【阿里云盘】的小工具

#### 安装教程

1.  电脑需要有JDK环境
2.  windows10 系统开发测试通过 mac暂无测试（理论可以运行）

#### 内置功能
1.  刷新Token：每小时刷新一次Token，避免token失效。
2.  文件分类：支持按文件类型分类上传，按【文档、图片、视频、音乐等】
3.  上传记忆：记录已经上传的文件，下次执行进行增量上传。
4.  目录监控：监听当前需要备份的目录，产生新文件时自动上传新文件。
5.  定时同步：开启程序后，每晚20:00 自动上传本地目录到【阿里云盘】
#### 使用说明

1.  双击打开程序，选择要备份的目录
2.  通过浏览器登录阿里云盘，登录成功后按F12打开浏览器控制台
3.  输入【`JSON.parse(localStorage.getItem('token')).refresh_token`】即可获得Token
![输入图片说明](https://images.gitee.com/uploads/images/2021/0519/175154_8de09162_4873209.png "屏幕截图.png")
4.   **备份目录则填写想要上传到【阿里云盘】的目录名称** 
5.  点击开始备份，即可运行。
 **注意：【程序初次运行会在同级目录生成：back_config.setting（程序配置文件，存放目录，token，备份模式等信息）、uploadLog.txt（则存放上传过的文件夹，此文件删除后会重新扫描目录下文件是否已上传）】** 
