package net.didion.pml.util.properties;

import static net.didion.pml.util.StringUtils.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

public class DefaultPropertyFileFormat<T> implements PropertyFileFormatter<T>, PropertyFileParser<T> {
    public static final char HASH = '#';
    public static final char BANG = '!';
    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("-+ (.*) -+");
    
    private final int padding;
    private final int maxLineWidth;
    private final char commentChar;
    private final boolean timestamp;
    private final boolean handleCommentedProperties;
    
    public DefaultPropertyFileFormat() {
        this(1, 80, HASH, true, false);
    }
    
    public DefaultPropertyFileFormat(int padding, int maxLineWidth, char commentChar, boolean timestamp, 
                                     boolean handleCommentedProperties) {
        if (commentChar != HASH && commentChar != BANG) {
            throw new IllegalArgumentException("Invalid comment character: " + commentChar);
        }
        this.padding = padding;
        this.maxLineWidth = maxLineWidth;
        this.commentChar = commentChar;
        this.timestamp = timestamp;
        this.handleCommentedProperties = handleCommentedProperties;
    }

    @Override
    public boolean writeBreakBeforeGroup(BufferedWriter writer) throws IOException{
        return writeBreak(writer);
    }

    @Override
    public boolean writeBreakBeforeProperty(BufferedWriter writer) throws IOException {
        return writeBreak(writer);
    }

    protected boolean writeBreak(BufferedWriter writer) throws IOException {
        for (int i = 0; i < this.padding; i++) {
            writer.newLine();
        }
        return this.padding > 0;
    }
    
    @Override
    public boolean writeFileHeader(BufferedWriter writer, String comment) throws IOException {
        if (this.timestamp) {
            comment = (comment == null) ? new Date().toString() : comment + "\n" + new Date().toString();
        }
        if (null == comment) {
            return false;
        }

        writeCommentChars(writer, this.maxLineWidth);
        writer.newLine();
        
        writer.write(breakString(encodeHex(comment), this.maxLineWidth, String.valueOf(this.commentChar) + " ", true));
        writer.newLine();
        
        writeCommentChars(writer, this.maxLineWidth);
        writer.newLine();
        
        return true;
    }

    @Override
    public boolean writeGroupHeader(BufferedWriter writer, String name) throws IOException {
        final int width = name.length() + 6;
        final String divider = chars('-', width);
        final int nameBracketLen = width - name.length() - 3;
        final String namePrefix = chars('-', nameBracketLen / 2);
        final String nameSuffix = (nameBracketLen % 2 == 0) ? namePrefix : namePrefix + "-";
        
        writer.write(this.commentChar);
        writer.write(' ');
        writer.write(divider);
        writer.newLine();
        writer.write(this.commentChar);
        writer.write(' ');
        writer.write(namePrefix);
        writer.write(' ');
        writer.write(name);
        writer.write(' ');
        writer.write(nameSuffix);
        writer.newLine();
        writer.write(this.commentChar);
        writer.write(' ');
        writer.write(divider);
        writer.newLine();
        return true;
    }
    
    @Override
    public boolean writePropertyComment(BufferedWriter writer, String comment) throws IOException {
        if (null == comment) {
            return false;
        }
        writer.write(this.commentChar);
        writer.newLine();
        
        writer.write(breakString(encodeHex(comment), this.maxLineWidth, String.valueOf(this.commentChar) + " ", true));
        writer.newLine();
        
        writer.write(this.commentChar);
        writer.newLine();

        return true;
    }
    
    @Override
    public boolean writeProperty(BufferedWriter writer, String key, T value, boolean escUnicode) throws IOException {
        final String safeKey = escape(key, true, escUnicode);
        final String safeVal = escape(toString(value), false, escUnicode);
        
        writer.write(safeKey);
        writer.write('=');
        writer.write(safeVal);
        writer.newLine();
        return true;
    }        
    
