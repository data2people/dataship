package eu.dataship.dataship.AppDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import eu.dataship.dataship.AppDatabase.InstalledApp;
import eu.dataship.dataship.AppDatabase.InstalledAppDao;

@Database(entities = {InstalledApp.class}, version = 1)
public abstract class InstalledAppsDatabase extends RoomDatabase {

    private static final String DB_NAME = "installedAppsDatabase.db";
    private static volatile InstalledAppsDatabase instance;

    public static synchronized InstalledAppsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static InstalledAppsDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                InstalledAppsDatabase.class,
                DB_NAME).build();
    }

    public abstract InstalledAppDao getInstalledAppDao();
}

