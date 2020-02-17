package vlcdemo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.player.base.Marquee;
import uk.co.caprica.vlcj.player.base.MarqueePosition;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 22:38
 */

/**
 * http://capricasoftware.co.uk/projects/vlcj-4/tutorials
 * http://caprica.github.io/vlcj/javadoc/4.1.0/index.html
 */
public class MyMediaPlayer {
    private static JFrame frame;

    //private final EmbeddedMediaPlayerComponent mediaPlayerComponent; //macos下用不了，见官方文档
    private static CallbackMediaPlayerComponent mediaPlayerComponent;

    private static long totalTime = 0;
    private static long curTime = 0;

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

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
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

        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
        JPanel controlsPane = new JPanel();
        JButton pauseButton = new JButton("Pause");
        controlsPane.add(pauseButton);
        JButton stopButton = new JButton("Stop");
        controlsPane.add(stopButton);

        final JSlider slider = new JSlider();
        slider.setMinimum(0);
        slider.setMaximum(100);
        slider.setMinimumSize(new Dimension(400, 20));
        controlsPane.add(slider);

        final JLabel totalTimeLabel = new JLabel("--");
        final JLabel splitLabel = new JLabel("/");
        final JLabel curTimeLabel = new JLabel("--");
        controlsPane.add(curTimeLabel);
        controlsPane.add(splitLabel);
        controlsPane.add(totalTimeLabel);
        contentPane.add(controlsPane, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.setVisible(true);

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                });
            }
        });

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                //System.out.println(source.getValue() + String.valueOf(!source.getValueIsAdjusting()));
                if (Math.abs(source.getValue() - curTime/1000) > 5) {
                    mediaPlayerComponent.mediaPlayer().controls().pause();
                    //System.out.println(source.getValue() + " & " + curTime/1000);
                    long newTime = source.getValue() * 1000;
                    //System.out.println(curTime + "->" + newTime);
                    mediaPlayerComponent.mediaPlayer().controls().setTime(newTime);
                    mediaPlayerComponent.mediaPlayer().controls().play();
                }
            }
        });

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

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
                curTime = newTime;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        curTimeLabel.setText(MyMediaPlayer.parseTimestamp(newTime));
                        slider.setValue((int)(newTime / 1000));
                    }
                });
                super.timeChanged(mediaPlayer, newTime);
            }
        });

        mediaPlayerComponent.mediaPlayer().events().addMediaEventListener(new MediaEventAdapter() {
            @Override
            public void mediaDurationChanged(Media media, final long newDuration) {
                totalTime = newDuration;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        totalTimeLabel.setText(MyMediaPlayer.parseTimestamp(newDuration));
                        slider.setMaximum((int)(newDuration / 1000));
                    }
                });
                super.mediaDurationChanged(media, newDuration);
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
        //mediaPlayerComponent.mediaPlayer().marquee().set(marquee);
        mediaPlayerComponent.mediaPlayer().media().play(url);
    }

    private static String parseTimestamp(long ms) {
        long sec = BigDecimal.valueOf(ms).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_EVEN).longValue();
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;

        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static void main(String[] args) throws InterruptedException {
        MyMediaPlayer.startVideo("/Users/jimsshom/Desktop/test.avi");
        Thread.currentThread().join();
    }
}
