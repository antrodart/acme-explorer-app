package us.master.acmeexplorer.resttype;

public class Weather {
    private int id;
    private String min;
    private String description;
    private String icon;

    public Weather(int id, String min, String description, String icon) {
        this.id = id;
        this.min = min;
        this.description = description;
        this.icon = icon;
    }

    public Weather() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
