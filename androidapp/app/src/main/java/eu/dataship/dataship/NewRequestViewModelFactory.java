package eu.dataship.dataship;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class NewRequestViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private InstalledAppRepository mParam;


    public NewRequestViewModelFactory(InstalledAppRepository param) {
        mParam = param;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NewRequestViewModel(mParam);
    }
}