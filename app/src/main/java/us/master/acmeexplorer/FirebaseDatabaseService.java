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
            mDatabase.setPersistenceEnabled(true); // Permite almacenar datos en local si se pierde la conexión, y luego se sincronizan
        }

        if(userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public DatabaseReference getTrip(String tripId) {
        return mDatabase.getReference("user/u123/trip/" + tripId).getRef();
    }

    public DatabaseReference getTripPrice(String tripId) {
        return mDatabase.getReference("user/u123/trip/" + tripId + "/price").getRef();
    }

    public DatabaseReference getTrip() {
        return mDatabase.getReference("user/u123/trip").getRef();
    }

    public void saveTrip(Trip trip, DatabaseReference.CompletionListener completionListener) {
        TripDTO tripDTO = new TripDTO(trip);
        mDatabase.getReference("trips").push().setValue(tripDTO, completionListener);
    }

    public void saveUser(User user, DatabaseReference.CompletionListener completionListener) {
        UserDTO userDTO = new UserDTO(user);
        mDatabase.getReference("user").child(user.getId()).setValue(userDTO, completionListener);
    }

    public DatabaseReference getUser(String userId) {
        return mDatabase.getReference("user/" + userId).getRef();
    }

    public DatabaseReference getTripsFromCreator(String creatorId) {
        return mDatabase.getReference("trips").orderByChild("creator").equalTo(creatorId).getRef();
    }

}
