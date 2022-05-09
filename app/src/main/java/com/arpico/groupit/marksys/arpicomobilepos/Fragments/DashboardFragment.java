package com.arpico.groupit.marksys.arpicomobilepos.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogAuth;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Common.DataDbFileSync;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ThreadReceiver;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.zxing.client.android.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends Fragment {
    private ActionBar actionBar;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private Button btn_download, btn_shopping, btn_upload, btn_reset;
    private SharedPreferences sp;
    private TextView txt_tot_down, txt_tot_upload, txt_footer;
    private DataDbFileSync dataDbFileSync;
    private ComDialogAuth comAuth;
    private TextView txt_loccod, txt_gunnum, txt_userid, txt_date;

    private UpdatePageHeaderBroadCastReceiver updatePageHeaderBroadCastReceiver;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        updatePageHeaderBroadCastReceiver = new UpdatePageHeaderBroadCastReceiver();
        getActivity().registerReceiver(updatePageHeaderBroadCastReceiver, new IntentFilter(getResources().getString(R.string.broadcast_dashboard)));

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);

        new Thread(new ThreadReceiver(getContext(), sp)).start();

        comHelper = new ComHelper(getContext(), sp);
        dbHelper = new DbHelper(getContext());
        dataDbFileSync = new DataDbFileSync(getContext(), sp);
        comAuth = new ComDialogAuth(getContext(), sp);

        comHelper.addSP(ConfigMP.TITLE, actionBar.getTitle().toString(), true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_loccod = view.findViewById(R.id.txt_loccod);
        txt_loccod.setText(sp.getString(ConfigMP.SP_LOCCOD, ""));

        txt_gunnum = view.findViewById(R.id.txt_gunnum);
        txt_gunnum.setText(sp.getString(ConfigMP.SP_TBCODE, ""));

        txt_userid = view.findViewById(R.id.txt_userid);
        txt_userid.setText(sp.getString(ConfigMP.SP_USERID, ""));

        txt_date = view.findViewById(R.id.txt_date);
        txt_date.setText(dbHelper.getTime(2));

        btn_download = view.findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getString(ConfigMP.SP_TBCODE, "").isEmpty() || sp.getString(ConfigMP.SP_LOCCOD, "").isEmpty() ||
                        sp.getString(ConfigMP.SP_INPFIL, "").isEmpty() || sp.getString(ConfigMP.SP_OUTFIL, "").isEmpty()) {
                    comHelper.alert("Please Set Setting First");
                } else {
                    comHelper.addSP(ConfigMP.SP_CLICK, getResources().getString(R.string.thread_dashboard_download), true);
                    comAuth.dialogAuth();
                }
            }
        });

        btn_shopping = view.findViewById(R.id.btn_shopping);
        btn_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.setTitle(R.string.title_shopping_item);
                comHelper.loadFragment(new FindItemsFragment());
            }
        });

        btn_upload = view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (txt_tot_upload.getText().equals("0")) {
//                    comHelper.alert(getResources().getString(R.string.msg_data_not_found));
//                } else {
                    dataDbFileSync.conformationDialogForFTP(2);
//                }
            }
        });

        btn_reset = view.findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (txt_tot_upload.getText().equals("0")) {
//                    comHelper.alert(getResources().getString(R.string.msg_data_not_found));
//                } else {
                    comHelper.addSP(ConfigMP.SP_CLICK, getResources().getString(R.string.thread_dashboard_reset), true);
                    comAuth.dialogAuth();
//                }
            }
        });

        txt_tot_down = view.findViewById(R.id.txt_tot_down);
        txt_tot_upload = view.findViewById(R.id.txt_tot_uplo);

        totHeader();

        txt_footer = view.findViewById(R.id.txt_footer);
        txt_footer.setText(Html.fromHtml("<font color=#ED2024>&copy;&nbsp;" + dbHelper.getTime(4) + "&nbsp;Marksys (" + getResources().getString(R.string.app_name) + ") All Rights Reserved.</font><br>" +
                "<font color=#980507>Solution by Group ICT - Richard Pieris & Company PLC </font><br><font color=#36EDACAD>Version: " + BuildConfig.VERSION_NAME + "</font>"));

        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                actionBar.setTitle(R.string.title_settings);
                comHelper.loadFragment(new SettingsFragment());
                return true;
            case R.id.action_updated_files:
                actionBar.setTitle(R.string.title_uploaded_files);
                comHelper.loadFragment(new UploadedFilesFragment());
                return true;

            case R.id.action_backup_files:
                actionBar.setTitle(R.string.title_uploaded_files);
                dataDbFileSync.conformationDialogForFTP(4);
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(updatePageHeaderBroadCastReceiver, new IntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(updatePageHeaderBroadCastReceiver);
    }

    public void totHeader() {
        dbHelper = new DbHelper(getContext());
        JSONObject obj = dbHelper.displayData();
        try {
            txt_tot_down.setText(obj.get("TOT_DOWN").toString());
            txt_tot_upload.setText(obj.get("TOT_UPLO").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UpdatePageHeaderBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(getString(R.string.broadcast_dashboard))) {

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        dbHelper.resetData();

                        totHeader();

                        JSONObject obj = dbHelper.locConfig();
                        if (obj != null) {
                            try {
                                comHelper.addSP(ConfigMP.SP_LOCATION, obj.get(ConfigMP.SP_LOCATION).toString(), true);
                                comHelper.addSP(ConfigMP.SP_LOC_TEL_NO, obj.get(ConfigMP.SP_LOC_TEL_NO).toString(), true);
                                comHelper.addSP(ConfigMP.SP_LOC_FAX_NO, obj.get(ConfigMP.SP_LOC_FAX_NO).toString(), true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        }
    }

}
