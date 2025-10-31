package com.example.appwork.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appwork.R;
import com.example.appwork.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<NewsItem> newsList = new ArrayList<>();
    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public void addData(List<NewsItem> newsList) {
        this.newsList.addAll(newsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNews;
        TextView tvTitle;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNews = itemView.findViewById(R.id.iv_news);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.tvTitle.setText(newsItem.getTitle());
        holder.tvTime.setText(newsItem.getDate());

        // 加载第一张缩略图
        Glide.with(context)
                .load(newsItem.getThumbnail_pic_s())
                .placeholder(R.drawable.img2)
                .error(R.drawable.img2)
                .into(holder.ivNews);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(newsItem);
            }
        });
    }
}