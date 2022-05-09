package com.arpico.groupit.marksys.arpicomobilepos.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogAuth;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Common.DataDbFileSync;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.R;

import java.util.ArrayList;
import java.util.Arrays;

public class UploadedFilesFragment extends Fragment {
    private ListView listUploadedFiles;
    private ActionBar actionBar;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private SharedPreferences sp;
    private DataDbFileSync dataDbFileSync;
    private RadioGroup radioGroupDate, radioGroupType;
    private RadioButton radio_first, radio_second, radio_third, radio_current_user;
    private ComDialogAuth comAuth;
    private String selectedDate = "";
    ArrayList<String> myList;

    private ViewUploadFilesListBroadCastReceiver viewUploadFilesListBroadCastReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uploded_files, container, false);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);

        comHelper = new ComHelper(getContext(), sp);
        dbHelper = new DbHelper(getContext());
        dataDbFileSync = new DataDbFileSync(getContext(), sp);
        comAuth = new ComDialogAuth(getContext(), sp);

        comHelper.addSP(ConfigMP.TITLE, actionBar.getTitle().toString(), true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        viewUploadFilesListBroadCastReceiver = new ViewUploadFilesListBroadCastReceiver();
        getActivity().registerReceiver(viewUploadFilesListBroadCastReceiver, new IntentFilter(getResources().getString(R.string.broadcast_view_upload_files)));

        listUploadedFiles = view.findViewById(R.id.listUploadedFiles);

        radio_first = (RadioButton) view.findViewById(R.id.radio_first);
        radio_first.setText(dbHelper.getTime(4) + "-" + dbHelper.getTime(5) + "-" + String.valueOf(Integer.parseInt(dbHelper.getTime(6)) - 2));

        radio_second = (RadioButton) view.findViewById(R.id.radio_second);
        radio_second.setText(dbHelper.getTime(4) + "-" + dbHelper.getTime(5) + "-" + String.valueOf(Integer.parseInt(dbHelper.getTime(6)) - 1));

        radio_third = (RadioButton) view.findViewById(R.id.radio_third);
        radio_third.setText(dbHelper.getTime(4) + "-" + dbHelper.getTime(5) + "-" + String.valueOf(Integer.parseInt(dbHelper.getTime(6))));

        radio_third.setChecked(true);
        radioGroupDate = (RadioGroup) view.findViewById(R.id.radioGroupDate);
        radioGroupDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    myList.clear();
                    myList.add("No Files Found");
                    setAdaptor();

                    radio_current_user.setChecked(true);
                    selectedDate = rb.getText().toString();
                    dataDbFileSync.viewUploadedFiles(selectedDate, false, false);
                }
            }
        });

        radio_current_user = (RadioButton) view.findViewById(R.id.radio_current_user);
        radio_current_user.setChecked(true);
        radioGroupType = (RadioGroup) view.findViewById(R.id.radioGroupType);
        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    myList.clear();
                    myList.add("No Files Found");
                    setAdaptor();

                    if (rb.getText().toString().equalsIgnoreCase(getResources().getString(R.string.title_view_current_user))) {
                        dataDbFileSync.viewUploadedFiles(selectedDate, false, false);

                    } else if (rb.getText().toString().equalsIgnoreCase(getResources().getString(R.string.title_view_backup))) {
                        dataDbFileSync.viewUploadedFiles(selectedDate, true, true);

                    } else if (rb.getText().toString().equalsIgnoreCase(getResources().getString(R.string.title_view_files))) {
                        dataDbFileSync.viewUploadedFiles(selectedDate, true, false);
                    }
                }
            }
        });

        dataDbFileSync.viewUploadedFiles(radio_third.getText().toString(), false, false);

        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                dataDbFileSync.viewUploadedFiles(radio_third.getText().toString(), false, false);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(viewUploadFilesListBroadCastReceiver, new IntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(viewUploadFilesListBroadCastReceiver);
    }

    private class ViewUploadFilesListBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, final Intent intent) {
            if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(getString(R.string.broadcast_view_upload_files))) {

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        String stringList[] = intent.getStringExtra("list").replace("[", "").replace("]", "").split(",");
                        myList = new ArrayList<String>(Arrays.asList(stringList));
                        if (stringList[0].equals("")) {
                            myList.clear();
                            myList.add("No Files Found");
                        }
                        setAdaptor();
                    }
                });
            }
        }
    }

    private void setAdaptor(){
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        listUploadedFiles.setAdapter(itemsAdapter);
    }
}
