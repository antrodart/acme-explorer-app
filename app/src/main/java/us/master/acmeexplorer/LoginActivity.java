package us.master.acmeexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import us.master.acmeexplorer.dto.UserDTO;
import us.master.acmeexplorer.entity.User;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0x152;
    private FirebaseAuth mAuth;
    private Button signinButtonGoogle;
    private Button signinButtonMail;
    private Button loginButtonSignup;
    private ProgressBar progressBar;
    private TextInputLayout loginEmailParent;
    private TextInputLayout loginPassParent;
    private AutoCompleteTextView loginEmail;
    private AutoCompleteTextView loginPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.login_progress);
        loginEmail = findViewById(R.id.login_email_et);
        loginPass = findViewById(R.id.login_pass_et);
        loginEmailParent = findViewById(R.id.login_email);
        loginPassParent = findViewById(R.id.login_pass);
        signinButtonGoogle = findViewById(R.id.login_button_google);
        signinButtonMail = findViewById(R.id.login_button_mail);
        loginButtonSignup = findViewById(R.id.login_button_register);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        signinButtonGoogle.setOnClickListener(l -> attemptLoginGoogle(googleSignInOptions));

        signinButtonMail.setOnClickListener(l -> attemptLoginEmail());

        loginButtonSignup.setOnClickListener(l -> redirectSignupActivity());

        progressBar.setVisibility(View.GONE);
    }

    private void redirectSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(SignupActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);
    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        progressBar.setVisibility(View.VISIBLE);
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signinIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                if (mAuth == null) {
                    mAuth = FirebaseAuth.getInstance();
                }
                if (mAuth != null) {
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            checkUserDatabaseLogin(user);
                        } else {
                            showErrorDialogMail();
                        }
                    });
                } else {
                    showGooglePlayServicesError();
                }
            } catch (ApiException e) {
                showErrorDialogMail();
            }
        }
    }

    // Accedemos a Firebase Auth y comprobaremos si las credenciales son correctas.
    // Antes comprobamos si estas credenciales cumplen con una serie de restricciones.
    private void attemptLoginEmail() {
        progressBar.setVisibility(View.VISIBLE);
        loginEmailParent.setError(null);
        loginPassParent.setError(null);

        if (loginEmail.getText().length() == 0) {
            loginEmailParent.setErrorEnabled(true);
            loginEmailParent.setError(getString(R.string.login_mail_error_1));
            progressBar.setVisibility(View.GONE);
        } else if(loginPass.getText().length() == 0) {
            loginPassParent.setErrorEnabled(true);
            loginPassParent.setError(getString(R.string.login_mail_error_2));
            progressBar.setVisibility(View.GONE);
        } else {
            signInEmail();
        }
    }

    private void signInEmail(){
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString()).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful() || task.getResult().getUser() == null) {
                    showErrorDialogMail();
                    progressBar.setVisibility(View.GONE);
                } else if(!task.getResult().getUser().isEmailVerified()) {
                    showErrorEmailVerified(task.getResult().getUser());
                    progressBar.setVisibility(View.GONE);
                } else {
                    FirebaseUser user = task.getResult().getUser();
                    checkUserDatabaseLogin(user);
                }
            });
        } else {
            showGooglePlayServicesError();
        }
    }

    private void showGooglePlayServicesError() {
        Snackbar.make(loginButtonSignup, R.string.login_google_play_services_error, Snackbar.LENGTH_LONG).setAction(R.string.login_download_gps, view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gps_download_url))));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_download_url))));
            }
        });
    }

    private void saveUserIntoDatabase(User user) {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        firebaseDatabaseService.saveUser(user, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Log.i("AcmeExplorer", "El nuevo usuario se ha guardado correctamente: " + user.getId());
                progressBar.setVisibility(View.GONE);
                redirectMainActivity(user);
            } else {
                Log.i("AcmeExplorer", "Error al guardar el user recién creado: " + databaseError.getMessage());
            }
        });
    }

    private void redirectMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.USER_PRINCIPAL, user);
        startActivity(intent);
    }

    private void checkUserDatabaseLogin(FirebaseUser firebaseUser) {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        firebaseDatabaseService.getUser(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                    if (userDTO != null) {
                        User user = new User(userDTO);
                        progressBar.setVisibility(View.GONE);
                        redirectMainActivity(user);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, getText(R.string.get_user_database_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Hay que crear y guardar el Usuario en nuestra base de datos
                    User user = new User(firebaseUser);
                    saveUserIntoDatabase(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "En el cancelled", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showErrorEmailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.login_verified_mail_error)
            .setPositiveButton(R.string.login_verified_mail_error_ok, ((dialog, which) -> {
                user.sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Snackbar.make(loginEmail, R.string.login_verified_mail_error_sent, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(loginEmail, R.string.login_verified_mail_error_no_sent, Snackbar.LENGTH_SHORT).show();
                    }
                });
            })).setNegativeButton(R.string.login_verified_mail_error_cancel, (dialog, which) -> {
        }).show();
    }

    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(signinButtonMail, getString(R.string.login_mail_access_error), Snackbar.LENGTH_SHORT).show();
    }

    private void hideLoginButton(boolean hide) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();

        layoutFade.setDuration(1000);
        transitionSet.addTransition(layoutFade);
        TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);

        if (hide) {
            progressBar.setVisibility(View.VISIBLE);
            signinButtonMail.setVisibility(View.GONE);
            signinButtonGoogle.setVisibility(View.GONE);
            loginButtonSignup.setVisibility(View.GONE);
            loginEmailParent.setEnabled(false);
            loginPassParent.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            signinButtonMail.setVisibility(View.VISIBLE);
            signinButtonGoogle.setVisibility(View.VISIBLE);
            loginButtonSignup.setVisibility(View.VISIBLE);
            loginEmailParent.setEnabled(true);
            loginPassParent.setEnabled(true);
        }
    }

    /*private void apuntes() {
        //Dummy
        Toast.makeText(this, String.format(getString(R.string.login_completed), user.getEmail()), Toast.LENGTH_LONG).show();



        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2020,8,22);
        endDate.set(2020,9,1);
        firebaseDatabaseService.saveTrip(new Trip(4L, "Barcelona", "Varsovia", "Precioso viaje desde Barcelona a Varsovia",
                "https://static.ideal.es/www/multimedia/201912/02/media/cortadas/varsovia-kpUH-U90852040771bX-624x385@Ideal.jpg",
                startDate, endDate, 800L), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.i("AcmeExplorer", "Trip insertado correctamente");
                } else {
                    Log.i("AcmeExplorer", "Error al insertar trip: " + databaseError.getMessage());
                }
            }
        });


        // Solo se ejecuta una vez. Una vez se ejecuta, no se queda esperando para más. Para ello se
        // puede usar el metodo addValueEventListener
        firebaseDatabaseService.getTrip("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    TripDTO tripDTO = dataSnapshot.getValue(TripDTO.class);
                    if(tripDTO != null) {
                        Trip trip = new Trip(tripDTO);
                        Toast.makeText(LoginActivity.this, trip.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.get_trip_database_error), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Controla cambios en el elmeneto en concreto. Consume bastante.
        ValueEventListener valueEventListener = firebaseDatabaseService.getTrip("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    TripDTO tripDTO = dataSnapshot.getValue(TripDTO.class);
                    if(tripDTO != null) {
                        Trip trip = new Trip(tripDTO);
                        Log.i("AcmeExplorer", "Elemento modificado individualmente: " + trip.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Se puede desuscribir del listener anterior para ahorrar recursos si ya no lo necesitamos:
        //firebaseDatabaseService.getTrip("2").removeEventListener(valueEventListener);

        // Controla cambios en el precio del objeto únicamente
        firebaseDatabaseService.getTripPrice("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Long price = dataSnapshot.getValue(Long.class);
                    Log.i("AcmeExplorer", "Precio modificado: " + price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ChildEventListener childEventListener = firebaseDatabaseService.getTrip().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    TripDTO tripDTO = dataSnapshot.getValue(TripDTO.class);
                    if(tripDTO != null) {
                        Trip trip = new Trip(tripDTO);
                        Toast.makeText(LoginActivity.this, "Nuevo elemento añadido: " + trip.toString(), Toast.LENGTH_LONG).show();
                        Log.i("AcmeExplorer", "Nuevo elemento añadido: " + trip.toString());
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.get_trip_database_error), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    TripDTO tripDTO = dataSnapshot.getValue(TripDTO.class);
                    if(tripDTO != null) {
                        Trip trip = new Trip(tripDTO);
                        Toast.makeText(LoginActivity.this, "El elemento se ha modificado: " + trip.toString(), Toast.LENGTH_LONG).show();
                        Log.i("AcmeExplorer", "El elemento se ha modificado: " + trip.toString());
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.get_trip_database_error), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    TripDTO tripDTO = dataSnapshot.getValue(TripDTO.class);
                    if(tripDTO != null) {
                        Trip trip = new Trip(tripDTO);
                        Toast.makeText(LoginActivity.this, "El elemento se ha eliminado: " + trip.toString(), Toast.LENGTH_LONG).show();
                        Log.i("AcmeExplorer", "El elemento se ha eliminado: " + trip.toString());
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.get_trip_database_error), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    TripDTO tripDTO = dataSnapshot.getValue(TripDTO.class);
                    if(tripDTO != null) {
                        Trip trip = new Trip(tripDTO);
                        Toast.makeText(LoginActivity.this, "El elemento se ha movido: " + trip.toString(), Toast.LENGTH_LONG).show();
                        Log.i("AcmeExplorer", "El elemento se ha movido: " + trip.toString());
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.get_trip_database_error), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
