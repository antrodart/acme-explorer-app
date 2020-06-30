package us.master.acmeexplorer.resttype;


import java.util.List;

public class WeatherResponse {
    private WeatherLatLng coord;
    private List<Weather> weather;
    private String base;
    private WeatherConditions main;
    private WeatherWind wind;
    private long timezone;
    private long id;
    private String name;
    private int cod;

    public WeatherResponse(WeatherLatLng coord, List<Weather> weather, String base, WeatherConditions main, WeatherWind wind, long timezone, long id, String name, int cod) {
        this.coord = coord;
        this.weather = weather;
        this.base = base;
        this.main = main;
        this.wind = wind;
        this.timezone = timezone;
        this.id = id;
        this.name = name;
        this.cod = cod;
    }

    public WeatherResponse() {
    }

    public WeatherLatLng getCoord() {
        return coord;
    }

    public void setCoord(WeatherLatLng coord) {
        this.coord = coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public WeatherConditions getMain() {
        return main;
    }

    public void setMain(WeatherConditions main) {
        this.main = main;
    }

    public WeatherWind getWind() {
        return wind;
    }

    public void setWind(WeatherWind wind) {
        this.wind = wind;
    }

    public long getTimezone() {
        return timezone;
    }

    public void setTimezone(long timezone) {
        this.timezone = timezone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}
