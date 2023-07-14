package com.standalone.tradingplan.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.adapters.AdapterFilterable;
import com.standalone.droid.utils.Humanize;
import com.standalone.tradingplan.R;
import com.standalone.tradingplan.database.OrderDb;
import com.standalone.tradingplan.models.Order;

import java.util.Collections;
import java.util.List;

public class OrderAdapter extends AdapterFilterable<Order, OrderAdapter.ViewHolder> {

    private final OrderDb dbHandler;

    public OrderAdapter(Context context, OrderDb dbHandler) {
        super(context);
        this.dbHandler = dbHandler;

        loadItemList();
    }

    @Override
    public void setItemList(List<Order> itemList) {
        Collections.sort(itemList);
        super.setItemList(itemList);
    }

    public void loadItemList() {
        setItemList(dbHandler.fetchAll());
    }

    public Order getItem(int position) {
        return itemList.get(position);
    }

    public void removeItem(int position) {
        Order order = itemList.get(position);
        dbHandler.remove(order.getId());
        itemList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    protected boolean applyFilterCriteria(CharSequence constraint, Order order) {
        return false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(instantiateItemView(R.layout.item_order, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Order order = itemList.get(position);

        holder.tvSymbol.setText(order.getSymbol());
        holder.tvPrice.setText(Humanize.doubleComma(order.getPrice()));
        holder.tvShares.setText(Humanize.intComma(order.getShares()));
        holder.tvDate.setText(order.getDate());
        holder.tvMessage.setText(order.getMessage());
        holder.tvOrderType.setText(order.getType().toString());

        int colorNegative = ContextCompat.getColor(getContext(), R.color.negative);
        int colorAccent = ContextCompat.getColor(getContext(), R.color.accent);
        holder.tvOrderType.setTextColor(order.getType().equals(Order.Type.BUY) ? colorAccent : colorNegative);

        holder.imWarning.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol;
        TextView tvPrice;
        TextView tvShares;
        TextView tvDate;
        TextView tvOrderType;
        TextView tvMessage;
        ImageView imWarning;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSymbol = itemView.findViewById(R.id.tv_symbol);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvShares = itemView.findViewById(R.id.tv_shares);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvOrderType = itemView.findViewById(R.id.tv_order_type);
            tvMessage = itemView.findViewById(R.id.tv_message);
            imWarning = itemView.findViewById(R.id.ic_warning);
        }
    }
}
