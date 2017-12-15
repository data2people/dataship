package eu.dataship.dataship;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dataship.dataship.utils.NewRequestViewModelFactory;
import eu.dataship.dataship.controllers.NewRequestController;
import eu.dataship.dataship.data.InstalledAppsDatabase;
import eu.dataship.dataship.utils.ThreadPerTaskExecutor;
import eu.dataship.dataship.repositories.InstalledAppRepository;
import eu.dataship.dataship.repositories.UserInfoRepository;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "dataship";
    private Router router;
    private NewRequestViewModel viewModel;

    @BindView(R.id.controller_container)
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        InstalledAppRepository installedAppRepository =
                new InstalledAppRepository(
                        InstalledAppsDatabase.getInstance(this).getInstalledAppDao(),
                        new ThreadPerTaskExecutor(),
                        this.getPackageManager()
                );

        UserInfoRepository userInfoRepository =
                new UserInfoRepository(
                        getPreferences(Context.MODE_PRIVATE)
                );

        viewModel =
                ViewModelProviders.of(this,
                        new NewRequestViewModelFactory(installedAppRepository, userInfoRepository))
                        .get(NewRequestViewModel.class);

        NewRequestController newRequestController = new NewRequestController();
        newRequestController.setViewModel(viewModel);
        newRequestController.setHasOptionsMenu(true);

        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(newRequestController));
        }
    }

    @Override
    public void onBackPressed() {
        if(!router.handleBack()) {
            super.onBackPressed();
        }
    }

}
