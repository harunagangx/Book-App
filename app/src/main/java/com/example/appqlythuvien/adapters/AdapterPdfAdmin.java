package com.example.appqlythuvien.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appqlythuvien.MyApplication;
import com.example.appqlythuvien.activities.EditPdfActivity;
import com.example.appqlythuvien.activities.PdfDetailActivity;
import com.example.appqlythuvien.databinding.RowPdfAdminBinding;
import com.example.appqlythuvien.filter.FilterPdfAdmin;
import com.example.appqlythuvien.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private Context context;

    public ArrayList<ModelPdf> pdfArrayList, filterList;

    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filterPdfAdmin;

    private ProgressDialog progressDialog;
    
    private static final String TAG = "PDF_ADAPTER_TAG";

//    public static final long MAX_BYTES_PDF = 50000000;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {

        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
//        long timestamp = model.getTimestamp();

//        String formatDate = MyApplication.formatTimeStamp(timestamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);

        MyApplication.loadCategory(
                "" + categoryId,
                holder.categoryTv
        );
        
        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfView,
                holder.progressBar
        );

       holder.moreBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               openedMoreOptionsDialog(model, holder);
           }
       });

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(context, PdfDetailActivity.class);
               intent.putExtra("bookId", pdfId);
               context.startActivity(intent);
           }
       });
    }

    private void openedMoreOptionsDialog(ModelPdf model, HolderPdfAdmin holder)
    {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chọn chức năng")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0)
                        {
                            //EditBook
                            Intent intent = new Intent(context, EditPdfActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);

                        }
                        else if (i == 1)
                        {
                            //deleteBook
                            MyApplication.deleteBook(
                                    context,
                                    "" + bookId,
                                    "" + bookUrl,
                                    "" + bookTitle);
                        }
                    }
                })
                .show();
    }

//    private void deleteBook(ModelPdf model, HolderPdfAdmin holder)
//    {
//        String bookId = model.getId();
//        String bookUrl = model.getUrl();
//        String bookTitle = model.getTitle();
//
//
//        Log.d(TAG, "deleteBook: Deleting...");
//        progressDialog.setMessage("Đang xóa...");
//        progressDialog.show();
//
//        Log.d(TAG, "deleteBook: Deleting from Storage");
//        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
//        storageReference.delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "onSuccess: Deleted from Storage");
//                        Log.d(TAG, "onSuccess: Now deleting from database");
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
//                        reference.child(bookId)
//                                .removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Log.d(TAG, "onSuccess: Deleted from database");
//                                        progressDialog.dismiss();
//                                        Toast.makeText(context, "Xóa sách thành công", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d(TAG, "onFailure: Falied to delete from storage due to " + e.getMessage());
//                                        Toast.makeText(context, "Xóa sách thất bại", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: Falied to delete from storage due to " + e.getMessage());
//                        progressDialog.dismiss();
//                    }
//                });
//    }

//    private void loadPdfFromUrl(ModelPdf model, HolderPdfAdmin holder)
//    {
//        String pdfUrl = model.getUrl();
//
//        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
//        ref.getBytes(MAX_BYTES_PDF)
//                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Log.d(TAG, "onSuccess: " + model.getTitle() + " successfully got the file");
//
//                        holder.pdfView.fromBytes(bytes)
//                                .pages(0)
//                                .spacing(0)
//                                .swipeHorizontal(false)
//                                .enableSwipe(false)
//                                .onError(new OnErrorListener() {
//                                    @Override
//                                    public void onError(Throwable t) {
//                                        holder.progressBar.setVisibility(View.INVISIBLE);
//                                        Log.d(TAG, "onPageError: " + t.getMessage());
//                                    }
//                                })
//                                .onPageError(new OnPageErrorListener() {
//                                    @Override
//                                    public void onPageError(int page, Throwable t) {
//                                        holder.progressBar.setVisibility(View.INVISIBLE);
//                                        Log.d(TAG, "onPageError: " + t.getMessage());
//                                    }
//                                })
//                                .onLoad(new OnLoadCompleteListener() {
//                                    @Override
//                                    public void loadComplete(int nbPages) {
//                                        holder.progressBar.setVisibility(View.INVISIBLE);
//                                    }
//                                })
//                                .load();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: failed getting file from url due to " + e.getMessage());
//                    }
//                });
//    }

//    private void loadCategory(ModelPdf model, HolderPdfAdmin holder)
//    {
//        String categoryId = model.getCategoryId();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
//        ref.child(categoryId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String category = "" + snapshot.child("category").getValue();
//
//                        holder.categoryTv.setText(category);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterPdfAdmin == null)
        {
            filterPdfAdmin = new FilterPdfAdmin(filterList, this);
        }
        return filterPdfAdmin;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder {

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            moreBtn = binding.moreBtn;
        }
    }
}
