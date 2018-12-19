import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;



public class Lexer {
    // 注释的标志
    private boolean isNotation = false;
    // 错误信息
    private String errorInfo = "";
    // 分析后得到的tokens集合，用于其后的语法及语义分析
    private ArrayList<Token> tokens = new ArrayList<>();
    // 分析后得到的所有tokens集合，包含注释、空格等
    private ArrayList<Token> displayTokens = new ArrayList<>();
    // 存储所有词法分析的错误
    private ArrayList<String> errorList = new ArrayList<>();
    // 读取CMM文件文本
    private BufferedReader reader;
    // 用于输出语法分析的结果
    private StringBuilder result = new StringBuilder();

    public StringBuilder getResult() {
        return result;
    }

    private boolean isNotation() {
        return isNotation;
    }

    private void setNotation(boolean isNotation) {
        this.isNotation = isNotation;
    }

    private void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public ArrayList<Token> getDisplayTokens() {
        return displayTokens;
    }

    private void setDisplayTokens(ArrayList<Token> displayTokens) {
        this.displayTokens = displayTokens;
    }
    
    //执行
    private void executeLine(String cmmText, int lineNum) {
        // 创建当前行根结点
        String content = "第" + lineNum + "行： " + cmmText;
        //TreeNode node = new TreeNode(content);
        System.out.println(content);
        result.append(content+"\n");
        // 词法分析每行结束的标志
        cmmText += "\n";
        int length = cmmText.length();
        // switch状态值
        int state = 0;
        // 记录token开始位置
        int begin = 0;
        // 记录token结束位置
        int end = 0;
        // 逐个读取当前行字符，进行分析，如果不能判定，向前多看k位
        for (int i = 0; i < length; i++) {
            char ch = cmmText.charAt(i);
            if (!isNotation) {
                if (ch == '(' || ch == ')' || ch == ';' || ch == '{'
                        || ch == '}' || ch == '[' || ch == ']' || ch == ','
                        || ch == '+' || ch == '-' || ch == '*' || ch == '/'
                        || ch == '=' || ch == '<' || ch == '>' || ch == '"'
                        || Match.isLetter(ch) || Match.isDigit(ch)
                        || String.valueOf(ch).equals(" ")
                        || String.valueOf(ch).equals("\n")
                        || String.valueOf(ch).equals("\r")
                        || String.valueOf(ch).equals("\t")) {
                    switch (state) {
                        case 0:
                            // 分隔符直接打印
                            if (ch == '(' || ch == ')' || ch == ';' || ch == '{'
                                    || ch == '}' || ch == '[' || ch == ']'
                                    || ch == ',') {
                                state = 0;
                                //node.add(new TreeNode("分隔符 ： " + ch));
                                System.out.println("\t"+"分隔符 ： " + ch);
                                result.append("分隔符 ： " + ch +"\n");
                                tokens.add(new Token(lineNum, i + 1, "分隔符", String.valueOf(ch)));
                                //displayTokens.add(new Token(lineNum, i + 1, "分隔符", String.valueOf(ch)));
                            }
                            // 加号+
                            else if (ch == '+')
                                state = 1;
                                // 减号-
                            else if (ch == '-')
                                state = 2;
                                // 乘号*
                            else if (ch == '*')
                                state = 3;
                                // 除号/
                            else if (ch == '/')
                                state = 4;
                                // 赋值符号==或者等号=
                            else if (ch == '=')
                                state = 5;
                                // 小于符号<或者不等于<>
                            else if (ch == '<')
                                state = 6;
                                // 大于>
                            else if (ch == '>')
                                state = 9;
                                // 关键字或者标识符
                            else if (Match.isLetter(ch)) {
                                state = 7;
                                begin = i;
                            }
                            // 整数或者浮点数
                            else if (Match.isDigit(ch)) {
                                begin = i;
                                state = 8;
                            }
                            // 双引号"
                            else if (String.valueOf(ch).equals(Token.DQ)) {
                                begin = i + 1;
                                state = 10;
                                //node.add(new TreeNode("分隔符 ： " + ch));
                                System.out.println("\t"+"分隔符 ： " + ch);
                                result.append("分隔符 ： " + ch+"\n");
                                tokens.add(new Token(lineNum, begin, "分隔符", Token.DQ));
                                //displayTokens.add(new Token(lineNum, begin, "分隔符",Token.DQ));
                            }
                            // 空白符
                            else if (String.valueOf(ch).equals(" ")) {
                                state = 0;
                                //displayTokens.add(new Token(lineNum, i + 1, "空白符"," "));
                            }
                            // 换行符
                            else if (String.valueOf(ch).equals("\n")) {
                                state = 0;
                                //displayTokens.add(new Token(lineNum, i + 1, "换行符",
                                        "\n"));
                            }
                            // 回车符
                            else if (String.valueOf(ch).equals("\r")) {
                                state = 0;
                                //displayTokens.add(new Token(lineNum, i + 1, "回车符","\r"));
                            }
                            // 制表符
                            else if (String.valueOf(ch).equals("\t")) {
                                state = 0;
                                //displayTokens.add(new Token(lineNum, i + 1, "制表符","\t"));
                            }
                            break;
                        case 1:
                            //node.add(new TreeNode("运算符 ： " + Token.PLUS));
                            System.out.println("\t"+"运算符 ： " + Token.PLUS);
                            result.append("运算符 ： " + Token.PLUS+"\n");
                            tokens.add(new Token(lineNum, i, "运算符", Token.PLUS));
                            //displayTokens.add(new Token(lineNum, i, "运算符",Token.PLUS));
                            i--;
                            state = 0;
                            break;
                        case 2:
                            String temp = tokens.get(tokens.size() - 1).getKind();
                            String c = tokens.get(tokens.size() - 1).getContent();
                            if (temp.equals("整数") || temp.equals("标识符")
                                    || temp.equals("实数") || c.equals(")")
                                    || c.equals("]")) {
                                //node.add(new TreeNode("运算符 ： " + Token.MINUS));
                                System.out.println("\t"+"运算符 ： " + Token.PLUS);
                                result.append("运算符 ： " + Token.PLUS+"\n");
                                tokens.add(new Token(lineNum, i, "运算符",
                                        Token.MINUS));
                                //displayTokens.add(new Token(lineNum, i, "运算符",
                                        Token.MINUS));
                                i--;
                                state = 0;
                            } else if (String.valueOf(ch).equals("\n")) {
                                //displayTokens.add(new Token(lineNum, i - 1, "错误",
                                        Token.MINUS));
                            } else {
                                begin = i - 1;
                                state = 8;
                            }
                            break;
                        case 3:
                            if (ch == '/') {
                                errorInfo = "ERROR: " + lineNum + "行, " + i
                                        + "列：" + "运算符\"" + Token.TIMES
                                        + "\"使用错误  \n";
                                //node.add(new TreeNode(Token.ERROR + "运算符\"" + Token.TIMES + "\"使用错误"));
                                System.out.println(Token.ERROR + "运算符\"" + Token.TIMES + "\"使用错误");
                                result.append(Token.ERROR + "运算符\"" + Token.TIMES + "\"使用错误"+"\n");
                                errorList.add(errorInfo);
                                //displayTokens.add(new Token(lineNum, i, "错误",
                                        cmmText.substring(i - 1, i + 1)));
                            } else {
                                //node.add(new TreeNode("运算符 ： " + Token.TIMES));
                                System.out.println("\t"+"运算符 ： " + Token.TIMES);
                                result.append("运算符 ： " + Token.TIMES+"\n");
                                tokens.add(new Token(lineNum, i, "运算符",
                                        Token.TIMES));
                                //displayTokens.add(new Token(lineNum, i, "运算符",
                                        Token.TIMES));
                                i--;
                            }
                            state = 0;
                            break;
                        case 4:
                            if (ch == '/') {
                                //node.add(new TreeNode("单行注释 //"));
                                System.out.println("\t"+"单行注释 //");
                                result.append("单行注释 //" + "\n");
                                //displayTokens.add(new Token(lineNum, i, "单行注释符号",
                                        "//"));
                                begin = i + 1;
                                //displayTokens.add(new Token(lineNum, i, "注释",
                                        cmmText.substring(begin, length - 1)));
                                i = length - 2;
                                state = 0;
                            } else if (ch == '*') {
                                //node.add(new TreeNode("多行注释 /*"));
                                System.out.println("\t"+"多行注释 /*");
                                result.append("多行注释 /*" + "\n");
                                //displayTokens.add(new Token(lineNum, i, "多行注释开始符号",
                                        "/*"));
                                begin = i + 1;
                                isNotation = true;
                            } else {
                                //node.add(new TreeNode("运算符 ： " + Token.DIVIDE));
                                System.out.println("\t"+"运算符 ： " + Token.DIVIDE);
                                result.append("运算符 ： " + Token.DIVIDE + "\n");
                                tokens.add(new Token(lineNum, i, "运算符",
                                        Token.DIVIDE));
                                //displayTokens.add(new Token(lineNum, i, "运算符",
                                        Token.DIVIDE));
                                i--;
                                state = 0;
                            }
                            break;
                        case 5:
                            if (ch == '=') {
                                //node.add(new TreeNode("运算符 ： " + Token.EQUAL));
                                System.out.println("\t"+"运算符 ： " + Token.EQUAL);
                                result.append("运算符 ： " + Token.EQUAL + "\n");
                                tokens.add(new Token(lineNum, i, "运算符",
                                        Token.EQUAL));
                                //displayTokens.add(new Token(lineNum, i, "运算符",
                                        Token.EQUAL));
                                state = 0;
                            } else {
                                state = 0;
                                //node.add(new TreeNode("运算符 ： " + Token.ASSIGN));
                                System.out.println("\t"+"运算符 ： " + Token.ASSIGN);
                                result.append("运算符 ： " + Token.ASSIGN + "\n");
                                tokens.add(new Token(lineNum, i, "运算符",
                                        Token.ASSIGN));
                                //displayTokens.add(new Token(lineNum, i, "运算符",
                                        Token.ASSIGN));
                                i--;
                            }
                            break;
                        case 6:
                            if (ch == '>') {
                                //node.add(new TreeNode("运算符 ： " + Token.NEQUAL));
                                System.out.println("\t"+"运算符 ： " + Token.ASSIGN);
                                result.append("运算符 ： " + Token.ASSIGN + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.NEQUAL));
                                //displayTokens.add(new Token(lineNum, i, "运算符", Token.NEQUAL));
                                state = 0;
                            } else {
                                state = 0;
                                //node.add(new TreeNode("运算符 ： " + Token.LT));
                                System.out.println("\t"+"运算符 ： " + Token.LT);
                                result.append("运算符 ： " + Token.LT + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.LT));
                                //displayTokens.add(new Token(lineNum, i, "运算符", Token.LT));
                                i--;
                            }
                            break;
                        case 7:
                            if (Match.isLetter(ch) || Match.isDigit(ch)) {
                                state = 7;
                            } else {
                                end = i;
                                String id = cmmText.substring(begin, end);
                                if (Match.isKey(id)) {
                                    //node.add(new TreeNode("关键字 ： " + id));
                                    System.out.println("\t"+"关键字 ： " + id);
                                    result.append("关键字 ： " + id + "\n");
                                    tokens.add(new Token(lineNum, begin + 1, "关键字", id));
                                    //displayTokens.add(new Token(lineNum, begin + 1, "关键字", id));
                                } else if (Match.matchID(id)) {
                                    //node.add(new TreeNode("标识符 ： " + id));
                                    System.out.println("\t"+"标识符 ： " + id);
                                    result.append("标识符 ： " + id + "\n");
                                    tokens.add(new Token(lineNum, begin + 1, "标识符", id));
                                    //displayTokens.add(new Token(lineNum, begin + 1, "标识符", id));
                                } else {
                                    errorInfo = "ERROR: " + lineNum + "行, " + (begin + 1) + "列：" + id + "是非法标识符\n";
                                    //node.add(new TreeNode(Token.ERROR + id + "是非法标识符"));
                                    System.out.println(Token.ERROR + id + "是非法标识符");
                                    result.append(Token.ERROR + id + "是非法标识符" + "\n");
                                    errorList.add(errorInfo);
                                    //displayTokens.add(new Token(lineNum, begin + 1, "错误", id));
                                }
                                i--;
                                state = 0;
                            }
                            break;
                        case 8:
                            if (Match.isDigit(ch) || String.valueOf(ch).equals(".")) {
                                state = 8;
                            } else {
                                if (Match.isLetter(ch)) {
                                    errorInfo = "ERROR: " + lineNum + "行, "
                                            + i + "列：" + "数字格式错误或者标志符错误\n";
                                    //node.add(new TreeNode(Token.ERROR + "数字格式错误或者标志符错误"));
                                    System.out.println(Token.ERROR + "数字格式错误或者标志符错误");
                                    result.append(Token.ERROR + "数字格式错误或者标志符错误" + "\n");
                                    errorList.add(errorInfo);
                                    //displayTokens.add(new Token(lineNum, i, "错误",
                                            cmmText.substring(begin, Match.find(begin,
                                                    cmmText) + 1)));
                                    i = Match.find(begin, cmmText);
                                } else {
                                    end = i;
                                    String id = cmmText.substring(begin, end);
                                    if (!id.contains(".")) {
                                        if (Match.matchInteger(id)) {
                                            //node.add(new TreeNode("整数    ： " + id));
                                            System.out.println("\t"+"整数    ： " + id);
                                            result.append("整数    ： " + id + "\n");
                                            tokens.add(new Token(lineNum,
                                                    begin + 1, "整数", id));
                                            //displayTokens.add(new Token(lineNum,
                                                    begin + 1, "整数", id));
                                        } else {
                                            errorInfo = "ERROR: " + lineNum
                                                    + "行, " + (begin + 1) + "列："
                                                    + id + "是非法整数\n";
                                            //node.add(new TreeNode(Token.ERROR + id + "是非法整数"));
                                            System.out.println(Token.ERROR + id + "是非法整数");
                                            result.append(Token.ERROR + id + "是非法整数" + "\n");
                                            errorList.add(errorInfo);
                                            //displayTokens.add(new Token(lineNum,
                                                    begin + 1, "错误", id));
                                        }
                                    } else {
                                        if (Match.matchDouble(id)) {
                                            //node.add(new TreeNode("实数    ： " + id));
                                            System.out.println("\t"+"实数    ： " + id);
                                            result.append("实数    ： " + id + "\n");
                                            tokens.add(new Token(lineNum,
                                                    begin + 1, "实数", id));
                                            //displayTokens.add(new Token(lineNum,
                                                    begin + 1, "实数", id));
                                        } else {
                                            errorInfo = "ERROR: " + lineNum
                                                    + "行, " + (begin + 1) + "列："
                                                    + id + "是非法实数\n";
                                            //node.add(new TreeNode(Token.ERROR + id + "是非法实数"));
                                            System.out.println(Token.ERROR + id + "是非法实数");
                                            result.append(Token.ERROR + id + "是非法实数" + "\n");
                                            errorList.add(errorInfo);
                                            //displayTokens.add(new Token(lineNum,
                                                    begin + 1, "错误", id));
                                        }
                                    }
                                    i = Match.find(i, cmmText);
                                }
                                state = 0;
                            }
                            break;
                        case 9:
                            //node.add(new TreeNode("运算符 ： " + Token.GT));
                            System.out.println("\t"+"运算符 ： " + Token.GT);
                            result.append("运算符 ： " + Token.GT + "\n");
                            tokens.add(new Token(lineNum, i, "运算符", Token.GT));
                            //displayTokens.add(new Token(lineNum, i, "运算符",
                                    Token.GT));
                            i--;
                            state = 0;
                            break;
                        case 10:
                            if (ch == '"') {
                                end = i;
                                String string = cmmText.substring(begin, end);
                                //node.add(new TreeNode("字符串 ： " + string));
                                System.out.println("\t"+"字符串 ： " + string);
                                result.append("字符串 ： " + string + "\n");
                                tokens.add(new Token(lineNum, begin + 1, "字符串", string));
                                //displayTokens.add(new Token(lineNum, begin + 1, "字符串", string));
                                //node.add(new TreeNode("分隔符 ： " + Token.DQ));
                                System.out.println("\t"+"分隔符 ： " + Token.DQ);
                                result.append("分隔符 ： " + Token.DQ + "\n");
                                tokens.add(new Token(lineNum, end + 1, "分隔符", Token.DQ));
                                //displayTokens.add(new Token(lineNum, end + 1, "分隔符", Token.DQ));
                                state = 0;
                            } else if (i == length - 1) {
                                String string = cmmText.substring(begin);
                                errorInfo = "ERROR: " + lineNum + "行, "
                                        + (begin + 1) + "列：" + "字符串 " + string
                                        + " 缺少引号  \n";
                                //node.add(new TreeNode(Token.ERROR + "字符串 " + string + " 缺少引号  \n"));
                                System.out.println(Token.ERROR + "字符串 " + string + " 缺少引号  \n");
                                result.append(Token.ERROR + "字符串 " + string + " 缺少引号  \n");
                                errorList.add(errorInfo);
                                //displayTokens.add(new Token(lineNum, i + 1, "错误",
                                        string));
                            }
                    }
                } else {
                    if (ch > 19967 && ch < 40870 || ch == '\\' || ch == '~'
                            || ch == '`' || ch == '|' || ch == '、' || ch == '^'
                            || ch == '?' || ch == '&' || ch == '^' || ch == '%'
                            || ch == '$' || ch == '@' || ch == '!' || ch == '#'
                            || ch == '；' || ch == '【' || ch == '】' || ch == '，'
                            || ch == '。' || ch == '“' || ch == '”' || ch == '‘'
                            || ch == '’' || ch == '？' || ch == '（' || ch == '）'
                            || ch == '《' || ch == '》' || ch == '·') {
                        errorInfo = "ERROR: " + lineNum + "行, "
                                + (i + 1) + "列：" + "\"" + ch + "\"是不可识别符号  \n";
                        //node.add(new TreeNode(Token.ERROR + "\"" + ch+ "\"是不可识别符号"));
                        System.out.println(Token.ERROR + "\"" + ch+ "\"是不可识别符号");
                        result.append(Token.ERROR + "\"" + ch+ "\"是不可识别符号" + "\n");
                        errorList.add(errorInfo);
                        if (state == 0)
                            //displayTokens.add(new Token(lineNum, i + 1, "错误",
                                    String.valueOf(ch)));
                    }
                }
            } else {
                if (ch == '*') {
                    state = 3;
                } else if (ch == '/' && state == 3) {
                    //node.add(new TreeNode("多行注释 */"));
                    System.out.println("\t"+"多行注释 */");
                    result.append("多行注释 */" + "\n");
                    //displayTokens.add(new Token(lineNum, begin + 1, "注释", cmmText.substring(begin, i - 1)));
                    //displayTokens.add(new Token(lineNum, i, "多行注释结束符号", "*/"));
                    state = 0;
                    isNotation = false;
                } else if (i == length - 2) {
                    //displayTokens.add(new Token(lineNum, begin + 1, "注释", cmmText.substring(begin, length - 1)));
                    //displayTokens.add(new Token(lineNum, length - 1, "换行符", "\n"));
                    state = 0;
                } else {
                    state = 0;
                }
            }
        }
    }


    public void execute(String cmmText) {
        setErrorInfo("");
        setTokens(new ArrayList<Token>());
        setDisplayTokens(new ArrayList<Token>());
        setNotation(false);
        StringReader stringReader = new StringReader(cmmText);
        String eachLine = "";
        int lineNum = 1;
        reader = new BufferedReader(stringReader);
        while (eachLine != null) {
            try {
                eachLine = reader.readLine();
                if (eachLine != null) {
                    if (isNotation() && !eachLine.contains("*/")) {
                        eachLine += "\n";
                        System.out.println("\t"+"多行注释");
                        result.append("多行注释" + "\n");
                        //displayTokens.add(new Token(lineNum, 1, "注释", eachLine
                                .substring(0, eachLine.length() - 1)));
                        //displayTokens.add(new Token(lineNum,
                                eachLine.length() - 1, "换行符", "\n"));
                        //root.add(temp);
                        lineNum++;
                        continue;
                    } else {
                        executeLine(eachLine, lineNum);
                    }
                }
                lineNum++;
            } catch (IOException e) {
                System.err.println("读取文本时出错了！");
            }
        }
        System.out.println("\n错误总数: "+errorList.size()+"个"+"\n");
        result.append("\n错误总数: "+errorList.size()+"个"+"\n");
        for (int i = 0; i < errorList.size(); i++) {
            System.out.print(errorList.get(i));
        }
        for (int i = 0; i < tokens.size(); i++) {
            Token temp = tokens.get(i);
            System.out.println(temp.getLine()+" "+temp.getCulomn()+" "+temp.getKind()+" "+temp.getContent());
            //result.append(temp.getLine()+" "+temp.getCulomn()+" "+temp.getKind()+" "+temp.getContent() + "\n");
        }
    }
}