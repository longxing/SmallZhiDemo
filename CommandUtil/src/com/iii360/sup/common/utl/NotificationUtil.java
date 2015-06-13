package com.iii360.sup.common.utl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

public class NotificationUtil {
	private NotificationManager notificationManager ;
	private Context mContext ;
	public NotificationUtil(Context context) {
		 mContext = context ;
		 notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
	}
	/**
	 * 
	 * @param drawableId
	 * @param flag
	 */
	public void sendNotification(int drawableId,int flag,String title,String eventInfo,PendingIntent pendintIntent,int id) {
		Notification notification = new Notification(drawableId,title,System.currentTimeMillis());
		notification.setLatestEventInfo(mContext, title	, eventInfo, pendintIntent) ;
		notificationManager.notify(id, notification);
	}
}
