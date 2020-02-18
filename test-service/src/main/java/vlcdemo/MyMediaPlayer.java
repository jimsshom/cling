package vlcdemo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;

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

/**
 * FIXME:
 * 4. 捕获不到键盘事件
 */
public class MyMediaPlayer {
    private static JFrame frame;

    //private final EmbeddedMediaPlayerComponent mediaPlayerComponent; //macos下用不了，见官方文档
    private static CallbackMediaPlayerComponent mediaPlayerComponent;

    private static long totalTime = 0;
    private static long curTime = 0;

    public static void startVideo(String url) {
        String path = "/Users/jimsshom/Works/GitRepo/cling/test-service/src/main/resources/";
        final Icon fullIcon = new ImageIcon(new ImageIcon(path + "FullScreen.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        final Icon pauseIcon = new ImageIcon(new ImageIcon(path + "pause.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        final Icon playIcon = new ImageIcon(new ImageIcon(path + "play.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        final Icon volumeIcon = new ImageIcon(new ImageIcon(path + "volume.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        final Icon stopIcon = new ImageIcon(new ImageIcon(path + "Stop.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

        final JPanel contentPane = new JPanel();
        final JPanel controlsPane = new JPanel();
        final JPanel volumePane = new JPanel(new BorderLayout());
        final JPanel progressPane = new JPanel(new BorderLayout());

        final JButton pauseButton = new JButton();
        final JButton stopButton = new JButton();
        final JLabel volumeLabel = new JLabel();
        final JSlider volumeBar = new JSlider();
        final JButton fullScreenButton = new JButton();
        final JSlider progressBar = new JSlider();

        final JLabel totalTimeLabel = new JLabel("--");
        totalTimeLabel.setSize(100, 29);
        final JLabel splitLabel = new JLabel("/");
        final JLabel curTimeLabel = new JLabel("--");
        curTimeLabel.setSize(100, 29);

        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 800, 600);
        frame.setMinimumSize(new Dimension(600, 450));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = frame.getWidth() - 438;
                //System.out.println(frame.getWidth() + "->" + newWidth);
                progressPane.setPreferredSize(new Dimension(newWidth, 29));
                super.componentResized(e);
            }
        });

        contentPane.setLayout(new BorderLayout());
        mediaPlayerComponent = new CallbackMediaPlayerComponent() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PLAYING)) {
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayerComponent.mediaPlayer().controls().pause();
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            pauseButton.setIcon(playIcon);
                        }
                    });
                } else {
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayerComponent.mediaPlayer().controls().play();
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            pauseButton.setIcon(pauseIcon);
                        }
                    });
                }
            }

            @Override
            public void keyPressed(KeyEvent e) { //FIXME: 不知为何收不到键盘消息
                System.out.println(e.getKeyCode());
                System.out.println(e.getKeyChar());
                super.keyPressed(e);
            }
        };

        mediaPlayerComponent.mediaPlayer().input().enableKeyInputHandling(false);
        mediaPlayerComponent.mediaPlayer().input().enableMouseInputHandling(false);

        mediaPlayerComponent.videoSurfaceComponent().requestFocus();

        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);

        pauseButton.setIcon(pauseIcon);
        controlsPane.add(pauseButton);

        stopButton.setIcon(stopIcon);
        controlsPane.add(stopButton);

        volumePane.setPreferredSize(new Dimension(100, 29));

        volumeLabel.setIcon(volumeIcon);
        controlsPane.add(volumeLabel);

        volumeBar.setMinimum(0);
        volumeBar.setMaximum(100);
        volumeBar.setValue(100);
        volumePane.add(volumeBar);
        controlsPane.add(volumePane);

        fullScreenButton.setIcon(fullIcon);
        controlsPane.add(fullScreenButton);

        progressPane.setPreferredSize(new Dimension(362, 29));
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressPane.add(progressBar);
        controlsPane.add(progressPane);

        controlsPane.add(curTimeLabel);
        controlsPane.add(splitLabel);
        controlsPane.add(totalTimeLabel);
        contentPane.add(controlsPane, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.setVisible(true);

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PLAYING)) {
                    mediaPlayerComponent.mediaPlayer().controls().pause();
                    pauseButton.setIcon(playIcon);
                } else if (mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PAUSED)) {
                    mediaPlayerComponent.mediaPlayer().controls().play();
                    pauseButton.setIcon(pauseIcon);
                }
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

        fullScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
            }
        });

        progressBar.addChangeListener(new ChangeListener() {
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
                    pauseButton.setIcon(pauseIcon);
                }
            }
        });

        volumeBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    mediaPlayerComponent.mediaPlayer().audio().setVolume(source.getValue());
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
                        progressBar.setValueIsAdjusting(true);
                        //System.out.println("newTime=" + newTime);
                        progressBar.setValue((int)(newTime / 1000));
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
                        progressBar.setMaximum((int)(newDuration / 1000));
                        //progressBar.setValueIsAdjusting(true);
                        //progressBar.setValue(0);
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

        mediaPlayerComponent.mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(frame) {
            @Override
            protected void onBeforeEnterFullScreen() {
                //controlsPane.setVisible(false);
                super.onBeforeEnterFullScreen();
            }

            @Override
            protected void onAfterExitFullScreen() {
                //controlsPane.setVisible(true);
                super.onAfterExitFullScreen();
            }
        });
        //mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
        //mediaPlayerComponent.mediaPlayer().marquee().set(marquee);
        mediaPlayerComponent.mediaPlayer().media().play(url);

        System.out.println("contentPane=" + contentPane.getWidth());
        System.out.println("controlsPane=" + controlsPane.getWidth());
        System.out.println("pauseButton=" + pauseButton.getWidth());
        System.out.println("stopButton=" + stopButton.getWidth());
        System.out.println("volumePane=" + volumePane.getSize());
        System.out.println("volumeLabel=" + volumeLabel.getWidth());
        System.out.println("volumeBar=" + volumeBar.getSize());
        System.out.println("fullScreenButton=" + fullScreenButton.getWidth());
        System.out.println("progressPane=" + progressPane.getSize());
        System.out.println("progressBar=" + progressBar.getWidth());
        System.out.println("curTimeLabel=" + curTimeLabel.getSize());
        System.out.println("splitLabel=" + splitLabel.getSize());
        System.out.println("totalTimeLabel=" + totalTimeLabel.getSize());
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
