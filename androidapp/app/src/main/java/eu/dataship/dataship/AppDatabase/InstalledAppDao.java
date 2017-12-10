package eu.dataship.dataship.AppDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Update
    void updateSingleApp(InstalledApp installedApp);

    @Delete
    void delete(InstalledApp installedApp);

}
