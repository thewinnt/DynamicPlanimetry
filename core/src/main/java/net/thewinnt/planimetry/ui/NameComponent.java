package net.thewinnt.planimetry.ui;

public record NameComponent(byte letter, int index, short dashes) {
    public static final String[] ALLOWED_NAMES = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public String toString() {
        StringBuilder builder = new StringBuilder(ALLOWED_NAMES[letter]);
        if (index != 0) builder.append(index);
        builder.append("'".repeat(dashes));
        return builder.toString();
    }
}
