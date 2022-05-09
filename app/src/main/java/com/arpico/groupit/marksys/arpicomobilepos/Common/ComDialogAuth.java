package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.DashboardFragment;
import com.arpico.groupit.marksys.arpicomobilepos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ComDialogAuth extends Fragment {
    private String loginID, password;
    private SharedPreferences sp;
    private Context mContext;
    private Socket socket;
    private PrintWriter printWriter;
    private String host = "";
    private ComHelper comHelper;
    private DataDbFileSync dataDbFileSync;
    private DbHelper dbHelper;
    private DashboardFragment dashboardFragment;

    public ComDialogAuth(Context mContext, SharedPreferences shpref) {
        this.loginID = "";
        this.password = "";
        this.sp = shpref;
        this.mContext = mContext;
        this.host = sp.getString(ConfigMP.SP_HOST, "");
        this.comHelper = new ComHelper(mContext, sp);
        this.dataDbFileSync = new DataDbFileSync(mContext, sp);
        this.dbHelper = new DbHelper(mContext);
        this.dashboardFragment = new DashboardFragment();
    }

    public void dialogAuth() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Please enter login credentials..");

        final EditText inputID = new EditText(mContext);
        inputID.setHint("ID");
        final EditText inputPW = new EditText(mContext);
        inputPW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPW.setHint("password");
        LinearLayout lay = new LinearLayout(mContext);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(inputID);
        lay.addView(inputPW);
        lay.setPadding(20, 5, 20, 5);
        builder.setView(lay);

        builder.setPositiveButton("Authorized By", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loginID = inputID.getText().toString();
                Md5 m = new Md5(inputPW.getText().toString());
                m.processString();
                password = m.getStringDigest();
                if (loginID.isEmpty()) {
                    Toast.makeText(mContext, "Please enter ID..", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(mContext, "Please enter password..", Toast.LENGTH_SHORT).show();
                } else {
                    String autType = "";
                    comHelper.addSP(ConfigMP.SP_AUTHID, loginID, true);
                    if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_download))) {
                        autType = "Download_posDownload";

                    } else if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_reset)) ||
                            sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_upload_reset))) {
                        autType = "Download_posReset";


                    } else if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_find_item)) ||
                            sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_find_item))) {
                        autType = "Download_posFindItem";
                    }

                    new authorizedResetRequest().execute(autType);
                }

            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    class authorizedResetRequest extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... voids) {
            String type = voids[0];

            try {
                JSONObject obj = new JSONObject();
                obj.put("SOCKET", comHelper.getWiFiAddress());
                obj.put(ConfigMP.C_LOCCOD, sp.getString(ConfigMP.SP_LOCCOD, ""));
                obj.put(ConfigMP.C_TBCODE, sp.getString(ConfigMP.SP_TBCODE, ""));
                obj.put(ConfigMP.C_USERID, loginID);
                obj.put(ConfigMP.C_USERPW, password);
                obj.put("TYPE", type);

                socket = new Socket(host, ConfigMP.PORT);
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(obj.toString());
                printWriter.flush();
                printWriter.close();
                socket.close();

                loginID = "";
                password = "";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
