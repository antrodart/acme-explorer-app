package us.master.acmeexplorer.dto;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import us.master.acmeexplorer.entity.User;

public class UserDTO {

    private String id;
    private String name;
    private String surname;
    private String email;
    private String city;
    private String birthDate;
    private String picture;
    private Double latitude;
    private Double longitude;
    private Map<String, String> selectedTrips = new HashMap<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.city = user.getCity();
        this.picture = user.getPicture();
        if(user.getBirthDate() != null) {
            this.birthDate = user.getBirthDate().get(Calendar.YEAR) + "-" + (user.getBirthDate().get(Calendar.MONTH) + 1) + "-" + user.getBirthDate().get(Calendar.DAY_OF_MONTH);
        }
        this.selectedTrips = user.getSelectedTrips();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
    }

    public  UserDTO() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Map<String, String> getSelectedTrips() {
        return selectedTrips;
    }

    public void setSelectedTrips(Map<String, String> selectedTrips) {
        this.selectedTrips = selectedTrips;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
