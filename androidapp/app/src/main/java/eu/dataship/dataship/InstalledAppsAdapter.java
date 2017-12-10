package eu.dataship.dataship;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.dataship.dataship.AppDatabase.InstalledApp;

public class InstalledAppsAdapter extends PagedListAdapter<InstalledApp, InstalledAppsAdapter.ViewHolder> {

    private PackageManager packageManager;
    private NewRequestViewModel viewModel;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.installedappslist_layout)
        LinearLayout row;
        @BindView(R.id.installedappslist_row_tv)
        TextView appName;
        @BindView(R.id.installedappslist_row_icon)
        ImageView appIcon;
        @BindView(R.id.installedappslist_row_checkbox)
        CheckBox checkBox;

        private InstalledApp app;

        private PackageManager packageManager;
        private NewRequestViewModel viewModel;

        public ViewHolder(View view, NewRequestViewModel viewModel, PackageManager packageManager) {
            super(view);
            ButterKnife.bind(this, view);
            this.viewModel = viewModel;
            this.packageManager = packageManager;
        }

        public void bindTo(InstalledApp app) {
            this.app = app;
            appName.setText(app.getName());
            Drawable icon = null;
            try {
                icon = packageManager.getApplicationIcon(app.getPackage_name());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appIcon.setImageDrawable(icon);
            checkBox.setChecked(app.isSelected());
        }
        public void clear() {
            this.appName.setText("Loading...");
            this.checkBox.setVisibility(View.GONE);
            this.appIcon.setVisibility(View.GONE);
        }

        @OnClick(R.id.installedappslist_layout)
        public void rowClicked(LinearLayout clickedRow) {
            viewModel.updateSingleApp(app, !checkBox.isChecked());
            // force viewholder update (to reflect database data
            bindTo(app);
        }

        @OnClick(R.id.installedappslist_row_checkbox)
        public void overrideCheckboxClick() {}
    }

    public InstalledAppsAdapter(NewRequestViewModel viewModel, PackageManager packageManager) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.packageManager = packageManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.installedappslist_row, parent, false);
        return new ViewHolder(view, viewModel, packageManager);
    }

    @Override
    public void onBindViewHolder(InstalledAppsAdapter.ViewHolder holder, int position) {
        InstalledApp app = getItem(position);
        if (app != null) {
            holder.bindTo(app);
        } else {
            // Null defines a placeholder item - PagedListAdapter will automatically invalidate
            // this row when the actual object is loaded from the database
            holder.clear();
        }
    }
    public static final DiffCallback<InstalledApp> DIFF_CALLBACK = new DiffCallback<InstalledApp>() {

        @Override
        public boolean areItemsTheSame(@NonNull InstalledApp oldApp, @NonNull InstalledApp newApp) {
            // should be checking package_name (id)
            return oldApp.getPackage_name().equals(newApp.getPackage_name());
        }
        @Override
        public boolean areContentsTheSame(@NonNull InstalledApp oldApp, @NonNull InstalledApp newApp) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldApp.getName().equals(newApp.getName());
        }
    };
}

