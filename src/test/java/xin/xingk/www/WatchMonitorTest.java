package xin.xingk.www;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Console;
import xin.xingk.www.common.utils.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class WatchMonitorTest {
    public static void main(String[] args) {
        monitorFolder();
    }

    /**
     * 监控目录
     */
    private static void monitorFolder() {
        File file = FileUtil.file("F:\\用户目录\\文档\\WeChat Files\\wxid_3wc96wg6zgf022\\FileStorage\\File");
        //这里只监听文件或目录的修改事件
        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY,WatchMonitor.ENTRY_CREATE);
        watchMonitor.setWatcher(new Watcher(){
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("创建：{}-> {}", currentPath, obj);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("修改：{}-> {}", currentPath, obj);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                /*Object obj = event.context();
                Console.log("删除：{}-> {}", currentPath, obj);*/
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                /*Object obj = event.context();
                Console.log("Overflow：{}-> {}", currentPath, obj);*/
            }
        });
        //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        //监听所有目录
        watchMonitor.setMaxDepth(Integer.MAX_VALUE);
        //启动监听
        watchMonitor.start();
    }

}
