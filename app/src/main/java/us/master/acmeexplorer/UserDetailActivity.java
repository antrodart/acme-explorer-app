package us.master.acmeexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

import us.master.acmeexplorer.entity.User;

public class UserDetailActivity extends AppCompatActivity {

    public static String USER_PRINCIPAL = "userPrincipal";
    private ImageView profilePhotoIV;
    private TextView nameTV;
    private TextView surnameTV;
    private TextView emailTV;
    private TextView cityTV;
    private TextView birthDateTV;
    private Button editProfileButton;
    private Button viewCloseUsersButton;
    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private User user;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x513;
    private static final int TAKE_PHOTO_CODE = 0x514;
    private Button takePictureButton;
    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        profilePhotoIV = findViewById(R.id.user_detail_picture);
        nameTV = findViewById(R.id.user_detail_name);
        surnameTV = findViewById(R.id.user_detail_surname);
        emailTV = findViewById(R.id.user_detail_email);
        cityTV = findViewById(R.id.user_detail_city);
        birthDateTV = findViewById(R.id.user_detail_birthdate);
        editProfileButton = findViewById(R.id.user_detail_edit_button);
        takePictureButton = findViewById(R.id.user_detail_change_picture_button);
        viewCloseUsersButton = findViewById(R.id.user_detail_usuarios_cerca);

        user = getIntent().getParcelableExtra(USER_PRINCIPAL);

        if (user != null) {
            nameTV.setText(user.getName());
            surnameTV.setText(user.getSurname());
            emailTV.setText(user.getEmail());
            cityTV.setText(user.getCity());
            if (user.getBirthDate() != null) {
                birthDateTV.setText(Util.dateFormatSpanish(user.getBirthDate()));
            }

            if (!Util.checkStringEmpty(user.getPicture())) {
                Glide.with(UserDetailActivity.this)
                        .load(user.getPicture())
                        .placeholder(R.drawable.default_user_pic)
                        .centerCrop()
                        .into(profilePhotoIV);
            }

            editProfileButton.setOnClickListener(v -> redirectEditUserProfile());
        } else {
            Toast.makeText(UserDetailActivity.this, getText(R.string.error_load_user_profile), Toast.LENGTH_LONG).show();
            editProfileButton.setVisibility(View.GONE);
        }

        takePictureButton.setOnClickListener(l -> takePicture());

        viewCloseUsersButton.setOnClickListener(b -> redirectCloseUser());

    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(takePictureButton, R.string.take_picture_camera_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_camera_rationale_ok, click -> {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            // Permiso está concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(takePictureButton, R.string.save_picture_external_storage, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_camera_rationale_ok, click -> {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                }
            } else {
                // Aquí tenemos todos los permisos. Primero establecemos la política de privacidad
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                String dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/masterItsAcmeExplorerApp";
                File newFile = new File(dir);
                newFile.mkdirs();

                file = dir + "/" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                File newFilePicture = new File(file);
                try {
                    newFilePicture.createNewFile();
                } catch (Exception ignore) {
                }

                Uri outputFileDir = Uri.fromFile(newFilePicture);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            File filePicture = new File(file);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child(filePicture.getName());
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(filePicture));
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.e("AcmeExplorer", "Firebase Storage: completed " + task.getResult().getTotalByteCount());

                    storageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            user.setPicture(task1.getResult().toString());
                            FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

                            firebaseDatabaseService.saveUser(user, ((databaseError, databaseReference) -> {
                                if (databaseError == null) {
                                    Log.i("AcmeExplorer", "El usuario se ha actualizado correctamente: " + user.getId());
                                    Glide.with(UserDetailActivity.this)
                                            .load(task1.getResult())
                                            .placeholder(R.drawable.default_user_pic)
                                            .centerCrop()
                                            .into(profilePhotoIV);
                                } else {
                                    Log.i("AcmeExplorer", "Error al actualizar el usuario: " + databaseError.getMessage());
                                }
                            }));
                        }
                    });
                }
            });
            uploadTask.addOnFailureListener(e -> Log.e("AcmeExplorer", "Firebase Storage: error " + e.getMessage()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, R.string.camera_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, R.string.storage_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void redirectEditUserProfile() {
        Intent intent = new Intent(this, UserEditActivity.class);
        intent.putExtra(USER_PRINCIPAL, user);
        startActivity(intent);
    }

    private void redirectCloseUser() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra(USER_PRINCIPAL, user);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.USER_PRINCIPAL, user);
        startActivity(intent);
    }
}
