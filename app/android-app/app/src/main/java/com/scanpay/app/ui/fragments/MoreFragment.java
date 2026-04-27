package com.scanpay.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scanpay.app.R;
import com.scanpay.app.data.repository.AuthRepository;
import com.scanpay.app.ui.auth.LoginActivity;
import com.scanpay.app.utils.SessionManager;

public class MoreFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private LinearLayout btnProfile, btnSettings, btnHelp, btnLogout;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupUI();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        btnProfile = view.findViewById(R.id.btn_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnHelp = view.findViewById(R.id.btn_help);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupUI() {
        tvUserName.setText(sessionManager.getUserName());
        tvUserEmail.setText(sessionManager.getUserEmail());
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            AuthRepository authRepository = new AuthRepository(sessionManager);
            authRepository.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}

