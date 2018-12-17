public class Token {

    /* 运算符 */
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String DIVIDE = "/";
    public static final String TIMES = "*";
    public static final String LT = "<";
    public static final String GT = ">";
    public static final String EQUAL = "==";
    public static final String NEQUAL = "<>";//不等
    public static final String ASSIGN = "=";

    /* 保留字 */
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String WHILE = "while";
    public static final String IF = "if";
    public static final String FOR = "for";
    public static final String ELSE = "else";
    public static final String INT = "int";
    public static final String DOUBLE = "double";
    public static final String BOOL = "bool";
    public static final String STRING = "string";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /* 分隔符*/
    public static final String DQ = "\"";
    public static final String RBRACE = "}";
    public static final String LBRACE = "{";
    public static final String RPAREN = ")";
    public static final String LPAREN = "(";
    public static final String RBRACKET = "]";
    public static final String LBRACKET = "[";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";

    /* 注释符*/
    public static final String ROW = "//";
    public static final String LEFT = "/*";
    public static final String RIGHT = "*/";

    /* 错误*/
    public static final String ERROR = "错误  ： ";


    /* token类型*/
    private String kind;
    /* token所在行*/
    private int line;
    /* token所在列*/
    private int culomn;
    /* token内容*/
    private String content;
    /* 标识符类型*/
    private String idKind;

    public Token(int l, int c, String k, String con) {
        this.line = l;
        this.culomn = c;
        this.kind = k;
        this.content = con;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCulomn() {
        return culomn;
    }

    public void setCulomn(int culomn) {
        this.culomn = culomn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdKind() {
        return idKind;
    }

    public void setIdKind(String idKind) {
        this.idKind = idKind;
    }

}