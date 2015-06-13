package com.iii360.box.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.iii360.box.R;
import com.iii360.box.base.BaseDialog;

/**
 * 鍒楄〃鐨凞ialog
 * 
 * @author hefeng
 * 
 */
public class ListDialog extends BaseDialog {
    private ListView mListView;
    private ListAdapter mAdapter;

    private OnListItemClickListener itemClickListener;

    public OnListItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ListDialog(Context context) {
        super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
    }

    public ListDialog(Context context, String title) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list_dialog);

        this.mListView = (ListView) findViewById(R.id.list_dialog_lv);
        this.mListView.setSelector(R.drawable.main_set_item_selector);
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                dismiss();
                if (itemClickListener != null) {
                    itemClickListener.onListItemClick(position);
                }
            }
        });
    }

    public void setAdpter(ListAdapter adapter, OnListItemClickListener listener) {
        this.mAdapter = adapter;
        this.itemClickListener = listener;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.dismiss();
    }

    public interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        this.mListView.setAdapter(mAdapter);
    }
}
