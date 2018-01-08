package eu.dataship.dataship.controllers;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import de.cketti.mailto.EmailIntentBuilder;
import eu.dataship.dataship.R;
import eu.dataship.dataship.data.InstalledApp;
import eu.dataship.dataship.Base.ButterKnifeLifecycleController;
import eu.dataship.dataship.data.UserInfo;
import eu.dataship.dataship.other_views.InstalledAppsAdapter;

public class NewRequestController extends ButterKnifeLifecycleController {

    private static final String TAG = "dataship";

    private InstalledAppsAdapter adapter;

    private boolean loadingEmailAddresses = false;
    private ArrayList<String> sendToAddresses = new ArrayList<>();

    @BindView(R.id.newrequest_action_spinner)
    MaterialSpinner action_spinner;
    @BindView(R.id.providers_recycler_view)
    RecyclerView provider_recycler_view;
    @BindView(R.id.fab_to)
    FloatingActionButton fab_to;
    @BindView(R.id.fab_to_textview)
    TextView fab_to_textview;
    @BindArray(R.array.gdpr_actions)
    String[] possible_actions;
    @BindView(R.id.email_download_progress)
    ProgressBar progressBar;
    @BindView(R.id.fab_send_email)
    FloatingActionButton fabSendEmail;

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
        action_spinner.setOnItemSelectedListener((view1, position, id, item) -> {
            if (position == 2) {
                // show to button
                fab_to.setVisibility(View.VISIBLE);
                fab_to_textview.setVisibility(View.VISIBLE);
            }
            else {
                // hide to button if it is present
                fab_to.setVisibility(View.GONE);
                fab_to_textview.setVisibility(View.GONE);
            }
        });

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
    public void prepareEmail() {

        int action = action_spinner.getSelectedIndex();
        UserInfo userInfo = getViewModel().getUserInfo().getValue();

        if (action == 2 && getViewModel().getOrganizationReceiver() == null) {
            showDialogOrganizationReceiverHelper();
        } else if (userInfo == null || userInfo.getFullName() == null || userInfo.getEmailAddress() == null) {
            Log.d(TAG, "sendEmails: User info null.");
            // user needs to: input info
            pushToUserInfoController(true);
        } else {
            // we can retrieve addresses and send email
            Log.d(TAG, "sendEmails: User info: " + userInfo.getEmailAddress() + " " + userInfo.getFullName());
            List<InstalledApp> selected = adapter.getSelected();
            List<String> selectedEmails = new ArrayList<>();
            ArrayList<InstalledApp> emailsToDownload = new ArrayList<>();
            Log.d(TAG, "sendEmails: " + Integer.toString(selected.size()));
            for (InstalledApp app : selected) {
                String email = app.getEmail();
                Log.d(TAG, "sendEmails1: email address: " + email);
                if (!app.getEmail().trim().equals("NaN")) {
                    selectedEmails.add(app.getEmail());
                    Log.d(TAG, "sendEmails2: email address: " + email);
                } else {
                    emailsToDownload.add(app);
                    //email = getViewModel().updateAndGetEmail(app);
                    //Log.d(TAG, "sendEmails3: email address: " + email);
                    //selectedEmails.add(email);
                }
            }
            sendToAddresses.addAll(selectedEmails);
            if (sendToAddresses.size() == selected.size()) {
                sendEmails(sendToAddresses);
            } else {
                DownloadEmailsTask downloadEmailsTask = new DownloadEmailsTask();
                downloadEmailsTask.execute(emailsToDownload);
                setLoadingEmailAddresses(true);
            }
        }
    }

