package com.base.resource;

import java.io.InputStream;

public interface IResourceManager {
    public InputStream getInputStream(Object context, String path);

    public String[] list(Object context, String string);
    public boolean checkStorageVaild();
}
