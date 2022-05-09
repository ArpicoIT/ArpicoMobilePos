package com.arpico.groupit.marksys.arpicomobilepos.Fragments;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Common.CreatePDF;
import com.arpico.groupit.marksys.arpicomobilepos.Common.mailBody;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;

public class SendReceiptFragment extends Fragment {

    MaterialButton btn_send_whatsapp, btn_send_mail;
    EditText edit_MOBILE, edit_MAIL, edit_NAME;
    private ComHelper comHelper;
    private SharedPreferences sp;
    private File saveFile = null;

    public SendReceiptFragment(File file) {
        this.saveFile = file;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_receipt, container, false);

        edit_MOBILE = view.findViewById(R.id.edit_MOBILE);
        edit_MAIL = view.findViewById(R.id.edit_MAIL);
        edit_NAME = view.findViewById(R.id.edit_NAME);

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);

        comHelper = new ComHelper(getContext(), sp);

        btn_send_whatsapp = view.findViewById(R.id.btn_send_whatsapp);
        btn_send_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_NAME.getText().toString().trim().equals("") && !edit_MOBILE.getText().toString().trim().equals("")) {
                    Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, ContactsContract.Contacts.CONTENT_URI);
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, edit_NAME.getText().toString());
                    intent.setData(Uri.parse("tel:+94" + edit_MOBILE.getText().toString()));
                    startActivityForResult(intent, 1);
                } else {
                    comHelper.alert("Please Enter WhatsApp Name & Number ");
                }

            }
        });

        btn_send_mail = view.findViewById(R.id.btn_send_mail);
        btn_send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getContext(), "com.arpico.groupit.marksys.arpicomobilepos.provider", saveFile);

                if (!edit_MAIL.getText().toString().trim().equals("")) {
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{edit_MAIL.getText().toString()});
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Arpico Super Center POS Bill");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Dear Sir/Madam,\n\n" +
                            "Please Find the Attachment that Bill.\n\n" +
                            "Thanks,\n" +
                            "Arpico Super Center");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("application/pdf");
                    sendIntent.setPackage("com.google.android.gm");
                    startActivity(sendIntent);
                } else {
                    comHelper.alert("Please Enter Email ");
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Uri uri = FileProvider.getUriForFile(getContext(), "com.arpico.groupit.marksys.arpicomobilepos.provider", saveFile);

            if (!edit_MOBILE.getText().toString().trim().equals("")) {
                String toNumber = "+94" + edit_MOBILE.getText().toString(); // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("application/pdf");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            } else {
                comHelper.alert("Please Enter WhatsApp Number ");
            }
        }
    }

}

