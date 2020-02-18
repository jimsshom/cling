package vlcdemo;

import java.awt.*;

import uk.co.caprica.vlcj.player.base.Marquee;
import uk.co.caprica.vlcj.player.base.MarqueePosition;

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

    public static void startVideo(String url) {
        mediaPlayerManager = new MediaPlayerManager();
        playerFrame = new PlayerFrame(mediaPlayerManager);
        mediaPlayerManager.initialEventListener(playerFrame);

        Marquee marquee = Marquee.marquee()
            .text("vlcj tutorial")
            .size(40)
            .colour(Color.WHITE)
            .timeout(10000)
            .position(MarqueePosition.BOTTOM_RIGHT)
            .opacity(0.8f)
            .enable();

        //mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
        //mediaPlayerComponent.mediaPlayer().marquee().set(marquee);
        mediaPlayerManager.startPlayByUrl(url);
    }


    public static void main(String[] args) throws InterruptedException {
        MyMediaPlayer.startVideo("/Users/jimsshom/Desktop/test.avi");
        Thread.currentThread().join();
    }
}
