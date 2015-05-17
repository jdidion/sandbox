package net.didion.pml.util.properties;

public class KeyValue<T> {
    public String key;
    public T value;
    
    public KeyValue(String key, T value) {
        this.key = key;
        this.value = value;
    }
}
