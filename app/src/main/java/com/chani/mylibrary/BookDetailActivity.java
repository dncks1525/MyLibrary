package com.chani.mylibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chani.mylibrary.data.BookDatabase;
import com.chani.mylibrary.databinding.ActivityBookDetailBinding;

public class BookDetailActivity extends AppCompatActivity {
    private ActivityBookDetailBinding mBinding;
    private boolean mIsBookmarked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        BookDatabase.BookDetails detail = i.getParcelableExtra(LibraryConst.KEY_THUMBNAIL);
        mIsBookmarked = i.getBooleanExtra(LibraryConst.KEY_BOOKMARK, false);
        if (detail != null) {
            mBinding.titleTxt.setText(detail.getTitle());
            mBinding.subtitleTxt.setText(detail.getSubtitle());
            mBinding.authorsTxt.setText(detail.getAuthors());
            mBinding.publisherTxt.setText(detail.getPublisher());
            mBinding.langTxt.setText(detail.getLanguage());
            mBinding.yearTxt.setText("" + detail.getYear());
            mBinding.ratingTxt.setText("" + detail.getRating());
            mBinding.pageTxt.setText("" + detail.getPages());
            mBinding.isbn10Txt.setText(detail.getIsbn10());
            mBinding.isbn13Txt.setText(detail.getIsbn13());
            mBinding.urlTxt.setText(detail.getUrl());
            mBinding.descTxt.setText(detail.getDesc());

            if (mIsBookmarked)
                mBinding.bookmarkImg.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
            mBinding.bookmarkImg.setOnClickListener((view) -> {
                mIsBookmarked = !mIsBookmarked;
                mBinding.bookmarkImg.setBackgroundResource((mIsBookmarked)
                        ? R.drawable.ic_bookmark_black_24dp
                        : R.drawable.ic_bookmark_border_black_24dp);
            });
            Glide.with(this)
                    .load(detail.getImage())
                    .into(mBinding.thumbnailImg);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent i = new Intent();
        i.putExtra(LibraryConst.KEY_BOOKMARK, mIsBookmarked);
        i.putExtra(LibraryConst.KEY_ISBN13, mBinding.isbn13Txt.getText());
        setResult(1, i);
        supportFinishAfterTransition();
        return super.onSupportNavigateUp();
    }
}
