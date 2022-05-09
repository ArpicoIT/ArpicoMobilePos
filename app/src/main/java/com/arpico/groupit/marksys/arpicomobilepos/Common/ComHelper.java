package com.arpico.groupit.marksys.arpicomobilepos.Common;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class ComHelper {
    private Context mContext;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public ComHelper(Context mContext, SharedPreferences sp) {
        this.mContext = mContext;
        this.sp = sp;
    }

    public static boolean validateIP(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public void hiddenKeybord(SearchView editText, int flg) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), flg);
    }

    public String getWiFiAddress() {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public void alert(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public void snackbar(Activity activity, String txt) {
        Snackbar.make(activity.getWindow().getDecorView().getRootView(), txt, Snackbar.LENGTH_LONG).show();
    }

    public void Bluetooth(boolean boolEnable) {
        if (boolEnable) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.enable();
        } else {
            bluetoothAdapter.disable();
        }
    }

    public void addSP(String key, String value, boolean add) {
        ed = sp.edit();
        if (add) {
            ed.putString(key, value);
        } else {
            ed.remove(key);
        }
        ed.commit();
    }

    public void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());

        addSP(ConfigMP.SP_LANGUA, lang, true);
    }

    public void loadFragment(Fragment fragment) {
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public String getCurrentFragment() {
        return ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.container).getClass().getSimpleName();
    }

    public void backClick(ActionBar actionBar, Fragment fragment, boolean boolBt) {
        actionBar.setTitle(R.string.title_dashboard);
        loadFragment(fragment);
        Bluetooth(boolBt);
    }

    public boolean wifiConnectivity(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
