package com.arpico.groupit.marksys.arpicomobilepos.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arpico.groupit.marksys.arpicomobilepos.Activitys.MainActivity;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogAuth;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComDialogUpdateItem;
import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Common.CreatePDF;
import com.arpico.groupit.marksys.arpicomobilepos.Common.Md5;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.arpico.groupit.marksys.arpicomobilepos.Recycler.RecyclerViewItemsAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.BuildConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FindItemsFragment extends Fragment {
    private List<ItemsModel> findItemList;
    private List<ItemsModel> list_search_item = new ArrayList<>();
    private List<ItemsModel> list_found_item = new ArrayList<>();

    private RecyclerView searchRecyclerViewItems;
    private RecyclerView foundRecyclerViewItems;
    private RecyclerViewItemsAdapter searchItemAdapter;
    private RecyclerViewItemsAdapter foundItemAdapter;
    private TextView txt_recent_count, txt_recent_qty, tot_qty, tot_amount;
    private int intFoundItemCount = 0;

    private ActionBar actionBar;
    private Type listType;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private ComDialogUpdateItem comDialogUpdateItem;
    private SharedPreferences sp;
    private Handler handler = new Handler();
    int count = 0;
    MaterialButton btn_pay_cash;
    double sumRowAmt = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_items, container, false);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);

        comHelper = new ComHelper(getContext(), sp);
        dbHelper = new DbHelper(getContext());

        comHelper.addSP(ConfigMP.TITLE, actionBar.getTitle().toString(), true);
        comHelper.addSP(ConfigMP.SP_COUNT_TYPE, getResources().getString(R.string.title_shopping_item), true);
        comHelper.addSP(ConfigMP.SP_START_TIME, dbHelper.getTime(3), true);

        listType = new TypeToken<ArrayList<ItemsModel>>() {
        }.getType();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        searchRecyclerViewItems = view.findViewById(R.id.listFindItem);
        searchRecyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));

        foundRecyclerViewItems = view.findViewById(R.id.listRecentItem);
        foundRecyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));

        txt_recent_count = view.findViewById(R.id.txt_recent_count);
        txt_recent_qty = view.findViewById(R.id.txt_recent_qty);

        tot_qty = view.findViewById(R.id.tot_qty);
        tot_amount = view.findViewById(R.id.tot_amount);

        btn_pay_cash = view.findViewById(R.id.btn_pay_cash);
        btn_pay_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sumRowAmt > 0) {
                    dialogPayment();
                } else {
                    comHelper.alert("Please Add Items...");
                }
            }
        });

        comDialogUpdateItem = new ComDialogUpdateItem(getContext(), sp, comHelper, dbHelper, list_found_item, foundItemAdapter, foundRecyclerViewItems, intFoundItemCount, tot_qty, tot_amount, true);
        comDialogUpdateItem.getItemsData("");

        return view;
    }

    public void dialogPayment() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cash Payment");

        final TextView amount = new TextView(getContext());
        amount.setText("Total Amount : " + sumRowAmt);
        amount.setTypeface(Typeface.DEFAULT_BOLD);
        amount.setPadding(5, 10, 5, 5);
        amount.setTextSize(15);
        amount.setTextColor(Color.GRAY);

        final EditText cashamt = new EditText(getContext());
        cashamt.setInputType(InputType.TYPE_CLASS_NUMBER);
        cashamt.setHint("Enter Amount");

        final TextView changeamt = new TextView(getContext());
        changeamt.setHint("Change Amount : ");
        changeamt.setPadding(5, 10, 5, 5);
        changeamt.setTextSize(15);
        changeamt.setTextColor(Color.RED);
        cashamt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!cashamt.getText().toString().trim().equals("")) {
                    double finAmt = Double.parseDouble(cashamt.getText().toString()) - sumRowAmt;
                    changeamt.setText("Change Amount : " + String.format("%.2f", finAmt));
                } else {
                    changeamt.setText("Change Amount : ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final EditText remark = new EditText(getContext());
        remark.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        remark.setLines(5);
        remark.setMaxLines(5);
        remark.setHint("Enter Remark");
        remark.setVerticalScrollBarEnabled(true);
        remark.setMovementMethod(ScrollingMovementMethod.getInstance());
        remark.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        LinearLayout lay = new LinearLayout(getContext());
        lay.setOrientation(LinearLayout.VERTICAL);

        lay.addView(amount);
        lay.addView(cashamt);
        lay.addView(changeamt);
        lay.addView(remark);
        lay.setPadding(20, 5, 20, 5);
        builder.setView(lay);

        builder.setPositiveButton("Process", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                comHelper.addSP(ConfigMP.SP_END_TIME, dbHelper.getTime(3), true);
                try {
                   String _changeamt =changeamt.getText().toString().replace("Change Amount : ","");

                    final JSONObject insobj = dbHelper.saveReceipt(list_found_item, sp, String.valueOf(sumRowAmt), cashamt.getText().toString(), _changeamt, remark.getText().toString());
                    if (insobj.get("result").toString().equalsIgnoreCase("success")) {

                        comHelper.addSP(ConfigMP.SP_BILLNO, insobj.get("doc_no").toString(), true);

                        CreatePDF createPDF = new CreatePDF();
                        File file = createPDF.createReportPDF(list_found_item, insobj.get("doc_no").toString(), dbHelper.getTime(2), sp, getContext());
                        if (file != null) {
                            actionBar.setTitle(R.string.title_send_receipt);
                            comHelper.loadFragment(new SendReceiptFragment(file));
                        }

                        dbHelper.updateCartItems(null);
                        comDialogUpdateItem.getItemsData("");
                        comHelper.alert(insobj.get("message").toString());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_upload).setVisible(false);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) ((MainActivity) getContext()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) getContext()).getComponentName()));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new getItems().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchItemAdapter != null) {
                    list_search_item.clear();
                    searchItemAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.forSupportFragment(FindItemsFragment.this).initiateScan();
                break;
            default:
                break;
        }
        return false;
    }

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

    private void validListAdd(int rowCount, ItemsModel findItem) {
        list_search_item.add(new ItemsModel(String.valueOf(rowCount), findItem.getPLUCOD(), findItem.getITMCOD(), findItem.getITMDES(), findItem.getBARCOD(), findItem.getUNIPRI(), findItem.getENCODE(), findItem.getICOUNT(), findItem.getROWSUM(), findItem.getTBCODE(), findItem.getCREABY(), findItem.getCREADT()));
        searchItemAdapter = new RecyclerViewItemsAdapter(list_search_item, true, new RecyclerViewItemsAdapter.OnItemClick() {
            @Override
            public void onAdd(ItemsModel itemsModel, int position) {
                count = Integer.parseInt(itemsModel.getICOUNT()) + 1;
                if (count >= 1) {
                    itemsModel.setICOUNT(String.valueOf(count));
                    itemsModel.setROWSUM(String.valueOf(getRowSum(itemsModel)));
                    notifyData();

                    dbHelper.updateCartItems(itemsModel);
                }
            }

            @Override
            public void onRemove(ItemsModel itemsModel, int position) {
                count = Integer.parseInt(itemsModel.getICOUNT()) - 1;
                if (count >= 1) {
                    itemsModel.setICOUNT(String.valueOf(count));
                    itemsModel.setROWSUM(String.valueOf(getRowSum(itemsModel)));
                    notifyData();

                    dbHelper.updateCartItems(itemsModel);
                }
            }

            @Override
            public void onAddItem(ItemsModel itemsModel, int position) {
                if (list_search_item.size() > 0) {
                    JSONObject resObj = dbHelper.updateCartItems(list_search_item.get(position));
                    try {
                        if (resObj != null) {
                            if (resObj.getString("result").equalsIgnoreCase("success")) {
                                resetData();
                                comDialogUpdateItem.getItemsData("");
                            }
                            comHelper.alert(resObj.getString("message"));
                            count = 0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    comHelper.alert("Data not found...");
                }
            }

            @Override
            public void onDeleteItem(ItemsModel itemsModel, int position) {
            }
        });
        notifyData();
    }

    private void resetData() {
        list_search_item.clear();
        if (searchItemAdapter != null) {
            searchItemAdapter.notifyDataSetChanged();
            searchRecyclerViewItems.setAdapter(searchItemAdapter);
        }
//        list_found_item.clear();
//        if (foundItemAdapter != null) {
//            foundItemAdapter.notifyDataSetChanged();
//            foundRecyclerViewItems.setAdapter(foundItemAdapter);
//        }
    }

    private void notifyData() {
        searchItemAdapter.notifyDataSetChanged();
        searchRecyclerViewItems.setAdapter(searchItemAdapter);
    }

    private double getRowSum(ItemsModel itemsModel) {
        sumRowAmt = 0;
        double amount = Double.parseDouble(itemsModel.getUNIPRI());
        double qty = Double.parseDouble(itemsModel.getICOUNT());

        sumRowAmt = amount * qty;

        return Double.parseDouble(String.format("%.2f", sumRowAmt));
    }

//    private double getTotSum(ItemsModel itemsModel) {
//        double sumRowAmt = 0;
//        double amount = Double.parseDouble(itemsModel.getUNIPRI());
//        double qty = Double.parseDouble(itemsModel.getICOUNT());
//
//        sumRowAmt = amount * qty;
//
//        return sumRowAmt;
//    }

    class getItems extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;
        int count = 0;

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
            list_search_item.clear();
            findItemList = new Gson().fromJson(dbHelper.getViewItemsData(code, "AND " + ConfigMP.C_ICOUNT + " = '0'").toString(), listType);
            handler.post(new Runnable() {

                public void run() {
                    for (ItemsModel selectItem : findItemList) {
                        selectItem.setICOUNT(String.valueOf(Integer.parseInt(selectItem.getICOUNT()) + 1));
                        selectItem.setROWSUM(String.valueOf(getRowSum(selectItem)));

                        validListAdd(count, selectItem);

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

                int _count = (count - 1);
                if (_count == 0) {
                    comHelper.alert("Item Not Found...");
                }
            }
        }
    }

}
