import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConsoleTest {

    //词法分析
    private static Lexer lexer = new Lexer();
    //语法分析
    private static Parser parser;
    //语义分析
    private static Semantic semantic;

    private static String getFile(String fileName) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bf= new BufferedReader(new FileReader(fileName));
        String s = null;
        while((s = bf.readLine())!=null){//使用readLine方法，一次读一行
            buffer.append(s.trim()+"\n");
        }
        String xml = buffer.toString();
        return xml;
    }

    // 运行：分析并运行CMM程序，显示运行结果,词法分析语法分析都过了才能进行语义分析
    public static void main(String[] args) throws IOException {
        lexer.execute(getFile("test2.cmm"));
        parser = new Parser(lexer.getTokens());
        parser.execute();

        semantic = new Semantic(parser.getRoot());
        semantic.run();

        System.out.println(semantic.getResult().toString());
    }

    public static void show(TreeNode temp){
        TreeNode child;
        int count = temp.getChildCount();
        //System.out.println(temp.getContent());
        //System.out.println(count);
        for (int i = 0; i < temp.getChildCount(); i++) {
            child = temp.getChildAt(i);
            System.out.print(child.getChildCount());
            for (int j = 0; j < child.getLevel(); j++) {
                System.out.print("   |");
            }
            System.out.println(child.getContent());
            show(child);
        }
    }
}
