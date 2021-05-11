package xin.xingk.www;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test extends JFrame implements ActionListener {
    // 日志界面
    private JTextArea logArea;
    //日志滚动条
    private JScrollBar textAreaScroll;
    // 配置界面
    private JTextArea configArea;
    // 根路径
    private JTextField rootPathFiled;
    // 项目路径
    private JTextField childPathFiled;
    // 需求路径
    private JTextField secondPathFiled;

    // 生成按钮
    private JButton okButton;
    // 检查按钮
    private JButton checkBtn;

    // 存取遍历路径结果
    private ArrayList<String> pathList;

    // 存取RM配置文件
    private HashMap<String, String> rmMap;

    private final String CONFIGPATH = System.getProperty("user.dir") + File.separator + "config.properties";

    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JCheckBox checkBox4;
    private JCheckBox checkBox5;

    private JFileChooser rootPathChooser = new JFileChooser();
    private JFileChooser childPathChooser = new JFileChooser();
    private JFileChooser secondPathChooser = new JFileChooser();

    private JButton rootSelectbtn;
    private JButton childSelectbtn;
    private JButton secondSelectbtn;

    // 迭代一单选框
    private JRadioButton iterOneRadio;
    // 迭代二单选框
    private JRadioButton iterTwoRadio;

    // 前后代码
    private JCheckBox newOldCheckBox;
    // 测试报告
    private JCheckBox testCheckBox;
    // 设计文档
    private JCheckBox designCheckBox;
    // 反讲纪要
    private JCheckBox sumaryCheckBox;
    // 代码评审记录
    private JCheckBox reviewCheckBox;

    // 刷新配置文件
    private JButton refreshBtn;
    // 保存配置文件
    private JButton saveBtn;

    public Test() {
        initConfig();
        initUi();
        initDefaultValue();
        this.setVisible(true);
    }

    private void initConfig() {
        System.setProperty("sun.jnu.encoding", "utf-8");
        // 设置界面使用字体
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体", Font.PLAIN, 12));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }

        this.setSize(800, 900);
        this.setTitle("FileCreateTool");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置位于屏幕中间
        int windowWidth = this.getWidth(); // 获得窗口宽
        int windowHeight = this.getHeight(); // 获得窗口高
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高
        this.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示
    }

    public void initUi() {
        JTabbedPane maintab = new JTabbedPane();
        Container container = new Container();

        // 初始化控件
        JLabel rootPathLable = new JLabel("根目录");
        rootPathFiled = new JTextField();
        JLabel childPathLable = new JLabel("子目录");
        childPathFiled = new JTextField();
        JLabel secondPathLable = new JLabel("文件夹");
        secondPathFiled = new JTextField();
        JLabel chooseIterLable = new JLabel("迭代");

        // 路径选择区域
        JPanel northPanle = new JPanel();
        northPanle.setLayout(null);

        rootPathLable.setBounds(10, 20, 166, 25);
        container.add(rootPathLable);
        rootPathFiled.setBounds(80, 20, 560, 35);
        container.add(rootPathFiled);

        childPathLable.setBounds(10, 60, 166, 25);
        container.add(childPathLable);
        childPathFiled.setBounds(80, 60, 560, 35);
        container.add(childPathFiled);

        secondPathLable.setBounds(10, 100, 166, 25);
        container.add(secondPathLable);
        secondPathFiled.setBounds(80, 100, 560, 35);
        container.add(secondPathFiled);

        rootSelectbtn = new JButton("...");
        rootSelectbtn.setBounds(640, 25, 30, 25);
        rootSelectbtn.addActionListener(this);
        container.add(rootSelectbtn);

        iterOneRadio = new JRadioButton("迭代一");
        iterOneRadio.setBounds(100, 150, 100, 25);
        iterOneRadio.setBackground(container.getBackground());
        container.add(iterOneRadio);
        iterTwoRadio = new JRadioButton("迭代二", true);
        iterTwoRadio.setBounds(250, 150, 100, 25);
        iterTwoRadio.setBackground(container.getBackground());
        container.add(iterTwoRadio);

        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(iterOneRadio);
        bGroup.add(iterTwoRadio);

        newOldCheckBox = new JCheckBox("前后文件", true);
        newOldCheckBox.setBounds(50, 200, 80, 25);
        newOldCheckBox.setBackground(container.getBackground());
        container.add(newOldCheckBox);

        testCheckBox = new JCheckBox("测试报告", true);
        testCheckBox.setBounds(140, 200, 80, 25);
        testCheckBox.setBackground(container.getBackground());
        container.add(testCheckBox);

        designCheckBox = new JCheckBox("设计文档", true);
        designCheckBox.setBackground(container.getBackground());
        designCheckBox.setBounds(230, 200, 80, 25);
        container.add(designCheckBox);

        sumaryCheckBox = new JCheckBox("反讲纪要", true);
        sumaryCheckBox.setBackground(container.getBackground());
        sumaryCheckBox.setBounds(320, 200, 80, 25);
        container.add(sumaryCheckBox);

        reviewCheckBox = new JCheckBox("代码评审", true);
        reviewCheckBox.setBackground(container.getBackground());
        reviewCheckBox.setBounds(410, 200, 80, 25);
        container.add(reviewCheckBox);

        okButton = new JButton("创建");
        okButton.setBounds(600, 195, 60, 30);
        okButton.addActionListener(this);
        container.add(okButton);

        checkBtn = new JButton("检查");
        checkBtn.setBounds(680, 195, 60, 30);
        checkBtn.addActionListener(this);
        container.add(checkBtn);

        logArea = new JTextArea("Program Start...\n");
        logArea.setForeground(Color.white);
        logArea.setBackground(Color.BLACK);
//        logArea.setBounds(0, 300, 800, 600);
        //启动自动换行
        logArea.setLineWrap(true);
        //换行不断字
        logArea.setWrapStyleWord(true);
//        logArea.setBounds(0, 300, 770, 600);

        JScrollPane js = new JScrollPane(logArea);
        js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollBar jb = js.getVerticalScrollBar();
        jb.setSize(100,100);
        js.setBounds(0, 300, 800, 600);
        container.add(js);

//        textAreaScroll = new JScrollBar(JScrollBar.VERTICAL, 10, 10, 0, 100);
//        textAreaScroll.setBounds(770,300,30,600);
//        container.add(textAreaScroll);

        maintab.add("主界面", container);

        Container configContainer = new Container();
        refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(this);
        refreshBtn.setBounds(250, 10, 100, 30);
        configContainer.add(refreshBtn);

        saveBtn = new JButton("保存");
        saveBtn.addActionListener(this);
        saveBtn.setBounds(400, 10, 100, 30);
        configContainer.add(saveBtn);

        configArea = new JTextArea();
        //自动换行
        configArea.setLineWrap(false);
        //换行不断字
        configArea.setWrapStyleWord(true);
        configArea.setBackground(Color.WHITE);

        JScrollPane configScroll=new JScrollPane(configArea);
        configScroll.setBounds(0, 100, 800, 750);

        configContainer.add(configScroll);

        maintab.add("配置界面", configContainer);

        // 配置界面

        this.setContentPane(maintab);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rootSelectbtn) {
            rootPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int intRetVal = rootPathChooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                rootPathFiled.setText(rootPathChooser.getSelectedFile().getPath());
            }
        }

        if (e.getSource() == childSelectbtn) {
            childPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int intRetVal = childPathChooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                childPathFiled.setText(childPathChooser.getSelectedFile().getPath());
            }
        }

        if (e.getSource() == secondSelectbtn) {
            secondPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int intRetVal = secondPathChooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                secondPathFiled.setText(secondPathChooser.getSelectedFile().getPath());
            }
        }

        if (e.getSource() == okButton) {
            if (makeDir()) {
                wirteConifg("childPath", childPathFiled.getText());
                wirteConifg("secondPath", secondPathFiled.getText());
                try {
                    java.awt.Desktop.getDesktop().open(new File(rootPathFiled.getText()));
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                }
            } else {
                JOptionPane.showMessageDialog(null, "创建文件夹失败", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == checkBtn) {
            HashMap<String, String> map = getConfigMap();
            String seq = File.separator;
            ArrayList<String> inDirRmList;
            HashMap<String, String> pathMap = new HashMap<String, String>();

            String rootPath = rootPathFiled.getText();
            // 前后代码
            pathMap.put("newOld", rootPath + seq + map.get("newOld"));
            // 测试报告
            pathMap.put("test", rootPath + map.get("test"));
            // 设计文档
            pathMap.put("design", rootPath + map.get("design"));
            // 反讲纪要
            pathMap.put("sumary", rootPath + map.get("sumary"));
            // 代码评审记录
            pathMap.put("review", rootPath + map.get("review"));

            logArea.setText("开始检查...\n");
            // pathList=new ArrayList<String>();
            // getAllFileInDir(newOld);
            // for(String str:pathList){
            // logArea.append(str+"\n");
            // }
            getRMConfig();
            for (Entry<String, String> entry : pathMap.entrySet()) {
                switch (entry.getKey()) {
                    case "newOld":
                        logArea.append("前后文件\n");
                        break;
                    case "test":
                        logArea.append("测试报告\n");
                        break;
                    case "design":
                        logArea.append("设计文档\n");
                        break;
                    case "sumary":
                        logArea.append("反讲纪要\n");
                        break;
                    case "review":
                        logArea.append("代码检视\n");
                        break;
                    default:
                        break;
                }
                inDirRmList = getRmInPath(entry.getValue());
                for (Entry<String, String> rmEntry : rmMap.entrySet()) {
                    if (!inDirRmList.contains(rmEntry.getKey())) {
                        logArea.append(rmEntry.getKey() + ": " + rmEntry.getValue() + "\n");
                    }
                }
            }
        }

        if (e.getSource() == refreshBtn) {
            configArea.setText(getConfigStr());
            logArea.append("刷新配置文件成功\n");
        }

        if (e.getSource() == saveBtn) {
            wirteConifg();
            JOptionPane.showMessageDialog(null, "写入配置成功", "Info", JOptionPane.INFORMATION_MESSAGE);
            logArea.append("写入配置文件成功");
        }

        if (e.getSource() == rootSelectbtn) {
            boolean isOk = wirteConifg("rootPath", rootPathChooser.getSelectedFile().getAbsolutePath());
            if (!isOk) {
                logArea.append("路径保存失败");
            }
        }

        if (e.getSource() == textAreaScroll) {
//            this.logArea.set
        }

    }

    private ArrayList<String> getRmInPath(String path) {
        ArrayList<String> result = new ArrayList<String>();
        this.pathList = new ArrayList<String>();
        String regEx = "RM\\d{3}_\\d{3}";
        getAllFileInDir(path);
        for (String str : this.pathList) {
            Matcher matcher = Pattern.compile(regEx).matcher(str);
            if (matcher.find()) {
                result.add(matcher.group());
            }
        }
        return result;

    }

    private void initDefaultValue() {
        // 设置初始路径
        HashMap<String, String> map = getConfigMap();
        rootPathFiled.setText(map.get("rootPath"));
        childPathFiled.setText(map.get("childPath"));
        secondPathFiled.setText(map.get("secondPath"));

        // 设置初始配置文件
        String configStr = getConfigStr();
        configArea.setText(configStr);
    }

    private HashMap<String, String> getConfigMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        Properties prop = new Properties();// 属性集合对象
        FileInputStream in;
        try {
            in = new FileInputStream(CONFIGPATH);
            InputStreamReader reader = new InputStreamReader(in, "utf-8");
            prop.load(reader);// 将属性文件流装载到Properties对象中
            in.close();// 关闭流
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return map;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return map;
        }
        Iterator<String> it = prop.stringPropertyNames().iterator();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key, prop.getProperty(key));
        }
        return map;
    }

    private boolean makeDir() {

        HashMap<String, String> map = getConfigMap();
        String rootPath = rootPathFiled.getText();
        String childPath = childPathFiled.getText();
        String secondPath = secondPathFiled.getText();

        // 前后代码
        String newOld = map.get("newOld");
        // 测试报告
        String test = map.get("test");
        // 设计文档
        String design = map.get("design");
        // 反讲纪要
        String sumary = map.get("sumary");
        // 代码评审记录
        String review = map.get("review");
        // 迭代
        String iter = iterOneRadio.isSelected() ? "迭代一" : "迭代二";

        String seq = File.separator;
        ArrayList<String> filePath = new ArrayList<String>();
        if (newOldCheckBox.isSelected()) {
            String path = rootPath + seq + newOld + seq + iter + seq + childPath + seq + secondPath;
            filePath.add(path);
        }
        if (testCheckBox.isSelected()) {
            String path = rootPath + seq + test + seq + iter + seq + childPath + seq + secondPath;
            filePath.add(path);
        }
        if (designCheckBox.isSelected()) {
            String path = rootPath + seq + design + seq + iter + seq + childPath + seq + secondPath;
            filePath.add(path);
        }
        if (sumaryCheckBox.isSelected()) {
            String path = rootPath + seq + sumary + seq + iter + seq + childPath + seq + secondPath;
            filePath.add(path);
        }
        if (reviewCheckBox.isSelected()) {
            String path = rootPath + seq + review + seq + iter + seq + childPath + seq + secondPath;
            filePath.add(path);
        }
        int i = 0;
        logArea.setText("开始创建文件夹...\n");
        for (String path : filePath) {
            File file = new File(path);
            try {
                i++;
                file.mkdirs();
                logArea.append(i + ". " + path + "\n");
            } catch (Exception e) {
                return false;
            }

        }
        return true;
    }

    // 递归遍历获取路径下的所有文件
    private void getAllFileInDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                getAllFileInDir(file.getAbsolutePath());
            } else {
                pathList.add(file.getAbsolutePath());
            }
        }
    }

    /**
     * 从配置文件读取内容，返回字符串
     *
     * @return
     */
    private String getConfigStr() {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        File configFile = new File(CONFIGPATH);

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            logArea.append(e.toString());
        }

        return sb.toString();
    }

    private void wirteConifg() {
        try {
            FileWriter fileWriter = new FileWriter(CONFIGPATH);
            String content = configArea.getText();
            fileWriter.write(content);
            fileWriter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logArea.append(e.toString());
        }
    }

    private Boolean wirteConifg(String key, String value) {

        Properties prop = new Properties();// 属性集合对象
        FileInputStream fis;
        try {
            fis = new FileInputStream(CONFIGPATH);
            InputStreamReader reader = new InputStreamReader(fis, "utf-8");
            prop.load(reader);// 将属性文件流装载到Properties对象中
            fis.close();// 关闭流
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        prop.setProperty(key, value);
        // 文件输出流
        try {
            FileOutputStream fos = new FileOutputStream(CONFIGPATH);
            // 将Properties集合保存到流中
            prop.store(new OutputStreamWriter(fos, "utf-8"), "Copyright (c) Alon Studio");
            fos.close();// 关闭流
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void getRMConfig() {
        rmMap = new HashMap<String, String>();
        Properties prop = new Properties();
        try {
            // 读取属性文件a.properties
            InputStream in = new BufferedInputStream(new FileInputStream(CONFIGPATH));
            prop.load(new InputStreamReader(in, "utf-8")); /// 加载属性列表
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.startsWith("RM")) {
                    rmMap.put(key, prop.getProperty(key));
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new Test();
    }

}