package us.master.acmeexplorer.entity;

import java.util.ArrayList;
import java.util.List;

import us.master.acmeexplorer.R;
import us.master.acmeexplorer.SelectedTripActivity;
import us.master.acmeexplorer.TripActivity;

public class Link {
    private String description;
    private int resourceImageView;
    private Class intentClass;

    private Link(String description, int resourceImageView, Class intentClass) {
        this.description = description;
        this.resourceImageView = resourceImageView;
        this.intentClass = intentClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getResourceImageView() {
        return resourceImageView;
    }

    public void setResourceImageView(int resourceImageView) {
        this.resourceImageView = resourceImageView;
    }

    public Class getIntentClass() {
        return intentClass;
    }

    public void setIntentClass(Class intentClass) {
        this.intentClass = intentClass;
    }

    public static List<Link> generateLinks(){
        List<Link> links = new ArrayList<>();

        links.add(new Link("Viajes disponibles", R.drawable.take_a_trip, TripActivity.class));
        links.add(new Link("Viajes seleccionados", R.drawable.next_trip, SelectedTripActivity.class));

        return links;
    }
}
