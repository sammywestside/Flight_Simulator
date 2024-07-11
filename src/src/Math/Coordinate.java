package Math;

import App.Constants;

public class Coordinate {
    private final double longitude;
    private final double latitude;
    private final double r = Constants.Length;

    public Coordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public Vector vector() {
        return new Vector(
                this.r * Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(this.longitude)),
                this.r * Math.cos(Math.toRadians(this.latitude)) * Math.sin(Math.toRadians(this.longitude)),
                this.r * Math.sin(Math.toRadians(this.latitude))
        );
    }
    @Override
    public String toString(){
        return STR."(\{this.latitude}, \{this.longitude})";
    }
}
