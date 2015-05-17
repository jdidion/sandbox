package net.didion.pml.util.properties;

import java.io.BufferedWriter;
import java.io.IOException;

public interface PropertyFileFormatter<T> {
    boolean writeBreakBeforeProperty(BufferedWriter writer) throws IOException;
    
    boolean writeBreakBeforeGroup(BufferedWriter writer) throws IOException;
    
    boolean writeFileHeader(BufferedWriter writer, String comment) throws IOException;
    
    boolean writeGroupHeader(BufferedWriter writer, String name) throws IOException;
    
    boolean writePropertyComment(BufferedWriter writer, String comment) throws IOException;
    
    boolean writeProperty(BufferedWriter writer, String key, T value, boolean escUnicode) throws IOException;
}
