package xin.xingk.www;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import xin.xingk.www.context.UserContextHolder;

/**
 * 测试类
 */
public class Test{

    public static void main(String[] args)  {
        TimeInterval timer = DateUtil.timer();
        ConcurrencyTester tester = ThreadUtil.concurrencyTest(100, () -> {
            String dbVersion = UserContextHolder.getDbVersion();
            System.out.println("当前行数："+dbVersion);
//            // 测试的逻辑内容
//            long delay = RandomUtil.randomLong(100, 1000);
//            ThreadUtil.sleep(delay);
//            Console.log("{} test finished, delay: {}", Thread.currentThread().getName(), delay);
        });
        System.out.println("耗时："+timer.interval()+" ms");
    }



}