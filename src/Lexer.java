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
    // 存储所有词法分析的错误
    private ArrayList<String> errorList = new ArrayList<>();
    // 读取CMM文件文本
    private BufferedReader reader;
    // 用于输出语法分析的结果
    private StringBuilder result = new StringBuilder();

    // 标志是否存在词法错误
    private boolean hasError = false;

    public boolean getHasError() {
        return hasError;
    }

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

    
    //执行
    private void executeLine(String cmmText, int lineNum) {
        // 创建当前行根结点
        String content = "第" + lineNum + "行： " + cmmText;
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
        // 逐个读取当前行字符
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
                            // 分隔符
                            if (ch == '(' || ch == ')' || ch == ';' || ch == '{'
                                    || ch == '}' || ch == '[' || ch == ']'
                                    || ch == ',') {
                                state = 0;
                                System.out.println("\t"+"分隔符 ： " + ch);
                                result.append("分隔符 ： " + ch +"\n");
                                tokens.add(new Token(lineNum, i + 1, "分隔符", String.valueOf(ch)));
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
                                System.out.println("\t"+"分隔符 ： " + ch);
                                result.append("分隔符 ： " + ch+"\n");
                                tokens.add(new Token(lineNum, begin, "分隔符", Token.DQ));
                            }
                            else if (String.valueOf(ch).equals(" ")
                                    ||String.valueOf(ch).equals("\n")
                                    ||String.valueOf(ch).equals("\r")
                                    ||String.valueOf(ch).equals("\t")) {
                                state = 0;
                            }
                            break;
                        case 1:
                            System.out.println("\t"+"运算符 ： " + Token.PLUS);
                            result.append("运算符 ： " + Token.PLUS+"\n");
                            tokens.add(new Token(lineNum, i, "运算符", Token.PLUS));
                            i--;
                            state = 0;
                            break;
                        case 2:
                            String temp = tokens.get(tokens.size() - 1).getKind();
                            String c = tokens.get(tokens.size() - 1).getContent();
                            if (temp.equals("整数") || temp.equals("标识符") || temp.equals("实数") || c.equals(")") || c.equals("]")) {
                                System.out.println("\t"+"运算符 ： " + Token.MINUS);
                                result.append("运算符 ： " + Token.MINUS+"\n");
                                tokens.add(new Token(lineNum, i, "运算符",
                                        Token.MINUS));
                                i--;
                                state = 0;
                            } else {
                                begin = i - 1;
                                state = 8;
                            }
                            break;
                        case 3:
                            if (ch == '/') {
                                errorInfo = "ERROR: " + lineNum + "行, " + i + "列：" + "运算符\"" + Token.TIMES + "\"使用错误  \n";
                                System.out.println(Token.ERROR + "运算符\"" + Token.TIMES + "\"使用错误");
                                result.append(Token.ERROR + "运算符\"" + Token.TIMES + "\"使用错误"+"\n");
                                errorList.add(errorInfo);
                            } else {
                                System.out.println("\t"+"运算符 ： " + Token.TIMES);
                                result.append("运算符 ： " + Token.TIMES+"\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.TIMES));
                                i--;
                            }
                            state = 0;
                            break;
                        case 4:
                            if (ch == '/') {
                                System.out.println("\t"+"单行注释 //");
                                result.append("单行注释 //" + "\n");
                                begin = i + 1;
                                i = length - 2;
                                state = 0;
                            } else if (ch == '*') {
                                System.out.println("\t"+"多行注释 /*");
                                result.append("多行注释 /*" + "\n");
                                begin = i + 1;
                                isNotation = true;
                            } else {
                                System.out.println("\t"+"运算符 ： " + Token.DIVIDE);
                                result.append("运算符 ： " + Token.DIVIDE + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.DIVIDE));
                                i--;
                                state = 0;
                            }
                            break;
                        case 5:
                            if (ch == '=') {
                                System.out.println("\t"+"运算符 ： " + Token.EQUAL);
                                result.append("运算符 ： " + Token.EQUAL + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.EQUAL));
                                state = 0;
                            } else {
                                state = 0;
                                System.out.println("\t"+"运算符 ： " + Token.ASSIGN);
                                result.append("运算符 ： " + Token.ASSIGN + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.ASSIGN));
                                i--;
                            }
                            break;
                        case 6:
                            if (ch == '>') {
                                System.out.println("\t"+"运算符 ： " + Token.NEQUAL);
                                result.append("运算符 ： " + Token.NEQUAL + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.NEQUAL));
                                state = 0;
                            } else {
                                state = 0;
                                System.out.println("\t"+"运算符 ： " + Token.LT);
                                result.append("运算符 ： " + Token.LT + "\n");
                                tokens.add(new Token(lineNum, i, "运算符", Token.LT));
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
                                    System.out.println("\t"+"关键字 ： " + id);
                                    result.append("关键字 ： " + id + "\n");
                                    tokens.add(new Token(lineNum, begin + 1, "关键字", id));
                                } else if (Match.matchID(id)) {
                                    System.out.println("\t"+"标识符 ： " + id);
                                    result.append("标识符 ： " + id + "\n");
                                    tokens.add(new Token(lineNum, begin + 1, "标识符", id));
                                } else {
                                    errorInfo = "ERROR: " + lineNum + "行, " + (begin + 1) + "列：" + id + "是非法标识符\n";
                                    System.out.println(Token.ERROR + id + "是非法标识符");
                                    result.append(Token.ERROR + id + "是非法标识符" + "\n");
                                    errorList.add(errorInfo);
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
                                    errorInfo = "ERROR: " + lineNum + "行, " + i + "列：" + "数字格式错误或者标志符错误\n";
                                    System.out.println(Token.ERROR + "数字格式错误或者标志符错误");
                                    result.append(Token.ERROR + "数字格式错误或者标志符错误" + "\n");
                                    errorList.add(errorInfo);
                                    i = Match.find(begin, cmmText);
                                } else {
                                    end = i;
                                    String id = cmmText.substring(begin, end);
                                    if (!id.contains(".")) {
                                        if (Match.matchInteger(id)) {
                                            System.out.println("\t"+"整数    ： " + id);
                                            result.append("整数    ： " + id + "\n");
                                            tokens.add(new Token(lineNum, begin + 1, "整数", id));
                                        } else {
                                            errorInfo = "ERROR: " + lineNum + "行, " + (begin + 1) + "列：" + id + "是非法整数\n";
                                            System.out.println(Token.ERROR + id + "是非法整数");
                                            result.append(Token.ERROR + id + "是非法整数" + "\n");
                                            errorList.add(errorInfo);
                                        }
                                    } else {
                                        if (Match.matchDouble(id)) {
                                            System.out.println("\t"+"实数    ： " + id);
                                            result.append("实数    ： " + id + "\n");
                                            tokens.add(new Token(lineNum, begin + 1, "实数", id));
                                        } else {
                                            errorInfo = "ERROR: " + lineNum + "行, " + (begin + 1) + "列：" + id + "是非法实数\n";
                                            System.out.println(Token.ERROR + id + "是非法实数");
                                            result.append(Token.ERROR + id + "是非法实数" + "\n");
                                            errorList.add(errorInfo);
                                        }
                                    }
                                    i = Match.find(i, cmmText);
                                }
                                state = 0;
                            }
                            break;
                        case 9:
                            System.out.println("\t"+"运算符 ： " + Token.GT);
                            result.append("运算符 ： " + Token.GT + "\n");
                            tokens.add(new Token(lineNum, i, "运算符", Token.GT));
                            i--;
                            state = 0;
                            break;
                        case 10:
                            if (ch == '"') {
                                end = i;
                                String string = cmmText.substring(begin, end);
                                System.out.println("\t"+"字符串 ： " + string);
                                result.append("字符串 ： " + string + "\n");
                                tokens.add(new Token(lineNum, begin + 1, "字符串", string));
                                System.out.println("\t"+"分隔符 ： " + Token.DQ);
                                result.append("分隔符 ： " + Token.DQ + "\n");
                                tokens.add(new Token(lineNum, end + 1, "分隔符", Token.DQ));
                                state = 0;
                            } else if (i == length - 1) {
                                String string = cmmText.substring(begin);
                                errorInfo = "ERROR: " + lineNum + "行, " + (begin + 1) + "列：" + "字符串 " + string + " 缺少引号  \n";
                                System.out.println(Token.ERROR + "字符串 " + string + " 缺少引号  \n");
                                result.append(Token.ERROR + "字符串 " + string + " 缺少引号  \n");
                                errorList.add(errorInfo);
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
                        errorInfo = "ERROR: " + lineNum + "行, " + (i + 1) + "列：" + "\"" + ch + "\"是不可识别符号  \n";
                        System.out.println(Token.ERROR + "\"" + ch+ "\"是不可识别符号");
                        result.append(Token.ERROR + "\"" + ch+ "\"是不可识别符号" + "\n");
                        errorList.add(errorInfo);
                    }
                }
            } else {
                if (ch == '*') {
                    state = 3;
                } else if (ch == '/' && state == 3) {
                    System.out.println("\t"+"多行注释 */");
                    result.append("多行注释 */" + "\n");
                    state = 0;
                    isNotation = false;
                } else if (i == length - 2) {
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
        //setDisplayTokens(new ArrayList<Token>());
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
        for (String anErrorList : errorList) {
            System.out.print(anErrorList);
            result.append(anErrorList);
        }
        if (errorList.size()!=0) hasError = true;
    }
}