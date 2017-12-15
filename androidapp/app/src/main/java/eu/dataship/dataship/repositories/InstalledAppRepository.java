package eu.dataship.dataship.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import eu.dataship.dataship.data.InstalledApp;
import eu.dataship.dataship.data.InstalledAppDao;

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

    public DataSource.Factory<Integer, InstalledApp> getPagedApps() {
        refreshAppNames();

        return installedAppDao.getAllPagedApps();
    }

    private void refreshAppNames() {
        Log.d(TAG, "refreshAppNames: called refreshAppNames");
        executor.execute(() -> {
            // running in a background thread

            //get a list of installed apps.
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

            List<InstalledApp> updatedList = new ArrayList<InstalledApp>();

            for (PackageInfo packageInfo : packages) {

                // get package name
                String package_name = packageInfo.packageName;
                // get app name
                String app_name = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                // check for null as package_name will be primary key in Room database
                if (package_name != null && !isSystemPackage(packageInfo)) {
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

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

}
