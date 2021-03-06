package com.jimsshom.streamingplayer.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jimsshom.streamingplayer.eventbus.EventAdapter;
import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;
import com.jimsshom.streamingplayer.MainLauncher;
import com.jimsshom.streamingplayer.mediaplayer.MediaPlayManager;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:06
 */
public class UIManager {
    private final Icon  fullIcon = new ImageIcon(MainLauncher.class.getClassLoader().getResource("FullScreen.png"));
    private final Icon pauseIcon = new ImageIcon(MainLauncher.class.getClassLoader().getResource("pause.png"));
    private final Icon playIcon = new ImageIcon(MainLauncher.class.getClassLoader().getResource("play.png"));
    private final Icon volumeIcon = new ImageIcon(MainLauncher.class.getClassLoader().getResource("volume.png"));
    private final Icon stopIcon = new ImageIcon(MainLauncher.class.getClassLoader().getResource("Stop.png"));

    //frame
    private JFrame frame;
    private CardLayout frameLayout;
    //console card
    private JPanel consolePane;
    //  console
    private JTextArea textArea;
    private JScrollPane scrollPane;

    //player card
    private JPanel playerPane;
    //  control
    private JPanel controlPane;
    //      left
    private JPanel leftPane;
    private JButton pauseButton;
    private JButton stopButton;
    private JLabel volumeLabel;
    private JSlider volumeBar;
    private JButton fullScreenButton;
    //      centre
    private JPanel centrePane;
    private JSlider progressBar;

    //      right
    private JPanel rightPane;
    private JLabel curTimeLabel;
    private JLabel splitLabel;
    private JLabel totalTimeLabel;


    private MediaPlayManager mediaPlayManager;
    private KeyAdapter exitFullScreenKeyAdapter;
    private long curTime = 0;
    private String curState = "";

    public UIManager(MediaPlayManager mediaPlayManager) {
        this.mediaPlayManager = mediaPlayManager;
        initialStaticComponent();
    }

    public void initial() {
        registerEventConsumer();
        registerEventProducer();
    }

