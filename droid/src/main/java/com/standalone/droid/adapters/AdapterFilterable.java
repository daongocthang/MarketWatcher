package com.standalone.droid.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AdapterFilterable<T, VH extends RecyclerView.ViewHolder> extends BaseAdapter<T,VH> implements Filterable {

    private final Context context;

    public AdapterFilterable(Context context) {
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<T> filteredList;
                String keywords = charSequence.toString();
                if (TextUtils.isEmpty(keywords)) {
                    filteredList = itemList;
                } else {
                    filteredList = new ArrayList<>();
                    for (T t : itemList) {
                        if (applyFilterCriteria(charSequence, t)) {
                            filteredList.add(t);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                @SuppressWarnings("unchecked")
                List<T> filteredList = (List<T>) filterResults.values;
                setItemList(filteredList);
            }
        };
    }

    protected abstract boolean applyFilterCriteria(CharSequence constraint, T t);
}
