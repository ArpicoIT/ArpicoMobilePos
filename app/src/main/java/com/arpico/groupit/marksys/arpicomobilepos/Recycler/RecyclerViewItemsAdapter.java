package com.arpico.groupit.marksys.arpicomobilepos.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewItemsAdapter extends RecyclerView.Adapter<RecyclerViewItemsAdapter.ViewHolder> implements Filterable {
    private List<ItemsModel> data = new ArrayList<>();
    private List<ItemsModel> filterData;
    private OnItemClick onItemClick;
    boolean boolAdd;

    public RecyclerViewItemsAdapter(List<ItemsModel> data, boolean boolAdd, OnItemClick onItemClick) {
        this.data = data;
        filterData = new ArrayList<>(data);
        this.onItemClick = onItemClick;
        this.boolAdd = boolAdd;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final ItemsModel itemsModel = data.get(position);

        holder.ROWNUM.setText(String.valueOf(itemsModel.getROWNUM()));
        holder.PLUCOD.setText(itemsModel.getPLUCOD());
        holder.ITMCOD.setText(itemsModel.getITMCOD());
        holder.ITMDES.setText(itemsModel.getITMDES());
        holder.BARCOD.setText(itemsModel.getBARCOD());
        holder.UNIPRI.setText(itemsModel.getUNIPRI());
        holder.ENCODE.setText(itemsModel.getENCODE());
        holder.ICOUNT.setText(itemsModel.getICOUNT());
        holder.ROWSUM.setText(itemsModel.getROWSUM());
//        holder.TBCODE.setText(data.get(position).getTBCODE());
//        holder.CREABY.setText(data.get(position).getCREABY());
//        holder.CREADT.setText(data.get(position).getCREADT());

        if (boolAdd) {
            holder.btn_delete_item.setVisibility(View.GONE);
            holder.btn_add_item.setVisibility(View.VISIBLE);
        } else {
            holder.btn_delete_item.setVisibility(View.VISIBLE);
            holder.btn_add_item.setVisibility(View.GONE);
        }

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onAdd(itemsModel, position);
            }
        });

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onRemove(itemsModel, position);
            }
        });

        holder.btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onAddItem(itemsModel, position);
            }
        });

        holder.btn_delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onDeleteItem(itemsModel, position);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return filterDataList;
    }

    private Filter filterDataList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ItemsModel> filterList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(filterData);
            } else {
                String filterPattern = charSequence.toString().toLowerCase();

                for (ItemsModel item : filterData) {
                    if (charSequence.length() == 6) {
                        if (item.getPLUCOD().toLowerCase().contains(filterPattern)) {
                            filterList.add(item);
                        }
                    } else {
                        if (item.getBARCOD().toLowerCase().contains(filterPattern) || item.getENCODE().toLowerCase().contains(filterPattern)) {
                            filterList.add(item);
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            data.clear();
            data.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ROWNUM, PLUCOD, ITMCOD, ITMDES, BARCOD, UNIPRI, ENCODE, ICOUNT, TBCODE, CREABY, CREADT, ROWSUM;
        MaterialButton btn_add, btn_remove, btn_add_item, btn_delete_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ROWNUM = itemView.findViewById(R.id.txt_ROWNUM);
            PLUCOD = itemView.findViewById(R.id.txt_PLUCOD);
            ITMCOD = itemView.findViewById(R.id.txt_ITMCOD);
            ITMDES = itemView.findViewById(R.id.txt_ITMDES);
            BARCOD = itemView.findViewById(R.id.txt_BARCOD);
            UNIPRI = itemView.findViewById(R.id.txt_UNIPRI);
            ENCODE = itemView.findViewById(R.id.txt_ENCODE);
            ICOUNT = itemView.findViewById(R.id.txt_ICOUNT);
            ROWSUM = itemView.findViewById(R.id.txt_ROWSUM);

//            TBCODE = itemView.findViewById(R.id.txt_TBCODE);
//            CREABY = itemView.findViewById(R.id.txt_CREABY);
//            CREADT = itemView.findViewById(R.id.txt_CREADT);

            btn_add = itemView.findViewById(R.id.btn_add);
            btn_remove = itemView.findViewById(R.id.btn_remove);

            btn_add_item = itemView.findViewById(R.id.btn_add_item);
            btn_delete_item = itemView.findViewById(R.id.btn_delete_item);
        }
    }

//    public interface ClickListener {
//        void onClick(View view, int position);
//
//        void onLongClick(View view, int position);
//
//        void onAddQTY(ItemsModel itemsModel, int position);
//    }
//
//    public static class ItemTouchListener implements RecyclerView.OnItemTouchListener {
//
//        private ClickListener clicklistener;
//        private GestureDetector gestureDetector;
//
//        public ItemTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {
//
//            this.clicklistener = clicklistener;
//            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    return true;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && clicklistener != null) {
//                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
//                    }
//                }
//            });
//        }
//
//        @Override
//        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//            View child = rv.findChildViewUnder(e.getX(), e.getY());
//            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
//                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
//            }
//
//            return false;
//        }
//
//        @Override
//        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//        }
//
//        @Override
//        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        }
//
//    }

    public interface OnItemClick {
        void onAdd(ItemsModel itemsModel, int position);

        void onRemove(ItemsModel itemsModel, int position);

        void onAddItem(ItemsModel itemsModel, int position);

        void onDeleteItem(ItemsModel itemsModel, int position);


    }
}
