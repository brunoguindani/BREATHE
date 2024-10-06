package panels;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import app.App_temp;
import data.Action;
import utils.Pair;

import java.util.ArrayList;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

public class ScenarioPanel {
	
	/*
	 * Panel to create scenario
	 */

    private JPanel mainPanel;
    private JComboBox<String> fileComboBox = new JComboBox<>();

    
    private ArrayList<Pair<Action, Integer>> actions = new ArrayList<>();
    
    
    public ScenarioPanel(App_temp app) {
    	
    	//Main panel
    	mainPanel = new JPanel();
    	mainPanel.setLayout(new GridBagLayout());
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	
        GridBagConstraints gbc = new GridBagConstraints();
        
        //PATIENT NAME SETUP
        String[] directories = {"./states/", "./states/exported/"};
        updatePatientFiles(directories);
        addLabelAndField("Patient:", fileComboBox, mainPanel, gbc, 0);
        
        //SCENARIO NAME SETUP
        JTextField scenarioNameField = new JTextField(25);
        addLabelAndField("Scenario Name:", scenarioNameField, mainPanel, gbc, 1);

        
        //TABLE SETUP
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Action", "Time"}, 0) {

			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable actionsTable = new JTable(tableModel);
        
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel() {

			private static final long serialVersionUID = 1L;

			@Override
            public void setSelectionInterval(int index0, int index1) {
                //Can only select lines not starting with "	  ", so only action names
                String action = (String) tableModel.getValueAt(index0, 0);
                if (!action.startsWith("    ")) {
                    super.setSelectionInterval(index0, index0);
                } else {
                    clearSelection();
                }
            }
        };

        // Assign logic to table
        actionsTable.setSelectionModel(selectionModel);
        updateActionsDisplay(tableModel);
        JScrollPane actionsScrollPane = new JScrollPane(actionsTable);
        addLabelAndField("", actionsScrollPane, mainPanel, gbc, 2);


        //BUTTON SETUP
        JButton createScenarioButton = new JButton("Create Scenario");;
        createScenarioButton.setBackground(new Color(0, 122, 255)); 
        createScenarioButton.setForeground(Color.WHITE);
        
        JButton removeActionButton = new JButton("Remove Selected Actions");
        removeActionButton.setBackground(new Color(255, 59, 48));
        removeActionButton.setForeground(Color.WHITE);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(removeActionButton, gbc); 
        
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(createScenarioButton, gbc);

        createScenarioButton.addActionListener(e -> {
        });

        removeActionButton.addActionListener(e -> {
        });
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
    
    private void addLabelAndField(String labelText, JComponent textField, JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(textField, gbc);
    }


    // get all patients from folder 
	public void updatePatientFiles(String[] directories) {
        for (String dirPath : directories) {
            File dir = new File(dirPath);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".json")) { // Pick only files .json
                            fileComboBox.addItem(file.getName());
                        }
                    }
                }
            }
        }
    }
	
    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    
    private void updateActionsDisplay(DefaultTableModel tableModel) {

        tableModel.setRowCount(0);

        for (Pair<Action, Integer> action : actions) {
            String actionString = action.getKey().toString();
            String timeString = formatTime(action.getValue());

            String actionName = actionString.split("\n")[0];
            tableModel.addRow(new Object[]{actionName, timeString});

            String[] lines = actionString.split("\n");
            for (int i = 1; i < lines.length; i++) {
                tableModel.addRow(new Object[]{"    " + lines[i], ""});
            }
            tableModel.addRow(new Object[]{"    ", "    "});
        }

    }
    
}
