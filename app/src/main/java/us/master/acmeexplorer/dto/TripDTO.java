package us.master.acmeexplorer.dto;

import java.util.Calendar;
import java.util.Objects;

import us.master.acmeexplorer.entity.Trip;

public class TripDTO {

    private Long id;
    private String startPlace;
    private String endPlace;
    private String description;
    private String url;
    private String startDate;
    private String endDate;
    private Long price;
    private Boolean selected;

    public TripDTO() {
    }

    public TripDTO (Trip trip) {
        this.id = trip.getId();
        this.startPlace = trip.getStartPlace();
        this.endPlace = trip.getEndPLace();
        this.description = trip.getDescription();
        this.url = trip.getUrl();
        this.price = trip.getPrice();
        this.selected = trip.isSelected();
        this.startDate = trip.getStartDate().get(Calendar.YEAR) + "-" + trip.getStartDate().get(Calendar.MONTH) + "-" + trip.getStartDate().get(Calendar.DAY_OF_MONTH);
        this.endDate = trip.getEndDate().get(Calendar.YEAR) + "-" + trip.getEndDate().get(Calendar.MONTH) + "-" + trip.getEndDate().get(Calendar.DAY_OF_MONTH);
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

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDTO tripDTO = (TripDTO) o;
        return Objects.equals(id, tripDTO.id) &&
                Objects.equals(startPlace, tripDTO.startPlace) &&
                Objects.equals(endPlace, tripDTO.endPlace) &&
                Objects.equals(description, tripDTO.description) &&
                Objects.equals(url, tripDTO.url) &&
                Objects.equals(startDate, tripDTO.startDate) &&
                Objects.equals(endDate, tripDTO.endDate) &&
                Objects.equals(price, tripDTO.price) &&
                Objects.equals(selected, tripDTO.selected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPlace, endPlace, description, url, startDate, endDate, price, selected);
    }

    @Override
    public String toString() {
        return "TripDTO{" +
                "id=" + id +
                ", startPlace='" + startPlace + '\'' +
                ", endPLace='" + endPlace + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", price=" + price +
                ", selected='" + selected + '\'' +
                '}';
    }
}
