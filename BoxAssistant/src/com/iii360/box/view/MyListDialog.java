package com.iii360.box.view;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.iii360.box.R;
import com.iii360.box.adpter.WifiConfigListApdater;
import com.iii360.box.base.BaseDialog;
import com.iii360.box.entity.WifiInfoMessage;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WifiInfoUtils;

/**
 * 列表的Dialog
 * 
 * @author hefeng
 * 
 */
public class MyListDialog extends BaseDialog {
    private WifiConfigListApdater mAdapter;
    private List<WifiInfoMessage> mList;
    private ListView mDialogLv;

    private OnListItemClickListener itemClickListener;

    public OnListItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MyListDialog(Context context) {
        super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
    }

    public MyListDialog(Context context, String title) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list_dialog);

        this.mDialogLv = (ListView) findViewById(R.id.list_dialog_lv);

        this.mList = WifiInfoUtils.getWifiList(context);
        LogManager.i("size=" + mList.size());
        this.mAdapter = new WifiConfigListApdater(context, mList);
      //  setListViewHeightBasedOnChildren(this.mDialogLv) ;
        this.mDialogLv.setAdapter(mAdapter);
        this.mDialogLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                dismiss();
                if (itemClickListener != null) {
                    itemClickListener.onListItemClick(position, mList.get(position));
                }
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        //获取listview的适配器
        ListAdapter listAdapter = listView.getAdapter();
        //item的高度
        int itemHeight = 50;

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            totalHeight += Dp2Px(context, itemHeight) + listView.getDividerHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;

        listView.setLayoutParams(params);
    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.dismiss();
    }

    public interface OnListItemClickListener {
        void onListItemClick(int position, WifiInfoMessage info);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        this.mList = WifiInfoUtils.getWifiList(context);
        this.mAdapter.notifyDataSetChanged();
    }
}