    @Override
    public Comment parseComment(LineReader lr) throws IOException {
        if (!lr.isComment) {
            return null;
        }
        
        Comment.Type type = null;
        final StringBuilder comment = new StringBuilder();
        
        // ignore
        if (lr.limit >= 3 && lr.lineBuf[0] == this.commentChar && lr.lineBuf[1] == this.commentChar 
                && lr.lineBuf[2] == this.commentChar && (lr.limit == 3 || Character.isWhitespace(lr.lineBuf[3]))) {
            type = Comment.Type.IGNORE;
            comment.append(lr.lineBuf, 3, lr.limit-3);
            lr.readLine();
        }
        // commented out property
        else if (lr.lineBuf[0] == ((this.commentChar == HASH) ? BANG : HASH)) {
            if (this.handleCommentedProperties) {
                type = Comment.Type.COMMENTED_PROPERTY;
                comment.append(lr.lineBuf, 1, lr.limit-1);
            }
            lr.readLine();
        }
        // property comment
        else if (lr.limit == 1 || (lr.limit == 2 && Character.isWhitespace(lr.lineBuf[1])) 
                || (lr.limit > 2 && Character.isWhitespace(lr.lineBuf[2]))) {
            type = Comment.Type.PROPERTY_COMMENT;
            while (lr.isComment) {
                if (lr.lineBuf[0] == ((this.commentChar == HASH) ? BANG : HASH)) {
                    break;
                }
                int start = 1;
                while (start < lr.limit && (lr.lineBuf[start] == this.commentChar || Character.isWhitespace(lr.lineBuf[start]))) {
                    start++;
                }
                if (start < lr.limit) {
                    if (comment.length() > 0) {
                        comment.append(' ');
                    }
                    comment.append(lr.lineBuf, start, lr.limit-start);
                }
                lr.readLine();
            }
        }
        // group name
        else {
            lr.readLine();
            
            if (!lr.isComment) {
                throw new RuntimeException("There should be a comment here");
            }
            
            if (lr.lineBuf[2] == '-') {
                
                final Matcher m = GROUP_NAME_PATTERN.matcher(new Segment(lr.lineBuf, 2, lr.limit-2));
                if (m.matches()) {
                    type = Comment.Type.GROUP_NAME;
                    comment.append(m.group(1));
                    lr.readLine();
                }
                else {
                    return new Comment(Comment.Type.IGNORE, new String(lr.lineBuf, 1, lr.limit-1));
                }
            }
            // file header
            else {
                type = Comment.Type.FILE_HEADER;
                
                while (lr.isComment) {
                    if (lr.lineBuf[0] == ((this.commentChar == HASH) ? BANG : HASH)) {
                        break;
                    }
                    if (lr.lineBuf[1] == this.commentChar) {
                        break;
                    }
                    int start = 1;
                    while (start < lr.limit && (lr.lineBuf[start] == this.commentChar || Character.isWhitespace(lr.lineBuf[start]))) {
                        start++;
                    }
                    if (start < lr.limit) {
                        if (comment.length() > 0) {
                            comment.append(' ');
                        }
                        comment.append(lr.lineBuf, start, lr.limit-start);
                    }
                    lr.readLine();
                }
            }
            
            if (lr.isComment) {
                lr.readLine();
            }
        }
        
        return (null == type) ? null : new Comment(type, comment.toString());
    }
    
    @Override
    public KeyValue<T> parseProperty(LineReader lr) throws IOException {
        final KeyValue<T> kv = parseProperty((CharSequence) lr);
        lr.readLine();
        return kv;
    }

    @Override
    public KeyValue<T> parseProperty(CharSequence seq) throws IOException {
        int keyLen = 0;
        int valueStart = 0;
        char c = 0;
        boolean hasSep = false;
        boolean precedingBackslash = false;
        
        while (keyLen < seq.length()) {
            c = seq.charAt(keyLen);
            //need check if escaped.
            if ((c == '=' ||  c == ':') && !precedingBackslash) {
                valueStart = keyLen + 1;
                hasSep = true;
                break;
            } 
            else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
                valueStart = keyLen + 1;
                break;
            } 
            if (c == '\\') {
                precedingBackslash = !precedingBackslash;
            } 
            else {
                precedingBackslash = false;
            }
            keyLen++;
        }
        
        while (valueStart < seq.length()) {
            c = seq.charAt(valueStart);
            if (c != ' ' && c != '\t' &&  c != '\f') {
                if (!hasSep && (c == '=' ||  c == ':')) {
                    hasSep = true;
                } 
                else {
                    break;
                }
            }
            valueStart++;
        }
        
        final String key = unescape(seq.subSequence(0, keyLen));
        final T value = fromString(unescape(seq.subSequence(valueStart, seq.length())));
        
        return new KeyValue<T>(key, value);
    }

    protected void writeCommentChars(BufferedWriter writer, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            writer.write(this.commentChar);
        }
    }
    
    protected String toString(T value) {
        return value.toString();
    }
    
    @SuppressWarnings("unchecked")
    protected T fromString(String s) {
        return (T) s;
    }
}
