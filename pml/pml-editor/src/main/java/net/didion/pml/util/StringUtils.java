package net.didion.pml.util;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("\\\\u([0-9A-F]{4})");
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(.[^A-Z]*)");
    
    public static String concat(Object...objects) {
        return concat(Arrays.asList(objects));
    }
    
    public static String concat(List<?> objects) {
        final StringBuilder s = new StringBuilder();
        for (Object o : objects) {
            if (s.length() > 0) {
                s.append(',');
            }
            s.append(o.toString());
        }
        return s.toString();
    }
    
    public static String prepadInt(int i, int len) {
        return prepad(String.valueOf(i), '0', len);
    }
    
    public static String prepad(String s, char padChar, int len) {
        if (s.length() >= len) {
            return s;
        }
        else {
            final StringBuilder sb = new StringBuilder();
            final int n = s.length() - len;
            for (int i = 0; i < n; i++) {
                sb.append(padChar);
            }
            sb.append(s);
            return sb.toString();
        }
    }

    public static String clean(String s) {
        return (null == s) ? null : s.trim();
    }
    
    public static String[] clean(String[] s) {
        for (int i = 0; i < s.length; i++) {
            s[i] = clean(s[i]);
        }
        return s;
    }
    
    public static boolean nullOrEmpty(String s) {
        return null == s || s.trim().length() == 0;
    }
    
    public static boolean nullOrEqual(Object o1, Object o2) {
        return (null == o1) ? (null == o2) : (o1.equals(o2));
    }
    
    public static String chars(char c, int len) {
        char[] chars = new char[len];
        Arrays.fill(chars, c);
        return new String(chars);
    }
    
    public static void main(String[] args ) {
        System.out.println(camelCaseToTitle("myDadHatesYou"));
    }
    
    /**
     * Breaks a string into one or more lines, each of which has the specified maximum length.
     * 
     * TODO: this method cannot correctly handle words whose length is greater than maxLineWidth.
     * 
     * @param s the string to break
     * @param maxLineWidth the maximum length of a line
     * @param linePrefix a prefix to add to each line, or null if no prefix
     * @param trim whether to trim the whitespace from each line before adding the line break
     * @return
     */
    public static String breakString(String s, int maxLineWidth, String linePrefix, boolean trim) {
        final StringBuilder broken = new StringBuilder(s.length());
        
        if (null == linePrefix) {
            linePrefix = "";
        }
        final int prefixLen = linePrefix.length();
        
        final BreakIterator breaker = BreakIterator.getLineInstance();
        breaker.setText(s);
        
        int prevWord = 0, prevLine = 0;
        for (int n = breaker.first(); n != BreakIterator.DONE; n = breaker.next()) {
            if (n - prevLine  + prefixLen > maxLineWidth) {
                String line = s.substring(prevLine, prevWord);
                if (trim) {
                    line = line.trim();
                }
                broken.append(linePrefix).append(line).append("\n");
                prevLine = prevWord;
            }
            else {
                prevWord = n;
            }
            
            int newline = -1;
            for (int i = n-1; i >= prevLine; i--) {
                final char c = s.charAt(i);
                if (!Character.isWhitespace(c)) {
                    break;
                }
                if (c == '\r' || c == '\n') {
                    newline = i;
                }
            }
            if (newline >= 0) {
                String line = s.substring(prevLine, newline);
                if (trim) {
                    line = line.trim();
                }
                broken.append(linePrefix).append(line).append("\n");
                prevLine = prevWord = n;
            }
        }
        
        if (prevLine < s.length()) {
            broken.append(linePrefix).append(s.substring(prevLine));
        }
        
        return broken.toString();
    }
    
    public static String escape(String s, boolean escapeSpace, boolean escapeUnicode) {
        int len = s.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        final StringBuffer outBuffer = new StringBuffer(bufLen);

        for (int x = 0; x < len; x++) {
            char c = s.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((c > 61) && (c < 127)) {
                if (c == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(c);
                continue;
            }
            switch(c) {
            case ' ':
                if (x == 0 || escapeSpace) {
                    outBuffer.append('\\');
                }
                outBuffer.append(' ');
                break;
            case '\t': outBuffer.append('\\').append('t'); break;
            case '\n': outBuffer.append('\\').append('n'); break;
            case '\r': outBuffer.append('\\').append('r'); break;
            case '\f': outBuffer.append('\\').append('f'); break;
            case '=': // Fall through
            case ':': // Fall through
            case '#': // Fall through
            case '!':
                outBuffer.append('\\').append(c);
                break;
            default:
                if (((c < 0x0020) || (c > 0x007e)) & escapeUnicode) {
                    outBuffer.append(encodeHexChar(c));
                } 
                else {
                    outBuffer.append(c);
                }
            }
        }
        return outBuffer.toString();
    }
    
    public static String unescape(CharSequence s) {
        int len = s.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        char[] out = new char[bufLen];
        char c;
        int outLen = 0;
        int off = 0, end = len;

        while (off < end) {
            c = s.charAt(off++);
            if (c == '\\') {
                c = s.charAt(off++);   
                if (c == 'u') {
                    c = decodeHexChar(s, off);
                    off += 4;
                } 
                else {
                    if (c == 't') c = '\t'; 
                    else if (c == 'r') c = '\r';
                    else if (c == 'n') c = '\n';
                    else if (c == 'f') c = '\f';
                }
            }
            out[outLen++] = c;
        }
        
        return new String(out, 0, outLen);
    }
    
    public static String encodeHex(String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c > '\u00ff') {
                sb.append(encodeHexChar(c));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static String encodeHexChar(char c) {
        return new StringBuilder("\\u")
            .append(toHex((c >> 12) & 0xF))
            .append(toHex((c >>  8) & 0xF))
            .append(toHex((c >>  4) & 0xF))
            .append(toHex( c        & 0xF))
            .toString();
    }
    
    public static String decodeHex(String s) {
        final Matcher m = HEX_PATTERN.matcher(s);
        
        StringBuilder sb = null;
        int start = 0;
        while (m.find()) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append(s.substring(start, m.start())).append(decodeHexChar(m.group(1)));
            start = m.end();
        }
        
        if (null != sb) {
            if (start < s.length()) {
                sb.append(s.substring(start, s.length()));
            }
            return sb.toString();
        }
        
        return null;
    }
    
    public static char decodeHexChar(String s) {
        return decodeHexChar(s, 0);
    }
    
    public static char decodeHexChar(CharSequence s, int offset) {
        if (s.charAt(offset) == '\\' && s.charAt(offset+1) == 'u') {
            offset += 2;
        }
        int value = 0;
        for (int i = offset; i < offset + 4; i++) {
            final char c = s.charAt(i);
            switch (c) {
            case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                value = (value << 4) + c - '0';
                break;
            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                value = (value << 4) + 10 + c - 'a';
                break;
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                value = (value << 4) + 10 + c - 'A';
                break;
            default:
                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            }
        }
        return (char) value;
    }
    
    public static String camelCaseToTitle(String s) {
        final Matcher m = CAMEL_CASE_PATTERN.matcher(s);
        final StringBuilder sb = new StringBuilder();
        while (m.find()) {
            final String word = m.group(1);
            if (sb.length() == 0) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word, 1, word.length());
            }
            else {
                sb.append(' ').append(word);
            }
        }
        return sb.toString();
    }
    
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }
    
    /** A table of hex digits */
    private static final char[] hexDigit = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
    
    private StringUtils() {
    }    
}
