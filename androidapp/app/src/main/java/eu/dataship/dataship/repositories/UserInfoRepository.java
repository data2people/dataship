package eu.dataship.dataship.repositories;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

public class UserInfoRepository {

    private static final String TAG = "dataship";
    private String SHARED_PREF_USER_FULL_NAME = "user_full_name";
    private String SHARED_PREF_USER_EMAIL_ADDRESS = "user_email_address";
    private String SHARED_PREF_USER_EMAIL_ADDRESS_OPTIONAL = "user_email_address_optional";
    private SharedPreferences sharedPreferences;

    public UserInfoRepository(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public @Nullable String getUserFullName() {
        // default value is null
        return sharedPreferences.getString(SHARED_PREF_USER_FULL_NAME, null);
    }

    public void setUserFullName(String fullName) {
        Log.d(TAG, "setUserEmailAddress: User info " + fullName);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREF_USER_FULL_NAME, fullName);
        editor.apply();
    }

    public @Nullable String getUserEmailAddress() {
        // default value is null
        return sharedPreferences.getString(SHARED_PREF_USER_EMAIL_ADDRESS, null);
    }

    public void setUserEmailAddress(String emailAddress) {
        Log.d(TAG, "setUserEmailAddress: User info " + emailAddress);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREF_USER_EMAIL_ADDRESS, emailAddress);
        editor.apply();
    }

    public @Nullable String getUserEmailAddressOptional() {
        // default value is null
        return sharedPreferences.getString(SHARED_PREF_USER_EMAIL_ADDRESS_OPTIONAL, null);
    }

    public void setUserEmailAddressOptional(String emailAddressOptional) {
        Log.d(TAG, "setUserEmailAddress: User info " + emailAddressOptional);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREF_USER_EMAIL_ADDRESS_OPTIONAL, emailAddressOptional);
        editor.apply();
    }
}
