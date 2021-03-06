package vlcdemo;

import avtransportserver.MyUpnpServiceManager;

/**
 * @author jimsshom
 * @date 2020/02/13
 * @time 22:38
 */

/**
 * http://capricasoftware.co.uk/projects/vlcj-4/tutorials
 * http://caprica.github.io/vlcj/javadoc/4.1.0/index.html
 */

/**
 * FIXME:
 * 1. 播放暂停时最大化，界面没有自适应
 */
public class MyMediaPlayer {
    private static MediaPlayerManager mediaPlayerManager;
    private static PlayerFrame playerFrame;
    private static MyMediaPlayer myMediaPlayer = new MyMediaPlayer();
    private static MyUpnpServiceManager upnpServiceManager = new MyUpnpServiceManager();

    private MyMediaPlayer() {
        mediaPlayerManager = new MediaPlayerManager();
        playerFrame = new PlayerFrame(mediaPlayerManager);
        mediaPlayerManager.initialEventListener(playerFrame);
    }

    public static void startVideo(String url) {
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.stop();
        }
        mediaPlayerManager.startPlayByUrl(url);
        System.out.println("afterPlay: " + url);
    }

    public static void stopVideo() {
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.stop();
        }
        System.out.println("afterStop");
    }

    public static void seekByTime(long second) {
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.seekByTime(second * 1000);
        }
        System.out.println("afterSeek: " + second);
    }

    public static void main(String[] args) throws InterruptedException {
        myMediaPlayer.startVideo("/Users/jimsshom/Desktop/test.mp4");
        mediaPlayerManager.registerEventListener(upnpServiceManager.getMediaPlayerEventListener());

        Thread serverThread = new Thread(upnpServiceManager);
        serverThread.setDaemon(false);
        serverThread.start();

        Thread.currentThread().join();
    }
}
