package us.master.acmeexplorer.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import us.master.acmeexplorer.dto.UserDTO;

public class User implements Parcelable {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String city;
    private Calendar birthDate;
    private String picture;
    private Double latitude;
    private Double longitude;
    private Map<String, String> selectedTrips = new HashMap<>();

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
        this.selectedTrips = userDTO.getSelectedTrips();
        this.latitude = userDTO.getLatitude();
        this.longitude = userDTO.getLongitude();
    }

    public User(FirebaseUser firebaseUser) {
        this.id = firebaseUser.getUid();
        this.email = firebaseUser.getEmail();
        this.name = firebaseUser.getDisplayName();
        if(firebaseUser.getPhotoUrl() != null) {
            this.picture = firebaseUser.getPhotoUrl().toString();
        }
    }

    public User() {

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

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private User(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
        city = in.readString();
        long birthdateMiliseconds = in.readLong();
        String birthdateTimezone = in.readString();
        picture = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        readMapFromBundle(in, selectedTrips, selectedTrips.getClass().getClassLoader());


        if (birthdateMiliseconds != 1L) {
            birthDate = new GregorianCalendar(TimeZone.getTimeZone(birthdateTimezone));
            birthDate.setTimeInMillis(birthdateMiliseconds);
        }
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

    public Map<String, String> getSelectedTrips() {
        return selectedTrips;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setSelectedTrips(Map<String, String> selectedTrips) {
        this.selectedTrips = selectedTrips;
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(surname);
        out.writeString(email);
        out.writeString(city);
        if (birthDate == null) {
            out.writeLong(1L);
            out.writeString( "");
        } else {
            out.writeLong(birthDate.getTimeInMillis());
            out.writeString(birthDate.getTimeZone().getID());
        }
        out.writeString(picture);
        if (latitude == null || longitude == null) {
            out.writeDouble(37);
            out.writeDouble(-6);
        } else {
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }
        writeMapAsBundle(out, selectedTrips);
    }

    private void writeMapAsBundle(Parcel dest, Map<String, String> map) {
        Bundle bundle = new Bundle();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                bundle.putSerializable(entry.getKey(), entry.getValue());
            }
        }
        dest.writeBundle(bundle);
    }

    private void readMapFromBundle(Parcel in, Map<String, String> map, ClassLoader keyClassLoader) {
        Bundle bundle = in.readBundle(keyClassLoader);
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
    }


}
