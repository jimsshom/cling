import eventbus.EventBusManager;
import eventbus.EventType;
import mediaplayer.MediaPlayManager;
import ui.UIManager;
import upnp.UpnpServiceManager;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:04
 */

/**
 * FIXME:
 * 2. UI上音量键长度调小
 * 3. UI上进度label显示偏上
 */
public class Launcher {
    private static MediaPlayManager mediaPlayManager;
    private static UIManager uiManager;
    private static UpnpServiceManager upnpServiceManager;
    private static EventBusManager eventBusManager;

    public static void main(String[] args) {
        mediaPlayManager = new MediaPlayManager();
        uiManager = new UIManager(mediaPlayManager);
        //upnpServiceManager =  new UpnpServiceManager();

        uiManager.initial();
        mediaPlayManager.initial();
        //upnpServiceManager.initial();
        EventBusManager.fireEvent(EventType.START_NEW_URL, "/Users/jimsshom/Desktop/test.mp4");
    }
}
