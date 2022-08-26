package com.example.ubill;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ubill.data.GSTBillingContract;

import java.util.Objects;

public class BillsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, BillAdapter.BillItemClickListener {

    private RecyclerView unpaidRecyclerView;
    private BillAdapter adapter;
    private String billListStatus;
    private int billDividerColor;
    private String billSortOrder;
    Toolbar toolbar;

    private static final int BILL_LOADER_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        toolbar = findViewById(R.id.toolbar);

        if(savedInstanceState != null){
            billListStatus = savedInstanceState.getString(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_STATUS);
        }else {
            billListStatus = GSTBillingContract.BILL_STATUS_UNPAID;
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        isStoragePermissionGranted();

        switch (billListStatus){
            case GSTBillingContract.BILL_STATUS_PAID:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.paid_bills_title);
                billDividerColor = Color.GREEN;
                billSortOrder = " DESC";
                break;
            case GSTBillingContract.BILL_STATUS_UNPAID:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.unpaid_bills_title);
                billDividerColor = Color.RED;
                billSortOrder = " ASC";
                break;
        }

        FloatingActionButton fab = findViewById(R.id.fab_unpaid);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BillsActivity.this, NewBillCustomerActivity.class));
            }
        });


        unpaidRecyclerView = findViewById(R.id.unpaid_recycler_view);
        unpaidRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        unpaidRecyclerView.setHasFixedSize(true);
        adapter = new BillAdapter(this, this, billDividerColor);
        unpaidRecyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(BILL_LOADER_ID, null, this);

    }


    public void isStoragePermissionGranted(){
        if(Build.VERSION.SDK_INT >= 25){
            if(!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bills_list, menu);
        if(billListStatus.equals(GSTBillingContract.BILL_STATUS_PAID)){
            menu.findItem(R.id.action_swap_bills_list).setTitle(R.string.action_show_unpaid_bills);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_swap_bills_list){

            switch (billListStatus){
                case GSTBillingContract.BILL_STATUS_UNPAID:
                    billListStatus = GSTBillingContract.BILL_STATUS_PAID;
                    item.setTitle(getString(R.string.action_show_unpaid_bills));
                    getSupportActionBar().setTitle(getString(R.string.paid_bills_title));
                    billDividerColor = Color.GREEN;
                    billSortOrder = " DESC";
                    break;
                case GSTBillingContract.BILL_STATUS_PAID:
                    billListStatus = GSTBillingContract.BILL_STATUS_UNPAID;
                    item.setTitle(getString(R.string.action_show_paid_bills));
                    getSupportActionBar().setTitle(getString(R.string.unpaid_bills_title));
                    billDividerColor = Color.RED;
                    billSortOrder = " ASC";
                    break;
            }

            getSupportLoaderManager().restartLoader(BILL_LOADER_ID, null, this);

        }
        if(id==R.id.logout_button){
            startActivity(new Intent(BillsActivity.this,LoginActivity.class));
            Toast.makeText(BillsActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case BILL_LOADER_ID:
                return new CursorLoader(
                        this,
                        GSTBillingContract.GSTBillingEntry.CONTENT_URI,
                        null,
                        GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_STATUS + "='" + billListStatus + "'",
                        null,
                        GSTBillingContract.GSTBillingEntry._ID + billSortOrder
                );
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data, billDividerColor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null, Color.RED);
    }

    @Override
    public void onBillItemClick(String clickedBillId, String customerName, String phoneNumber) {
        Intent detailIntent = new Intent(this, DetailActivity.class);

        detailIntent.putExtra(GSTBillingContract.GSTBillingEntry._ID, clickedBillId);
        detailIntent.putExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_STATUS, billListStatus);
        detailIntent.putExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_NAME, customerName);
        detailIntent.putExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_PHONE_NUMBER, phoneNumber);

        startActivity(detailIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_STATUS, billListStatus);

        super.onSaveInstanceState(outState);
    }

}
