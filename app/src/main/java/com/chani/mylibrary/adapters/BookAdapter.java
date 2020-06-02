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
import com.chani.mylibrary.data.BookData;
import com.chani.mylibrary.data.BookData.Book;
import com.chani.mylibrary.data.PageItem;
import com.chani.mylibrary.data.PageItem.OnItemClickListener;
import com.chani.mylibrary.databinding.ItemBookBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {
    private List<Book> mItems;
    private OnItemClickListener mOnItemClickListener;

    public BookAdapter(List<Book> items, OnItemClickListener listener) {
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
        Book item = mItems.get(position);
        holder.bind(item, mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    public void request(int what) {
        switch (what) {
            case PageAdapter.REQUEST_LOAD_MORE:
                notifyItemInserted(getItemCount());
                break;
            case PageAdapter.REQUEST_SORT_BY_TITLE_ASC:
                Comparator<Book> c1 = ((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
                Collections.sort(mItems, c1);
                notifyDataSetChanged();
                break;
            case PageAdapter.REQUEST_SORT_BY_TITLE_DSC:
                Comparator<Book> c2 = ((b1, b2) -> b2.getTitle().compareTo(b1.getTitle()));
                Collections.sort(mItems, c2);
                notifyDataSetChanged();
                break;
            case PageAdapter.REQUEST_SORT_BY_PRICE_ASC:
                Comparator<Book> c3 = ((b1, b2) -> {
                    float f1 = Float.parseFloat(b1.getPrice().substring(1));
                    float f2 = Float.parseFloat(b2.getPrice().substring(1));
                    return Float.compare(f1, f2);
                });
                Collections.sort(mItems, c3);
                notifyDataSetChanged();
                break;
            case PageAdapter.REQUEST_SORT_BY_PRICE_DSC:
                Comparator<Book> c4 = ((b1, b2) -> {
                    float f1 = Float.parseFloat(b1.getPrice().substring(1));
                    float f2 = Float.parseFloat(b2.getPrice().substring(1));
                    return Float.compare(f2, f1);
                });
                Collections.sort(mItems, c4);
                notifyDataSetChanged();
                break;
        }
    }

    public static class BookHolder extends RecyclerView.ViewHolder {
        private ItemBookBinding mBinding;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemBookBinding.bind(itemView);
        }

        public void bind(Book book, OnItemClickListener listener) {
            Animation anim = AnimationUtils.loadAnimation(itemView.getContext(), android.R.anim.fade_in);
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
    }
}