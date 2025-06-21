package dev.koifysh.archipelago;

public class Utils {

    public static String getFileSafeName(String text)
    {
        if(text == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            switch(c)
            {
                case '<':
                case '>':
                case ':':
                case '"':
                case '/':
                case '\\':
                case '|':
                case '?':
                case '*':
                    continue;
                default:
                    sb.append(c);

            }
        }
        return sb.toString();
    }
}
