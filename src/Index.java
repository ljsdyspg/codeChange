import sun.java2d.cmm.kcms.CMM;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;


//还要改一个字号，再把内容和文本框联系起来
public class Index extends JFrame{

    private File codeFile;

    //词法分析
    private CMMLexer lexer = new CMMLexer();
    //语法分析
    private static CMMParser parser;
    //语义分析
    private static CMMSemanticAnalysis cmmSemanticAnalysis;

    private JSplitPane splitPane;
    private JSplitPane lsplitPane;
    private JSplitPane rsplitPane;

    private JMenuBar menuBar;

    private JMenu menu_file;
    private JMenu menu_edit;
    private JMenu menu_about;

    private JMenuItem item_new_file;
    private JMenuItem item_open_file;
    private JMenuItem item_exit;
    private JMenuItem runProj;

    private JTextArea textarea;
    private JTextArea parse_log;
    private JTextArea lexer_log;
    private JTextArea console;

    private JScrollPane textarea_scroll;
    private JScrollPane parse_log_scroll;
    private JScrollPane lexer_log_scroll;
    private JScrollPane console_scroll;

    private final String code = "Here are my codes.";

    public Index(){

        this.setTitle("CMM解释器");
        this.setSize(1280,720);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //代码显示区
        textarea = new JTextArea(code);
        textarea_scroll = new JScrollPane(textarea);
        //控制台
        console = new JTextArea("This is a console.");
        console_scroll = new JScrollPane(console);
        //词法分析结果
        lexer_log = new JTextArea("Here are lexer results.");
        lexer_log_scroll = new JScrollPane(lexer_log);
        //语法分析结果
        parse_log = new JTextArea("Here are parse results.");
        parse_log_scroll = new JScrollPane(parse_log);

        lsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        lsplitPane.setLeftComponent(textarea_scroll);
        lsplitPane.setRightComponent(console_scroll);
        lsplitPane.setDividerLocation(3*this.getHeight()/5);

        rsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rsplitPane.setLeftComponent(lexer_log_scroll);
        rsplitPane.setRightComponent(parse_log_scroll);
        rsplitPane.setDividerLocation(this.getHeight()/2);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(4*this.getWidth()/5);
        splitPane.setLeftComponent(lsplitPane);
        splitPane.setRightComponent(rsplitPane);



        menuBar = new JMenuBar();

        menu_file = new JMenu("文件");
        item_new_file = new JMenuItem("新建");
        item_open_file = new JMenuItem("打开");
        item_open_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOpenDialog(textarea,textarea);
            }
        });
        item_exit = new JMenuItem("退出");
        item_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu_file.add(item_new_file);
        menu_file.add(item_open_file);
        menu_file.add(item_exit);

        menu_edit = new JMenu("运行");
        runProj = new JMenuItem("运行");
        runProj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        menu_edit.add(runProj);
        menu_about = new JMenu("关于");

        menuBar.add(menu_file);
        menuBar.add(menu_edit);
        menuBar.add(menu_about);

        this.setJMenuBar(menuBar);
        this.add(splitPane);
    }

    private void showOpenDialog(Component parent, JTextArea textarea){
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        fileChooser.setFileFilter(new FileNameExtensionFilter( "文本文档", "txt","cmm"));

        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION){
            codeFile = fileChooser.getSelectedFile();
            try {
                textarea.setText("");
                Scanner scanner = new Scanner(codeFile);
                while (scanner.hasNext()){
                    textarea.append(scanner.nextLine()+"\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        lexer = new CMMLexer();
        try {
            lexer.execute(getFile(codeFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lexer_log.setText("词法分析：\n");
        lexer_log.append(String.valueOf(lexer.getResult()));

        parser = new CMMParser(lexer.getTokens());
        parser.execute();
        parse_log.setText("语法分析：\n");
        parse_log.append(String.valueOf(parser.getResult()));

        cmmSemanticAnalysis = new CMMSemanticAnalysis(parser.getRoot());
        cmmSemanticAnalysis.run();
        console.setText("语义分析：\n");
        console.append(String.valueOf(cmmSemanticAnalysis.getResult()));
    }


    private String getFile(File fileName) throws FileNotFoundException {
        String getCode = "";
        Scanner scanner = new Scanner(fileName);
        while(scanner.hasNext()){
            getCode += scanner.nextLine()+"\n";
        }
        return getCode;
    }

    public static void main(String[] args) {
        Index index = new Index();
        index.setVisible(true);
    }
}
