package com.humanbymistake.alpha.data;

import java.util.*;

public class Object {
    private static final LongGenerator ID_GENERATOR = new LongGenerator();

    private long _id;
    private String _name;
    private Object _parent;
    private Map _members = new HashMap();

    public Object(Object parent) {
        this(new String(""), parent);
    }

    public Object(String name, Object parent) {
        _id = ID_GENERATOR.nextLong();
        setName(name);
        setParent(parent);
    }

    public long getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setParent(Object parent) {
        _parent = parent;
    }

    public Object getParent() {
        return _parent;
    }

    /**
     * Used for "casting." For example:
     *  1. Object hierarchy: Object -> Entity -> Animal -> Human
     *  2. Entity has method Evolve
     *  3. Human overrides the Evolve method
     *  4. To call ((Entity)Fred).Evolve: eval(Fred.getParent("Entity").getMember("Evolve") [, Fred]);
     * @param name
     * @return
     */
    public Object getParent(String name) {
        Object parent = getParent();
        for (; parent != null && !parent.getName().equals(name); parent = parent.getParent());
        return parent;
    }

    public Object getMember(String name) {
        return (Object)_members.get(name);
    }

    private static class LongGenerator {
        long l = 0;
        private synchronized long nextLong() {
            return l++;
        }
    }
}
