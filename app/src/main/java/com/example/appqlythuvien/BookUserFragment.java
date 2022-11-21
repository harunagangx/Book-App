package com.example.appqlythuvien;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appqlythuvien.adapters.AdapterPdfUser;
import com.example.appqlythuvien.databinding.FragmentBookUserBinding;
import com.example.appqlythuvien.models.ModelCategory;
import com.example.appqlythuvien.models.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookUserFragment extends Fragment {

    private String categoryId;
    private String category;
    private String uid;

    private ArrayList<ModelPdf> pdfArrayList;

    private AdapterPdfUser adapterPdfUser;

    private FragmentBookUserBinding binding;

    private static final String TAG = "BOOK_USER_TAG";

    public BookUserFragment() {
        // Required empty public constructor
    }



    public static BookUserFragment newInstance(String categoryId, String category, String uid) {
        BookUserFragment fragment = new BookUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookUserBinding.inflate(LayoutInflater.from(getContext()), container, false);

        Log.d(TAG, "onCreateView: Category: " + category);

        if (category.equals("All"))
        {
            loadAllBooks();
        }
        else if (category.equals("Most Viewed"))
        {
            loadMostViewedBooks("viewCount");
        }
        else
        {
            loadCategorizedBook();
        }

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterPdfUser.getFilter().filter(charSequence);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }

    private void loadCategorizedBook() {

        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        pdfArrayList.clear();

                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            ModelPdf model = ds.getValue(ModelPdf.class);

                            pdfArrayList.add(model);
                        }

                        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);

                        binding.bookRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMostViewedBooks(String viewCount) {

        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(viewCount).limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                pdfArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren())
                {
                    ModelPdf model = ds.getValue(ModelPdf.class);

                    pdfArrayList.add(model);
                }

                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);

                binding.bookRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAllBooks() {

        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                pdfArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren())
                {
                    ModelPdf model = ds.getValue(ModelPdf.class);

                    pdfArrayList.add(model);
                }

                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);

                binding.bookRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}