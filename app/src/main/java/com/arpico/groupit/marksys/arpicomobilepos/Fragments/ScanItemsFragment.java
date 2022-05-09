package com.arpico.groupit.marksys.arpicomobilepos.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogUpdateItem;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.arpico.groupit.marksys.arpicomobilepos.Recycler.RecyclerViewItemsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScanItemsFragment extends Fragment {

    private List<ItemsModel> findItemList;
    private List<ItemsModel> list_find_item = new ArrayList<>();

    private RecyclerView recyclerViewItems;
    private RecyclerViewItemsAdapter itemAdapter;
    TextView txt_scan_count;

    private ActionBar actionBar;
    private Type listType;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private SharedPreferences sp;
    private Handler handler = new Handler();

    private Button btn_scan, btn_reset, btn_save;

    private ComDialogUpdateItem comDialogUpdateItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_items, container, false);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);

        comHelper = new ComHelper(getContext(), sp);
        dbHelper = new DbHelper(getContext());

        txt_scan_count = view.findViewById(R.id.txt_scan_count);

        comHelper.addSP(ConfigMP.TITLE, actionBar.getTitle().toString(), true);
        comHelper.addSP(ConfigMP.SP_COUNT_TYPE, getResources().getString(R.string.title_scan_item), true);

        listType = new TypeToken<ArrayList<ItemsModel>>() {
        }.getType();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recyclerViewItems = view.findViewById(R.id.listScanItem);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));

        btn_scan = view.findViewById(R.id.btn_scan);
        btn_reset = view.findViewById(R.id.btn_reset);
        btn_save = view.findViewById(R.id.btn_save);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.forSupportFragment(ScanItemsFragment.this).initiateScan();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmation(0, "Reset");
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmationSave();
            }
        });

//        comDialogUpdateItem = new ComDialogUpdateItem(getContext(), sp, comHelper, dbHelper, list_find_item, itemAdapter, recyclerViewItems, 0, txt_scan_count, txt_scan_count, false);
//        comDialogUpdateItem.getItemsData("");

//        enableItemTouchScan();

        return view;
    }

    private void dialogConfirmationSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Do you want to Save ? ");
        builder.setMessage("\nCounted Item Save");
        builder.setIcon(R.drawable.ic_save);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (list_find_item.size() > 0) {
                    JSONObject resObj = dbHelper.updateItems(list_find_item.get(0), true);
                    try {
                        if (resObj != null) {
                            if (resObj.getString("result").equalsIgnoreCase("success")) {
                                list_find_item.clear();
                                itemAdapter.notifyDataSetChanged();
                            }
                            comHelper.alert(resObj.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    comHelper.alert("Data not found...");
                }

                dialog.dismiss();


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogConfirmation(final int position, final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Do you want to " + text + " ? ");
        builder.setMessage("\nCounted Item " + text + "");
        builder.setIcon(text.equals("Reset") ? R.drawable.ic_refresh : R.drawable.ic_delete);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (text.equals("Delete")) {
                    list_find_item.remove(position);

                } else {
                    list_find_item.clear();
                }
                if (itemAdapter != null) {
                    itemAdapter.notifyDataSetChanged();
                }
                txt_scan_count.setText("Counted: " + String.valueOf(list_find_item.size()));

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

//        builder.setNeutralButton("Delete All", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//
//                dbHelper.resetData(list_find_item.get(position));
//                if (itemAdapter != null) {
//                    itemAdapter.notifyDataSetChanged();
//                }
//                comDialogUpdateItem.getItemsData("");
//            }
//        });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    private void enableItemTouchScan() {
//        recyclerViewItems.addOnItemTouchListener(new RecyclerViewItemsAdapter.ItemTouchListener(getActivity(),
//                recyclerViewItems, new RecyclerViewItemsAdapter.ClickListener() {
//
//            @Override
//            public void onClick(View view, final int position) {
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                dialogConfirmation(position, "Delete");
//            }
//        }));
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                comHelper.alert("Invalid");
            } else {
                new getItems().execute(intentResult.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class getItems extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.setMessage("Fetching Data.\nPlease wait...");
            pd.show();
        }

        @Override
        protected Void doInBackground(String... voids) {
            String code = voids[0];
//            list_find_item.clear();
            findItemList = new Gson().fromJson(dbHelper.getViewItemsData(code, "").toString(), listType);
            handler.post(new Runnable() {

                public void run() {
                    for (ItemsModel selectItem : findItemList) {
                        validListAdd(selectItem);
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            txt_scan_count.setText("Counted: " + String.valueOf(list_find_item.size()));
        }
    }

    private void validListAdd(ItemsModel findItem) {
        list_find_item.add(new ItemsModel(findItem.getROWNUM(), findItem.getPLUCOD(), findItem.getITMCOD(), findItem.getITMDES(), findItem.getBARCOD(), findItem.getUNIPRI(), findItem.getENCODE(), findItem.getICOUNT(), findItem.getROWSUM(), findItem.getTBCODE(), findItem.getCREABY(), findItem.getCREADT()));
//        itemAdapter = new RecyclerViewItemsAdapter(list_find_item);
        itemAdapter.notifyDataSetChanged();
        recyclerViewItems.setAdapter(itemAdapter);
    }
}