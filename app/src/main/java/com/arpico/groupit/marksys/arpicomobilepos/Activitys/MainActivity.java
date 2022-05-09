package com.arpico.groupit.marksys.arpicomobilepos.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogAuth;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Common.VersionUpdate;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.DashboardFragment;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.FindItemsFragment;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.ScanItemsFragment;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.UpdateItemFragment;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActionBar actionBar;
    private ComHelper comHelper;
    private SharedPreferences sp;
    private VersionUpdate versionUpdate;
    ComDialogAuth comAuth;
    private MainBroadCastReceiver mainBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_dashboard);

        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        comHelper = new ComHelper(this, sp);

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        comHelper.loadFragment(new DashboardFragment());
        comAuth = new ComDialogAuth(MainActivity.this, sp);

        mainBroadCastReceiver = new MainBroadCastReceiver();
        registerReceiver(mainBroadCastReceiver, new IntentFilter(getResources().getString(R.string.thread_dashboard_find_item)));

        versionUpdate = new VersionUpdate(this);
        versionUpdate.getVersion(sp);

    }

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_dashboard:
//                    actionBar.setTitle(R.string.title_dashboard);
//                    comHelper.loadFragment(new DashboardFragment());
//                    return true;
//
//                case R.id.navigation_find_items:
//                    actionBar.setTitle(R.string.title_find_item);
//                    comHelper.loadFragment(new FindItemsFragment());
////                    comHelper.addSP(ConfigCC.SP_CLICK, getResources().getString(R.string.thread_dashboard_find_item), true);
////                    comAuth.dialogAuth();
////
//                    return true;
//
//                case R.id.navigation_scan_items:
//                    actionBar.setTitle(R.string.title_scan_item);
//                    comHelper.loadFragment(new ScanItemsFragment());
//                    return true;
//
//                case R.id.navigation_cycle_count:
//                    actionBar.setTitle(R.string.title_update_item_count);
//                    comHelper.loadFragment(new UpdateItemFragment());
//                    return true;
//            }
//            return false;
//        }
//    };

    @Override
    public void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        registerReceiver(mainBroadCastReceiver, new IntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        unregisterReceiver(mainBroadCastReceiver);

    }

    private class MainBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(getResources().getString(R.string.thread_dashboard_find_item))) {
                actionBar.setTitle(R.string.title_shopping_item);
                comHelper.loadFragment(new FindItemsFragment());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (comHelper.getCurrentFragment().equalsIgnoreCase("DashboardFragment")) {
            logout();
        } else{
            actionBar.setTitle(R.string.title_dashboard);
            comHelper.loadFragment(new DashboardFragment());
//            super.onBackPressed();
        }
    }

    private void logout() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder
                .setMessage(R.string.msg_are_you_sure_want_to_exit)
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })

                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
//                        moveTaskToBack(true);
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                        System.exit(1);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
