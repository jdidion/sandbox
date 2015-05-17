package net.didion.pml.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;

public class LineReader implements CharSequence {
    char[] lineBuf = new char[1024];
    int limit = 0;
    boolean isComment = false;
    char commentChar = 0;
    
    private byte[] inByteBuf;
    private char[] inCharBuf;
    private int inLimit = 0;
    private int inOff = 0;
    private InputStream inStream;
    private Reader reader;
    
    LineReader(InputStream inStream) {
        this.inStream = inStream;
        this.inByteBuf = new byte[8192]; 
    }

    LineReader(Reader reader) {
        this.reader = reader;
        this.inCharBuf = new char[8192]; 
    }

    @Override
    public char charAt(int index) {
        return this.lineBuf[index];
    }

    @Override
    public int length() {
        return this.limit;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return CharBuffer.wrap(this.lineBuf, start, end-start);
    }

    /**
     * Read in a "logical line" from an InputStream/Reader. <tt>comment</tt>
     * will be true if the line is a comment. Blank lines and leading whitespace 
     * (\u0020, \u0009 and \u000c) are removed from the beginning of non-comment
     * lines. Returns the char length of the "logical line" and stores  the line 
     * in <tt>lineBuf</tt>. 
     */
    int readLine() throws IOException {
        this.isComment = false;
        this.commentChar = 0;
        
        int len = 0;
        char c = 0;
        boolean skipWhiteSpace = true;
        boolean isNewLine = true;
        boolean appendedLineBegin = false;
        boolean precedingBackslash = false;
        boolean skipLF = false;

        while (true) {
            if (maybeRead() && this.inLimit <= 0) {
                this.limit = (len == 0) ? -1 : len;
                return this.limit;
            }     
            
            if (this.inStream != null) {
                //The line below is equivalent to calling a ISO8859-1 decoder.
                c = (char) (0xff & this.inByteBuf[this.inOff++]);
            } 
            else {
                c = this.inCharBuf[inOff++];
            }
            
            if (skipLF) {
                skipLF = false;
            
                if (c == '\n') {
                    continue;
                }
            }
        
            if (skipWhiteSpace) {
                if (c == ' ' || c == '\t' || c == '\f') {
                    continue;
                }
                if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                    continue;
                }
                skipWhiteSpace = false;
                appendedLineBegin = false;
            }
            
            if (isNewLine) {
                isNewLine = false;
                if (c == '#' || c == '!') {
                    this.isComment = true;
                    this.commentChar = c;
                }
                else if (this.isComment) {
                    this.limit = len;
                    return this.limit;
                }
            }
    
            if (c != '\n' && c != '\r') {
                this.lineBuf[len++] = c;
                maybeGrowLineBuf(len);
                //flip the preceding backslash flag
                if (c == '\\') {
                    precedingBackslash = !precedingBackslash;
                } 
                else {
                    precedingBackslash = false;
                }
            }
            else {
                // reached EOL
                if (len == 0) {
                    isNewLine = true;
                    skipWhiteSpace = true;
                    len = 0;
                    continue;
                }
                
                if (maybeRead() && this.inLimit <= 0) {
                    this.limit = len;
                    return this.limit;
                }
                
                if (precedingBackslash) {
                    len -= 1;
                    //skip the leading whitespace characters in following line
                    skipWhiteSpace = true;
                    appendedLineBegin = true;
                    precedingBackslash = false;
                    if (c == '\r') {
                        skipLF = true;
                    }
                } 
                else {
                    this.limit = len;
                    return this.limit;
                }
            }
        }
    }
    
    private boolean maybeRead() throws IOException {
        if (this.inOff >= this.inLimit) {
            this.inLimit = (this.inStream == null) ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf);
            this.inOff = 0;
            return true;
        }
        return false;
    }
    
    private boolean maybeGrowLineBuf(int len) {
        if (len == this.lineBuf.length) {
            int newLength = lineBuf.length * 2;
            if (newLength < 0) {
                newLength = Integer.MAX_VALUE;
            }
            char[] buf = new char[newLength];
            System.arraycopy(this.lineBuf, 0, buf, 0, this.lineBuf.length);
            this.lineBuf = buf;
            return true;
        }
        return false;
    }
}   
