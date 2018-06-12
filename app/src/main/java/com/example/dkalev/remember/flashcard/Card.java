package com.example.dkalev.remember.flashcard;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Card implements Parcelable {

    public static final String TAG = "Card";

    public static final String JSON_FRONT_TEXT = "frontText";
    public static final String JSON_BACK_TEXT = "backText";
    public static final String JSON_FRONT_PIC = "frontPic";
    public static final String JSON_BACK_PIC = "backPic";

    private final String mFrontText;
    private final String mBackText;
    private final String mFrontPic;
    private final String mBackPic;

    private final String mRawJson;

    public Card(JSONObject json) throws JSONException {
        mFrontText = json.getString(JSON_FRONT_TEXT);
        mBackText = json.getString(JSON_BACK_TEXT);
        mFrontPic = json.getString(JSON_FRONT_PIC);
        mBackPic = json.getString(JSON_BACK_PIC);
        mRawJson = json.toString();
    };


    @Override
    public int describeContents() {
        return 0;
    }

    public String getFrontText(){
        return mFrontText;
    }

    public String getBackText(){
        return mBackText;
    }

    //TODO getters for front and back images

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRawJson);
    }

    //CREATOR required to construct Card object from a Parcel
    public static final Parcelable.Creator<Card> CREATOR = new
            Parcelable.Creator<Card>() {

        public Card createFromParcel(Parcel parcel) {
            final String rawJson = parcel.readString();
            try {
                final JSONObject jsonObject = new JSONObject(rawJson);
                return new Card(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to create Property from JSON String: "
                        + e.getMessage());
                return null;
            }
        }


        public Card[] newArray( int size){
            return new Card[size];
        }

    };


}
