package eu.dataship.dataship;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.ArrayList;
import java.util.List;

import eu.dataship.dataship.AppDatabase.InstalledApp;

public class NewRequestViewModel extends ViewModel {

    private InstalledAppRepository installedAppRepository;

    private LiveData<PagedList<InstalledApp>> installedApps;

    public NewRequestViewModel(InstalledAppRepository installedAppRepository) {
        this.installedAppRepository = installedAppRepository;
    }

    public LiveData<PagedList<InstalledApp> > getApps() {
        if (this.installedApps == null) {
            installedApps = new LivePagedListBuilder<>(
                    installedAppRepository.getPagedApps(), 50).build();
        }
        return installedApps;
    }

    public void updateSingleApp(InstalledApp app, boolean checked) {
        // forward to repository
        installedAppRepository.updateSingleApp(app, checked);
    }

}
