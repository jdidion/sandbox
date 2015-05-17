package net.didion.pml.util.properties;

import java.io.IOException;

public interface PropertyFileParser<T> {
    Comment parseComment(LineReader lr) throws IOException;
    
    KeyValue<T> parseProperty(LineReader lr) throws IOException;
    
    KeyValue<T> parseProperty(CharSequence seq) throws IOException;
}
