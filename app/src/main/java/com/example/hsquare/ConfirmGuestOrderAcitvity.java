package com.example.hsquare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hsquare.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmGuestOrderAcitvity extends AppCompatActivity {

    EditText etName, etAddress, etNumber, etCityName;
    Button btnConfirm;
    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_guestorder);

        totalAmount = getIntent().getStringExtra("guestTotalAmount");

        etName = findViewById(R.id.et_confirm_guestname);
        etAddress = findViewById(R.id.et_confirm_guestaddress);
        etNumber = findViewById(R.id.et_confirm_guestname);
        etCityName = findViewById(R.id.et_confirm_guestcityname);
        btnConfirm = findViewById(R.id.btn_confirm_guestconfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEt();
            }
        });
    }

    //
    private void checkEt() {

        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError("Your Name is required can't be empty!");

        } else if (TextUtils.isEmpty(etNumber.getText().toString())) {
            etName.setError("Your Number is required can't be empty!");

        } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
            etName.setError("Your Address is required can't be empty!");

        } else if (TextUtils.isEmpty(etCityName.getText().toString())) {
            etName.setError("City Name is required can't be empty!");

        } else {
            cofirmOrder();
        }
    }

    private void cofirmOrder() {

        final String saveCurrentDate, saveCurrentTime;
        Calendar callForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        String orderId = UUID.randomUUID().toString();


        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Guest orders")
                .child(Singleton.obj.guestid).child(orderId);

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("TotalAmount", totalAmount);
        ordersMap.put("name", etName.getText().toString());
        ordersMap.put("phoneNumber", etNumber.getText().toString());
        ordersMap.put("address", etAddress.getText().toString());
        ordersMap.put("cityName", etCityName.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");


        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("Guest Cart")
                            .child(Singleton.obj.guestid)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmGuestOrderAcitvity.this, "Your Order has been placed!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmGuestOrderAcitvity.this, HomeGuestAcitvity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });
    }
}
