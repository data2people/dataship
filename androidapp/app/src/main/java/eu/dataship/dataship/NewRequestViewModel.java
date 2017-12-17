package eu.dataship.dataship;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import eu.dataship.dataship.data.InstalledApp;
import eu.dataship.dataship.data.UserInfo;
import eu.dataship.dataship.repositories.InstalledAppRepository;
import eu.dataship.dataship.repositories.UserInfoRepository;

public class NewRequestViewModel extends ViewModel {

    private InstalledAppRepository installedAppRepository = null;
    private UserInfoRepository userInfoRepository = null;

    private LiveData<PagedList<InstalledApp>> installedApps;
    private MutableLiveData<UserInfo> userInfo;

    private String organizationReceiver = null;

    public NewRequestViewModel(InstalledAppRepository installedAppRepository, UserInfoRepository userInfoRepository) {
        this.installedAppRepository = installedAppRepository;
        this.userInfoRepository = userInfoRepository;
    }

    public LiveData<PagedList<InstalledApp> > getApps() {
        if (this.installedApps == null) {
            installedApps = new LivePagedListBuilder<>(
                    installedAppRepository.getPagedApps(),
                    new PagedList.Config.Builder()
                            .setPrefetchDistance(10)
                            .setPageSize(20)
                            .build()).build();
        }
        return installedApps;
    }

    public LiveData<UserInfo> getUserInfo() {
        if (userInfo == null) {
            userInfo = new MutableLiveData<>();

            UserInfo newUserInfo = new UserInfo(
                userInfoRepository.getUserFullName(),
                userInfoRepository.getUserEmailAddress(),
                userInfoRepository.getUserEmailAddressOptional());

            userInfo.setValue(newUserInfo);
        }

        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        // update sharedPreferences for future references
        userInfoRepository.setUserEmailAddressOptional(userInfo.getEmailAddressOptional());
        userInfoRepository.setUserEmailAddress(userInfo.getEmailAddress());
        userInfoRepository.setUserFullName(userInfo.getFullName());
        // update current userInfo object
        this.userInfo.setValue(userInfo);
    }

    public String getOrganizationReceiver() {
        return organizationReceiver;
    }

    public void setOrganizationReceiver(String organizationReceiver) {
        this.organizationReceiver = organizationReceiver;
    }
}
