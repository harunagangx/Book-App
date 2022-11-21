package com.example.appqlythuvien.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appqlythuvien.MyApplication;
import com.example.appqlythuvien.activities.PdfDetailActivity;
import com.example.appqlythuvien.databinding.RowPdfUserBinding;
import com.example.appqlythuvien.filter.FilterPdfUser;
import com.example.appqlythuvien.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {

    private Context context;

    public ArrayList<ModelPdf> pdfArrayList, filterList;

    private FilterPdfUser filterPdfUser;

    private RowPdfUserBinding binding;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        //*Get data, set data, handle click...

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String bookId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        long timestamp = model.getTimestamp();

        String date = MyApplication.formatTimeStamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);

        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                binding.pdfView,
                binding.progressBar
        );

        MyApplication.loadCategory(
                "" + categoryId,
                holder.categoryTv
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", bookId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {

        if (filterPdfUser == null)
        {
            filterPdfUser = new FilterPdfUser(filterList, this);
        }

        return filterPdfUser;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder {

        TextView titleTv, descriptionTv, categoryTv;
        PDFView pdfView;
        ProgressBar progressBar;

        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);

            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
        }
    }
}
