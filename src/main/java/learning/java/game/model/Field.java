package learning.java.game.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class Field {

    @JsonIgnore
    private int id = -1;

    private int size;

    @JsonIgnore
    private int counterFigure = 0;

    private Figure[][] figures;

    public Field() {
        this(3);
    }

    public Field(int size) {
        this.size = size;
        figures = new Figure[size][size];
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        if (figures == null)
            figures = new Figure[size][size];

    }

    public void setFigure(final Point point, final Figure figure) {
        int x = point.getX();
        int y = point.getY();
        checkPoint(x, y);
        figures[x][y] = figure;
        counterFigure++;
    }

    public Figure getFigure(final Point point) {
        int x = point.getX();
        int y = point.getY();
        checkPoint(x, y);
        return figures[x][y];

    }

    private void checkPoint(int x, int y) {
        if (x >= size || x < 0 || y >= size || y < 0)
            throw new IndexOutOfBoundsException
                    ("Incorrect index.Expected point x = " + x + ", point y = " + y +
                            ". They must fall into the interval 0 <= point > " + size);
    }

    public Figure[][] getFigures() {
        return figures;
    }

    public void setFigures(Figure[][] figures) {
        this.figures = figures;
    }

    public int getCounterFigure() {
        return counterFigure;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        if (figures == null)
            return "null";
        StringBuilder str = new StringBuilder();

        for (Figure[] i : figures) {
            str.append("\n");
            for (Figure j : i)
                str.append(j).append(" ");
        }
        return str.toString();
    }

}
