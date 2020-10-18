package learning.java.game.model;

public enum Figure {
    X, O;

    public static Figure figureFromString(String string) {
        if (string == null || "null".equals(string))
            return null;
        for (Figure figure: Figure.values()) {
            if ((string.equals(figure.toString())))
            return figure;
        }
        throw new IllegalArgumentException("Not found enum Figure." + string);
    }
}
