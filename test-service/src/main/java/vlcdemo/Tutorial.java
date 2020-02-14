package vlcdemo;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.*;

import uk.co.caprica.vlcj.player.base.Marquee;
import uk.co.caprica.vlcj.player.base.MarqueePosition;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 22:38
 */
public class Tutorial {
    private static JFrame frame;

    //private final EmbeddedMediaPlayerComponent mediaPlayerComponent; //macos下用不了，见官方文档
    private static CallbackMediaPlayerComponent mediaPlayerComponent;

    public static void startVideo(String url) {
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });

        mediaPlayerComponent = new CallbackMediaPlayerComponent() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PLAYING)) {
                            mediaPlayerComponent.mediaPlayer().controls().pause();
                        } else if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PAUSED)) {
                            mediaPlayerComponent.mediaPlayer().controls().play();
                        }
                    }
                });
            }
        };
        frame.setContentPane(mediaPlayerComponent);

        frame.setVisible(true);

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                });
                //super.finished(mediaPlayer);
            }
        });
        Marquee marquee = Marquee.marquee()
            .text("vlcj tutorial")
            .size(40)
            .colour(Color.WHITE)
            .timeout(10000)
            .position(MarqueePosition.BOTTOM_RIGHT)
            .opacity(0.8f)
            .enable();

        mediaPlayerComponent.mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(frame));
        //mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
        mediaPlayerComponent.mediaPlayer().marquee().set(marquee);
        //mediaPlayerComponent.mediaPlayer().media().play("/Users/jimsshom/Desktop/test.mkv");
        mediaPlayerComponent.mediaPlayer().media().play(url);
    }

    public static void main(String[] args) {
        Tutorial thisApp = new Tutorial();
    }

    public Tutorial() {
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });

        mediaPlayerComponent = new CallbackMediaPlayerComponent() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PLAYING)) {
                            mediaPlayerComponent.mediaPlayer().controls().pause();
                        } else if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PAUSED)) {
                            mediaPlayerComponent.mediaPlayer().controls().play();
                        }
                    }
                });
            }
        };
        frame.setContentPane(mediaPlayerComponent);

        frame.setVisible(true);

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                });
                //super.finished(mediaPlayer);
            }
        });
        Marquee marquee = Marquee.marquee()
            .text("vlcj tutorial")
            .size(40)
            .colour(Color.WHITE)
            .timeout(10000)
            .position(MarqueePosition.BOTTOM_RIGHT)
            .opacity(0.8f)
            .enable();

        mediaPlayerComponent.mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(frame));
        //mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
        mediaPlayerComponent.mediaPlayer().marquee().set(marquee);
        //mediaPlayerComponent.mediaPlayer().media().play("/Users/jimsshom/Desktop/test.mkv");
        String url = "http://upos-sz-mirrorkodo.bilivideo.com/upgcxcode/38/22/151222238/151222238-1-208.mp4?e=ig8euxZM2rNcNbKV7WdVhwdl7wdBhwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_&ua=tvproj&uipk=5&nbs=1&deadline=1581691547&gen=playurl&os=kodobv&oi=2105142053&trid=b63d7740fbd746bd89e5ada3e0745b92T&upsig=b26c338e55825b68bce7267300820f44&uparams=e,ua,uipk,nbs,deadline,gen,os,oi,trid&mid=27584";
        mediaPlayerComponent.mediaPlayer().media().play(url);
    }
}
