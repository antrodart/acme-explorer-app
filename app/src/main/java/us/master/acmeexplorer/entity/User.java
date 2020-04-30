package us.master.acmeexplorer.entity;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Calendar;

import us.master.acmeexplorer.dto.UserDTO;

public class User implements Serializable {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String city;
    private Calendar birthDate;
    private String picture;

    public User(UserDTO userDTO) {
        this.id = userDTO.getId();
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
        this.email = userDTO.getEmail();
        this.city = userDTO.getCity();
        this.picture = userDTO.getPicture();
        if (userDTO.getBirthDate() != null && !userDTO.getBirthDate().equals("")) {
            String[] birthDateDTOArray = userDTO.getBirthDate().split("-");
            Calendar birthDateDTO = Calendar.getInstance();
            birthDateDTO.set(Integer.parseInt(birthDateDTOArray[0]),Integer.parseInt(birthDateDTOArray[1]),Integer.parseInt(birthDateDTOArray[2]));
            this.birthDate = birthDateDTO;
        }
    }

    public User(FirebaseUser firebaseUser) {
        this.id = firebaseUser.getUid();
        this.email = firebaseUser.getEmail();
        this.name = firebaseUser.getDisplayName();
        if(firebaseUser.getPhotoUrl() != null) {
            this.picture = firebaseUser.getPhotoUrl().toString();
        }
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

    public Calendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Calendar birthDate) {
        this.birthDate = birthDate;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
