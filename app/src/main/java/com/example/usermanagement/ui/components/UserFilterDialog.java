package com.example.usermanagement.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.example.usermanagement.R;
import com.example.usermanagement.ui.FilteredResultsActivity;
import com.example.usermanagement.viewmodel.UserViewModel;

public class UserFilterDialog extends Dialog {
    private final UserViewModel viewModel;
    private EditText firstNameEditText;
    private Button applyButton;
    private Button cancelButton;

    public static final String EXTRA_FIRST_NAME_FILTER = "com.example.usermanagement.FIRST_NAME_FILTER";

    public UserFilterDialog(@NonNull Context context, UserViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_filter);

        // Initialize views
        firstNameEditText = findViewById(R.id.editTextFirstName);
        applyButton = findViewById(R.id.buttonApply);
        cancelButton = findViewById(R.id.buttonCancel);

        // Set up click listeners
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                // No longer setting filter on ViewModel directly here, 
                // as FilteredResultsActivity will do it from the Intent.
                // viewModel.setFirstNameFilter(firstName.isEmpty() ? null : firstName);
                
                // Launch FilteredResultsActivity and pass the filter
                Intent intent = new Intent(getContext(), FilteredResultsActivity.class);
                if (!firstName.isEmpty()) {
                    intent.putExtra(EXTRA_FIRST_NAME_FILTER, firstName);
                }
                getContext().startActivity(intent);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
} 