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
import ru.yandex.whocallsya.service.SearchAsyncTask;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder> {

    @NonNull
    private final List<SearchAsyncTask.Response> responses;
    @NonNull
    private final OnItemClickListener listener;

    public SearchAdapter(@NonNull List<SearchAsyncTask.Response> responses, @NonNull OnItemClickListener listener) {
        this.responses = responses;
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
        holder.textTitle.setText(responses.get(position).getTitle());
        holder.textUrl.setText(responses.get(position).getUrl());
        holder.textDesc.setText(responses.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.info_title) TextView textTitle;
        @BindView(R.id.info_url) TextView textUrl;
        @BindView(R.id.info_desc) TextView textDesc;

        SearchItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
