package org.desafiofrontend.data;

public enum Hobby {
    SPORTS(1, "Sports"),
    READING(2, "Reading"),
    MUSIC(3, "Music");

    private final int index;
    private final String label;

    Hobby(int index, String label) {
        this.index = index;
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }
}