    private void registerEventProducer() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                EventBusManager.fireEvent(EventType.BEFORE_SYSTEM_EXIT, null);
                System.out.println("system.exit(0)");
                System.exit(0);
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventBusManager.fireEvent(EventType.PLAY_OR_PAUSE, null);
                mediaPlayManager.getDisplayComponent().requestFocus();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventBusManager.fireEvent(EventType.STOP, null);
                mediaPlayManager.getDisplayComponent().requestFocus();
            }
        });
        progressBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                //System.out.println(source.getValue() + String.valueOf(!source.getValueIsAdjusting()));
                if (Math.abs(source.getValue() - curTime/1000) > 5) {
                    //mediaPlayerManager.pause();
                    //System.out.println(source.getValue() + " & " + curTime/1000);
                    long newTime = source.getValue() * 1000;
                    //System.out.println(curTime + "->" + newTime);
                    EventBusManager.fireEvent(EventType.SEEK_BY_TIME, String.valueOf(newTime));
                    //mediaPlayerManager.play();
                }
                mediaPlayManager.getDisplayComponent().requestFocus();
            }
        });
        volumeBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    EventBusManager.fireEvent(EventType.SET_VOLUME, String.valueOf(source.getValue()));
                }
                mediaPlayManager.getDisplayComponent().requestFocus();
            }
        });
        fullScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayManager.getDisplayComponent().mediaPlayer().fullScreen().toggle();
                mediaPlayManager.getDisplayComponent().requestFocus();
            }
        });

        //Key & Mouse
        mediaPlayManager.getDisplayComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                UILog.log("key=" + e.getKeyCode());
                if (e.getKeyCode() == 39) {//right arrow
                    EventBusManager.fireEvent(EventType.SKIP_BY_TIME, "10000");
                    if ("paused".equals(curState)) {
                        curTime = curTime + 10000;
                        EventBusManager.fireEvent(EventType.PROGRESS_TIME, String.valueOf(curTime));
                    }
                } else if (e.getKeyCode() == 37) {//left arrow
                    EventBusManager.fireEvent(EventType.SKIP_BY_TIME, "-10000");
                    if ("paused".equals(curState)) {
                        curTime = curTime - 10000;
                        EventBusManager.fireEvent(EventType.PROGRESS_TIME, String.valueOf(curTime));
                    }
                } else if (e.getKeyCode() == 32) {//space
                    EventBusManager.fireEvent(EventType.PLAY_OR_PAUSE, null);
                }
                super.keyPressed(e);
            }
        });
        mediaPlayManager.getDisplayComponent().videoSurfaceComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EventBusManager.fireEvent(EventType.PLAY_OR_PAUSE, null);
                super.mouseClicked(e);
            }
        });

        exitFullScreenKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 27) {
                    mediaPlayManager.getDisplayComponent().mediaPlayer().fullScreen().toggle();
                }
                super.keyPressed(e);
            }
        };
        //FullScreen
        mediaPlayManager.getDisplayComponent().mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(frame) {
            @Override
            protected void onBeforeEnterFullScreen() {
                controlPane.setVisible(false);
                mediaPlayManager.getDisplayComponent().addKeyListener(exitFullScreenKeyAdapter);
                super.onBeforeEnterFullScreen();
            }

            @Override
            protected void onAfterExitFullScreen() {
                controlPane.setVisible(true);
                mediaPlayManager.getDisplayComponent().removeKeyListener(exitFullScreenKeyAdapter);
                super.onAfterExitFullScreen();
            }
        });
    }

    private void registerEventConsumer() {
        EventBusManager.addEventAdapter(EventType.START_NEW_URL, new EventAdapter() {
            @Override
            public void process(String param) {
                frameLayout.show(frame.getContentPane(), "player");
                mediaPlayManager.getDisplayComponent().requestFocus();
            }
        });
        EventBusManager.addEventAdapter(EventType.STATUS_CHANGE, new EventAdapter() {
            @Override
            public void process(String param) {
                if ("stopped".equals(param)) {
                    curState = "stopped";
                    frameLayout.show(frame.getContentPane(), "console");
                } else if ("playing".equals(param)) {
                    curState = "playing";
                    pauseButton.setIcon(pauseIcon);
                } else if ("paused".equals(param)) {
                    curState = "paused";
                    pauseButton.setIcon(playIcon);
                }
            }
        });
        EventBusManager.addEventAdapter(EventType.LOG, new EventAdapter() {
            @Override
            public void process(String param) {
                System.out.println(param);
                textArea.append(param + '\n');
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        });
        EventBusManager.addEventAdapter(EventType.PROGRESS_TIME, new EventAdapter() {
            @Override
            public void process(String param) {
                curTime = Integer.valueOf(param);
                progressBar.setValueIsAdjusting(true);
                progressBar.setValue(Integer.valueOf(param) / 1000);
                curTimeLabel.setText(parseTimestamp(Integer.valueOf(param)));
            }
        });
        EventBusManager.addEventAdapter(EventType.TOTAL_TIME, new EventAdapter() {
            @Override
            public void process(String param) {
                progressBar.setMaximum(Integer.valueOf(param) / 1000);
                totalTimeLabel.setText(parseTimestamp(Integer.valueOf(param)));
            }
        });
    }

    private void initialStaticComponent() {
        frame = new JFrame("Swing Streaming Player");
        frame.setBounds(100, 100, 800, 600);
        frame.setMinimumSize(new Dimension(600, 450));
        frameLayout = new CardLayout();
        frame.setLayout(frameLayout);

        initialConsoleCard();
        initialPlayerCard();

        frameLayout.show(frame.getContentPane(), "console");
        frame.setVisible(true);
    }

    private void initialPlayerCard() {
        playerPane = new JPanel(new BorderLayout());
        playerPane.add(mediaPlayManager.getDisplayComponent(), BorderLayout.CENTER);

        leftPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pauseButton = new JButton();
        pauseButton.setIcon(playIcon);
        leftPane.add(pauseButton);
        stopButton = new JButton();
        stopButton.setIcon(stopIcon);
        leftPane.add(stopButton);
        volumeLabel = new JLabel();
        volumeLabel.setIcon(volumeIcon);
        leftPane.add(volumeLabel);
        volumeBar = new JSlider();
        volumeBar.setMinimum(0);
        volumeBar.setMaximum(100);
        volumeBar.setValue(100);
        volumeBar.setPreferredSize(new Dimension(100, volumeBar.getPreferredSize().height));//preferredSize在渲染前设置
        leftPane.add(volumeBar);

        fullScreenButton = new JButton();
        fullScreenButton.setIcon(fullIcon);
        leftPane.add(fullScreenButton);

        centrePane = new JPanel(new BorderLayout());
        progressBar = new JSlider();
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        centrePane.add(progressBar);

        rightPane = new JPanel(new BorderLayout());
        rightPane.setBorder(new EmptyBorder(0, 0, 0, 5));
        curTimeLabel = new JLabel("--");
        rightPane.add(curTimeLabel, BorderLayout.WEST);
        splitLabel = new JLabel("/");
        rightPane.add(splitLabel, BorderLayout.CENTER);
        totalTimeLabel = new JLabel("--");
        rightPane.add(totalTimeLabel, BorderLayout.EAST);

        controlPane = new JPanel(new BorderLayout());
        controlPane.add(leftPane, BorderLayout.WEST);
        controlPane.add(centrePane, BorderLayout.CENTER);
        controlPane.add(rightPane, BorderLayout.EAST);

        playerPane.add(controlPane, BorderLayout.SOUTH);

        frame.getContentPane().add(playerPane, "player");
    }

    private void initialConsoleCard() {
        consolePane = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        textArea.setEnabled(true);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setDisabledTextColor(Color.GREEN);
        textArea.setFont(new Font("楷体",Font.BOLD,20));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        consolePane.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(consolePane, "console");
    }

    private String parseTimestamp(long ms) {
        long sec = BigDecimal.valueOf(ms).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_EVEN).longValue();
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;

        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
