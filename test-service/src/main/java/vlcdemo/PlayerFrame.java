package vlcdemo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;

/**
 * @author jimsshom
 * @date 2020/02/18
 * @time 14:57
 */
public class PlayerFrame {
    private final String path = "/Users/jimsshom/Works/GitRepo/cling/test-service/src/main/resources/";
    private final Icon fullIcon = new ImageIcon(new ImageIcon(path + "FullScreen.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
    private final Icon pauseIcon = new ImageIcon(new ImageIcon(path + "pause.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
    private final Icon playIcon = new ImageIcon(new ImageIcon(path + "play.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
    private final Icon volumeIcon = new ImageIcon(new ImageIcon(path + "volume.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
    private final Icon stopIcon = new ImageIcon(new ImageIcon(path + "Stop.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

    private JFrame frame;
    private MediaPlayerManager mediaPlayerManager;
    private JButton pauseButton;
    private JButton stopButton;
    private JSlider volumeBar;
    private JButton fullScreenButton;
    private JPanel progressPane;
    private JSlider progressBar;
    private JLabel totalTimeLabel;
    private JLabel curTimeLabel;

    private static long curTime = 0;

    public PlayerFrame(MediaPlayerManager mediaPlayerManager) {
        this.mediaPlayerManager = mediaPlayerManager;
        initialStaticLayout(mediaPlayerManager.getMediaPlayerComponent());
        initialEventListener();
    }

    public void initialEventListener() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerManager.releaseResource();
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

                mediaPlayerManager.returnFocus();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mediaPlayerManager.isPlaying()) {
                    mediaPlayerManager.pause();
                    updateIconWhenPause();
                } else {
                    mediaPlayerManager.play();
                    updateIconWhenPlay();
                }
                mediaPlayerManager.returnFocus();
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
                mediaPlayerManager.returnFocus();
            }
        });

        fullScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerManager.toggleFullScreen();
                mediaPlayerManager.returnFocus();
            }
        });

        progressBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                //System.out.println(source.getValue() + String.valueOf(!source.getValueIsAdjusting()));
                if (Math.abs(source.getValue() - curTime/1000) > 5) {
                    mediaPlayerManager.pause();
                    //System.out.println(source.getValue() + " & " + curTime/1000);
                    long newTime = source.getValue() * 1000;
                    //System.out.println(curTime + "->" + newTime);
                    mediaPlayerManager.seekByTime(newTime);
                    mediaPlayerManager.play();
                    updateIconWhenPlay();
                }
                mediaPlayerManager.returnFocus();
            }
        });

        volumeBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    mediaPlayerManager.setVolume(source.getValue());
                }
                mediaPlayerManager.returnFocus();
            }
        });
    }

    public JFrame getFrame() {
        return frame;
    }

    public void closeFrame() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public void updatePlayProgress(long curTime) {
        setCurTime(curTime);
        updateCurrentTimeLabel(curTime);
        setProgressValue(curTime);
    }

    public void updateTotalDuration(long totalTime) {
        updateTotalTimeLabel(totalTime);
        setProgressMaxValue(totalTime);
    }

    private static void setCurTime(long curTime) {
        PlayerFrame.curTime = curTime;
    }

    private void updateCurrentTimeLabel(long time) {
        curTimeLabel.setText(parseTimestamp(time));
    }

    private void updateTotalTimeLabel(long time) {
        totalTimeLabel.setText(parseTimestamp(time));
    }

    private void setProgressValue(long time) {
        progressBar.setValueIsAdjusting(true);
        progressBar.setValue((int)(time / 1000));
    }

    private void setProgressMaxValue(long time) {
        progressBar.setMaximum((int)(time / 1000));
    }

    private String parseTimestamp(long ms) {
        long sec = BigDecimal.valueOf(ms).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_EVEN).longValue();
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;

        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public void updateIconWhenPlay() {
        pauseButton.setIcon(pauseIcon);
    }

    public void updateIconWhenPause() {
        pauseButton.setIcon(playIcon);
    }

    /**
     * controlsPane尺寸，总宽度：800
     * pause | stop | volumeLable | volumeBar | fullScreen | progressBar     | curTime | split | totalTime
     * 36    | 36   | 20          | 100       | 36         | min:162 def:362 | 100 + 7 + 100 = 210
     * 438
     * 最小 600
     * 默认宽度 800
     */
    private void initialStaticLayout(CallbackMediaPlayerComponent mediaPlayerComponent) {
        final JPanel contentPane = new JPanel();
        final JPanel controlsPane = new JPanel();
        final JPanel volumePane = new JPanel(new BorderLayout());
        final JLabel volumeLabel = new JLabel();
        final JLabel splitLabel = new JLabel("/");

        contentPane.setLayout(new BorderLayout());
        progressPane = new JPanel(new BorderLayout());

        pauseButton = new JButton();
        stopButton = new JButton();
        volumeBar = new JSlider();
        fullScreenButton = new JButton();
        progressBar = new JSlider();

        totalTimeLabel = new JLabel("--");
        totalTimeLabel.setSize(100, 29);
        curTimeLabel = new JLabel("--");
        curTimeLabel.setSize(100, 29);

        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 800, 600);
        frame.setMinimumSize(new Dimension(600, 450));

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
    }
}
