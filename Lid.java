
/**
 * Lid class, a lid can be paired with a cup.
 * all lids are 1 cm height
 * 
 * @author (Murillo-Rubiano)
 * @version (1.0)
 */
public class Lid {
    private int number;
    private int height;
    private String color;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private Cup pairedCup;
    private Rectangle lidRectangle;

    private static final int HEIGHT_CM = 1;
    private static final int PIXELS_PER_CM = 10;

    public Lid(int number) {
        this.number = number;
        this.color = getColorForId(number);
        this.xPosition = 0;
        this.yPosition = 0;
        this.isVisible = false;
        this.pairedCup = null;

        lidRectangle = new Rectangle();
        lidRectangle.changeColor(this.color);
        lidRectangle.changeSize(HEIGHT_CM * PIXELS_PER_CM, 30);
    }

    private String getColorForId(int id) {
        String[] colors = { "blue", "red", "green", "yellow", "magenta", "cyan", "orange" };
        return colors[(id - 1) % colors.length];
    }

    public void setPosition(int x, int y) {
        erase();
        this.xPosition = x;
        this.yPosition = y;

        // La tapa es de 1cm, el rectangulo se ubica justo en y
        int lidY = yPosition - HEIGHT_CM * PIXELS_PER_CM;

        lidRectangle.moveHorizontal(x - 70);
        lidRectangle.moveVertical(lidY - 15);

        draw();
    }

    private void draw() {
        if (isVisible) {
            lidRectangle.makeVisible();
        }
    }

    private void erase() {
        if (isVisible) {
            lidRectangle.makeInvisible();
        }
    }

    public void makeVisible() {
        this.isVisible = true;
        lidRectangle.makeVisible();
    }

    public void makeInvisible() {
        this.isVisible = false;
        lidRectangle.makeInvisible();
    }

}