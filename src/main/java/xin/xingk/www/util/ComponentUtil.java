package xin.xingk.www.util;

import xin.xingk.www.App;

import java.awt.*;

/**
 * @author: Mr.chen
 * @date: 2022/2/11 23:38
 * @description: UI工具类
 */
public class ComponentUtil {

    //屏幕大小
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private static Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(App.mainFrame.getGraphicsConfiguration());

    public static int screenWidth = screenSize.width - screenInsets.left - screenInsets.right;

    public static int screenHeight = screenSize.height - screenInsets.top - screenInsets.bottom;

    /**
     * 设置组件大小并将其放置在屏幕中央
     */
    public static void setPreferSizeAndLocateToCenter(Component component, int preferWidth, int preferHeight) {
        component.setBounds((screenWidth - preferWidth) / 2, (screenHeight - preferHeight) / 2,
                preferWidth, preferHeight);
        Dimension preferSize = new Dimension(preferWidth, preferHeight);
        component.setPreferredSize(preferSize);
    }

    /**
     * 设置组件大小并将其放置在屏幕中心（基于屏幕宽度的百分比）
     */
    public static void setPreferSizeAndLocateToCenter(Component component, double preferWidthPercent, double preferHeightPercent) {
        int preferWidth = (int) (screenWidth * preferWidthPercent);
        int preferHeight = (int) (screenHeight * preferHeightPercent);
        setPreferSizeAndLocateToCenter(component, preferWidth, preferHeight);
    }



}
