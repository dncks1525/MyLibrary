package com.chani.mylibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chani.mylibrary.R;
import com.chani.mylibrary.data.BookData.Book;
import com.chani.mylibrary.data.PageItem;
import com.chani.mylibrary.databinding.ItemPageBinding;

import java.util.List;

import static com.chani.mylibrary.data.PageItem.*;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageHolder> {
    public static final int REQUEST_LOAD_MORE = 1;
    public static final int REQUEST_SORT_BY_TITLE_ASC = 2;
    public static final int REQUEST_SORT_BY_TITLE_DSC = 4;
    public static final int REQUEST_SORT_BY_PRICE_ASC = 8;
    public static final int REQUEST_SORT_BY_PRICE_DSC = 16;

    private List<PageItem> mItems;

    public PageAdapter(List<PageItem> items) {
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
            holder.request((int)payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    public void request(int position, int what) {
        notifyItemChanged(position, what);
    }

    public static class PageHolder extends RecyclerView.ViewHolder {
        private ItemPageBinding mBinding;

        public PageHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemPageBinding.bind(itemView);
        }

        public void bind(PageItem pageItem) {
            mBinding.recycler.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
            mBinding.recycler.setHasFixedSize(true);
            mBinding.recycler.setItemAnimator(null);
            mBinding.recycler.setAdapter(new BookAdapter(pageItem.getBookList(), pageItem.getOnItemClickListener()));

            if (pageItem.getOnScrollListener() != null) {
                ScrollListener listener = new ScrollListener(pageItem.getOnScrollListener());
                mBinding.recycler.clearOnScrollListeners();
                mBinding.recycler.addOnScrollListener(listener);
            }
        }

        public void request(int what) {
            if (mBinding.recycler.getAdapter() != null) {
                BookAdapter adapter = (BookAdapter) mBinding.recycler.getAdapter();
                adapter.request(what);
            }
        }

        public static class ScrollListener extends RecyclerView.OnScrollListener {
            private OnScrollListener mOnScrollListener;

            public ScrollListener(OnScrollListener listener) {
                mOnScrollListener = listener;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    mOnScrollListener.onLoadMoreIfExists();
                }
            }
        }
    }
}