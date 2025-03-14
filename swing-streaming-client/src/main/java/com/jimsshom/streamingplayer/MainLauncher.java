package com.jimsshom.streamingplayer;

import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;
import com.jimsshom.streamingplayer.mediaplayer.MediaPlayManager;
import com.jimsshom.streamingplayer.ui.UIManager;
import com.jimsshom.streamingplayer.upnpservice.UpnpServiceManager;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:04
 */

/**
 * TODO:
 * 1. 喇叭图片旁边空太多
 */
public class MainLauncher {
    private static MediaPlayManager mediaPlayManager;
    private static UIManager uiManager;
    private static UpnpServiceManager upnpServiceManager;
    private static EventBusManager eventBusManager;

    public static void main(String[] args) {
        String osArch = System.getProperty("os.arch");
        System.out.println("OS Architecture: " + osArch);

        mediaPlayManager = new MediaPlayManager();
        uiManager = new UIManager(mediaPlayManager);
        upnpServiceManager =  new UpnpServiceManager();

        upnpServiceManager.initial();
        uiManager.initial();
        mediaPlayManager.initial();

        //EventBusManager.fireEvent(EventType.START_NEW_URL, "/Users/jimsshom/Desktop/test.mp4");
    }
}
