package com.iii360.box.ximalaya;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.view.XListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AlbumAdapter extends BaseAdapter {
	private int ItemImageViewWidth;
	private Context context;
	private LayoutInflater inflater;
	// private ImageLoader imageLoader;
	private ArrayList<Album> albums;
	// private FinalBitmap fb;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	protected DisplayImageOptions options;

	public AlbumAdapter(final Context context, ArrayList<Album> albums, final XListView listview) {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_loading).showImageForEmptyUri(R.drawable.image_loading)
				.showImageOnFail(R.drawable.image_loading).cacheInMemory(true).cacheOnDisc(true).build();
		// fb = FinalBitmap.create(context);
		// fb.configLoadingImage(R.drawable.image_loading);
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		setData(albums);
		// imageLoader = new ImageLoader(context, new ImageLoader.CallBack() {
		// public void imageloaded(String path, Bitmap bm) {
		// ImageView iv = (ImageView) listview.findViewWithTag(path);
		// if (iv != null && bm != null)
		// iv.setImageBitmap(bm);
		// }
		// },4);
		getImageViewWidth();
	}

	public void setData(ArrayList<Album> albums) {
		if (albums == null) {
			this.albums = new ArrayList<Album>();
		} else {
			this.albums = albums;
		}
	}

	private void getImageViewWidth() {
		int margin = (int) (BoxManagerUtils.getScreenDensity(context) * 10 * 2);
		int height = (int) context.getResources().getDimension(R.dimen.ximalaya_album_item_height);
		ItemImageViewWidth = height - margin;
	}

	@Override
	public int getCount() {
		return albums.size();
	}

	@Override
	public Object getItem(int position) {
		return albums.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			v = inflater.inflate(R.layout.ximalaya_album_item, null);
			vh = new ViewHolder();
			v.setTag(vh);
			vh.iv = (ImageView) v.findViewById(R.id.ximalaya_album_imageView);
			vh.tv = (TextView) v.findViewById(R.id.ximalaya_album_textView);
			LayoutParams params = vh.iv.getLayoutParams();
			params.width = ItemImageViewWidth;
			params.height = ItemImageViewWidth;
			vh.iv.setLayoutParams(params);
			vh.iv.setScaleType(ScaleType.FIT_CENTER);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		vh.iv.setTag(albums.get(position).getImageUrl());
		// Bitmap bm =
		// imageLoader.getBitmap(albums.get(position).getImageUrl(),ItemImageViewWidth,ItemImageViewWidth);
		//
		// if (bm != null) {
		// vh.iv.setScaleType(ScaleType.FIT_XY);
		// vh.iv.setImageBitmap(bm);
		// } else {
		// vh.iv.setScaleType(ScaleType.FIT_CENTER);
		// // vh.iv.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
		// vh.iv.setImageResource(R.drawable.image_loading);
		// }
		// fb.display(vh.iv, albums.get(position).getImageUrl(),
		// ItemImageViewWidth, ItemImageViewWidth);
		imageLoader.displayImage(albums.get(position).getImageUrl(), vh.iv, options);
		vh.tv.setText("" + albums.get(position).getTitle());
		return v;
	}

	private class ViewHolder {
		ImageView iv;
		TextView tv;
	}

	public void close() {
		try {
			// imageLoader.close();
			imageLoader.clearMemoryCache();
			// fb.clearMemoryCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
