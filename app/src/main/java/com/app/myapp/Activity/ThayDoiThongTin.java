package com.app.myapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.Class.User;
import com.app.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ThayDoiThongTin extends AppCompatActivity {
    private Button btnCapNhat;
    private EditText editTextPhone,editText_Ten;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thay_doi_thong_tin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        btnCapNhat=findViewById(R.id.btnCapNhat);
        editText_Ten=findViewById(R.id.editText_Ten);
        editTextPhone=findViewById(R.id.editTextPhone);
        // Nút quay lại
        ImageView imback = findViewById(R.id.imBack);
        imback.setOnClickListener(v -> finish());
        //HIỆN thông tin
        setUserInformation();

        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userId = currentUser.getUid();
                String name= editText_Ten.getText().toString().trim();
                String phone= editTextPhone.getText().toString().trim();
                DatabaseReference databaseReferenceReviews = FirebaseDatabase.getInstance().getReference("User");
                databaseReferenceReviews.child(userId).child("name").setValue(name);
                databaseReferenceReviews.child(userId).child("phone").setValue(phone);
                Toast.makeText(ThayDoiThongTin.this,"Cập Nhật Thành Công",Toast.LENGTH_SHORT).show();
            }
        });


    }
    private  void setUserInformation(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        DatabaseReference databaseReferenceReviews = FirebaseDatabase.getInstance().getReference("User");
        databaseReferenceReviews.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userId = currentUser.getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user1 = snapshot.getValue(User.class);
                    if(userId.equals(user1.getId())) {
                        editText_Ten.setText(user1.getName());
                        editTextPhone.setText(user1.getPhone());
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

}