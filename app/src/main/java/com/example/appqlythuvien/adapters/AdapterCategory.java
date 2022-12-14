package com.example.appqlythuvien.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appqlythuvien.activities.PdfListAdminActivity;
import com.example.appqlythuvien.databinding.RowCategoryBinding;
import com.example.appqlythuvien.filter.FilterCategory;
import com.example.appqlythuvien.models.ModelCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {

    private Context context;

    public ArrayList<ModelCategory> categoryArrayList, filterList;

    private RowCategoryBinding binding;

    private FilterCategory filterCategory;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }


    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {

        //get data
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.categoryTv.setText(category);

        //X??? l?? n??t x??a
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("X??a th??? lo???i")
                        .setMessage("B???n c?? mu???n th??? lo???i s??ch n??y kh??ng?")
                        .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "??ang x??a...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        //X??? l?? s??? ki???n x??a category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("X??a")
                        .setMessage("B???n c?? ch???c mu???n x??a th??? lo???i s??ch n??y kh??ng?")
                        .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCategory(model, holder);
                            }
                        })
                        .setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        //X??? l?? s??? ki???n b???m v??o s??ch ??i t???i PdfListAdminActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfListAdminActivity.class);
                intent.putExtra("categoryId", id);
                intent.putExtra("categoryTitle", category);
                context.startActivity(intent);
            }
        });
    }

    private void deleteCategory(ModelCategory model, HolderCategory holder)
    {
        String id = model.getId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "X??a th??nh c??ng...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter()
    {
        if (filterCategory == null)
        {
            filterCategory = new FilterCategory(filterList, this);
        }

        return filterCategory;
    }

    //view holder to hold UI row_Category
    class HolderCategory extends RecyclerView.ViewHolder {

        //UI views of row_category.xml
        TextView categoryTv;
        ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            categoryTv = binding.categoryTv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
