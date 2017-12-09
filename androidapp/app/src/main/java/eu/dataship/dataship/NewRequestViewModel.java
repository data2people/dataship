package eu.dataship.dataship;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class NewRequestViewModel extends ViewModel {

    private InstalledAppRepository installedAppRepository;

    private LiveData<List<String> > appNames;

    public NewRequestViewModel(InstalledAppRepository installedAppRepository) {
        this.installedAppRepository = installedAppRepository;
    }

    public LiveData<List<String> > getAppNames() {
        if (this.appNames == null) {
            appNames = installedAppRepository.getAppNames();
        }
        return appNames;
    }
}
