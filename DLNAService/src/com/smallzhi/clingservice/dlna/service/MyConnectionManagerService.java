package com.smallzhi.clingservice.dlna.service;

import java.util.ArrayList;

import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.seamless.util.MimeType;

import com.iii360.sup.common.utl.LogManager;

public class MyConnectionManagerService extends ConnectionManagerService {
    
	protected final static ArrayList<MimeType> sSupportFormats = new ArrayList<MimeType>();
	
	private static final String TAG = "SmallZhiDLNA ConnectionManagerService";
	
    static {
        sSupportFormats.clear();

        /** MIME Type for audio formats */
        sSupportFormats.add(new MimeType("audio", "amr-wb"));
        sSupportFormats.add(new MimeType("audio", "mpeg"));
        sSupportFormats.add(new MimeType("audio", "amr"));
        sSupportFormats.add(new MimeType("audio", "aac"));
        sSupportFormats.add(new MimeType("audio", "midi"));
        sSupportFormats.add(new MimeType("audio", "flac"));
        sSupportFormats.add(new MimeType("audio", "mp4"));
        sSupportFormats.add(new MimeType("audio", "x-ms-wma"));
        sSupportFormats.add(new MimeType("audio", "x-wav"));

    }
    public MyConnectionManagerService() {
        for (MimeType mt : sSupportFormats) {
            try {
               
                sinkProtocolInfo.add(new ProtocolInfo(mt));
            } catch (IllegalArgumentException ex) {
                LogManager.e(TAG, "Ignoring invalid MIME type: " + mt);
            }
        }
        //LogManager.d(TAG，	"Supported MIME types: " + sinkProtocolInfo.size());
    }
}
