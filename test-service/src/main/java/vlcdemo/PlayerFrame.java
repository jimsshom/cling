package vlcdemo;

import javax.swing.*;

/**
 * @author xiaohe.yz
 * @date 2020/02/18
 * @time 14:57
 */
public class PlayerFrame {
    private JFrame frame;
    private JButton pauseButton;
    private JButton stopButton;
    private JSlider volumeBar;
    private JButton fullScreenButton;
    private JSlider progressBar;
    private JLabel totalTimeLabel;
    private JLabel curTimeLabel;

    public PlayerFrame() {
        frame = initialStaticLayout();
    }

    /**
     * controlsPane尺寸，总宽度：800
     * pause | stop | volumeLable | volumeBar | fullScreen | progressBar     | curTime | split | totalTime
     * 36    | 36   | 20          | 100       | 36         | min:162 def:362 | 100 + 7 + 100 = 210
     * 438
     * 最小 600
     * 默认宽度 800
     */
    private JFrame initialStaticLayout() {
        return null;
    }
}
