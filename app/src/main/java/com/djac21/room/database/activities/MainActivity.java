package com.djac21.room.database.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.djac21.room.database.models.Model;
import com.djac21.room.database.models.ListViewModel;
import com.djac21.room.database.R;
import com.djac21.room.database.adapters.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, DatePickerDialog.OnDateSetListener, RecyclerViewAdapter.ClickListener, RecyclerViewAdapter.LongClickListener {

    private ListViewModel viewModel;
    private RecyclerViewAdapter recyclerViewAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Date date;
    private Calendar calendar;
    ListViewModel listViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Model>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setClickListener(this);
        recyclerViewAdapter.setLongClickListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });

        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        viewModel.getItemAndPersonList().observe(MainActivity.this, new Observer<List<Model>>() {
            @Override
            public void onChanged(@Nullable List<Model> itemAndPeople) {
                recyclerViewAdapter.addItems(itemAndPeople);
            }
        });
    }

    public void addItemDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View dialogLayout = layoutInflater.inflate(R.layout.add_item_dialog, null);
        final EditText titleEditText = dialogLayout.findViewById(R.id.titleEditText);
        final EditText textEditText = dialogLayout.findViewById(R.id.textEditText);

        calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, MainActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        final Button dateDialog = dialogLayout.findViewById(R.id.dateButton);
        dateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Add item")
                .setView(dialogLayout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard(dialogLayout);
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (titleEditText.getText() == null || textEditText.getText() == null || date == null) {
                            Toast.makeText(MainActivity.this, "Missing fields", Toast.LENGTH_SHORT).show();
                        } else {
                            listViewModel.addItem(new Model(
                                    titleEditText.getText().toString(),
                                    textEditText.getText().toString(),
                                    date
                            ));
                            hideKeyboard(dialogLayout);
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = calendar.getTime();
    }

    @Override
    public void itemClicked(View view, int position) {
        Model model = (Model) view.getTag();
        Log.d(TAG, "ID: " + model.id + "\nTitle: " + model.getTitle()
                + "\nText: " + model.getText() + "\nDate: " + model.getDate());
    }

    @Override
    public void itemLongClicked(final View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Delete Item?")
                .setMessage("Are you sure you want to delete the current entry")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Model model = (Model) view.getTag();
                        viewModel.deleteItem(model);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(MainActivity.this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Delete Entire Log")
                    .setMessage("Are you sure you want to delete the entire")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.deleteAll();
                        }
                    });
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        recyclerViewAdapter.getFilter().filter(query);
        return false;
    }
}
