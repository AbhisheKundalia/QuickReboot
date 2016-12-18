package com.awiserk.kundalias.rootboot;

/**
 * Created by Abhishek on 12/18/2016.
 */

public class ButtonData {
    private int mTitleID;
    private int mDescriptionID;
    private int mImageResourceID;

    public ButtonData(int imageResourceID, int titleID, int descriptionID) {
        mImageResourceID = imageResourceID;
        mTitleID = titleID;
        mDescriptionID = descriptionID;
    }

    public int getImageResourceID() {
        return mImageResourceID;
    }

    public int getTitleID() {
        return mTitleID;
    }

    public int getDescriptionID() {
        return mDescriptionID;
    }
}
