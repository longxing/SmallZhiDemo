package com.iii360.box.music;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.R;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.util.AdaptUtil;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.BottomMenu;

public class MusicListItemAdapter extends BaseAdapter {
	private List<WifiMusicInfo> musics;
	private LayoutInflater inflater;

	private OnClickListener itemClickListener;
	private OnClickListener searchListener;
	private OnClickListener showBottomListener;
	private Drawable heartDrawable;
	private Drawable defaultDrawable;
	private int dataType;
	private WifiCRUDForMusic mWifiCRUDForMusic;
	private BasePreferences mPreferences;
	public static final int DATA_PLAY_LIST = 1;
	public static final int DATA_LOCAL_MUSIC = 2;
	public static final int FRAGMENT_PLAY_LIST = 3;
	public static final int FRAGMENT_GOODMUSIC_LIST = 4;
	private Activity activity;

	public List<WifiMusicInfo> getMusics() {
		if (musics == null)
			return new ArrayList<WifiMusicInfo>();
		return musics;
	}

	public MusicListItemAdapter(Activity activity, ArrayList<WifiMusicInfo> musics, int dataType) {
		this.dataType = dataType;
		setMusics(musics);
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		heartDrawable = activity.getResources().getDrawable(R.drawable.ico_list_heart);
		heartDrawable.setBounds(0, 0, heartDrawable.getMinimumWidth(), heartDrawable.getMinimumHeight());
		this.mWifiCRUDForMusic = new WifiCRUDForMusic(BoxManagerUtils.getBoxIP(activity), BoxManagerUtils.getBoxTcpPort(activity));
		defaultDrawable = new ColorDrawable(Color.TRANSPARENT);
		this.mPreferences = new BasePreferences(activity);
	}

