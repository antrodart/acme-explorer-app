package us.master.acmeexplorer.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import us.master.acmeexplorer.Constants;
import us.master.acmeexplorer.dto.TripDTO;

public class Trip implements Serializable {

    private Long id;
    private String startPlace;
    private String endPLace;
    private String description;
    private String url;
    private Calendar startDate;
    private Calendar endDate;
    private Long price;
    private boolean selected;

    public Trip() {
    }

    public Trip(Long id, String startPlace, String endPLace, String description, String url, Calendar startDate, Calendar endDate, Long price) {
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

    public Trip (TripDTO tripDTO) {
        this.id = tripDTO.getId();
        this.startPlace = tripDTO.getStartPlace();
        this.endPLace = tripDTO.getEndPlace();
        this.description = tripDTO.getDescription();
        this.url = tripDTO.getUrl();
        this.price = tripDTO.getPrice();
        this.selected = tripDTO.getSelected();
        String[] startDateDTOarray = tripDTO.getStartDate().split("-");
        String[] endDateDTOarray = tripDTO.getStartDate().split("-");
        Calendar startDateDTO = Calendar.getInstance();
        Calendar endDateDTO = Calendar.getInstance();
        startDateDTO.set(Integer.parseInt(startDateDTOarray[0]),Integer.parseInt(startDateDTOarray[1]),Integer.parseInt(startDateDTOarray[2]));
        endDateDTO.set(Integer.parseInt(endDateDTOarray[0]),Integer.parseInt(endDateDTOarray[1]),Integer.parseInt(endDateDTOarray[2]));
        this.startDate = startDateDTO;
        this.endDate = endDateDTO;
        }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
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

            trips.add(new Trip((long) (i + 1),
                    Constants.lugarSalida[startCityIndex],
                    Constants.ciudades[cityIndex],
                    Constants.adjetivo[r.nextInt(Constants.adjetivo.length)] + " viaje a "
                            + Constants.ciudades[cityIndex] + " desde " + Constants.lugarSalida[startCityIndex],
                    Constants.urlImagenes[r.nextInt(Constants.urlImagenes.length)],
                    startDateRandom,
                    endDateRandom,
                    (long) (r.nextInt(850) + 150)));
        }

        return trips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return selected == trip.selected &&
                Objects.equals(id, trip.id) &&
                Objects.equals(startPlace, trip.startPlace) &&
                Objects.equals(endPLace, trip.endPLace) &&
                description.equals(trip.description) &&
                url.equals(trip.url) &&
                Objects.equals(startDate, trip.startDate) &&
                Objects.equals(endDate, trip.endDate) &&
                Objects.equals(price, trip.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPlace, endPLace, description, url, startDate, endDate, price, selected);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", startPlace='" + startPlace + '\'' +
                ", endPLace='" + endPLace + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", startDate=" + startDate.get(Calendar.DAY_OF_MONTH) + "/" +  startDate.get(Calendar.MONTH) + "/" + startDate.get(Calendar.YEAR) +
                ", endDate=" + endDate.get(Calendar.DAY_OF_MONTH) + "/" +  endDate.get(Calendar.MONTH) + "/" + endDate.get(Calendar.YEAR) +
                ", price=" + price +
                ", selected=" + selected +
                '}';
    }
}
