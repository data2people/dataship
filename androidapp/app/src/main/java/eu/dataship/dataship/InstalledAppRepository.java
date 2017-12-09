package eu.dataship.dataship;

import android.arch.lifecycle.LiveData;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import eu.dataship.dataship.AppDatabase.InstalledApp;
import eu.dataship.dataship.AppDatabase.InstalledAppDao;

public class InstalledAppRepository {
    private static final String TAG = "dataship";
    private final InstalledAppDao installedAppDao;
    private final Executor executor;
    private final PackageManager packageManager;

    public InstalledAppRepository(InstalledAppDao installedAppDao, Executor executor, PackageManager packageManager) {
        this.installedAppDao = installedAppDao;
        this.executor = executor;
        this.packageManager = packageManager;
    }

    public LiveData<List<String>> getAppNames() {
        refreshAppNames();

        // return a LiveData directly from the database.
        return installedAppDao.getAllNames();
    }

    private void refreshAppNames() {
        Log.d(TAG, "refreshAppNames: called refreshAppNames");
        executor.execute(() -> {
            // running in a background thread

            //get a list of installed apps.
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

            List<InstalledApp> updatedList = new ArrayList<InstalledApp>();

            for (PackageInfo packageInfo : packages) {

                // get package name
                String package_name = packageInfo.packageName;
                // get app name
                String app_name = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                // check for null as package_name will be primary key in Room database
                if (package_name != null) {
                    updatedList.add(new InstalledApp(
                            package_name,
                            app_name,
                            "NaN"
                    ));
                }
            }

            installedAppDao.insertAll(updatedList);
        });
    }


}
