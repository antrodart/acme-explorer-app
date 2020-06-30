package us.master.acmeexplorer.resttype;

public class WeatherWind {
    private float speed;
    private float deg;

    public WeatherWind(float speed, float deg) {
        this.speed = speed;
        this.deg = deg;
    }

    public WeatherWind() {
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }
}
