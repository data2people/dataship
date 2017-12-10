package eu.dataship.dataship;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bluelinelabs.conductor.archlifecycle.LifecycleController;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.concurrent.Executor;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import eu.dataship.dataship.AppDatabase.InstalledAppsDatabase;

public class NewRequestController extends LifecycleController {

    private Unbinder unbinder;

    private NewRequestViewModel viewModel;

    @BindView(R.id.newrequest_action_spinner)
    MaterialSpinner action_spinner;
    @BindView(R.id.providers_recycler_view)
    RecyclerView provider_recycler_view;
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

        provider_recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        provider_recycler_view.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity,
                linearLayoutManager.getOrientation());
        provider_recycler_view.addItemDecoration(dividerItemDecoration);
        InstalledAppsAdapter adapter = new InstalledAppsAdapter(
                viewModel, activity.getPackageManager());

        viewModel.getApps().observe(this, apps -> {
            adapter.setList(apps);
        });
        provider_recycler_view.setAdapter(adapter);

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

    @OnClick(R.id.whats_gdpr_button)
    public void openDatashipWebsite() {
        String url = "http://dataship.eu/gdpr/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    class ThreadPerTaskExecutor implements Executor {
        public void execute( Runnable task ) {
            new Thread(task).start();
        }
    }

}
