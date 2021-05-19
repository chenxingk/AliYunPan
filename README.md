<div align="center">
<br/>

  <h1 align="center">
    本地文件备份
  </h1>
  <h4 align="center">
    一款可以定时备份本地目录到阿里云盘的小工具
  </h4> 

<div align="center">
  <img  width="92%" style="border-radius:10px;margin-top:20px;margin-bottom:20px;box-shadow: 2px 0 6px gray;" src="https://images.gitee.com/uploads/images/2021/0519/172731_35c58ed6_4873209.png" />
</div>
</div>
# AliYunPan

#### 介绍
Java Swing + Hutool + Okhttp3 自动备份文件小工具

#### 安装教程

1.  电脑需要有JDK环境
2.  windows10 系统开发测试通过 mac暂无测试

#### 内置功能

1.  定时同步：开启程序后，每晚20:00 自动上传本地目录到【阿里云盘】
2.  文件分类：支持按文件类型分类上传，按【文档、图片、视频、音乐等】
3.  上传记忆：记录已经上传的文件，下次执行进行增量上传。

#### 使用说明

1.  双击打开程序，选择要备份的目录
2.  通过浏览器登录阿里云盘，登录成功后按F12打开浏览器控制台
3.  输入【`JSON.parse(localStorage.getItem('token')).refresh_token`】即可获得Token
![输入图片说明](https://images.gitee.com/uploads/images/2021/0519/174415_3a7fbc5f_4873209.png "屏幕截图.png")
4.   **备份目录则填写想要上传到【阿里云盘】的目录名称** 
5.  点击开始备份，即可运行。
