package eu.dataship.dataship.Base;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.archlifecycle.LifecycleController;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.dataship.dataship.NewRequestViewModel;

public abstract class ButterKnifeLifecycleController extends LifecycleController {

    private Unbinder unbinder;

    private NewRequestViewModel viewModel;

    protected ButterKnifeLifecycleController() { }

    protected abstract View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container);

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflateView(inflater, container);
        unbinder = ButterKnife.bind(this, view);
        onViewBound(view);
        return view;
    }

    protected void onViewBound(@NonNull View view) { }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        unbinder.unbind();
        unbinder = null;
    }

    public void setViewModel(NewRequestViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public NewRequestViewModel getViewModel() {
        return viewModel;
    }

}
