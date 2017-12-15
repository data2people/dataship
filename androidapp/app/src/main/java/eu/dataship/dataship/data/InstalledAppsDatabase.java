package eu.dataship.dataship.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {InstalledApp.class}, version = 3)
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
                DB_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build();
    }

    public abstract InstalledAppDao getInstalledAppDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // add selected column
            database.execSQL("ALTER TABLE installedapp "
                    + " ADD COLUMN selected INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // delete selected column
            // create new table
            database.execSQL(
                    "CREATE TABLE installedapp_new (package_name TEXT NOT NULL,"
                            + "name TEXT,"
                            + "email TEXT,"
                            + "PRIMARY KEY(package_name))");
            // Copy the data
            database.execSQL("INSERT INTO installedapp_new (package_name, name, email) "
                    + "SELECT package_name, name, email "
                    + "FROM installedapp");
            // Remove the old table
            database.execSQL("DROP TABLE installedapp");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE installedapp_new RENAME TO installedapp");
        }
    };
}