    private class DownloadEmailsTask extends AsyncTask<ArrayList<InstalledApp>, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<InstalledApp>... installedApps) {
            ArrayList<String> emails = new ArrayList<>();
            for (InstalledApp app : installedApps[0]) {
                emails.add(getViewModel().updateAndGetEmail(app));
            }
            return emails;
        }

        @Override
        protected void onPostExecute(ArrayList<String> emails) {
            super.onPostExecute(emails);
            for (String email : emails) {
                Log.d(TAG, "sendEmails: address " + email);
                if (email != null) {
                    sendToAddresses.add(email);
                } else {
                    Toast.makeText(getActivity(), "Some email addresses were lost.", Toast.LENGTH_SHORT).show();
                }
            }
            sendEmails(sendToAddresses);
        }
    }

    private void sendEmails(List<String> addresses) {
        String subject = "";
        String body = "";
        String emailAddressOptional = "";

        int action = action_spinner.getSelectedIndex();
        UserInfo userInfo = getViewModel().getUserInfo().getValue();

        if (userInfo.getEmailAddressOptional() != null) {
            emailAddressOptional = ", " + userInfo.getEmailAddressOptional();
        }
        switch (action) {
                case 0:
                    // learn about
                    subject = getResources().getString(R.string.subject_learn_about);
                    body = getResources().getString(
                            R.string.body_learn_about,
                            getViewModel().getUserInfo().getValue().getFullName(),
                            getViewModel().getUserInfo().getValue().getEmailAddress(),
                            emailAddressOptional);
                    break;
                case 1:
                    // access
                    subject = getResources().getString(R.string.subject_access);
                    body = getResources().getString(
                            R.string.body_access,
                            getViewModel().getUserInfo().getValue().getFullName(),
                            getViewModel().getUserInfo().getValue().getEmailAddress(),
                            emailAddressOptional);
                    break;
                case 2:
                    // transfer
                    subject = getResources().getString(R.string.subject_transfer);
                    body = getResources().getString(
                            R.string.body_transfer,
                            getViewModel().getUserInfo().getValue().getFullName(),
                            getViewModel().getUserInfo().getValue().getEmailAddress(),
                            emailAddressOptional,
                            getViewModel().getOrganizationReceiver());
                    break;
                case 3:
                    // delete
                    subject = getResources().getString(R.string.subject_delete);
                    body = getResources().getString(
                            R.string.body_delete,
                            getViewModel().getUserInfo().getValue().getFullName());
                    break;
                default:
                    break;
            }

            Log.d(TAG, "sendEmails: subject: " + subject);
            Log.d(TAG, "sendEmails: body: " + body);

            List<String> purgedAddresses = purgeAddresses(addresses);

            if (purgedAddresses.size() > 0) {
                EmailIntentBuilder.from(getActivity())
                        .to(purgeAddresses(addresses))
                        .subject(subject)
                        .body(body)
                        .start();
            } else {
                Toast.makeText(getActivity(), "The apps you selected were not valid.", Toast.LENGTH_SHORT).show();
            }


            // clear addresses for next request
            sendToAddresses.clear();
    }

    @Override
    protected void onActivityPaused(@NonNull Activity activity) {
        super.onActivityPaused(activity);
        if (loadingEmailAddresses) {
            setLoadingEmailAddresses(false);
        }
    }

    private void setLoadingEmailAddresses(boolean loadingEmailAddresses) {
        if (loadingEmailAddresses) {
            progressBar.setVisibility(View.VISIBLE);
            fabSendEmail.setEnabled(false);
            fabSendEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentDark)));
        } else {
            progressBar.setVisibility(View.GONE);
            fabSendEmail.setEnabled(true);
            fabSendEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
        this.loadingEmailAddresses = loadingEmailAddresses;
    }

    private List<String> purgeAddresses(List<String> addresses) {
        for (Iterator<String> iterator = addresses.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            if (value == null || value.trim().equals("NaN")) {
                iterator.remove();
            }
        }
        return addresses;
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

    @OnClick(R.id.fab_to)
    public void showDialogOrganizationReceiver() {
        showDialogOrganizationReceiverHelper();
    }

    private void showDialogOrganizationReceiverHelper() {
        String prefill = "";
        if (getViewModel().getOrganizationReceiver() != null) {
            prefill = getViewModel().getOrganizationReceiver();
        }
        new MaterialDialog.Builder(getActivity())
                .title("Organization receiver")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.colorPrimary)
                .positiveColorRes(R.color.colorPrimary)
                .input("Organization receiver", prefill, (dialog, input) -> {
                     getViewModel().setOrganizationReceiver(input.toString());
                     Toast.makeText(getActivity(), "You can now hit send.", Toast.LENGTH_SHORT).show();
                }).show();
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
