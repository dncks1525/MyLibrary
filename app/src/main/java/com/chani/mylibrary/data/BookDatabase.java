package com.chani.mylibrary.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BookDatabase {
    private int error;
    private int total;
    private int page;
    private List<Book> books;

    public int getError() {
        return error;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public List<Book> getBooks() {
        return books;
    }

    public static class Book {
        private String title;
        private String subtitle;
        private String isbn13;
        private String price;
        private String image;
        private String url;
        private boolean isBookmarked;

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getIsbn13() {
            return isbn13;
        }

        public String getPrice() {
            return price;
        }

        public String getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }

        public boolean isBookmarked() {
            return isBookmarked;
        }

        public void setBookmarked(boolean bookmarked) {
            isBookmarked = bookmarked;
        }
    }

    public static class BookDetails implements Parcelable {
        private int error;
        private String title;
        private String subtitle;
        private String authors;
        private String publisher;
        private String language;
        private String isbn10;
        private String isbn13;
        private int pages;
        private int year;
        private int rating;
        private String desc;
        private String price;
        private String image;
        private String url;
        private String memo;

        protected BookDetails(Parcel in) {
            this.error = in.readInt();
            this.title = in.readString();
            this.subtitle = in.readString();
            this.authors = in.readString();
            this.publisher = in.readString();
            this.language = in.readString();
            this.isbn10 = in.readString();
            this.isbn13 = in.readString();
            this.pages = in.readInt();
            this.year = in.readInt();
            this.rating = in.readInt();
            this.desc = in.readString();
            this.price = in.readString();
            this.image = in.readString();
            this.url = in.readString();
            this.memo = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.error);
            dest.writeString(this.title);
            dest.writeString(this.subtitle);
            dest.writeString(this.authors);
            dest.writeString(this.publisher);
            dest.writeString(this.language);
            dest.writeString(this.isbn10);
            dest.writeString(this.isbn13);
            dest.writeInt(this.pages);
            dest.writeInt(this.year);
            dest.writeInt(this.rating);
            dest.writeString(this.desc);
            dest.writeString(this.price);
            dest.writeString(this.image);
            dest.writeString(this.url);
            dest.writeString(this.memo);
        }

        public static final Creator<BookDetails> CREATOR = new Creator<BookDetails>() {
            @Override
            public BookDetails createFromParcel(Parcel source) {
                return new BookDetails(source);
            }

            @Override
            public BookDetails[] newArray(int size) {
                return new BookDetails[size];
            }
        };

        public int getError() {
            return error;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getAuthors() {
            return authors;
        }

        public String getPublisher() {
            return publisher;
        }

        public String getLanguage() {
            return language;
        }

        public String getIsbn10() {
            return isbn10;
        }

        public String getIsbn13() {
            return isbn13;
        }

        public int getPages() {
            return pages;
        }

        public int getYear() {
            return year;
        }

        public int getRating() {
            return rating;
        }

        public String getDesc() {
            return desc;
        }

        public String getPrice() {
            return price;
        }

        public String getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }

        public String getMemo() {
            return memo;
        }
    }
}
