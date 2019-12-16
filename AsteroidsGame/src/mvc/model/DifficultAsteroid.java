package mvc.model;

import java.awt.*;

public class DifficultAsteroid extends Asteroid {
    private int hits = 3;

    //radius of a large asteroid
    private final int RAD = 100;

    public DifficultAsteroid() {
        super(0);
        setColor(Color.RED);
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getHits() {
        return hits;
    }

}
