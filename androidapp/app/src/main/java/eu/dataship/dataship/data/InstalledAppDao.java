package eu.dataship.dataship.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface InstalledAppDao {
    @Query("SELECT name FROM installedapp")
    LiveData<List<String> > getAllNames();

    @Query("SELECT * FROM installedapp ORDER BY name")
    DataSource.Factory<Integer, InstalledApp> getAllPagedApps();

    @Query("SELECT email FROM installedapp WHERE package_name = :request_package")
    String getEmailFromPackageName(String request_package);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<InstalledApp> installedApps);

    @Delete
    void delete(InstalledApp installedApp);

    @Query("DELETE FROM installedapp")
    void nukeTable();

}
