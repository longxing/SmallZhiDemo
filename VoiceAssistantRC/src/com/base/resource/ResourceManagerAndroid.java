package com.base.resource;

import java.io.IOException;
import java.io.InputStream;

import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;


public class ResourceManagerAndroid implements IResourceManager {

    @Override
    public InputStream getInputStream(Object context, String path) {
        Context c = (Context) context;
        AssetManager assetManager = c.getAssets();
        InputStream input = null;
        try {
            input = assetManager.open(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
        }
        
        return input;
    }
    
    public String [] list(Object context, String path) {
        Context c = (Context) context;
        AssetManager assetManager = c.getAssets();
        String [] lists = null;
        try {
            lists = assetManager.list(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lists;
    }

    @Override
    public boolean checkStorageVaild() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
