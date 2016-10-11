package ru.yandex.whocallsya.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.network.SearchItem;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder> {

    @NonNull
    private final List<SearchItem> searchItems;
    @NonNull
    private final OnItemClickListener listener;

    public SearchAdapter(@NonNull List<SearchItem> searchItems, @NonNull OnItemClickListener listener) {
        this.searchItems = searchItems;
        this.listener = listener;
    }

    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        SearchItemViewHolder h = new SearchItemViewHolder(v);
        v.setOnClickListener(it -> {
            int adapterPosition = h.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition);
            }
        });
        return h;
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        holder.textTitle.setText(searchItems.get(position).getTitle());
        holder.textUrl.setText(searchItems.get(position).getUrl());
        holder.textDesc.setText(searchItems.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.info_title)
        TextView textTitle;
        @BindView(R.id.info_url)
        TextView textUrl;
        @BindView(R.id.info_desc)
        TextView textDesc;

        SearchItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
