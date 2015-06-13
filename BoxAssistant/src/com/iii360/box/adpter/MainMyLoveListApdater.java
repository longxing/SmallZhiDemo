package com.iii360.box.adpter;

import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.MainMyLoveActivity;
import com.iii360.box.R;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.entity.ViewHolder;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.GetDataProgressUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.BottomPopupMenu;

/**
 * 
 * @author hefeng
 * 
 */
public class MainMyLoveListApdater extends BaseAdapter {
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private Activity context;
    private List<WifiMusicInfo> mMusicList;
    private BottomPopupMenu mBottomPopupMenu;
    private BasePreferences mPreferences;

    private int mSelectItem;
    private boolean mIsPlay = false;
    private WifiCRUDForMusic mWifiCRUDForMusic;
    private Handler handler;

    public MainMyLoveListApdater(Activity context, List<WifiMusicInfo> mMusicList, Handler handler) {
        // TODO Auto-generated constructor stub
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mMusicList = mMusicList;
        this.handler =handler;
        this.mPreferences = new BasePreferences(context);
        this.mWifiCRUDForMusic = new WifiCRUDForMusic(BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        this.setMenu();
    }

    private void setMenu() {
        this.mBottomPopupMenu = new BottomPopupMenu(context);
        this.mBottomPopupMenu.setPlayListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                LogManager.i("play id : " + mSelectItem);
                final String id = mMusicList.get(mSelectItem).getMusicId();
                GetDataProgressUtil.showSettingProgress(context);

                mWifiCRUDForMusic.play(id, new ResultForMusicListener() {
                    @Override
                    public void onResult(String errorCode, List<WifiMusicInfo> infos) {
                        // TODO Auto-generated method stub
                        GetDataProgressUtil.dismissProgress(context);

                        if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                            mPreferences.setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, id);
                            mPreferences.setPrefLong(KeyList.PKEY_SELECT_MUSIC_TIME, System.currentTimeMillis());
                            
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    notifyDataSetChanged();
                                }
                            });
                            handler.sendEmptyMessageDelayed(MainMyLoveActivity.HANDLER_TIMEOUT, MainMyLoveActivity.MUSIC_PLAY_TIMEOUT);
                        } else {
                            ToastUtils.show(context, R.string.ba_operation_error_toast);
                        }
                    }
                });
            }
        });

        this.mBottomPopupMenu.setDeleteListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                LogManager.i("delete id : " + mSelectItem);
                GetDataProgressUtil.showSettingProgress(context);
                final String id = mMusicList.get(mSelectItem).getMusicId();
                
                mWifiCRUDForMusic.delete(id, new ResultForMusicListener() {
                    @Override
                    public void onResult(String errorCode, List<WifiMusicInfo> infos) {
                        // TODO Auto-generated method stub
                        GetDataProgressUtil.dismissProgress(context);
                        if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    mMusicList.remove(mSelectItem);
                                    notifyDataSetChanged();
                                }
                            });
                        } else {
                            ToastUtils.show(context, R.string.ba_operation_error_toast);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.main_mylove_listview_item, null);

            mViewHolder.setTv1((TextView) (convertView.findViewById(R.id.music_name_tv)));
            mViewHolder.setTv2((TextView) (convertView.findViewById(R.id.music_author_tv)));
            mViewHolder.setIv1((ImageView) (convertView.findViewById(R.id.music_play_status_iv)));
            mViewHolder.setLayout1((LinearLayout) (convertView.findViewById(R.id.music_layout)));

            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.getTv1().setText(mMusicList.get(position).getName());
        mViewHolder.getTv2().setText(mMusicList.get(position).getAuthor());

        String id = mMusicList.get(position).getMusicId();

        if (mPreferences.getPrefString(KeyList.PKEY_SELECT_MUSIC_ID,"-1").equals(id)) {
            mViewHolder.getIv1().setVisibility(View.VISIBLE);
            mViewHolder.getLayout1().setBackgroundColor(context.getResources().getColor(R.color.item_blue_color));
        } else {
            mViewHolder.getIv1().setVisibility(View.GONE);
            mViewHolder.getLayout1().setBackgroundResource(R.drawable.ba_list_item_selector);
        }

        mViewHolder.getLayout1().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mSelectItem = position;

                LogManager.i("click item id : " + mSelectItem);
                mBottomPopupMenu.show();
            }
        });
        return convertView;
    }
}
