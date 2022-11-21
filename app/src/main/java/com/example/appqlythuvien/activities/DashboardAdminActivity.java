package com.example.appqlythuvien.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.appqlythuvien.R;
import com.example.appqlythuvien.adapters.AdapterCategory;
import com.example.appqlythuvien.databinding.ActivityDashboardAdminBinding;
import com.example.appqlythuvien.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    //view binding
    private ActivityDashboardAdminBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist
    private ArrayList<ModelCategory> categoryArrayList;

    //adapter
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterCategory.getFilter().filter(charSequence);
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Xử lý sự kiện đăng xuất
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        //Xử lý sự kiện thêm thể loại sách mới
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, AddCategoryActivity.class));
            }
        });

        binding.addPDFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, AddPdfActivity.class));
            }
        });
    }

    private void loadCategories()
    {
        //init arraylist
        categoryArrayList = new ArrayList<>();

        //get all categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    Log.e( "onDataChange: ", String.valueOf(model));

                    categoryArrayList.add(model);
                }
                adapterCategory = new AdapterCategory(DashboardAdminActivity.this, categoryArrayList);

                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUser()
    {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null)
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else
        {
            String email = firebaseUser.getEmail();

            binding.subTitleTv.setText(email);
        }
    }
}