package com.humanbymistake.alpha.data;

public class String extends Object {
    public static final String INSTANCE = new StringProxy();

    private String() {
        super(null);
    }

    private static class StringProxy extends String {
        private StringProxy() {
            super.setName(getInstance("String"));
        }

        public void setName(String name) {
            throw new RuntimeException("operation not supported");
        }

        public void setParent(Object parent) {
            throw new RuntimeException("operation not supported");
        }
    }
}