package com.example.hsquare.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsquare.ConfirmOrderAcitvity;
import com.example.hsquare.HomeActivity;
import com.example.hsquare.Model.Cart;
import com.example.hsquare.Prevalent.Prevalent;
import com.example.hsquare.ProductsDetailActivity;
import com.example.hsquare.R;
import com.example.hsquare.Singleton;
import com.example.hsquare.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnProceed;
    private TextView tvTotalPrice, tvMsg, tvIsEmpty;

    private int overAllTotalPrice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.rv_cart);
        btnProceed = view.findViewById(R.id.btn_cart_proceed);
        tvTotalPrice = view.findViewById(R.id.tv_cart_totalprice);
        tvMsg = view.findViewById(R.id.tv_cart_msg);
        tvIsEmpty = view.findViewById(R.id.tv_cart_isempty);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ////-----------checking if cart is empty or not
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            checkRef.child("Users Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Prevalent.currentOnlineUser.getPhone())) {

                        btnProceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ConfirmOrderAcitvity.class);
                                intent.putExtra("totalAmount", String.valueOf(overAllTotalPrice));
                                startActivity(intent);
                            }
                        });

                    } else {
                        btnProceed.setVisibility(View.GONE);
                        tvIsEmpty.setVisibility(View.VISIBLE);

                        //Toast.makeText(getContext(), "Cart is empty, Please Add some items to cart!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Singleton.obj.googleId != null) {
            checkRef.child("Google Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Singleton.obj.googleId)) {

                        btnProceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ConfirmOrderAcitvity.class);
                                intent.putExtra("totalAmount", String.valueOf(overAllTotalPrice));
                                startActivity(intent);
                            }
                        });

                    } else {
                        btnProceed.setVisibility(View.GONE);
                        tvIsEmpty.setVisibility(View.VISIBLE);

                        //Toast.makeText(getContext(), "Cart is empty, Please Add some items to cart!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Singleton.obj.fbId != null) {
            checkRef.child("Facebook Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Singleton.obj.fbId)) {

                        btnProceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ConfirmOrderAcitvity.class);
                                intent.putExtra("totalAmount", String.valueOf(overAllTotalPrice));
                                startActivity(intent);
                            }
                        });

                    } else {
                        btnProceed.setVisibility(View.GONE);
                        tvIsEmpty.setVisibility(View.VISIBLE);

                        //Toast.makeText(getContext(), "Cart is empty, Please Add some items to cart!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        return view;
    }

    private void checkOrdersState() {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("Orders");

        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            reference.child(Prevalent.currentOnlineUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String shippingState = snapshot.child("state").getValue().toString();
                        String UserName = snapshot.child("name").getValue().toString();

                        if (shippingState.equals("shipped")) {

                            tvTotalPrice.setText("Dear " + UserName + "\n order is shipped successfully");
                            recyclerView.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText("Your final order has been shipped, soon you will get your order at your door step!");

                            btnProceed.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You can order more products once your received your first order!", Toast.LENGTH_SHORT).show();
                        } else if (shippingState.equals("not shipped")) {

                            tvTotalPrice.setText("Shipping status : Not Shipped");
                            recyclerView.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvIsEmpty.setVisibility(View.GONE);

                            btnProceed.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You can order more products once your received your first order!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Singleton.obj.googleId != null) {
            reference.child("Google Users").child(Singleton.obj.googleId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String shippingState = snapshot.child("state").getValue().toString();
                        String UserName = snapshot.child("name").getValue().toString();

                        if (shippingState.equals("shipped")) {

                            tvTotalPrice.setText("Dear " + UserName + "\n order is shipped successfully");
                            recyclerView.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText("Your final order has been shipped, soon you will get your order at your door step!");

                            btnProceed.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You can order more products once your received your first order!", Toast.LENGTH_SHORT).show();
                        } else if (shippingState.equals("not shipped")) {

                            tvTotalPrice.setText("Shipping status : Not Shipped");
                            recyclerView.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvIsEmpty.setVisibility(View.GONE);

                            btnProceed.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You can order more products once your received your first order!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Singleton.obj.fbId != null) {

            reference.child("Facebook Users").child(Singleton.obj.fbId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String shippingState = snapshot.child("state").getValue().toString();
                        String UserName = snapshot.child("name").getValue().toString();

                        if (shippingState.equals("shipped")) {

                            tvTotalPrice.setText("Dear " + UserName + "\n order is shipped successfully");
                            recyclerView.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText("Your final order has been shipped, soon you will get your order at your door step!");

                            btnProceed.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You can order more products once your received your first order!", Toast.LENGTH_SHORT).show();
                        } else if (shippingState.equals("not shipped")) {

                            tvTotalPrice.setText("Shipping status : Not Shipped");
                            recyclerView.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvIsEmpty.setVisibility(View.GONE);

                            btnProceed.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You can order more products once your received your first order!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        checkOrdersState();
        if (Singleton.obj.googleId == null && Singleton.obj.fbId == null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");

            FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                    .setQuery(databaseReference.child("Users Cart")
                            .child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class)
                    .build();


            FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                    holder.tvPname.setText(model.getPname());
                    holder.tvprice.setText("Price : " + model.getPrice());
                    holder.tvquantity.setText("Qty : " + model.getQuantity());

                    int singleItemTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                    overAllTotalPrice = overAllTotalPrice + singleItemTotalPrice;
                    tvTotalPrice.setText("Total: Rs. " + overAllTotalPrice);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Edit",
                                            "Remove"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Cart options:");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        Intent intent = new Intent(getContext(), ProductsDetailActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);
                                    }
                                    if (i == 1) {
                                        databaseReference.child("Users Cart")
                                                .child(Prevalent.currentOnlineUser.getPhone())
                                                .child("Products")
                                                .child(model.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "Item removed successfully!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                            builder.show();
                        }


                    });
                }

                @NonNull
                @Override
                public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_items, parent, false);

                    return new CartViewHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
            adapter.startListening();

        } else if (Singleton.obj.googleId != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");

            FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                    .setQuery(databaseReference.child("Google Users")
                            .child(Singleton.obj.googleId).child("Products"), Cart.class)
                    .build();


            FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                    holder.tvPname.setText(model.getPname());
                    holder.tvprice.setText("Price : " + model.getPrice());
                    holder.tvquantity.setText("Qty : " + model.getQuantity());

                    int singleItemTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                    overAllTotalPrice = overAllTotalPrice + singleItemTotalPrice;
                    tvTotalPrice.setText("Total: Rs. " + overAllTotalPrice);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Edit",
                                            "Remove"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Cart options:");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        Intent intent = new Intent(getContext(), ProductsDetailActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);
                                    }
                                    if (i == 1) {
                                        databaseReference.child("Google Users")
                                                .child(Singleton.obj.googleId)
                                                .child("Products")
                                                .child(model.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "Item removed successfully!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                            builder.show();
                        }


                    });
                }

                @NonNull
                @Override
                public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_items, parent, false);

                    return new CartViewHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
            adapter.startListening();

        } else if (Singleton.obj.fbId != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");

            FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                    .setQuery(databaseReference.child("Facebook Users")
                            .child(Singleton.obj.fbId).child("Products"), Cart.class)
                    .build();


            FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                    holder.tvPname.setText(model.getPname());
                    holder.tvprice.setText("Price : " + model.getPrice());
                    holder.tvquantity.setText("Qty : " + model.getQuantity());

                    int singleItemTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                    overAllTotalPrice = overAllTotalPrice + singleItemTotalPrice;
                    tvTotalPrice.setText("Total: Rs. " + overAllTotalPrice);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Edit",
                                            "Remove"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Cart options:");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        Intent intent = new Intent(getContext(), ProductsDetailActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);
                                    }
                                    if (i == 1) {
                                        databaseReference.child("Facebook Users")
                                                .child(Singleton.obj.fbId)
                                                .child("Products")
                                                .child(model.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "Item removed successfully!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                            builder.show();
                        }


                    });
                }

                @NonNull
                @Override
                public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_items, parent, false);

                    return new CartViewHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }
}