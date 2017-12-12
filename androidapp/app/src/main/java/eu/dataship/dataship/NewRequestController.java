package eu.dataship.dataship;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bluelinelabs.conductor.archlifecycle.LifecycleController;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import eu.dataship.dataship.AppDatabase.InstalledApp;
import eu.dataship.dataship.AppDatabase.InstalledAppsDatabase;

public class NewRequestController extends LifecycleController {

    private static final String TAG = "dataship";
    private Unbinder unbinder;

    private Activity activity = null;

    private NewRequestViewModel viewModel;
    private InstalledAppsAdapter adapter;

    @BindView(R.id.newrequest_action_spinner)
    MaterialSpinner action_spinner;
    @BindView(R.id.providers_recycler_view)
    RecyclerView provider_recycler_view;
    @BindArray(R.array.gdpr_actions)
    String[] possible_actions;

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
        adapter = new InstalledAppsAdapter(
                viewModel, activity.getPackageManager());

        viewModel.getApps().observe(this, apps -> {
            // update app list
            adapter.setList(apps);
        });
        provider_recycler_view.setAdapter(adapter);

        action_spinner.setItems(possible_actions);

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

    @OnClick(R.id.fab_send_email)
    public void sendEmails() {
        int action = action_spinner.getSelectedIndex();
        List<InstalledApp> selected = adapter.getSelected();
        List<String> selectedEmails = new ArrayList<>();
        for (InstalledApp app : selected) {
            selectedEmails.add(app.getEmail());
        }
        Toast.makeText(activity, "Emails: " + selectedEmails + " action: " + action, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.fab_select_all)
    public void toggleSelectAll() {
        adapter.toggleSelectAll();
    }

    class ThreadPerTaskExecutor implements Executor {
        public void execute( Runnable task ) {
            new Thread(task).start();
        }
    }

}
