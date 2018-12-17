import java.util.ArrayList;

/**
 * CMM语法分析
 *
 * @author Leeham
 *
 */
public class CMMParser {

    // 词法分析得到的tokens向量
    private ArrayList<Token> tokens;
    // 标记当前token的游标
    private int index = 0;
    // 存放当前token的值
    private Token currentToken = null;
    // 错误个数
    private int errorNum = 0;
    // 错误信息
    private String errorInfo = "";
    // 语法分析根结点
    private static TreeNode root;

    public CMMParser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        if (tokens.size() != 0)
            currentToken = tokens.get(0);
    }

    public int getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public TreeNode execute() {
        root = new TreeNode("PROGRAM");
        for (; index < tokens.size();) {
            root.add(statement());
        }
        return root;
    }


    private void nextToken() {
        index++;
        if (index > tokens.size() - 1) {
            currentToken = null;
            if (index > tokens.size())
                index--;
            return;
        }
        currentToken = tokens.get(index);
    }


    private void error(String error) {
        String line = "    ERROR:第 ";
        Token previous = tokens.get(index - 1);
        if (currentToken != null
                && currentToken.getLine() == previous.getLine()) {
            line += currentToken.getLine() + " 行,第 " + currentToken.getCulomn()
                    + " 列：";
        } else
            line += previous.getLine() + " 行,第 " + previous.getCulomn() + " 列：";
        errorInfo += line + error;
        errorNum++;
    }


    private final TreeNode statement() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        // 赋值语句
        if (currentToken != null && currentToken.getKind().equals("标识符")) {
            tempNode = assign_stm(false);
        }
        // 声明语句
        else if (currentToken != null
                && (currentToken.getContent().equals(Token.INT)
                || currentToken.getContent().equals(Token.DOUBLE) || currentToken
                .getContent().equals(Token.BOOL))
                || currentToken.getContent().equals(Token.STRING)) {
            tempNode = declare_stm();
        }
        // For循环语句
        else if (currentToken != null
                && currentToken.getContent().equals(Token.FOR)) {
            tempNode = for_stm();
        }
        // If条件语句
        else if (currentToken != null
                && currentToken.getContent().equals(Token.IF)) {
            tempNode = if_stm();
        }
        // While循环语句
        else if (currentToken != null
                && currentToken.getContent().equals(Token.WHILE)) {
            tempNode = while_stm();
        }
        // read语句
        else if (currentToken != null
                && currentToken.getContent().equals(Token.READ)) {
            TreeNode readNode = new TreeNode("关键字", Token.READ, currentToken
                    .getLine());
            readNode.add(read_stm());
            tempNode = readNode;
        }
        // write语句
        else if (currentToken != null
                && currentToken.getContent().equals(Token.WRITE)) {
            TreeNode writeNode = new TreeNode("关键字", Token.WRITE,
                    currentToken.getLine());
            writeNode.add(write_stm());
            tempNode = writeNode;
        }
        // 出错处理
        else {
            String error = " 语句以错误的token开始" + "\n";
            error(error);
            tempNode = new TreeNode(Token.ERROR + "语句以错误的token开始");
            nextToken();
        }
        return tempNode;
    }


    private final TreeNode for_stm() {
        // 是否有大括号,默认为true
        boolean hasBrace = true;
        // if函数返回结点的根结点
        TreeNode forNode = new TreeNode("关键字", "for", currentToken.getLine());
        nextToken();
        // 匹配左括号(
        if (currentToken != null
                && currentToken.getContent().equals(Token.LPAREN)) {
            nextToken();
        } else { // 报错
            String error = " for循环语句缺少左括号\"(\"" + "\n";
            error(error);
            forNode.add(new TreeNode(Token.ERROR + "for循环语句缺少左括号\"(\""));
        }
        // initialization
        TreeNode initializationNode = new TreeNode("initialization",
                "Initialization", currentToken.getLine());
        initializationNode.add(assign_stm(true));
        forNode.add(initializationNode);
        // 匹配分号;
        if (currentToken != null
                && currentToken.getContent().equals(Token.SEMICOLON)) {
            nextToken();
        } else {
            String error = " for循环语句缺少分号\";\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "for循环语句缺少分号\";\"");
        }
        // condition
        TreeNode conditionNode = new TreeNode("condition", "Condition",
                currentToken.getLine());
        conditionNode.add(condition());
        forNode.add(conditionNode);
        // 匹配分号;
        if (currentToken != null
                && currentToken.getContent().equals(Token.SEMICOLON)) {
            nextToken();
        } else {
            String error = " for循环语句缺少分号\";\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "for循环语句缺少分号\";\"");
        }
        // change
        TreeNode changeNode = new TreeNode("change", "Change", currentToken
                .getLine());
        changeNode.add(assign_stm(true));
        forNode.add(changeNode);
        // 匹配右括号)
        if (currentToken != null
                && currentToken.getContent().equals(Token.RPAREN)) {
            nextToken();
        } else { // 报错
            String error = " if条件语句缺少右括号\")\"" + "\n";
            error(error);
            forNode.add(new TreeNode(Token.ERROR + "if条件语句缺少右括号\")\""));
        }
        // 匹配左大括号{
        if (currentToken != null
                && currentToken.getContent().equals(Token.LBRACE)) {
            nextToken();
        } else {
            hasBrace = false;
        }
        // statement
        TreeNode statementNode = new TreeNode("statement", "Statements",
                currentToken.getLine());
        forNode.add(statementNode);
        if(hasBrace) {
            while (currentToken != null) {
                if (!currentToken.getContent().equals(Token.RBRACE))
                    statementNode.add(statement());
                else if (statementNode.getChildCount() == 0) {
                    forNode.remove(forNode.getChildCount() - 1);
                    statementNode.setContent("EmptyStm");
                    forNode.add(statementNode);
                    break;
                } else {
                    break;
                }
            }
            // 匹配右大括号}
            if (currentToken != null
                    && currentToken.getContent().equals(Token.RBRACE)) {
                nextToken();
            } else { // 报错
                String error = " if条件语句缺少右大括号\"}\"" + "\n";
                error(error);
                forNode.add(new TreeNode(Token.ERROR + "if条件语句缺少右大括号\"}\""));
            }
        } else {
            statementNode.add(statement());
        }
        return forNode;
    }


    private final TreeNode if_stm() {
        // if语句是否有大括号,默认为true
        boolean hasIfBrace = true;
        // else语句是否有大括号,默认为true
        boolean hasElseBrace = true;
        // if函数返回结点的根结点
        TreeNode ifNode = new TreeNode("关键字", "if", currentToken.getLine());
        nextToken();
        // 匹配左括号(
        if (currentToken != null
                && currentToken.getContent().equals(Token.LPAREN)) {
            nextToken();
        } else { // 报错
            String error = " if条件语句缺少左括号\"(\"" + "\n";
            error(error);
            ifNode.add(new TreeNode(Token.ERROR + "if条件语句缺少左括号\"(\""));
        }
        // condition
        TreeNode conditionNode = new TreeNode("condition", "Condition",
                currentToken.getLine());
        ifNode.add(conditionNode);
        conditionNode.add(condition());
        // 匹配右括号)
        if (currentToken != null
                && currentToken.getContent().equals(Token.RPAREN)) {
            nextToken();
        } else { // 报错
            String error = " if条件语句缺少右括号\")\"" + "\n";
            error(error);
            ifNode.add(new TreeNode(Token.ERROR + "if条件语句缺少右括号\")\""));
        }
        // 匹配左大括号{
        if (currentToken != null
                && currentToken.getContent().equals(Token.LBRACE)) {
            nextToken();
        } else {
            hasIfBrace = false;
        }
        // statement
        TreeNode statementNode = new TreeNode("statement", "Statements",
                currentToken.getLine());
        ifNode.add(statementNode);
        if (hasIfBrace) {
            while (currentToken != null) {
                if (!currentToken.getContent().equals(Token.RBRACE))
                    statementNode.add(statement());
                else if (statementNode.getChildCount() == 0) {
                    ifNode.remove(ifNode.getChildCount() - 1);
                    statementNode.setContent("EmptyStm");
                    ifNode.add(statementNode);
                    break;
                } else {
                    break;
                }
            }
            // 匹配右大括号}
            if (currentToken != null
                    && currentToken.getContent().equals(Token.RBRACE)) {
                nextToken();
            } else { // 报错
                String error = " if条件语句缺少右大括号\"}\"" + "\n";
                error(error);
                ifNode.add(new TreeNode(Token.ERROR + "if条件语句缺少右大括号\"}\""));
            }
        } else {
            if (currentToken != null)
                statementNode.add(statement());
        }
        if (currentToken != null
                && currentToken.getContent().equals(Token.ELSE)) {
            TreeNode elseNode = new TreeNode("关键字", Token.ELSE, currentToken
                    .getLine());
            ifNode.add(elseNode);
            nextToken();
            // 匹配左大括号{
            if (currentToken.getContent().equals(Token.LBRACE)) {
                nextToken();
            } else {
                hasElseBrace = false;
            }
            if (hasElseBrace) {
                // statement
                while (currentToken != null
                        && !currentToken.getContent().equals(Token.RBRACE)) {
                    elseNode.add(statement());
                }
                // 匹配右大括号}
                if (currentToken != null
                        && currentToken.getContent().equals(Token.RBRACE)) {
                    nextToken();
                } else { // 报错
                    String error = " else语句缺少右大括号\"}\"" + "\n";
                    error(error);
                    elseNode.add(new TreeNode(Token.ERROR
                            + "else语句缺少右大括号\"}\""));
                }
            } else {
                if (currentToken != null)
                    elseNode.add(statement());
            }
        }
        return ifNode;
    }


    private final TreeNode while_stm() {
        // 是否有大括号,默认为true
        boolean hasBrace = true;
        // while函数返回结点的根结点
        TreeNode whileNode = new TreeNode("关键字", Token.WHILE, currentToken
                .getLine());
        nextToken();
        // 匹配左括号(
        if (currentToken != null
                && currentToken.getContent().equals(Token.LPAREN)) {
            nextToken();
        } else { // 报错
            String error = " while循环缺少左括号\"(\"" + "\n";
            error(error);
            whileNode.add(new TreeNode(Token.ERROR + "while循环缺少左括号\"(\""));
        }
        // condition
        TreeNode conditionNode = new TreeNode("condition", "Condition",
                currentToken.getLine());
        whileNode.add(conditionNode);
        conditionNode.add(condition());
        // 匹配右括号)
        if (currentToken != null
                && currentToken.getContent().equals(Token.RPAREN)) {
            nextToken();
        } else { // 报错
            String error = " while循环缺少右括号\")\"" + "\n";
            error(error);
            whileNode.add(new TreeNode(Token.ERROR + "while循环缺少右括号\")\""));
        }
        // 匹配左大括号{
        if (currentToken != null
                && currentToken.getContent().equals(Token.LBRACE)) {
            nextToken();
        } else {
            hasBrace = false;
        }
        // statement
        TreeNode statementNode = new TreeNode("statement", "Statements",
                currentToken.getLine());
        whileNode.add(statementNode);
        if(hasBrace) {
            while (currentToken != null
                    && !currentToken.getContent().equals(Token.RBRACE)) {
                if (!currentToken.getContent().equals(Token.RBRACE))
                    statementNode.add(statement());
                else if (statementNode.getChildCount() == 0) {
                    whileNode.remove(whileNode.getChildCount() - 1);
                    statementNode.setContent("EmptyStm");
                    whileNode.add(statementNode);
                    break;
                } else {
                    break;
                }
            }
            // 匹配右大括号}
            if (currentToken != null
                    && currentToken.getContent().equals(Token.RBRACE)) {
                nextToken();
            } else { // 报错
                String error = " while循环缺少右大括号\"}\"" + "\n";
                error(error);
                whileNode.add(new TreeNode(Token.ERROR + "while循环缺少右大括号\"}\""));
            }
        } else {
            if(currentToken != null)
                statementNode.add(statement());
        }
        return whileNode;
    }


    private final TreeNode read_stm() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        nextToken();
        // 匹配左括号(
        if (currentToken != null
                && currentToken.getContent().equals(Token.LPAREN)) {
            nextToken();
        } else {
            String error = " read语句缺少左括号\"(\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "read语句缺少左括号\"(\"");
        }
        // 匹配标识符
        if (currentToken != null && currentToken.getKind().equals("标识符")) {
            tempNode = new TreeNode("标识符", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
            // 判断是否是为数组赋值
            if (currentToken != null
                    && currentToken.getContent().equals(Token.LBRACKET)) {
                tempNode.add(array());
            }
        } else {
            String error = " read语句左括号后不是标识符" + "\n";
            error(error);
            nextToken();
            return new TreeNode(Token.ERROR + "read语句左括号后不是标识符");
        }
        // 匹配右括号)
        if (currentToken != null
                && currentToken.getContent().equals(Token.RPAREN)) {
            nextToken();
        } else {
            String error = " read语句缺少右括号\")\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "read语句缺少右括号\")\"");
        }
        // 匹配分号;
        if (currentToken != null
                && currentToken.getContent().equals(Token.SEMICOLON)) {
            nextToken();
        } else {
            String error = " read语句缺少分号\";\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "read语句缺少分号\";\"");
        }
        return tempNode;
    }


    private final TreeNode write_stm() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        nextToken();
        // 匹配左括号(
        if (currentToken != null
                && currentToken.getContent().equals(Token.LPAREN)) {
            nextToken();
        } else {
            String error = " write语句缺少左括号\"(\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "write语句缺少左括号\"(\"");
        }
        // 调用expression函数匹配表达式
        tempNode = expression();
        // 匹配右括号)
        if (currentToken != null
                && currentToken.getContent().equals(Token.RPAREN)) {
            nextToken();
        } else {
            String error = " write语句缺少右括号\")\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "write语句缺少右括号\")\"");
        }
        // 匹配分号;
        if (currentToken != null
                && currentToken.getContent().equals(Token.SEMICOLON)) {
            nextToken();
        } else {
            String error = " write语句缺少分号\";\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "write语句缺少分号\";\"");
        }
        return tempNode;
    }


    private final TreeNode assign_stm(boolean isFor) {
        // assign函数返回结点的根结点
        TreeNode assignNode = new TreeNode("运算符", Token.ASSIGN, currentToken
                .getLine());
        TreeNode idNode = new TreeNode("标识符", currentToken.getContent(),
                currentToken.getLine());
        assignNode.add(idNode);
        nextToken();
        // 判断是否是为数组赋值
        if (currentToken != null
                && currentToken.getContent().equals(Token.LBRACKET)) {
            idNode.add(array());
        }
        // 匹配赋值符号=
        if (currentToken != null
                && currentToken.getContent().equals(Token.ASSIGN)) {
            nextToken();
        } else { // 报错
            String error = " 赋值语句缺少\"=\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "赋值语句缺少\"=\"");
        }
        // expression
        assignNode.add(condition());
        // 如果不是在for循环语句中调用声明语句,则匹配分号
        if (!isFor) {
            // 匹配分号;
            if (currentToken != null
                    && currentToken.getContent().equals(Token.SEMICOLON)) {
                nextToken();
            } else { // 报错
                String error = " 赋值语句缺少分号\";\"" + "\n";
                error(error);
                assignNode.add(new TreeNode(Token.ERROR + "赋值语句缺少分号\";\""));
            }
        }
        return assignNode;
    }


    private final TreeNode declare_stm() {
        TreeNode declareNode = new TreeNode("关键字", currentToken.getContent(),
                currentToken.getLine());
        nextToken();
        // declare_aid
        declareNode = declare_aid(declareNode);
        // 处理同时声明多个变量的情况
        String next = null;
        while (currentToken != null) {
            next = currentToken.getContent();
            if (next.equals(Token.COMMA)) {
                nextToken();
                declareNode = declare_aid(declareNode);
            } else {
                break;
            }
            if (currentToken != null)
                next = currentToken.getContent();
        }
        // 匹配分号;
        if (currentToken != null
                && currentToken.getContent().equals(Token.SEMICOLON)) {
            nextToken();
        } else { // 报错
            String error = " 声明语句缺少分号\";\"" + "\n";
            error(error);
            declareNode.add(new TreeNode(Token.ERROR + "声明语句缺少分号\";\""));
        }
        return declareNode;
    }


    private final TreeNode declare_aid(TreeNode root) {
        if (currentToken != null && currentToken.getKind().equals("标识符")) {
            TreeNode idNode = new TreeNode("标识符", currentToken.getContent(),
                    currentToken.getLine());
            root.add(idNode);
            nextToken();
            // 处理array的情况
            if (currentToken != null
                    && currentToken.getContent().equals(Token.LBRACKET)) {
                idNode.add(array());
            } else if (currentToken != null
                    && !currentToken.getContent().equals(Token.ASSIGN)
                    && !currentToken.getContent().equals(Token.SEMICOLON)
                    && !currentToken.getContent().equals(Token.COMMA)) {
                String error = " 声明语句出错,标识符后出现不正确的token" + "\n";
                error(error);
                root
                        .add(new TreeNode(Token.ERROR
                                + "声明语句出错,标识符后出现不正确的token"));
                nextToken();
            }
        } else { // 报错
            String error = " 声明语句中标识符出错" + "\n";
            error(error);
            root.add(new TreeNode(Token.ERROR + "声明语句中标识符出错"));
            nextToken();
        }
        // 匹配赋值符号=
        if (currentToken != null
                && currentToken.getContent().equals(Token.ASSIGN)) {
            TreeNode assignNode = new TreeNode("分隔符", Token.ASSIGN,
                    currentToken.getLine());
            root.add(assignNode);
            nextToken();
            assignNode.add(condition());
        }
        return root;
    }


    private final TreeNode condition() {
        // 记录expression生成的结点
        TreeNode tempNode = expression();
        // 如果条件判断为比较表达式
        if (currentToken != null
                && (currentToken.getContent().equals(Token.EQUAL)
                || currentToken.getContent().equals(Token.NEQUAL)
                || currentToken.getContent().equals(Token.LT) || currentToken
                .getContent().equals(Token.GT))) {
            TreeNode comparisonNode = comparison_op();
            comparisonNode.add(tempNode);
            comparisonNode.add(expression());
            return comparisonNode;
        }
        // 如果条件判断为bool变量
        return tempNode;
    }


    private final TreeNode expression() {
        // 记录term生成的结点
        TreeNode tempNode = term();

        // 如果下一个token为加号或减号
        while (currentToken != null
                && (currentToken.getContent().equals(Token.PLUS) || currentToken
                .getContent().equals(Token.MINUS))) {
            // add_op
            TreeNode addNode = add_op();
            addNode.add(tempNode);
            tempNode = addNode;
            tempNode.add(term());
        }
        return tempNode;
    }


    private final TreeNode term() {
        // 记录factor生成的结点
        TreeNode tempNode = factor();

        // 如果下一个token为乘号或除号
        while (currentToken != null
                && (currentToken.getContent().equals(Token.TIMES) || currentToken
                .getContent().equals(Token.DIVIDE))) {
            // mul_op
            TreeNode mulNode = mul_op();
            mulNode.add(tempNode);
            tempNode = mulNode;
            tempNode.add(factor());
        }
        return tempNode;
    }


    private final TreeNode factor() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null && currentToken.getKind().equals("整数")) {
            tempNode = new TreeNode("整数", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
        } else if (currentToken != null && currentToken.getKind().equals("实数")) {
            tempNode = new TreeNode("实数", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.TRUE)) {
            tempNode = new TreeNode("布尔值", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.FALSE)) {
            tempNode = new TreeNode("布尔值", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
        } else if (currentToken != null && currentToken.getKind().equals("标识符")) {
            tempNode = new TreeNode("标识符", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
            // array
            if (currentToken != null
                    && currentToken.getContent().equals(Token.LBRACKET)) {
                tempNode.add(array());
            }
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.LPAREN)) { // 匹配左括号(
            nextToken();
            tempNode = expression();
            // 匹配右括号)
            if (currentToken != null
                    && currentToken.getContent().equals(Token.RPAREN)) {
                nextToken();
            } else { // 报错
                String error = " 算式因子缺少右括号\")\"" + "\n";
                error(error);
                return new TreeNode(Token.ERROR + "算式因子缺少右括号\")\"");
            }
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.DQ)) { // 匹配双引号
            nextToken();
            tempNode = new TreeNode("字符串", currentToken.getContent(),
                    currentToken.getLine());
            nextToken();
            // 匹配另外一个双引号
            nextToken();
        } else { // 报错
            String error = " 算式因子存在错误" + "\n";
            error(error);
            if (currentToken != null
                    && !currentToken.getContent().equals(Token.SEMICOLON)) {
                nextToken();
            }
            return new TreeNode(Token.ERROR + "算式因子存在错误");
        }
        return tempNode;
    }


    private final TreeNode array() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.getContent().equals(Token.LBRACKET)) {
            nextToken();
        } else {
            String error = " 缺少左中括号\"[\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "缺少左中括号\"[\"");
        }
        // 调用expression函数匹配表达式
        tempNode = expression();
        if (currentToken != null
                && currentToken.getContent().equals(Token.RBRACKET)) {
            nextToken();
        } else { // 报错
            String error = " 缺少右中括号\"]\"" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "缺少右中括号\"]\"");
        }
        return tempNode;
    }


    private final TreeNode add_op() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.getContent().equals(Token.PLUS)) {
            tempNode = new TreeNode("运算符", Token.PLUS, currentToken
                    .getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.MINUS)) {
            tempNode = new TreeNode("运算符", Token.MINUS, currentToken
                    .getLine());
            nextToken();
        } else { // 报错
            String error = " 加减符号出错" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "加减符号出错");
        }
        return tempNode;
    }


    private final TreeNode mul_op() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.getContent().equals(Token.TIMES)) {
            tempNode = new TreeNode("运算符", Token.TIMES, currentToken
                    .getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.DIVIDE)) {
            tempNode = new TreeNode("运算符", Token.DIVIDE, currentToken
                    .getLine());
            nextToken();
        } else { // 报错
            String error = " 乘除符号出错" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "乘除符号出错");
        }
        return tempNode;
    }


    private final TreeNode comparison_op() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.getContent().equals(Token.LT)) {
            tempNode = new TreeNode("运算符", Token.LT, currentToken.getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.GT)) {
            tempNode = new TreeNode("运算符", Token.GT, currentToken.getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.EQUAL)) {
            tempNode = new TreeNode("运算符", Token.EQUAL, currentToken
                    .getLine());
            nextToken();
        } else if (currentToken != null
                && currentToken.getContent().equals(Token.NEQUAL)) {
            tempNode = new TreeNode("运算符", Token.NEQUAL, currentToken
                    .getLine());
            nextToken();
        } else { // 报错
            String error = " 比较运算符出错" + "\n";
            error(error);
            return new TreeNode(Token.ERROR + "比较运算符出错");
        }
        return tempNode;
    }

}