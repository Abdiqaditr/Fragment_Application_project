package com.iuea.fragment_application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExampleFragment extends Fragment {
    private TextView textView;
    private Button button, addButton;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<String> items;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String ITEMS_KEY = "items";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_example, container, false);

        textView = view.findViewById(R.id.textView);
        button = view.findViewById(R.id.button);
        addButton = view.findViewById(R.id.addButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(items);
        recyclerView.setAdapter(itemAdapter);

        // Load saved items
        loadData();

        // Button click listener to change the text
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Button Clicked!");
            }
        });

        // Add Item button listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });

        return view;
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Item");

        // Set up the input
        final EditText input = new EditText(getContext());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = input.getText().toString();
                if (!itemName.isEmpty()) {
                    items.add(itemName);
                    itemAdapter.updateItems(items);
                    saveData();  // Save data after adding
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String json = sharedPreferences.getString(ITEMS_KEY, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        items = new Gson().fromJson(json, type);

        if (items == null) {
            items = new ArrayList<>();  // Initialize if null
        }
        itemAdapter.updateItems(items);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(items);
        editor.putString(ITEMS_KEY, json);
        editor.apply();  // Save the changes
    }

    @Override
    public void onResume() {
        super.onResume();
        // Optional: Reload data if needed
        loadData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current state of the list
        outState.putStringArrayList("items", new ArrayList<>(items));
    }
}


