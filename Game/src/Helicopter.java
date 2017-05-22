/**
 * Created by mateusz on 12.05.17.
 */


public class Helicopter extends GameObject {
    private int acceleration = 0;

    public Helicopter(int initialWidth, int initialHeight, String imageLocation) {
        super(initialWidth, initialHeight, imageLocation);
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public void accelerate(int acceleration) {
        this.acceleration += acceleration;
    }
}
