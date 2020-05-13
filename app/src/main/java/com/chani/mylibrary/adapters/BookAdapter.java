package com.chani.mylibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chani.mylibrary.R;
import com.chani.mylibrary.data.BookDatabase;
import com.chani.mylibrary.databinding.ItemBookBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {
    private List<BookDatabase.Book> mItems;
    private PageAdapter.OnItemClickListener mOnItemClickListener;

    public BookAdapter(List<BookDatabase.Book> items, PageAdapter.OnItemClickListener listener) {
        this.mItems = items;
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        BookDatabase.Book item = mItems.get(position);
        holder.bind(item, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.update((BookDatabase.Book) payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    public void addItems(List<BookDatabase.Book> items) {
        if (mItems != null) {
            int size = mItems.size();
            mItems.addAll(items);
            notifyItemInserted(size);
        } else {
            mItems = items;
            notifyDataSetChanged();
        }
    }

    public void clearItems() {
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    public void sortBy(int sort) {
        if (mItems != null && mItems.size() > 0) {
            Comparator<BookDatabase.Book> comparator = (book1, book2) -> {
                switch (sort) {
                    case PageAdapter.SORT_BY_TITLE_ASC: {
                        return book1.getTitle().compareTo(book2.getTitle());
                    }
                    case PageAdapter.SORT_BY_TITLE_DSC: {
                        return book2.getTitle().compareTo(book1.getTitle());
                    }
                    case PageAdapter.SORT_BY_PRICE_ASC: {
                        float t1 = Float.parseFloat(book1.getPrice().substring(1));
                        float t2 = Float.parseFloat(book2.getPrice().substring(1));
                        return Float.compare(t1, t2);
                    }
                    case PageAdapter.SORT_BY_PRICE_DSC: {
                        float t1 = Float.parseFloat(book1.getPrice().substring(1));
                        float t2 = Float.parseFloat(book2.getPrice().substring(1));
                        return Float.compare(t2, t1);
                    }
                }

                return 0;
            };

            Collections.sort(mItems, comparator);
            notifyDataSetChanged();
        }
    }

    public static class BookHolder extends RecyclerView.ViewHolder {
        private ItemBookBinding mBinding;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemBookBinding.bind(itemView);
        }

        public void bind(BookDatabase.Book book, PageAdapter.OnItemClickListener listener) {
            Animation anim =  AnimationUtils.loadAnimation(itemView.getContext(), android.R.anim.fade_in);
            anim.setDuration(700);
            itemView.startAnimation(anim);

            mBinding.titleTxt.setText(book.getTitle());
            mBinding.subtitleTxt.setText(book.getSubtitle());
            mBinding.priceTxt.setText(book.getPrice());
            Glide.with(itemView)
                    .load(book.getImage())
                    .thumbnail(0.1f)
                    .into(mBinding.thumbnailImg);

            if (listener != null) {
                itemView.setOnClickListener(view -> listener.onItemClick(mBinding.thumbnailImg, book));
            }
        }

        public void update(BookDatabase.Book book) {
            mBinding.titleTxt.setText(book.getTitle());
            mBinding.subtitleTxt.setText(book.getSubtitle());
            mBinding.priceTxt.setText(book.getPrice());
            Glide.with(itemView)
                    .load(book.getImage())
                    .thumbnail(0.1f)
                    .into(mBinding.thumbnailImg);
        }
    }
}