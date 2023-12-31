package com.standalone.marketwatcher.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.adapters.AdapterFilterable;
import com.standalone.droid.utils.Humanize;
import com.standalone.marketwatcher.R;
import com.standalone.marketwatcher.database.OrderDb;
import com.standalone.marketwatcher.models.Order;
import com.standalone.marketwatcher.models.StockRealTime;

import java.util.Collections;
import java.util.List;

public class OrderAdapter extends AdapterFilterable<Order, OrderAdapter.ViewHolder> {

    private final OrderDb dbHandler;
    private List<StockRealTime> stockRealTimes;

    public OrderAdapter(Context context, OrderDb dbHandler) {
        super(context);
        this.dbHandler = dbHandler;
        loadItemList();
    }

    public StockRealTime find(String stockNo) {
        if (stockRealTimes == null || stockRealTimes.size() == 0) return null;
        return stockRealTimes.stream().filter(s -> s.stockNo != null && s.stockNo.equals(stockNo)).findFirst().orElse(null);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setStockRealTimes(List<StockRealTime> stockRealTimes) {
        this.stockRealTimes = stockRealTimes;
        notifyDataSetChanged();
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
        String[] orderTypeArray = getContext().getResources().getStringArray(R.array.order_type_array);

        holder.tvSymbol.setText(order.getCode());
        holder.tvOrderPrice.setText(Humanize.doubleComma(order.getTarget()));
        holder.tvDate.setText(order.getDate());
        holder.tvMessage.setText(order.getMessage());
        holder.tvOrderType.setText(orderTypeArray[order.getType()]);


        int colorNegative = ContextCompat.getColor(getContext(), R.color.negative);
        int colorAccent = ContextCompat.getColor(getContext(), R.color.accent);
        holder.tvOrderType.setTextColor(order.getType() == Order.TYPE_LONG ? colorAccent : colorNegative);

        StockRealTime s = find(order.getStockNo());
        if (s != null) {
            double mp = (double) s.getPrice() / 1000;
            holder.tvMatchedPrice.setText(Humanize.doubleComma(mp));
            holder.tvMatchedPrice.setTextColor(s.getColor(getContext()));

            boolean hidden = true;

            if (order.getTarget() <= mp && order.getType() == Order.TYPE_LONG) {
                hidden = false;
            } else if (order.getTarget() >= mp && order.getType() == Order.TYPE_SHORT) {
                hidden = false;
            }


            holder.tvDone.setVisibility(hidden ? View.INVISIBLE : View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol;
        TextView tvOrderPrice;
        TextView tvDate;
        TextView tvOrderType;
        TextView tvMessage;
        TextView tvMatchedPrice;

        TextView tvDone;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSymbol = itemView.findViewById(R.id.tv_symbol);
            tvOrderPrice = itemView.findViewById(R.id.tv_order_price);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvOrderType = itemView.findViewById(R.id.tv_order_type);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvMatchedPrice = itemView.findViewById(R.id.tv_matched_price);
            tvDone = itemView.findViewById(R.id.tv_done);
        }
    }


}
