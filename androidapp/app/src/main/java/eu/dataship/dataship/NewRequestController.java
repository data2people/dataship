package eu.dataship.dataship;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.archlifecycle.LifecycleController;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.concurrent.Executor;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.dataship.dataship.AppDatabase.InstalledAppsDatabase;

public class NewRequestController extends LifecycleController {

    private Unbinder unbinder;

    private NewRequestViewModel viewModel;

    @BindView(R.id.newrequest_action_spinner)
    MaterialSpinner action_spinner;
    @BindView(R.id.newrequest_provider_spinner)
    MaterialSpinner provider_spinner;
    @BindArray(R.array.gdpr_actions)
    String[] possible_actions;

    private Activity activity = null;

    public NewRequestController() {
        LifecycleObserver lifecycleObserver = new LifecycleObserver() {
//            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//            void onCreate() {
//                Log.d(TAG, "LifecycleObserver onCreate() called");
//            }

            // for other see: https://github.com/bluelinelabs/Conductor/blob/develop/demo/src/main/java/com/bluelinelabs/conductor/demo/controllers/ArchLifecycleController.java
        };

        getLifecycle().addObserver(lifecycleObserver);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_newrequest, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getActivity() != null) {
            activity = getActivity();
        }

        InstalledAppRepository repository =
                new InstalledAppRepository(
                        InstalledAppsDatabase.getInstance(activity).getInstalledAppDao(),
                        new ThreadPerTaskExecutor(),
                        activity.getPackageManager());

        viewModel =
                ViewModelProviders.of((AppCompatActivity) activity,
                        new NewRequestViewModelFactory(repository))
                        .get(NewRequestViewModel.class);

        viewModel.getAppNames().observe(this, appNames -> {
            provider_spinner.setItems(appNames);
        });
        // TODO change this
        provider_spinner.setOnItemSelectedListener((materialSpinner, position, id, item) -> {
            Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
        });

        action_spinner.setItems(possible_actions);
        // TODO change this
        action_spinner.setOnItemSelectedListener((materialSpinner, position, id, item) -> {
            Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
        });

        return view;
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        unbinder.unbind();
        unbinder = null;
    }

    class ThreadPerTaskExecutor implements Executor {
        public void execute( Runnable task ) {
            new Thread(task).start();
        }
    }

}
