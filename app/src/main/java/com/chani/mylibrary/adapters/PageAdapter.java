package com.chani.mylibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chani.mylibrary.R;
import com.chani.mylibrary.data.BookDatabase;
import com.chani.mylibrary.data.BookDatabase.Book;
import com.chani.mylibrary.data.PageInfo;
import com.chani.mylibrary.databinding.ItemPageBinding;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageHolder> {
    public static final int SORT_BY_TITLE_ASC = 2;
    public static final int SORT_BY_TITLE_DSC = 4;
    public static final int SORT_BY_PRICE_ASC = 8;
    public static final int SORT_BY_PRICE_DSC = 16;

    private List<PageInfo> mItems;

    public PageAdapter(List<PageInfo> items) {
        this.mItems = items;
    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new PageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Object object = payloads.get(0);
            if (object == null) {
                holder.update(null);
            } else if (object instanceof List) {
                holder.update((List<Book>) object);
            } else if (object instanceof Integer) {
                holder.sortBy((Integer) object);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    public void addBooks(int position, List<Book> bookList) {
        notifyItemChanged(position, bookList);
    }

    public void clearBooks(int position) {
        mItems.get(position).clearBook();
        notifyItemChanged(position, null);
    }

    public void sortBy(int position, int sort) {
        notifyItemChanged(position, sort);
    }

    public static class PageHolder extends RecyclerView.ViewHolder {
        private ItemPageBinding mBinding;
        private boolean mIsLoading;

        public PageHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemPageBinding.bind(itemView);
        }

        public void bind(PageInfo pageInfo) {
            mIsLoading = false;
            mBinding.recycler.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
            mBinding.recycler.setHasFixedSize(true);
            mBinding.recycler.setItemAnimator(null);
            mBinding.recycler.setAdapter(new BookAdapter(pageInfo.getBookList(), pageInfo.getOnItemClickListener()));

            if (pageInfo.getOnScrollListener() != null) {
                ScrollListener listener = new ScrollListener(pageInfo.getOnScrollListener());
                mBinding.recycler.clearOnScrollListeners();
                mBinding.recycler.addOnScrollListener(listener);
            }
        }

        public void update(List<Book> bookList) {
            mIsLoading = false;
            BookAdapter adapter = (BookAdapter) mBinding.recycler.getAdapter();
            if (adapter != null) {
                if (bookList == null) {
                    adapter.clearItems();
                } else {
                    adapter.addItems(bookList);
                }
            }
        }

        public void sortBy(int sort) {
            BookAdapter adapter = (BookAdapter) mBinding.recycler.getAdapter();
            if (adapter != null) {
                adapter.sortBy(sort);
            }
        }

        public class ScrollListener extends RecyclerView.OnScrollListener {
            private OnScrollListener mOnScrollListener;

            public ScrollListener(OnScrollListener listener) {
                mOnScrollListener = listener;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (!mIsLoading && mOnScrollListener != null) {
                        mIsLoading = true;
                        mOnScrollListener.onLoadMoreIfExists();
                    }
                }
            }
        }
    }

    public interface OnScrollListener {
        void onLoadMoreIfExists();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Book book);
    }
}