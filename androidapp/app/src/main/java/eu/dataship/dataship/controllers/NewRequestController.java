package eu.dataship.dataship.controllers;

import android.arch.lifecycle.LifecycleObserver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import eu.dataship.dataship.R;
import eu.dataship.dataship.data.InstalledApp;
import eu.dataship.dataship.Base.ButterKnifeLifecycleController;
import eu.dataship.dataship.data.UserInfo;
import eu.dataship.dataship.other_views.InstalledAppsAdapter;

public class NewRequestController extends ButterKnifeLifecycleController {

    private static final String TAG = "dataship";

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

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        provider_recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        provider_recycler_view.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        provider_recycler_view.addItemDecoration(dividerItemDecoration);
        adapter = new InstalledAppsAdapter(
                getViewModel(), getActivity().getPackageManager());

        getViewModel().getApps().observe(this, apps -> {
            // update app list
            adapter.setList(apps);
        });

        provider_recycler_view.setAdapter(adapter);

        action_spinner.setItems(possible_actions);

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_newrequest, container, false);
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

        UserInfo userInfo = getViewModel().getUserInfo().getValue();
        if (userInfo == null || userInfo.getFullName() == null || userInfo.getEmailAddress() == null) {
            Log.d(TAG, "sendEmails: User info null.");
            // user needs to input info
            pushToUserInfoController(true);
        } else {

            Log.d(TAG, "sendEmails: User info: " + userInfo.getEmailAddress() + " " + userInfo.getFullName());
            // user needs to input info
            int action = action_spinner.getSelectedIndex();
            List<InstalledApp> selected = adapter.getSelected();
            List<String> selectedEmails = new ArrayList<>();
            for (InstalledApp app : selected) {
                selectedEmails.add(app.getEmail());
            }
            Toast.makeText(getActivity(), "Emails: " + selectedEmails + " action: " + action, Toast.LENGTH_LONG).show();

        }
    }

    @OnClick(R.id.fab_select_all)
    public void toggleSelectAll() {
        adapter.toggleSelectAll();
    }

    private void pushToUserInfoController(boolean notUserIssuedTransition) {
        UserInfoController userInfoController = new UserInfoController();
        userInfoController.setViewModel(getViewModel());
        userInfoController.setShowExplanationSnackbar(notUserIssuedTransition);
        getRouter().pushController(RouterTransaction
                .with(userInfoController)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    // --------------------------------------------------------------------------------- option menu

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_info:
                pushToUserInfoController(false);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onChangeStarted(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        setOptionsMenuHidden(!changeType.isEnter);
    }

}
