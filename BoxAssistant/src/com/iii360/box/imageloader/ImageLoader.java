package com.iii360.box.imageloader;


public class ImageLoader {
//	private Map<String, SoftReference<Bitmap>> bitMaps;
//	private CallBack callBack;
//	private Handler mainHandler;
//	private Vector<LoadTask> tasks;
//	private Context context;
//	private PicCacheDao cacheDao;
//	private ExecutorService threadPool;
//
//	public Bitmap getBitmap(String path, int width, int height) {
//		Bitmap bm = null;
//		if (bitMaps.containsKey(path)) {
//			SoftReference<Bitmap> references = bitMaps.get(path);
//			bm = references.get();
//			if (bm == null) {
//				bitMaps.remove(path);
//			}
//		}
//		if (bm == null) {
//			try {
//				String base_path = context.getExternalCacheDir().getAbsolutePath() + "/pictrues";
//				File file = new File(base_path, HexUtils.str2HexStr(path));
//				if (file.exists() && file.isFile()) {
//					try {
//						bm = BitmapFactory.decodeFile(file.getAbsolutePath());
//						bitMaps.put(path, new SoftReference<Bitmap>(bm));
//					} catch (OutOfMemoryError error) {
//						error.printStackTrace();
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		if (bm == null) {
//			LoadTask loadTask = new LoadTask(path, width, height);
//			if (tasks.contains(loadTask)) {
//				return bm;
//			}
//			// Message msg = new Message();
//			// msg.obj = loadTask;
//			// try {
//			// workHandlers.get(i % threadSize).sendMessage(msg);
//			// i++;
//			tasks.add(loadTask);
//			// } catch (Exception e) {
//			// e.printStackTrace();
//			// i = 0;
//			// }
//			threadPool.execute(new DownLoadPic(loadTask));
//		}
//		return bm;
//
//	}
//
//	private class DownLoadPic implements Runnable {
//		private LoadTask task;
//
//		public DownLoadPic(LoadTask task) {
//			this.task = task;
//		}
//
//		@Override
//		public void run() {
//			try {
//				getBitmapFromHttp(task);
//			} catch (Exception e) {
//			}
//			mainHandler.post(new Runnable() {
//				public void run() {
//					tasks.remove(task);
//				}
//			});
//		}
//	}
//
//	public ImageLoader(final Context context, CallBack callBack,int threadSize) {
//		threadPool = Executors.newFixedThreadPool(threadSize);
//		cacheDao = new PicCacheDao(context);
//		this.context = context;
//		mainHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//			}
//		};
//		this.callBack = callBack;
//		bitMaps = new HashMap<String, SoftReference<Bitmap>>();
//		tasks = new Vector<ImageLoader.LoadTask>();
//	}
//
//	protected void getBitmapFromHttp(final LoadTask task) throws Exception {
//		cacheDao.deleteByUrl(task.path);
//		HttpClient client = new DefaultHttpClient();
//		HttpResponse res = client.execute(new HttpGet(task.path));
//		byte[] data = EntityUtils.toByteArray(res.getEntity());
//		Options opts = new Options();
//		opts.inJustDecodeBounds = true;
//		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//		opts.inSampleSize = computeSampleSize(opts, -1, task.width * task.height);
//		LogUtil.i("inSampleSize:" + opts.inSampleSize+","+task.width+","+task.height);
//		opts.inJustDecodeBounds = false;
//		final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//		mainHandler.post(new Runnable() {
//			public void run() {
//				bitMaps.put(task.path, new SoftReference<Bitmap>(bitmap));
//				callBack.imageloaded(task.path, bitmap);
//			}
//		});
//		saveLocal(task.path, bitmap);
//
//	}
//
//	public static int computeSampleSize(BitmapFactory.Options options,
//
//	int minSideLength, int maxNumOfPixels) {
//
//		int initialSize = computeInitialSampleSize(options, minSideLength,
//
//		maxNumOfPixels);
//
//		int roundedSize;
//
//		if (initialSize <= 8) {
//
//			roundedSize = 1;
//
//			while (roundedSize < initialSize) {
//
//				roundedSize <<= 1;
//
//			}
//
//		} else {
//
//			roundedSize = (initialSize + 7) / 8 * 8;
//
//		}
//
//		return roundedSize;
//
//	}
//
//	private static int computeInitialSampleSize(BitmapFactory.Options options,
//
//	int minSideLength, int maxNumOfPixels) {
//
//		double w = options.outWidth;
//
//		double h = options.outHeight;
//
//		int lowerBound = (maxNumOfPixels == -1) ? 1 :
//
//		(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
//
//		int upperBound = (minSideLength == -1) ? 128 :
//
//		(int) Math.min(Math.floor(w / minSideLength),
//
//		Math.floor(h / minSideLength));
//
//		if (upperBound < lowerBound) {
//
//			// return the larger one when there is no overlapping zone.
//
//			return lowerBound;
//
//		}
//
//		if ((maxNumOfPixels == -1) &&
//
//		(minSideLength == -1)) {
//
//			return 1;
//
//		} else if (minSideLength == -1) {
//
//			return lowerBound;
//
//		} else {
//
//			return upperBound;
//
//		}
//
//	}
//
//	/***
//	 * 保存到本地
//	 * 
//	 * @param path
//	 * @param bitmap
//	 */
//	private void saveLocal(String url, Bitmap bitmap) {
//		if (!Environment.MEDIA_MOUNTED.equals((Environment.getExternalStorageState()))) {
//			return;
//		}
//		File dir = context.getExternalCacheDir();
//		if (dir == null)
//			return;
//		if (!dir.exists())
//			if (!dir.mkdirs())
//				return;
//		String base_path = context.getExternalCacheDir().getAbsolutePath() + "/pictrues";
//		CacheBean bean = new CacheBean();
//		bean.setPicUrl(url);
//		try {
//			cacheDao.add(bean);
//		} catch (Exception e) {
//			return;
//		}
//		OutputStream out = null;
//		try {
//			String filePath = HexUtils.str2HexStr(url);
//			File file = new File(base_path, filePath);
//			bean.setLocalPath(filePath);
//			if (!file.getParentFile().exists()) {
//				if (!file.getParentFile().mkdirs()) {
//					return;
//				}
//			}
//			out = new FileOutputStream(file);
//			bitmap.compress(CompressFormat.JPEG, 100, out);
//			out.flush();
//			cacheDao.add(bean);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				out.close();
//			} catch (Exception e) {
//			}
//		}
//	}
//
//	public void close() {
//		try {
//			threadPool.shutdownNow();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Iterator<Entry<String, SoftReference<Bitmap>>> it = bitMaps.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry<String, SoftReference<Bitmap>> entry = it.next();
//			Bitmap bitmap = entry.getValue().get();
//			if (bitmap != null && !bitmap.isRecycled()) {
//				bitmap.recycle();
//				System.gc();
//			}
//		}
//	}
//
//	private class LoadTask {
//		String path;
//		int width;
//		int height;
//
//		public LoadTask(String path, int width, int height) {
//			this.path = path;
//			this.width = width;
//			this.height = height;
//		}
//
//		@Override
//		public boolean equals(Object o) {
//			if (o == null)
//				return false;
//			if (o instanceof LoadTask) {
//				LoadTask task = (LoadTask) o;
//				return path.equals(task.path);
//			}
//			return false;
//		}
//	}
//
//	public interface CallBack {
//		void imageloaded(String path, Bitmap bm);
//	}
//
}
