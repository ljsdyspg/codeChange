public class Match {
    //判断字母
    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    //判断数字
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    //匹配整数
    public static boolean matchInteger(String input) {
        return input.matches("^-?\\d+$");
    }

    //匹配实数
    public static boolean matchDouble(String input) {
        return input.matches("^(-?\\d+)(\\.\\d+)?$");
    }

    //匹配标识符，CMM不以下划线结尾
    public static boolean matchID(String input) {
        //return input.matches("^\\w+$") && !input.endsWith("_") && input.substring(0, 1).matches("[A-Za-z]");
        return !input.matches("^\\S*_$") && input.matches("^[A-Za-z_][A-Za-z0-9_]*$");
    }

    //匹配关键字
    public static boolean isKey(String str) {
        return str.equals(Token.IF) || str.equals(Token.ELSE) || str.equals(Token.WHILE) || str.equals(Token.PRINT)
                || str.equals(Token.INT) || str.equals(Token.DOUBLE) || str.equals(Token.BOOL)
                || str.equals(Token.STRING) || str.equals(Token.TRUE) || str.equals(Token.FALSE)
                || str.equals(Token.FOR);
    }

    public static int find(int begin, String str) {
        if (begin >= str.length())
            return str.length();
        for (int i = begin; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\n' || c == ',' || c == ' ' || c == '\t' || c == '{'
                    || c == '}' || c == '(' || c == ')' || c == ';' || c == '='
                    || c == '+' || c == '-' || c == '*' || c == '/' || c == '['
                    || c == ']' || c == '<' || c == '>')
                return i - 1;
        }
        return str.length();
    }

}
