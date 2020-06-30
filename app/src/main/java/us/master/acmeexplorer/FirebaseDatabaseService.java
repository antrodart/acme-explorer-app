package us.master.acmeexplorer;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import us.master.acmeexplorer.dto.TripDTO;
import us.master.acmeexplorer.dto.UserDTO;
import us.master.acmeexplorer.entity.Trip;
import us.master.acmeexplorer.entity.User;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDatabase;

    /**
     * Factoría para crear la clase con todos sus atributos instanciados
     * @return
     */
    public static FirebaseDatabaseService getSeriveInstance() {
        if (service == null || mDatabase == null) {
            service = new FirebaseDatabaseService();
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(false); // Permite almacenar datos en local si se pierde la conexión, y luego se sincronizan
        }

        if(userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public DatabaseReference getTrip(String tripId) {
        return mDatabase.getReference("trips/" + tripId).getRef();
    }

    public void saveTrip(Trip trip, DatabaseReference.CompletionListener completionListener) {
        TripDTO tripDTO = new TripDTO(trip);
        String tripId = mDatabase.getReference("trips").push().getKey();
        trip.setId(tripId);
        tripDTO.setId(tripId);

        mDatabase.getReference("trips").child(tripId).setValue(tripDTO, completionListener);
    }

    public void saveUser(User user, DatabaseReference.CompletionListener completionListener) {
        UserDTO userDTO = new UserDTO(user);
        mDatabase.getReference("user").child(user.getId()).setValue(userDTO, completionListener);
    }

    public DatabaseReference getUser(String userId) {
        return mDatabase.getReference("user/" + userId).getRef();
    }

    public DatabaseReference getAllTrips() {
        return mDatabase.getReference("trips").getRef();
    }

    public DatabaseReference getTripsFromCreator(String creatorId) {
        return mDatabase.getReference("trips").orderByChild("creator").equalTo(creatorId).getRef();
    }

    /**
     * Devuelve un conjunto de identificadores de Viajes que el usuario ha seleccionado dentro de
     * la aplicación. Para obtener los objetos Trip en sí, es necesario iterar el conjunto que devuelve
     * y obtener el trip a partir del identificador en cuestión.
     *
     * @param userId
     * @return List<String> Lista o conjunto de identificadores de trips.
     */
    public DatabaseReference getSelectedTripsFromUser(String userId) {
        return mDatabase.getReference("user/" + userId + "/selectedTrips").getRef();
    }

    public void setTripAsSelected(String userId, String tripId, DatabaseReference.CompletionListener completionListener) {
        mDatabase.getReference("user/" + userId + "/selectedTrips").child(tripId).setValue(tripId, completionListener);
    }

    public void setTripAsNotSelected(String userId, String tripId, DatabaseReference.CompletionListener completionListener) {
        mDatabase.getReference("user/" + userId + "/selectedTrips").child(tripId).removeValue(completionListener);
    }

    public DatabaseReference getAllUsers() {
        return mDatabase.getReference("user").getRef();
    }

}
