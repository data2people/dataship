package eu.dataship.dataship.data;

import android.support.annotation.Nullable;

public class UserInfo {

    private @Nullable String fullName;
    private @Nullable String emailAddress;
    private @Nullable String emailAddressOptional;

    public UserInfo(String fullName, String emailAddress, String emailAddressOptional) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.emailAddressOptional = emailAddressOptional;
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

    @Nullable
    public String getEmailAddressOptional() {
        return emailAddressOptional;
    }

    public void setEmailAddressOptional(@Nullable String emailAddressOptional) {
        this.emailAddressOptional = emailAddressOptional;
    }
}
