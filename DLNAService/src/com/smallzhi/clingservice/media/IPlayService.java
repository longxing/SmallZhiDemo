package com.smallzhi.clingservice.media;

public interface IPlayService {
    public void IPlay();

    public void IPause();

    public void IStop();
    
    public void ISimpleStop();

    public void ISeek(int whereto);

    public void ISetVolume(int volume);

    public String IGetPlayerState();

    public int IGetDuration();

    public int IGetCurrentPosition();

    public void ISetUrl(String uri, String uriMetaData);
}
