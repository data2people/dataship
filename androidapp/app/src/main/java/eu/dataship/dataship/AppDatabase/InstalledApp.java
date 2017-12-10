package eu.dataship.dataship.AppDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class InstalledApp {
    @NonNull
    @PrimaryKey
    private String package_name;

    private String name;

    private String email;

    private boolean selected;


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public InstalledApp(String package_name, String name, String email) {
        this.package_name = package_name;
        this.name = name;
        this.email = email;
        this.selected = false;
    }
}
