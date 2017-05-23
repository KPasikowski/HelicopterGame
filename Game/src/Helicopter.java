import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mateusz on 12.05.17.
 */


public class Helicopter extends GameObject {
    private AtomicInteger acceleration = new AtomicInteger(0);

    public Helicopter(int initialWidth, int initialHeight, String imageLocation) {
        super(initialWidth, initialHeight, imageLocation);
    }

    public int getAcceleration() {
        return acceleration.get();
    }

    public void setAcceleration(int acceleration) {
        this.acceleration.set(acceleration);
    }

    public void accelerate(int acceleration) {
        this.acceleration.addAndGet(acceleration);
    }
}
