package com.arpico.groupit.marksys.arpicomobilepos.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.client.android.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private ActionBar actionBar;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private SharedPreferences sp;
    private EditText edit_TBCODE, edit_LOCCOD, edit_USERID, edit_BILLNO, edit_SER_IP, edit_FTP_IP, edit_FTP_PORT, edit_FTP_USER, edit_FTP_PASS;
    private TextView txt_version;
    private SwitchCompat switchIntro, switchFtp, switchDecimal;
    private RadioGroup rg_in, rg;
    LinearLayout lin_ftp_change;

    MaterialButton btn_save;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);

        comHelper = new ComHelper(getContext(), sp);
        dbHelper = new DbHelper(getContext());

        comHelper.addSP(ConfigMP.TITLE, actionBar.getTitle().toString(), true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_version = view.findViewById(R.id.txt_version);
        edit_LOCCOD = view.findViewById(R.id.edit_LOCCOD);
        edit_TBCODE = view.findViewById(R.id.edit_TBCODE);
        edit_USERID = view.findViewById(R.id.edit_USERID);
        edit_BILLNO = view.findViewById(R.id.edit_BILLNO);
        edit_SER_IP = view.findViewById(R.id.edit_SER_IP);

        edit_FTP_IP = view.findViewById(R.id.edit_FTP_IP);
        edit_FTP_PORT = view.findViewById(R.id.edit_FTP_PORT);
        edit_FTP_USER = view.findViewById(R.id.edit_FTP_USER);
        edit_FTP_PASS = view.findViewById(R.id.edit_FTP_PASS);

        switchIntro = view.findViewById(R.id.txt_intro);
        switchFtp = view.findViewById(R.id.txt_ftp);
        switchDecimal = view.findViewById(R.id.txt_decimal);

        btn_save = view.findViewById(R.id.btn_save);

        rg_in = view.findViewById(R.id.rg_file_input_type);
        rg = view.findViewById(R.id.rg_file_type);

        lin_ftp_change = view.findViewById(R.id.lin_ftp_change);

        setConfigFromTxt();
        setDefault(view);

        switchIntro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                comHelper.addSP(ConfigMP.SP_INTROW, (isChecked ? "Y" : "N"), true);
                comHelper.snackbar(getActivity(), (isChecked ? "Visible" : "Invisible"));
            }
        });

        switchFtp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                comHelper.addSP(ConfigMP.SP_FTP, (isChecked ? "Y" : "N"), true);
                comHelper.snackbar(getActivity(), (isChecked ? "Default Settings Applied." : "Set Ftp Settings"));
                lin_ftp_change.setVisibility(isChecked ? View.GONE : View.VISIBLE);

                if (!isChecked) {
                    edit_FTP_IP.setText("");
                    edit_FTP_PORT.setText("");
                    edit_FTP_USER.setText("");
                    edit_FTP_PASS.setText("");
                }
            }
        });

        switchDecimal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                comHelper.addSP(ConfigMP.SP_DECIMAL, (isChecked ? "Y" : "N"), true);
                comHelper.snackbar(getActivity(), (isChecked ? "With Decimal" : "Without Decimal"));
            }
        });

        rg_in.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                comHelper.addSP(ConfigMP.SP_INPFIL, (checkedId == R.id.input_radio1 ? ".DAT" : ".DB"), true);
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                comHelper.addSP(ConfigMP.SP_OUTFIL, (checkedId == R.id.radio1 ? ".DAT" : ".DB"), true);
            }
        });

        if (sp.getString(ConfigMP.SP_INPFIL, "") == null || sp.getString(ConfigMP.SP_INPFIL, "").isEmpty()) {
            comHelper.addSP(ConfigMP.SP_INPFIL, ".DB", true);
        }
        if (sp.getString(ConfigMP.SP_OUTFIL, "") == null || sp.getString(ConfigMP.SP_OUTFIL, "").isEmpty()) {
            comHelper.addSP(ConfigMP.SP_OUTFIL, ".DAT", true);
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loccode = edit_LOCCOD.getText().toString().trim();
                String tbcode = edit_TBCODE.getText().toString().trim();
                String userid = edit_USERID.getText().toString().trim();
                String billno = edit_BILLNO.getText().toString().trim();
                String ser_ip = edit_SER_IP.getText().toString().trim();

                if (loccode.isEmpty()) {
                    comHelper.alert("Please Enter Loc Code");

                } else if (tbcode.isEmpty()) {
                    comHelper.alert("Please Enter Gun Number");

                } else if (userid.isEmpty()) {
                    comHelper.alert("Please Enter User ID");

                } else if (billno.isEmpty()) {
                    comHelper.alert("Please Enter Current Bill No");

                } else if (ser_ip.isEmpty()) {
                    comHelper.alert("Please Enter IP address");

                } else {
                    comHelper.addSP(ConfigMP.SP_LOCCOD, loccode, true);
                    comHelper.addSP(ConfigMP.SP_TBCODE, tbcode, true);
                    comHelper.addSP(ConfigMP.SP_USERID, userid, true);
                    comHelper.addSP(ConfigMP.SP_USERID, userid, true);
                    comHelper.addSP(ConfigMP.SP_HOST, ser_ip, true);

                    if (sp.getString(ConfigMP.SP_FTP, "").equalsIgnoreCase("N")) {
                        String ftp_ip = edit_FTP_IP.getText().toString().trim();
                        String ftp_port = edit_FTP_PORT.getText().toString().trim();
                        String ftp_user = edit_FTP_USER.getText().toString().trim();
                        String ftp_pass = edit_FTP_PASS.getText().toString().trim();

                        if (ftp_ip.isEmpty()) {
                            comHelper.alert("Please Enter FTP IP address");
                        } else if (ftp_port.isEmpty()) {
                            comHelper.alert("Please Enter FTP PORT");
                        } else if (ftp_user.isEmpty()) {
                            comHelper.alert("Please Enter FTP User");
                        } else if (ftp_pass.isEmpty()) {
                            comHelper.alert("Please Enter FTP Password");
                        }

                        comHelper.addSP(ConfigMP.SP_FTP_IP, ftp_ip, true);
                        comHelper.addSP(ConfigMP.SP_FTP_PORT, ftp_port, true);
                        comHelper.addSP(ConfigMP.SP_FTP_USER, ftp_user, true);
                        comHelper.addSP(ConfigMP.SP_FTP_PASS, ftp_pass, true);
                    }

                    if (!edit_BILLNO.getText().toString().isEmpty()) {
                        dbHelper.updateSequence(sp, edit_BILLNO.getText().toString());
                    }

                    comHelper.alert("Successfully Saved");
                }
            }
        });

        return view;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
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
            case R.id.action_dashboard:
                actionBar.setTitle(R.string.title_dashboard);
                comHelper.loadFragment(new DashboardFragment());
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

    private void setConfigFromTxt() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "BackupSF/BackupFILE/config_cycle.txt");
        try {
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                int lineNo = 0;

                while ((line = br.readLine()) != null) {
                    if (lineNo == 0) {
                        comHelper.addSP(ConfigMP.SP_LOCCOD, (sp.getString(ConfigMP.SP_LOCCOD, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_LOCCOD, "")), true);
                    } else if (lineNo == 1) {
                        comHelper.addSP(ConfigMP.SP_TBCODE, (sp.getString(ConfigMP.SP_TBCODE, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_TBCODE, "")), true);
                    } else if (lineNo == 2) {
                        comHelper.addSP(ConfigMP.SP_HOST, (sp.getString(ConfigMP.SP_HOST, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_HOST, "")), true);

                    } else if (lineNo == 3) {
                        comHelper.addSP(ConfigMP.SP_FTP_IP, (sp.getString(ConfigMP.SP_FTP_IP, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_FTP_IP, "")), true);
                    } else if (lineNo == 4) {
                        comHelper.addSP(ConfigMP.SP_FTP_PORT, (sp.getString(ConfigMP.SP_FTP_PORT, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_FTP_PORT, "")), true);
                    } else if (lineNo == 5) {
                        comHelper.addSP(ConfigMP.SP_FTP_USER, (sp.getString(ConfigMP.SP_FTP_USER, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_FTP_USER, "")), true);
                    } else if (lineNo == 6) {
                        comHelper.addSP(ConfigMP.SP_FTP_PASS, (sp.getString(ConfigMP.SP_FTP_PASS, "").isEmpty() ? line.trim() : sp.getString(ConfigMP.SP_FTP_PASS, "")), true);
                    }
                    lineNo++;
                }
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefault(View view) {
        txt_version.setText(BuildConfig.VERSION_NAME);
        switchIntro.setChecked((sp.getString(ConfigMP.SP_INTROW, "").isEmpty() || (sp.getString(ConfigMP.SP_INTROW, "").equals("Y")) ? true : false));
        switchFtp.setChecked((sp.getString(ConfigMP.SP_FTP, "").isEmpty() || (sp.getString(ConfigMP.SP_FTP, "").equals("Y")) ? true : false));
        switchDecimal.setChecked((sp.getString(ConfigMP.SP_DECIMAL, "").isEmpty() || (sp.getString(ConfigMP.SP_DECIMAL, "").equals("Y")) ? true : false));
        edit_LOCCOD.setText(sp.getString(ConfigMP.SP_LOCCOD, ""));
        edit_TBCODE.setText(sp.getString(ConfigMP.SP_TBCODE, ""));
        edit_USERID.setText(sp.getString(ConfigMP.SP_USERID, ""));
        edit_BILLNO.setText(sp.getString(ConfigMP.SP_BILLNO, ""));
        edit_SER_IP.setText(sp.getString(ConfigMP.SP_HOST, ""));

        edit_FTP_IP.setText(sp.getString(ConfigMP.SP_FTP_IP, ""));
        edit_FTP_PORT.setText(sp.getString(ConfigMP.SP_FTP_PORT, ""));
        edit_FTP_USER.setText(sp.getString(ConfigMP.SP_FTP_USER, ""));
        edit_FTP_PASS.setText(sp.getString(ConfigMP.SP_FTP_PASS, ""));

        RadioButton rb_in1 = view.findViewById(R.id.input_radio1);
        RadioButton rb_in2 = view.findViewById(R.id.input_radio2);
        String rbINCheck = sp.getString(ConfigMP.SP_INPFIL, "");
        if (rbINCheck.isEmpty() || rbINCheck.equals(".DB")) {
            rb_in2.setChecked(true);
        } else {
            rb_in1.setChecked(true);
        }

        RadioButton rb1 = view.findViewById(R.id.radio1);
        RadioButton rb2 = view.findViewById(R.id.radio2);
        String rbCheck = sp.getString(ConfigMP.SP_OUTFIL, "");
        if (rbINCheck.isEmpty() || rbCheck.equals(".DAT")) {
            rb1.setChecked(true);
        } else {
            rb2.setChecked(true);
        }

        lin_ftp_change.setVisibility(switchFtp.isChecked() ? View.GONE : View.VISIBLE);
    }

}
