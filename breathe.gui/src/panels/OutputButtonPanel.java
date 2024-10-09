package panels;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import app.App;

public class OutputButtonPanel {

    JPanel mainPanel;
    private List<JToggleButton> buttons;
    private App app;

    public OutputButtonPanel(App app) {
        this.app = app;
        this.buttons = new ArrayList<>();

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));

        JScrollPane scrollablePanel = new JScrollPane(mainPanel);
        scrollablePanel.setPreferredSize(new Dimension(250, 120));
        scrollablePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollablePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollablePanel.setBorder(null);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addOutputButton(String outputName) {
        JToggleButton button = new JToggleButton(outputName);
        button.setSelected(true);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.updateOutputDisplay(buttons);
            }
        });

        button.setToolTipText(outputName);
        buttons.add(button);
        mainPanel.add(button);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
