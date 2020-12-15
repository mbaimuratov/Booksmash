package com.example.booksmash;

import java.io.Serializable;

public class BookItem implements Serializable {
    private String bookName;
    private String authorName;
    private int publishYear;
    private int pageCount;
    private int rating;
    private String genre;
    private int timeSpent;
    private int pageCountProgress;
    private String photoUrl;

    public BookItem() { }

    public BookItem(String bookName, String authorName, int publishYear, int pageCount, int rating,
                    String genre, int timeSpent, int pageCountProgress, String photoUrl) {
        this.setBookName(bookName);
        this.setAuthorName(authorName);
        this.setPublishYear(publishYear);
        this.setPageCount(pageCount);
        this.setRating(rating);
        this.setGenre(genre);
        this.setTimeSpent(timeSpent);
        this.setPageCountProgress(pageCountProgress);
        this.setPhotoUrl(photoUrl);
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getPageCountProgress() {
        return pageCountProgress;
    }

    public void setPageCountProgress(int pageCountProgress) {
        this.pageCountProgress = pageCountProgress;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "bookName='" + bookName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", publishYear=" + publishYear +
                ", pageCount=" + pageCount +
                ", rating=" + rating +
                ", genre='" + genre + '\'' +
                ", timeSpent=" + timeSpent +
                ", pageCountProgress=" + pageCountProgress +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
