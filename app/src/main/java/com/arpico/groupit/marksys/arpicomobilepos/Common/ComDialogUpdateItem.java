package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.arpico.groupit.marksys.arpicomobilepos.Recycler.RecyclerViewItemsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComDialogUpdateItem {
    private Context mContext;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private List<ItemsModel> itemList;
    private List<ItemsModel> list_item = new ArrayList<>();
    private RecyclerViewItemsAdapter itemAdapter;
    private SharedPreferences sp;
    private int intItemCount;
    private RecyclerView recyclerRecentItems;
    private TextView txt_qty, txt_amount;
    private boolean isFindItem;
    int count = 0;

    public ComDialogUpdateItem(Context mContext, SharedPreferences sp, ComHelper comHelper, DbHelper dbHelper, List<ItemsModel> list_item,
                               RecyclerViewItemsAdapter itemAdapter, RecyclerView recyclerRecentItems, int intItemCount, TextView txt_qty, TextView txt_amount, boolean isFindItem) {

        this.mContext = mContext;
        this.sp = sp;
        this.comHelper = comHelper;
        this.dbHelper = dbHelper;
        this.list_item = list_item;
        this.itemAdapter = itemAdapter;
        this.intItemCount = intItemCount;
        this.recyclerRecentItems = recyclerRecentItems;

        this.txt_qty = txt_qty;
        this.txt_amount = txt_amount;
        this.isFindItem = isFindItem;
    }

    public void getItemsData(String searchCode) {
        try {
            list_item.clear();
            if (itemAdapter != null) {
                itemAdapter.notifyDataSetChanged();
            }

//            recentItemHeader(intItemCount, false);
            itemList = new Gson().fromJson(dbHelper.getCountItems(searchCode, (searchCode.equals("") ? " WHERE " : " AND ") + ConfigMP.C_ICOUNT + " > '0' ").toString(),//ORDER BY " + ConfigMP.C_CREADT + " DESC
                    new TypeToken<ArrayList<ItemsModel>>() {
                    }.getType());
            if (itemList.size() > 0) {
                for (ItemsModel selectItem : itemList) {
                    validListAdd(selectItem);
//                    recentItemHeader(Integer.parseInt(selectItem.getICOUNT()), true);
                    getTotSum();

                    if (isFindItem) {
                        if (list_item.size() > 6) {
                            itemAdapter.removeItem(5);
                        }
                    }
                }
            } else {
                comHelper.alert(mContext.getResources().getString(R.string.msg_data_not_found));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validListAdd(ItemsModel findItem) {
        list_item.add(new ItemsModel(findItem.getROWNUM(), findItem.getPLUCOD(), findItem.getITMCOD(), findItem.getITMDES(), findItem.getBARCOD(), findItem.getUNIPRI(), findItem.getENCODE(), findItem.getICOUNT(), findItem.getROWSUM(), findItem.getTBCODE(), findItem.getCREABY(), findItem.getCREADT()));
        itemAdapter = new RecyclerViewItemsAdapter(list_item, false, new RecyclerViewItemsAdapter.OnItemClick() {
            @Override
            public void onAdd(ItemsModel itemsModel, int position) {
                count = Integer.parseInt(itemsModel.getICOUNT()) + 1;
                if (count >= 1) {
                    itemsModel.setICOUNT(String.valueOf(count));
                    itemsModel.setROWSUM(String.valueOf(getRowSum(itemsModel)));
                    getTotSum();
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
                    getTotSum();
                    notifyData();

                    dbHelper.updateCartItems(itemsModel);
                }
            }

            @Override
            public void onAddItem(ItemsModel itemsModel, int position) {
            }

            @Override
            public void onDeleteItem(ItemsModel itemsModel, int position) {
                itemsModel.setICOUNT(String.valueOf(0));
                dbHelper.updateCartItems(itemsModel);
                itemAdapter.removeItem(position);
                getTotSum();
                notifyData();
            }
        });
        notifyData();
    }

    private void notifyData() {
        itemAdapter.notifyDataSetChanged();
        recyclerRecentItems.setAdapter(itemAdapter);
    }

    private double getRowSum(ItemsModel itemsModel) {
        double sumRowAmt = 0;
        double amount = Double.parseDouble(itemsModel.getUNIPRI());
        double qty = Double.parseDouble(itemsModel.getICOUNT());

        sumRowAmt = amount * qty;

        return sumRowAmt;
    }

    private void getTotSum() {
        double totqty = 0;
        double totamount = 0;

        for (int i = 0; i < list_item.size(); i++) {
            ItemsModel itemsModel = list_item.get(i);

            totqty += Double.parseDouble(itemsModel.getICOUNT());
            totamount += Double.parseDouble(itemsModel.getROWSUM());

        }
        txt_qty.setText(String.valueOf(totqty));
        txt_amount.setText(String.valueOf(totamount));

    }

//    private void recentItemHeader(int prm_ordCount, boolean boolAdd) {
//        if (boolAdd) {
//            intItemCount += prm_ordCount;
//        } else {
//            intItemCount -= prm_ordCount;
//        }
//
//        txt_count.setText(mContext.getResources().getString(R.string.txt_count) + " " + list_item.size());
//        txt_qty.setText(mContext.getResources().getString(R.string.txt_qty) + " " + intItemCount);
//    }

}
