package eu.dataship.dataship.utils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import eu.dataship.dataship.NewRequestViewModel;
import eu.dataship.dataship.repositories.InstalledAppRepository;
import eu.dataship.dataship.repositories.UserInfoRepository;

public class NewRequestViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private InstalledAppRepository installedAppRepository;
    private UserInfoRepository userInfoRepository;

    public NewRequestViewModelFactory(InstalledAppRepository installedAppRepository, UserInfoRepository userInfoRepository) {
        this.installedAppRepository = installedAppRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NewRequestViewModel(installedAppRepository, userInfoRepository);
    }
}