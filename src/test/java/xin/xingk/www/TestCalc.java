package xin.xingk.www;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestCalc {
    public static void main(String[] args) {
       new Calculator();
    }

static class Calculator extends Frame{
    public Calculator(){
        setVisible(true);
        //3个文本框
        TextField num1 = new TextField(10);//字符数
        TextField num2 = new TextField(10);//字符数
        TextField num3 = new TextField(10);//字符数
        //1个按钮
        Button button=new Button("=");

        button.addActionListener(new MyCalculatorListener(num1,num2,num3));

        //1个标签
        Label label = new Label("+");

        setLayout(new FlowLayout());

        add(num1);
        add(label);
        add(num2);
        add(button);
        add(num3);
        pack();
    }
}

static class MyCalculatorListener implements ActionListener{
    private TextField num1,num2,num3;

    public MyCalculatorListener(TextField num1,TextField num2,TextField num3){
        this.num1=num1;
        this.num2=num2;
        this.num3=num3;
    }

    //获取三个变量
    @Override
    public void actionPerformed(ActionEvent e) {
        //1.获得加数和被家属
        int n1 = Integer.parseInt(num1.getText());
        int n2 = Integer.parseInt(num2.getText());

        //将这个值+法运算后，放到第三框
        num3.setText(""+(n1+n2));

        //清除前两个框
        num1.setText("");
        num2.setText("");
    }
}
}
