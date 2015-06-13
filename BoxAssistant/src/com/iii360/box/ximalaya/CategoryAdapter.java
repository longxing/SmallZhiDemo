package com.iii360.box.ximalaya;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.util.BoxManagerUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CategoryAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String> tagNames;
	private ArrayList<String> imageUrls;
	private int imageViewWidth;
	private int imageHeight = 140;
	private int imageWidth = 188;
//	private ImageLoader imageLoader;
//	private FinalBitmap finalBitmap;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	protected DisplayImageOptions options;
	public CategoryAdapter(Context context, ArrayList<String> tagNames, ArrayList<String> imageUrls, final GridView gridView) {
//		finalBitmap = FinalBitmap.create(context);
//		finalBitmap.configLoadingImage(R.drawable.image_loading);
		options =new DisplayImageOptions.Builder().showStubImage(R.drawable.image_loading)
				.showImageForEmptyUri(R.drawable.image_loading).showImageOnFail(R.drawable.image_loading)
				.cacheInMemory(true).cacheOnDisc(true).build();
		this.context = context;
		setData(tagNames, imageUrls);
		inflater = LayoutInflater.from(this.context);
		getItemImageViewWidth();
//		imageLoader = new ImageLoader(context, new CallBack() {
//			public void imageloaded(String path, Bitmap bm) {
//				ImageView iv = (ImageView) gridView.findViewWithTag(path);
//				if (iv != null&&bm!=null)
//					iv.setImageBitmap(bm);
//			}
//		},2);
	}

	private void getItemImageViewWidth() {
		int horizontalSpacing = (int) context.getResources().getDimension(R.dimen.ximalaya_gridview_horizontal_spacing);
		int gridViewMargin = (int) context.getResources().getDimension(R.dimen.ximalaya_gridview_margin);
		int gridViewItemMargin = (int) context.getResources().getDimension(R.dimen.ximalaya_catetory_item_margin);
		imageViewWidth = (BoxManagerUtils.getScreenWidthPx(context) - gridViewMargin * 2 - 2 * horizontalSpacing - gridViewItemMargin
				* XimalayaCategoryActivity.COLUMN_NUM * 2) / 3;
	}

	public void setData(ArrayList<String> tagNames, ArrayList<String> imageUrls) {
		if (tagNames == null || imageUrls == null) {
			this.tagNames = new ArrayList<String>();
			this.imageUrls = new ArrayList<String>();
		} else {
			this.tagNames = tagNames;
			this.imageUrls = imageUrls;
		}

	}

	@Override
	public int getCount() {
		return tagNames.size();
	}

	@Override
	public Object getItem(int position) {
		return tagNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			vh = new ViewHolder();
			v = inflater.inflate(R.layout.ximalaya_catetory_item, null);
			vh.iv = (ImageView) v.findViewById(R.id.iv);
			LayoutParams params = vh.iv.getLayoutParams();
			params.width = imageViewWidth;
			params.height = imageViewWidth * imageHeight / imageWidth;
			vh.iv.setLayoutParams(params);
			vh.tv = (TextView) v.findViewById(R.id.tv);
			vh.iv.setScaleType(ScaleType.FIT_CENTER);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		
		vh.iv.setTag(imageUrls.get(position));
//		Bitmap bm = imageLoader.getBitmap(imageUrls.get(position),	imageViewWidth,	imageViewWidth * imageHeight / imageWidth);
		
		// vh.iv.setImageResource(R.drawable.ximalaya_category_image);
//		if (bm != null) {
//		
//			vh.iv.setImageBitmap(bm);
//		} else {
//			vh.iv.setScaleType(ScaleType.FIT_CENTER);
//			//透明
////			vh.iv.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//			vh.iv.setImageResource(R.drawable.image_loading);
//		}
//		finalBitmap.display(vh.iv, imageUrls.get(position), imageViewWidth, imageViewWidth * imageHeight / imageWidth);
		imageLoader.displayImage(imageUrls.get(position),vh.iv,options);
		vh.tv.setText("" + tagNames.get(position));
		return v;
	}

	public void close() {
		try {
//			imageLoader.close();
			imageLoader.clearMemoryCache();
//			finalBitmap.clearMemoryCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ViewHolder {
		ImageView iv;
		TextView tv;
	}
}
