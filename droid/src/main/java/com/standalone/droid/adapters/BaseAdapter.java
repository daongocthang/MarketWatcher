package com.standalone.droid.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> itemList=new ArrayList<>();

    public View instantiateItemView(@LayoutRes int resId, @NotNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public T getItem(int pos){
        return itemList.get(pos);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setItemList(List<T> itemList){
        this.itemList=itemList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }

}
