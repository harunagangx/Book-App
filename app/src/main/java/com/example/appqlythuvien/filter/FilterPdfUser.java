package com.example.appqlythuvien.filter;

import android.widget.Filter;

import com.example.appqlythuvien.adapters.AdapterPdfUser;
import com.example.appqlythuvien.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {

    ArrayList<ModelPdf> filterList;

    AdapterPdfUser adapterPdfUser;

    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults = new FilterResults();

        if (charSequence != null || charSequence.length() > 0)
        {
            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ModelPdf> filterModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence))
                {
                    filterModels.add(filterList.get(i));
                }
            }

            filterResults.count = filterModels.size();
            filterResults.values = filterModels;
        }
        else
        {
            filterResults.count = filterList.size();
            filterResults.values = filterList;
        }

        return filterResults;

    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //apply filter changes
        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>)filterResults.values;

        adapterPdfUser.notifyDataSetChanged();
    }
}
