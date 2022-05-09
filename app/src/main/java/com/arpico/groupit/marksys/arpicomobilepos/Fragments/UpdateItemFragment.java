package com.arpico.groupit.marksys.arpicomobilepos.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpico.groupit.marksys.arpicomobilepos.Activitys.MainActivity;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogAuth;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogUpdateItem;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Common.DataDbFileSync;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ThreadReceiver;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.arpico.groupit.marksys.arpicomobilepos.Recycler.RecyclerViewItemsAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UpdateItemFragment extends Fragment {
    private List<ItemsModel> list_find_item = new ArrayList<>();
    private RecyclerView recyclerViewItems;
    private RecyclerViewItemsAdapter itemAdapter;

    private TextView txt_recent_count, txt_recent_qty;
    private int intRecentItemCount = 0;

    private ActionBar actionBar;
    private Type listType;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private SharedPreferences sp;
    private DataDbFileSync dataDbFileSync;
    private ComDialogAuth comAuth;
    private ComDialogUpdateItem comDialogUpdateItem;

    private UpdatePageListBroadCastReceiver updatePageListBroadCastReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_item, container, false);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);


        new Thread(new ThreadReceiver(getContext(), sp)).start();

        updatePageListBroadCastReceiver = new UpdatePageListBroadCastReceiver();
        getActivity().registerReceiver(updatePageListBroadCastReceiver, new IntentFilter(getResources().getString(R.string.broadcast_update_item)));

        comHelper = new ComHelper(getContext(), sp);
        dbHelper = new DbHelper(getContext());
        dataDbFileSync = new DataDbFileSync(getContext(), sp);
        comAuth = new ComDialogAuth(getContext(), sp);

        comHelper.addSP(ConfigMP.TITLE, actionBar.getTitle().toString(), true);

        listType = new TypeToken<ArrayList<ItemsModel>>() {
        }.getType();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recyclerViewItems = view.findViewById(R.id.listUpdateItem);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));

        txt_recent_count = view.findViewById(R.id.txt_recent_count);
        txt_recent_qty = view.findViewById(R.id.txt_recent_qty);

        comDialogUpdateItem = new ComDialogUpdateItem(getContext(), sp, comHelper, dbHelper, list_find_item, itemAdapter, recyclerViewItems, intRecentItemCount, txt_recent_count, txt_recent_qty, false);
        comDialogUpdateItem.getItemsData("");

        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        SearchManager searchManager = (SearchManager) (getContext()).getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) getContext()).getComponentName()));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                comDialogUpdateItem.getItemsData(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.forSupportFragment(UpdateItemFragment.this).initiateScan();
                break;
            case R.id.action_reset:
                if (list_find_item.size() > 0) {
                    comHelper.addSP(ConfigMP.SP_CLICK, "uploadReset", true);
                    comAuth.dialogAuth();
                } else {
                    comHelper.alert("Data Not Found");
                }
                break;

            case R.id.action_upload:
                if (list_find_item.size() > 0) {
                    dataDbFileSync.conformationDialogForFTP(2);
                } else {
                    comHelper.alert("Data Not Found");
                }
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(updatePageListBroadCastReceiver, new IntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(updatePageListBroadCastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                comHelper.alert("Invalid");
            } else {
                comDialogUpdateItem.getItemsData(intentResult.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class UpdatePageListBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(getString(R.string.broadcast_update_item))) {

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        comDialogUpdateItem.getItemsData("");
                    }
                });
            }
        }
    }

}
