package swingtest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author jimsshom
 * @date 2020/02/19
 * @time 16:36
 */
public class Test {
    private static int count = 0;
    public static void main(String[] args) {
        final JFrame frame = new JFrame("test");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final CardLayout cardLayout = new CardLayout();
        frame.setLayout(cardLayout);
        JPanel consolePane = new JPanel(new BorderLayout());
        frame.getContentPane().add(consolePane, "console");

        final JTextArea textArea = new JTextArea();
        textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        textArea.setEnabled(false);
        textArea.setLineWrap(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setDisabledTextColor(Color.GREEN);
        textArea.setFont(new Font("楷体",Font.BOLD,20));
        textArea.append("test one\n");
        textArea.append("two\n");

        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        consolePane.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPane = new JPanel();
        JButton appendButton = new JButton("append");
        JButton playerButton = new JButton("player");
        bottomPane.add(appendButton);
        bottomPane.add(playerButton);
        consolePane.add(bottomPane, BorderLayout.SOUTH);


        JPanel playerPane = new JPanel(new BorderLayout());
        frame.getContentPane().add(playerPane, "player");

        JPanel displayPane = new JPanel();
        displayPane.setBackground(Color.BLACK);
        playerPane.add(displayPane, BorderLayout.CENTER);

        final JPanel controlPane = new JPanel();
        controlPane.setBackground(Color.WHITE);
        controlPane.setLayout(new BorderLayout());

        final JPanel leftPane = new JPanel();
        leftPane.setBackground(Color.YELLOW);
        JButton consoleButton = new JButton("console");
        leftPane.add(consoleButton);
        leftPane.add(new JLabel("volume"));
        controlPane.add(leftPane, BorderLayout.WEST);

        final JPanel centerPane = new JPanel(new BorderLayout());//BorderLayout中只有一个组件则塞满
        centerPane.setBackground(Color.RED);
        final JSlider slider = new JSlider(0, 10000);
        centerPane.add(slider);
        controlPane.add(centerPane, BorderLayout.CENTER);

        final JPanel rightPane = new JPanel();
        rightPane.setBackground(Color.GREEN);
        JLabel test = new JLabel("test");
        rightPane.add(test);
        controlPane.add(rightPane, BorderLayout.EAST);

        playerPane.add(controlPane, BorderLayout.SOUTH);

        playerPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //int newWidth = controlPane.getWidth() - leftPane.getWidth() - rightPane.getWidth();
                //slider.setBounds(0, 0, newWidth, centerPane.getHeight());
                super.componentResized(e);
            }
        });

        consoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(frame.getContentPane(), "console");
            }
        });

        playerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(frame.getContentPane(), "player");
            }
        });

        appendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append(count + "追加一行\n");
                count += 1;
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        });

        cardLayout.show(frame.getContentPane(), "player");
        frame.setVisible(true);
    }
}
