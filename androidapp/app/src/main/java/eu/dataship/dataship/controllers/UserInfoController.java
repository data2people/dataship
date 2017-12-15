package eu.dataship.dataship.controllers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import eu.dataship.dataship.Base.ButterKnifeLifecycleController;
import eu.dataship.dataship.R;
import eu.dataship.dataship.data.UserInfo;

public class UserInfoController extends ButterKnifeLifecycleController {

    private Activity activity = null;
    private boolean showExplanationSnackbar = false;

    @BindView(R.id.user_info_fullname_edit_text)
    EditText fullnameEditText;
    @BindView(R.id.user_info_email_edit_text)
    EditText emailEditText;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_userinfo, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        getViewModel().getUserInfo().observe(this, userInfo -> {
            if (userInfo != null) {
                if (userInfo.getFullName() != null) {
                    fullnameEditText.setText(userInfo.getFullName());
                }
                if (userInfo.getEmailAddress() != null) {
                    emailEditText.setText(userInfo.getEmailAddress());
                }
            }
        });

    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        if (showExplanationSnackbar) {
            // snackbar automatically looks for a suitable parent view
            Snackbar.make(emailEditText,
                    "We'll need some info first.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.user_info_button)
    public void saveUserInfo() {
        String fullname = fullnameEditText.getText().toString().matches("") ? null : fullnameEditText.getText().toString();
        String emailAddress = emailEditText.getText().toString().matches("") ? null : emailEditText.getText().toString();
        UserInfo userInfo = new UserInfo(
                fullname,
                emailAddress
        );
        getViewModel().setUserInfo(userInfo);
        // snackbar automatically looks for a suitable parent view
        Snackbar.make(emailEditText,
                "Saved", Snackbar.LENGTH_SHORT).show();
    }

    public void setShowExplanationSnackbar(boolean showExplanationSnackbar) {
        this.showExplanationSnackbar = showExplanationSnackbar;
    }
}