	public void setMusics(List<WifiMusicInfo> musics) {
		if (musics == null)
			this.musics = new ArrayList<WifiMusicInfo>();
		else
			this.musics = musics;
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public WifiMusicInfo getItem(int position) {
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			vh = new ViewHolder();
			v = inflater.inflate(R.layout.ximalaya_audio_item, null);
			vh.titleTv = (TextView) v.findViewById(R.id.ximalaya_audio_title_tv);
			vh.nameTv = (TextView) v.findViewById(R.id.ximalaya_audio_name_tv);
			vh.mainLayout = (RelativeLayout) v.findViewById(R.id.music_play_layout);
			vh.searchLayout = (RelativeLayout) v.findViewById(R.id.music_play_search);
			vh.belowView = v.findViewById(R.id.music_play_search_below_line);
			vh.playStateIv = (ImageView) v.findViewById(R.id.music_play_status_iv);
			vh.rightBtn = (ImageView) v.findViewById(R.id.music_play_right_btn);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		if (dataType == DATA_PLAY_LIST) {
			if (position == 0) {
				vh.searchLayout.setVisibility(View.VISIBLE);
				vh.belowView.setVisibility(View.VISIBLE);
			} else {
				vh.searchLayout.setVisibility(View.GONE);
				vh.belowView.setVisibility(View.GONE);
			}
		}
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) vh.rightBtn.getLayoutParams();
		if (dataType == FRAGMENT_PLAY_LIST) {
			vh.rightBtn.setVisibility(View.INVISIBLE);
			params.leftMargin = 0;
			params.rightMargin = 0;
			vh.rightBtn.setPadding(0, 0, 0, 0);
			vh.rightBtn.setLayoutParams(params);
		}
		if (dataType == DATA_LOCAL_MUSIC || dataType == FRAGMENT_GOODMUSIC_LIST) {
			vh.rightBtn.setVisibility(View.VISIBLE);
		}
		if (showBottomListener == null) {
			showBottomListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					ViewHolder innerVh = (ViewHolder) v.getTag();
					int position = innerVh.position;
					if (dataType == DATA_LOCAL_MUSIC) {
						showBottomMenuForRemind(position);
					} else if (dataType == FRAGMENT_GOODMUSIC_LIST) {
						showBottomMenuForDeleteGoodMusic(position);
					}

				}
			};
		}
		vh.rightBtn.setTag(vh);
		vh.rightBtn.setOnClickListener(showBottomListener);
		vh.position = position;
		vh.mainLayout.setTag(vh);
		if (itemClickListener == null || searchListener == null) {
			itemClickListener = new OnClickListener() {
				public void onClick(View v) {
					ViewHolder innerVh = (ViewHolder) v.getTag();
					int position = innerVh.position;
					play(position);
					// if (dataType == FRAGMENT_PLAY_LIST) {
					// play(position);
					// } else {
					// showBottomMenu(position);
					// }
					// ToastUtils.show(context, "" + position);

				}
			};
			searchListener = new OnClickListener() {
				public void onClick(View v) {
					// ToastUtils.show(context, "searchListener");
					Intent intent = new Intent(activity, MusicSearchActivity.class);
					activity.startActivity(intent);
				}
			};
		}

		vh.searchLayout.setOnClickListener(searchListener);
		vh.mainLayout.setOnClickListener(itemClickListener);
		vh.nameTv.setText(("" + getItem(position).getAuthor()).trim());
		vh.titleTv.setText(("" + getItem(position).getName()).trim());
		if (getItem(position).is_isCollected()) {
			vh.nameTv.setCompoundDrawables(heartDrawable, null, null, null);
		} else {
			vh.nameTv.setCompoundDrawables(defaultDrawable, null, null, null);
		}
		if (dataType == FRAGMENT_PLAY_LIST || dataType == FRAGMENT_GOODMUSIC_LIST)
			vh.nameTv.setCompoundDrawables(defaultDrawable, null, null, null);
		if (mPreferences.getPrefString(KeyList.PKEY_SELECT_MUSIC_ID, "-1").equals(getItem(position).getMusicId())) {
			vh.mainLayout.setBackgroundColor(activity.getResources().getColor(R.color.item_blue_color));
			vh.playStateIv.setVisibility(View.VISIBLE);
		} else {
			vh.mainLayout.setBackgroundResource(R.drawable.main_set_item_selector);
			vh.playStateIv.setVisibility(View.GONE);
		}
		return v;
	}

	protected void showBottomMenuForDeleteGoodMusic(final int position) {
		BottomMenu menu = new BottomMenu(activity);
		menu.dismissAddToPlayListBtn();
		menu.dismissPlayBtn();
		menu.dismissSetRemindRingBtn();
		menu.setDeleteListener(new OnClickListener() {
			public void onClick(View v) {
				if (!AdaptUtil.isNewProtocol252()) {
					ToastUtils.show(activity, R.string.old_box_tip);
					return;
				}
				showDialog();
				final WifiMusicInfo info = musics.get(position);
				mWifiCRUDForMusic.delete(info.getMusicId() + "", new ResultForMusicListener() {
					public void onResult(String errorCode, List<WifiMusicInfo> infos) {
						dismissDialog(false);
						LogUtil.e("取消收藏：" + info.getMusicId() + "," + info.getName() + ",result=" + errorCode);
						if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
							ToastUtils.show(activity, activity.getString(R.string.ba_operation_error_toast));
						} else {
							handler.post(new Runnable() {
								public void run() {
									setNeedUpdate(true);
									musics.remove(position);
									notifyDataSetChanged();
								}
							});
						}
					}
				});
			}
		});
		menu.show();
	}

	private Handler handler = new Handler();

	protected void showBottomMenuForRemind(final int position) {

		BottomMenu menu = new BottomMenu(activity);
		menu.dismissAddToPlayListBtn();
		menu.dismissDeleteBtn();
		menu.dismissPlayBtn();
		menu.setSetRemindRingListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!AdaptUtil.isNewProtocol260()) {
					ToastUtils.show(activity, R.string.old_box_tip);
					return;
				}
				showDialog();
				final WifiMusicInfo info = musics.get(position);
				mWifiCRUDForMusic.setLocalMusicForRemind(new ResultForMusicListener() {

					@Override
					public void onResult(String errorCode, List<WifiMusicInfo> infos) {
						dismissDialog(false);
						LogUtil.e("设置备忘铃声" + info.getMusicId() + "," + info.getName() + ",result=" + errorCode);
						if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
							ToastUtils.show(activity, activity.getString(R.string.ba_operation_error_toast));
						}

					}
				}, info.getMusicId() + "");
				// ToastUtils.show(activity,
				// "设置为备忘铃声"+info.getName()+","+info.getMusicId()+","+info.getId());
			}
		});
		// menu.setDeleteListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// delete(position);
		// }
		// });
		// menu.setPlayListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// play(position);
		//
		// }
		// });
		menu.show();

	}

	public void showDialog() {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("showDialog", new Class[] {});
				method.invoke(activity, new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void dismissDialog(boolean isSetNull) {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("dismissDialog", new Class[] { boolean.class });
				method.invoke(activity, new Object[] { isSetNull });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setNeedUpdate(boolean needUpdate) {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("setNeedUpdate", new Class[] { boolean.class });
				method.invoke(activity, new Object[] { needUpdate });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void play(int position) {
		final WifiMusicInfo info = musics.get(position);
		if (dataType == FRAGMENT_GOODMUSIC_LIST) {
			if (!AdaptUtil.isNewProtocol260()) {
				ToastUtils.show(activity, R.string.old_box_tip);
				return;
			}
		}
		showDialog();
		// ToastUtils.show(context, info.getName() + "-------" +
		// info.getAuthor());
		if (dataType == DATA_LOCAL_MUSIC) {
			LogUtil.d("预置歌曲---getMusicId=" + info.getMusicId() + "getAuthor=" + info.getAuthor() + "getName=" + info.getName());
			mWifiCRUDForMusic.playLocalMusicByIdOrPosition(info.getMusicId() + "", String.valueOf(position), new ResultForMusicListener() {
				@Override
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {

					if (WifiCRUDUtil.isSuccessAll(errorCode)) {
						mPreferences.setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, info.getMusicId() + "");
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								notifyDataSetChanged();
								dismissDialog(false);
							}
						});
					} else {
						ToastUtils.show(activity, R.string.ba_operation_error_toast);
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dismissDialog(false);
							}
						});
					}
				}
			});
		} else if (dataType == FRAGMENT_GOODMUSIC_LIST) {
			LogUtil.d("红心列表---getMusicId=" + info.getMusicId() + "getAuthor=" + info.getAuthor() + "getName=" + info.getName() + "position=" + position);
			mWifiCRUDForMusic.play(position, new ResultForMusicListener() {
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {
					dismissDialog(false);
					if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
						ToastUtils.show(activity, R.string.ba_operation_error_toast);
					} else {
						setNeedUpdate(true);
						mPreferences.setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, info.getMusicId() + "");
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								notifyDataSetChanged();
							}
						});
					}
				}
			});
		} else {
			LogUtil.d("正在播放---getMusicId=" + info.getMusicId() + "getAuthor=" + info.getAuthor() + "getName=" + info.getName());
			mWifiCRUDForMusic.play(info.getMusicId() + "", new ResultForMusicListener() {
				@Override
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {

					if (WifiCRUDUtil.isSuccessAll(errorCode)) {
						mPreferences.setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, info.getMusicId() + "");
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								notifyDataSetChanged();
								dismissDialog(false);
							}
						});
					} else {
						ToastUtils.show(activity, R.string.ba_operation_error_toast);
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dismissDialog(false);
							}
						});
					}
				}
			});
		}

	}

	protected void delete(int position) {
		musics.remove(position);
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView titleTv;
		TextView nameTv;
		int position;
		RelativeLayout mainLayout;
		ImageView playStateIv;
		RelativeLayout searchLayout;
		View belowView;
		ImageView rightBtn;
	}
}
