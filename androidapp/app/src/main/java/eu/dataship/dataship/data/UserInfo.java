package eu.dataship.dataship.data;

import android.support.annotation.Nullable;

public class UserInfo {

    private @Nullable String fullName;
    private @Nullable String emailAddress;

    public UserInfo(String fullName, String emailAddress) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
    }

    @Nullable
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@Nullable String fullName) {
        this.fullName = fullName;
    }

    @Nullable
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@Nullable String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
