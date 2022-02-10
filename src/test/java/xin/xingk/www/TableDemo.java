package xin.xingk.www;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableDemo extends JFrame implements ActionListener {

//操作面板，用于放置增删改按钮

    private final JPanel controlPanel;

//定义表格的数据模型

    private final DefaultTableModel model;

//定义一个表格

    private final JTable table;

//定义一个滚动面板，用于放置表格

    private final JScrollPane scrollPane;

//增删改按钮

    private final JButton addBtn;
    private final JButton deleteBtn;
    private final JButton updateBtn;

    public TableDemo() {

//设置窗口尺寸

        setBounds(100, 100, 500, 500);

//JTable的表头标题

        String[] head = {"学号", "姓名", "班级", "性别"};

//JTable的初始化数据

        Object[][] datas = {{"001", "张三", "班级一", "男"},

                {"002", "李四", "班级二", "女"}};

//初始化JTable的数据模型

        model = new DefaultTableModel(datas, head);

//初始化表格

        table = new JTable(model);

//初始化滚动面板

        scrollPane = new JScrollPane(table);

//初始化按钮以及添加监听器

        addBtn = new JButton("增加");

        deleteBtn = new JButton("删除");

        updateBtn = new JButton("修改");

        addBtn.addActionListener(this);

        deleteBtn.addActionListener(this);

        updateBtn.addActionListener(this);

//初始化控制面板

        controlPanel = new JPanel();

        controlPanel.add(addBtn);

        controlPanel.add(deleteBtn);

        controlPanel.add(updateBtn);

//该窗口使用BorderLayout布局

        add(controlPanel, BorderLayout.NORTH);

        add(scrollPane, BorderLayout.CENTER);

//设置表格单元格字体居中显示

        DefaultTableCellRenderer render =

                new DefaultTableCellRenderer();

        render.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumn("学号").setCellRenderer(render);

        table.getColumn("姓名").setCellRenderer(render);

//设置表格宽度情况

        DefaultTableColumnModel dcm =

                (DefaultTableColumnModel) table.getColumnModel();

//设置表格显示的最好宽度，即此时表格显示的宽度。

        dcm.getColumn(0).setPreferredWidth(60);

//设置表格通过拖动列可以的最小宽度。

        dcm.getColumn(0).setMinWidth(45);

//设置表格通过拖动列可以的最大宽度。

        dcm.getColumn(0).setMaxWidth(75);

//给表格设置行高

        table.setRowHeight(35);

        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                int row = table.getSelectedRow();

                int col = table.getSelectedColumn();

                System.out.println("编辑前：" + table.

                        getValueAt(row, col));

            }

        });

        table.getModel().addTableModelListener(

                new TableModelListener() {

                    public void tableChanged(TableModelEvent e) {

                        if (e.getType() == TableModelEvent.UPDATE) {

                            int row = e.getLastRow();

                            int col = e.getColumn();

                            System.out.println("编辑后:" +

                                    table.getValueAt(row, col));

                        }

                    }

                });

        setVisible(true);

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new TableDemo();

    }

    @Override

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addBtn) {

            model.addRow(new Object[]{"003", "王五", "班级一", "男"});

        } else if (e.getSource() == deleteBtn) {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(this, "请先选择一条记录！");

                return;

            }

            model.removeRow(table.getSelectedRow());

        } else if (e.getSource() == updateBtn) {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(this, "请先选择一条记录！");

                return;

            }

            table.setValueAt("测试", row, 1);

        }

    }

}