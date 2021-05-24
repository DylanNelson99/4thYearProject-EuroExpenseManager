package com.project.euroexpensemanager.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class NewEntryModelClass implements Parcelable {
    private String image;
    private String type;
    private String title;
    private String description;
    private String date;
    private String time;
    private String amount;
    private String category;
    private String userId;
    private String docId;
    private Timestamp currentTimeStamp;
    private String monthOfYear;

    public NewEntryModelClass() {
    }

    public NewEntryModelClass(String image, String type, String title, String description, String date, String time, String amount, String category, String userId, String docId, Timestamp currentTimeStamp, String monthOfYear) {
        this.image = image;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.category = category;
        this.userId = userId;
        this.docId = docId;
        this.currentTimeStamp = currentTimeStamp;
        this.monthOfYear = monthOfYear;
    }


    protected NewEntryModelClass(Parcel in) {
        image = in.readString();
        type = in.readString();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        time = in.readString();
        amount = in.readString();
        category = in.readString();
        userId = in.readString();
        docId = in.readString();
        currentTimeStamp = in.readParcelable(Timestamp.class.getClassLoader());
        monthOfYear = in.readString();
    }

    public static final Creator<NewEntryModelClass> CREATOR = new Creator<NewEntryModelClass>() {
        @Override
        public NewEntryModelClass createFromParcel(Parcel in) {
            return new NewEntryModelClass(in);
        }

        @Override
        public NewEntryModelClass[] newArray(int size) {
            return new NewEntryModelClass[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Timestamp getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    public void setCurrentTimeStamp(Timestamp currentTimeStamp) {
        this.currentTimeStamp = currentTimeStamp;
    }

    public String getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(String monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image);
        parcel.writeString(type);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(amount);
        parcel.writeString(category);
        parcel.writeString(userId);
        parcel.writeString(docId);
        parcel.writeParcelable(currentTimeStamp, i);
        parcel.writeString(monthOfYear);
    }
}
