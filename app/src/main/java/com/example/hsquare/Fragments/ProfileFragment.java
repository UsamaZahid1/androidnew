package com.example.hsquare.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsquare.HomeActivity;
import com.example.hsquare.MainActivity;
import com.example.hsquare.Prevalent.Prevalent;
import com.example.hsquare.R;
import com.example.hsquare.Singleton;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private CircleImageView profileImageView, googleImage;
    private EditText etFullName, etNumber, etAddress, etPassword;
    private Button btnUpdate;
    private TextView tvChangePic;
    private StorageTask uploadTask;
    private String img_url;
    private Uri imageUri;
    private String myurl;
    private StorageReference storageProfilePictureReference;
    private String checker = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.civ_profileimage);
        googleImage = view.findViewById(R.id.civ_gooleimage);
        etFullName = view.findViewById(R.id.et_profile_name);
        etNumber = view.findViewById(R.id.et_profile_number);
        etAddress = view.findViewById(R.id.et_profile_adress);
        etPassword = view.findViewById(R.id.et_profile_password);
        btnUpdate = view.findViewById(R.id.btn_profile_update);


        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            etPassword.setVisibility(View.VISIBLE);
       //     tvChangePic.setVisibility(View.VISIBLE);
            profileImageView.setVisibility(View.VISIBLE);
            googleImage.setVisibility(View.GONE);

        } else if (Singleton.obj.googleId != null) {
//            tvChangePic.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            profileImageView.setVisibility(View.GONE);
            googleImage.setVisibility(View.VISIBLE);
        } else if (Singleton.obj.fbId != null) {
         //   tvChangePic.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            profileImageView.setVisibility(View.GONE);
            googleImage.setVisibility(View.VISIBLE);
        }


        Drawable alert = getResources().getDrawable(R.drawable.alert);
        alert.setBounds(0, 0, alert.getIntrinsicWidth(), alert.getIntrinsicHeight());

        etNumber.setError("This Phone Number will not change your authentication Phone Number!", alert);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userInfoDisplay(profileImageView, etFullName, etNumber, etAddress, etPassword);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")) {

                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

//        tvChangePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                profileImageView.setVisibility(View.VISIBLE);
//                checker = "clicked";
//
//                CropImage.activity(imageUri)
//                        .setAspectRatio(1, 1)
//                        .start(view.getContext(), ProfileFragment.this);
//            }
//        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(view.getContext(), ProfileFragment.this);
            }
        });


        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        } else {
            startActivity(new Intent(getContext(), HomeActivity.class));
        }
    }

    private void updateOnlyUserInfo() {
        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name", etFullName.getText().toString());
            userMap.put("address", etAddress.getText().toString());
            userMap.put("phoneOrder", etNumber.getText().toString());
            userMap.put("password", etPassword.getText().toString());

            ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            // getActivity().finish();

        } else if (Singleton.obj.googleId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name", etFullName.getText().toString());
            userMap.put("address", etAddress.getText().toString());
            userMap.put("phoneOrder", etNumber.getText().toString());


            ref.child(Singleton.obj.googleId).updateChildren(userMap);


            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            // getActivity().finish();
        } else if (Singleton.obj.fbId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name", etFullName.getText().toString());
            userMap.put("address", etAddress.getText().toString());
            userMap.put("phoneOrder", etNumber.getText().toString());


            ref.child(Singleton.obj.fbId).updateChildren(userMap);


            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
        }


    }

    private void userInfoSaved() {

        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            if (TextUtils.isEmpty(etFullName.getText().toString())) {

                Toast.makeText(getContext(), "Name is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etPassword.getText().toString())) {

                Toast.makeText(getContext(), "Password is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etNumber.getText().toString())) {

                Toast.makeText(getContext(), "Number is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etAddress.getText().toString())) {

                Toast.makeText(getContext(), "Address is Required...", Toast.LENGTH_SHORT).show();

            } else if (checker.equals("clicked")) {
                uploadImage();
            }

        } else if (Singleton.obj.googleId != null) {
            if (TextUtils.isEmpty(etFullName.getText().toString())) {

                Toast.makeText(getContext(), "Name is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etNumber.getText().toString())) {

                Toast.makeText(getContext(), "Number is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etAddress.getText().toString())) {

                Toast.makeText(getContext(), "Address is Required...", Toast.LENGTH_SHORT).show();

            }
        } else if (Singleton.obj.fbId != null) {
            if (TextUtils.isEmpty(etFullName.getText().toString())) {

                Toast.makeText(getContext(), "Name is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etNumber.getText().toString())) {

                Toast.makeText(getContext(), "Number is Required...", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(etAddress.getText().toString())) {

                Toast.makeText(getContext(), "Address is Required...", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            if (imageUri != null) {
                final StorageReference filereference = storageProfilePictureReference
                        .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

                uploadTask = filereference.putFile(imageUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filereference.getDownloadUrl();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUrl = task.getResult();
                                    myurl = downloadUrl.toString();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", etFullName.getText().toString());
                                    userMap.put("address", etAddress.getText().toString());
                                    userMap.put("phoneOrder", etNumber.getText().toString());
                                    userMap.put("password", etPassword.getText().toString());
                                    userMap.put("image", myurl);

                                    ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                                    progressDialog.dismiss();

                                    Toast.makeText(getContext(), "Profile Info Updated Successfully!", Toast.LENGTH_SHORT).show();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Image is not Selected", Toast.LENGTH_LONG).show();

            }
        }


    }

    private void userInfoDisplay(final CircleImageView profileImageView,
                                 final EditText etFullName, final EditText etNumber, final EditText etAddress,
                                 final EditText etPassword) {
        DatabaseReference databaseReference;

        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("image").exists()) {
                            String img = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();
                            String pass = snapshot.child("password").getValue().toString();
                            String number = snapshot.child("phone").getValue().toString();
                            String address = snapshot.child("address").getValue().toString();


                            Picasso.get().load(img).into(profileImageView);
                            etFullName.setText(name);
                            etPassword.setText(pass);
                            etNumber.setText(number);
                            etAddress.setText(address);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Singleton.obj.googleId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Singleton.obj.googleId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("phoneOrder").exists()) {
                            String name = snapshot.child("name").getValue().toString();
                            String number = snapshot.child("phoneOrder").getValue().toString();
                            String address = snapshot.child("address").getValue().toString();

                            GoogleSignInAccount signIn = GoogleSignIn.getLastSignedInAccount(getContext());

                            Picasso.get().load(signIn.getPhotoUrl()).into(googleImage);
                            etFullName.setText(name);
                            etNumber.setText(number);
                            etAddress.setText(address);
                        } else {
                            GoogleSignInAccount signIn = GoogleSignIn.getLastSignedInAccount(getContext());

                            String name = snapshot.child("name").getValue().toString();
                            Picasso.get().load(signIn.getPhotoUrl()).into(googleImage);
                            etFullName.setText(name);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Singleton.obj.fbId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Singleton.obj.fbId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("phoneOrder").exists()) {
                            String name = snapshot.child("name").getValue().toString();
                            String number = snapshot.child("phoneOrder").getValue().toString();
                            String address = snapshot.child("address").getValue().toString();
                            String image = snapshot.child("image").getValue().toString();

                            Picasso.get().load(image).into(googleImage);
                            etFullName.setText(name);
                            etNumber.setText(number);
                            etAddress.setText(address);
                        }else{
                            String image = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();
                            Picasso.get().load(image).into(googleImage);
                            etFullName.setText(name);
                        }
                    }
                }



                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}