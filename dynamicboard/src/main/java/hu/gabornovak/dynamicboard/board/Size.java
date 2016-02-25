package hu.gabornovak.dynamicboard.board;

/**
 * Created by gnovak on 2/7/2016.
 */
public class Size {
    private int width;
    private int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size(Size size) {
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size = (Size) o;

        if (width != size.width) return false;
        return height == size.height;

    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    public boolean smallerThanOrEqual(Size size) {
        if (size == null){
            return true;
        }
        return (size.getHeight() >= getHeight()) && (size.getWidth() >= getWidth());
    }

    public boolean largerThanOrEqual(Size size) {
        if (size == null){
            return true;
        }
        return (size.getHeight() <= getHeight()) && (size.getWidth() <= getWidth());
    }
}
