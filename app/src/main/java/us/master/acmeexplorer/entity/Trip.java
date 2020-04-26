package us.master.acmeexplorer.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import us.master.acmeexplorer.Constants;

public class Trip implements Serializable {

    private int id;
    private String startPlace;
    private String endPLace;
    private String description;
    private String url;
    private Calendar startDate;
    private Calendar endDate;
    private int price;
    private boolean selected;

    private Trip(int id, String startPlace, String endPLace, String description, String url, Calendar startDate, Calendar endDate, int price) {
        this.id = id;
        this.startPlace = startPlace;
        this.endPLace = endPLace;
        this.description = description;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getEndPLace() {
        return endPLace;
    }

    public void setEndPLace(String endPLace) {
        this.endPLace = endPLace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static List<Trip> generateTrips(int max){
        ArrayList<Trip> trips = new ArrayList<>(max);
        Random r = new Random();

        for (int i = 0; i < max; i++){
            // Se crean los índices aleatorios de las listas de ciudades
            int cityIndex = r.nextInt(Constants.ciudades.length);
            int startCityIndex = r.nextInt(Constants.lugarSalida.length);
            // La fecha final será igual a la inicial más un número aleatorio de días, hasta 14.
            int randomMonth = r.nextInt(9)+4;
            int randomDaysOfMonth = r.nextInt(30)+1;
            int randomPlusDays = r.nextInt(14);
            Calendar startDateRandom = Calendar.getInstance();
            Calendar endDateRandom = Calendar.getInstance();

            startDateRandom.set(2020,randomMonth, randomDaysOfMonth);
            endDateRandom.set(2020,randomMonth, randomDaysOfMonth);
            endDateRandom.add(Calendar.DATE,randomPlusDays);

            trips.add(new Trip(i+1,
                    Constants.lugarSalida[startCityIndex],
                    Constants.ciudades[cityIndex],
                    Constants.adjetivo[r.nextInt(Constants.adjetivo.length)] + " viaje a "
                            + Constants.ciudades[cityIndex] + " desde " + Constants.lugarSalida[startCityIndex],
                    Constants.urlImagenes[r.nextInt(Constants.urlImagenes.length)],
                    startDateRandom,
                    endDateRandom,
                    r.nextInt(850) + 150));
        }

        return trips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id == trip.id &&
                price == trip.price &&
                selected == trip.selected &&
                Objects.equals(startPlace, trip.startPlace) &&
                Objects.equals(endPLace, trip.endPLace) &&
                Objects.equals(description, trip.description) &&
                Objects.equals(url, trip.url) &&
                Objects.equals(startDate, trip.startDate) &&
                Objects.equals(endDate, trip.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPlace, endPLace, description, url, startDate, endDate, price, selected);
    }
}
