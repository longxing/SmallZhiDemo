package com.iii360.box.view;

import android.app.AlertDialog;
import android.content.Context;

public class Test {
    public static void create(Context context) {

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_2,new String[]{"111111111","111111111","111111111","111111111","111111111"}); 
////        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyDialog));
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
////        builder.setInverseBackgroundForced(true);
//        builder.setAdapter(adapter, new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                Log.i("hefeng", "which:" + which);
//            }
//        });
//        builder.create().show();
        new AlertDialog.Builder(context).setTitle("你可以说")
                .setItems(new String[] { "Item1", "Item2", "Item1", "Item2", "Item1", "Item2", "Item1", "Item2" }, null)
                .setNegativeButton("确定", null).show();
        
//        WifiControlInfo 序列化
    }
}