package xin.xingk.www;

import xin.xingk.www.mybatis.MybatisPlusUtil;
import xin.xingk.www.service.UserService;
import xin.xingk.www.ui.MainFrame;

/**
 * 备份程序
 *
 */
public class App {
    public static MainFrame mainFrame;
    static UserService userService = MybatisPlusUtil.userService;

    public static void main( String[] args ) {
        userService.insert();
        userService.insert();
    }

    /*

    public static void main( String[] args ) {
        FlatDarkLaf.setup();
        mainFrame = new MainFrame();
        mainFrame.init();
        UserMapper userMapper = MybatisPlusUtil.getMapper(UserMapper.class);
        User user = new User();
        user.setName("陈靖杰");
        user.setToken("666666666");
        user.setTheme(0);
        userMapper.insert(user);
        MybatisPlusUtil.closeSqlSession();

        //删除旧的运行日志
        FileUtil.del(UploadLogUtil.RUN_LOG);
        //检查是否有更新
        //if (checkForUpdate()) return;
        //boolean login = new AliYunPanUtil().getAliYunPanInfo();

        if(false){
            mainFrame.initHome();
            mainFrame.add(Home.getInstance().getHomePanel());
            Home.getInstance().initUi();

//            mainFrame.add(About.getInstance().getAboutPanel());
        }else{
            mainFrame.initLogin();
            mainFrame.add(Login.getInstance().getLoginPanel());
            Login.getInstance().initUi();
        }
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.getWidth() <= 1366) {
            // The window is automatically maximized at low resolution
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    *//**
     * 检查更新
     *//*
    private static boolean checkForUpdate() {
        String result = HttpUtil.get("http://yunpan.xingk.xin/备份助手/upload.json");
        while (StrUtil.isEmpty(result)){
            result = HttpUtil.get("http://yunpan.xingk.xin/备份助手/upload.json");
        }
        JSONObject versionJson = JSONUtil.parseObj(result);
        String url = versionJson.getStr("url");
        String desc = versionJson.getStr("desc");
        Object version = versionJson.get("version");
        int update = versionJson.getInt("update");
        boolean isUpdate = NumberUtil.isGreater((BigDecimal) version, CommonConstants.VERSION);
        if (isUpdate){//检测到有新版
            CommonUI.setFont();//设置字体
            int button = JOptionPane.showConfirmDialog(null, desc, "检测到有新版，是否更新？", JOptionPane.YES_NO_OPTION);
            if (button==0){//选择是打开浏览器
                DesktopUtil.browse(url);
                return true;
            }else {
                if (update==1){//强更新
                    System.exit(0);
                }
            }
        }
        return false;
    }*/

}
