package us.master.acmeexplorer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;

import us.master.acmeexplorer.dto.TripDTO;

public class Trip implements Parcelable {

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
    private String startPlace;
    private String endPlace;
    private String description;
    private String url;
    private Calendar startDate;
    private Calendar endDate;
    private Long price;
    private boolean selected;
    private User creator;

    public Trip() {
    }

    private String id;

    public Trip (TripDTO tripDTO) {
        this.id = tripDTO.getId();
        this.startPlace = tripDTO.getStartPlace();
        this.endPlace = tripDTO.getEndPlace();
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

    public Trip(String id, String startPlace, String endPlace, String description, String url,
                Calendar startDate, Calendar endDate, Long price, User creator) {
        this.id = id;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.description = description;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.selected = false;
        this.creator = creator;
    }

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Trip(Parcel in) {
        id = in.readString();
        startPlace = in.readString();
        endPlace = in.readString();
        description = in.readString();
        url = in.readString();
        long startDateMiliseconds = in.readLong();
        String startDateTimeZone = in.readString();
        long endDateMiliseconds = in.readLong();
        String endDateTimeZone = in.readString();
        price = in.readLong();
        creator = in.readParcelable(User.class.getClassLoader());
        if (startDateMiliseconds != 1L) {
            startDate = new GregorianCalendar(TimeZone.getTimeZone(startDateTimeZone));
            startDate.setTimeInMillis(startDateMiliseconds);
        }
        if (endDateMiliseconds != 1L) {
            endDate = new GregorianCalendar(TimeZone.getTimeZone(endDateTimeZone));
            endDate.setTimeInMillis(endDateMiliseconds);
        }
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

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return selected == trip.selected &&
                Objects.equals(id, trip.id) &&
                Objects.equals(startPlace, trip.startPlace) &&
                Objects.equals(endPlace, trip.endPlace) &&
                description.equals(trip.description) &&
                url.equals(trip.url) &&
                Objects.equals(startDate, trip.startDate) &&
                Objects.equals(endDate, trip.endDate) &&
                Objects.equals(price, trip.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPlace, endPlace, description, url, startDate, endDate, price, selected);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", startPlace='" + startPlace + '\'' +
                ", endPLace='" + endPlace + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", startDate=" + startDate.get(Calendar.DAY_OF_MONTH) + "/" +  startDate.get(Calendar.MONTH) + "/" + startDate.get(Calendar.YEAR) +
                ", endDate=" + endDate.get(Calendar.DAY_OF_MONTH) + "/" +  endDate.get(Calendar.MONTH) + "/" + endDate.get(Calendar.YEAR) +
                ", price=" + price +
                ", selected=" + selected +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(startPlace);
        out.writeString(endPlace);
        out.writeString(description);
        out.writeString(url);
        if (startDate == null) {
            out.writeLong(1L);
            out.writeString("");
        } else {
            out.writeLong(startDate.getTimeInMillis());
            out.writeString(startDate.getTimeZone().getID());
        }
        if (endDate == null) {
            out.writeLong(1L);
            out.writeString("");
        } else {
            out.writeLong(endDate.getTimeInMillis());
            out.writeString(endDate.getTimeZone().getID());
        }
        out.writeLong(price);
        out.writeParcelable(creator, flags);
    }
}
