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
        if (x >= size || x < 0 || y >= size || y < 0)
            throw new IndexOutOfBoundsException
                    ("Incorrect index.Expected point x = " + x + ", point y = " + y +
                            ". They must fall into the interval 0 <= point > " + size);

        Figure actualFigure = figures[x][y];
        if (actualFigure != null)
            throw new IllegalArgumentException
                    ("Incorrect index. Figure in point x = " + x + ", point y = " + y +
                            " not NULL!\n" + this.toString());

        figures[x][y] = figure;
        counterFigure++;
    }

    public Figure getFigure(final Point point) {
        return figures[point.getX()][point.getY()];

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
