package panels;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.kitware.pulse.cdm.actions.SEAdvanceTime;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.scenario.SEScenario;
import com.kitware.pulse.cdm.actions.SEAction;
import java.util.ArrayList;
import utils.Pair;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

public class ScenarioPanel {

    private JPanel scenarioPanel;

    JComboBox<String> fileComboBox = new JComboBox<>();
    private JTextField scenarioNameField;
    private JTable actionsTable;
    private DefaultTableModel tableModel;
    private JButton createScenarioButton;
    private JButton removeActionButton; 

    private ArrayList<Pair<SEAction, Integer>> actions = new ArrayList<>();

    private String[] directories = {"./states/", "./states/exported/"};

    public ScenarioPanel() {
        scenarioPanel = new JPanel();
        scenarioPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        scenarioNameField = new JTextField(25);
        createScenarioButton = new JButton("Create Scenario");
        createScenarioButton.setBackground(new Color(0, 122, 255)); 
        createScenarioButton.setForeground(Color.WHITE);
        
        removeActionButton = new JButton("Remove Selected Actions"); 
        removeActionButton.setBackground(new Color(255, 59, 48));
        removeActionButton.setForeground(Color.WHITE);

        updatePatientFiles();

        addLabelAndField("Patient:", fileComboBox, scenarioPanel, gbc, 0);
        addLabelAndField("Scenario Name:", scenarioNameField, scenarioPanel, gbc, 1);

        tableModel = new DefaultTableModel(new Object[]{"Action", "Time"}, 0) {

			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        actionsTable = new JTable(tableModel);
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void setSelectionInterval(int index0, int index1) {
                // Permetti la selezione solo per le righe che non iniziano con "    " (indentate)
                String action = (String) tableModel.getValueAt(index0, 0);
                if (!action.startsWith("    ")) {
                    super.setSelectionInterval(index0, index0); // Permetti solo la selezione della riga
                } else {
                    clearSelection(); // Altrimenti non selezionare
                }
            }
        };

        // Assegna il selection model alla tabella
        actionsTable.setSelectionModel(selectionModel);
        
        JScrollPane actionsScrollPane = new JScrollPane(actionsTable);
        addLabelAndField("", actionsScrollPane, scenarioPanel, gbc, 2);

        updateActionsDisplay();

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        scenarioPanel.add(removeActionButton, gbc); 
        
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        scenarioPanel.add(createScenarioButton, gbc);

        createScenarioButton.addActionListener(e -> {
            createScenario();
        });

        removeActionButton.addActionListener(e -> {
        	removeActionButtonAction();
        });
    }


	public void updatePatientFiles() {
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

    private void addLabelAndField(String labelText, JComponent textField, JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(textField, gbc);
    }

    public void createScenario() {
        SEScenario sce = new SEScenario();
        String scenarioName = scenarioNameField.getText();

        if (scenarioName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a name for the scenario.", "Missing Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File scenarioFile = new File("./scenario/" + scenarioName + ".json");
        if (scenarioFile.exists()) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "A file named \"" + scenarioName + ".json\" already exists. Do you want to overwrite it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        sce.setName(scenarioName);

        String patientFile = (String) fileComboBox.getSelectedItem();
        File patientTempFile = new File("./states/" + patientFile);
        if (patientTempFile.exists())
            sce.setEngineState("./states/" + patientFile);
        else
            sce.setEngineState("./states/exported/" + patientFile);

        int seconds = 0;
        SEAdvanceTime adv = new SEAdvanceTime();
        adv.getTime().setValue(1, TimeUnit.s);
        for (Pair<SEAction, Integer> action : actions) {
            int target = action.getValue();

            while (seconds < target) {
                sce.getActions().add(adv);
                seconds++;
            }

            sce.getActions().add(action.getKey());
        }

        sce.writeFile("./scenario/" + scenarioName + ".json");
        MiniLogPanel.append("Scenario exported\n");
    }

    public void addAction(SEAction action, int seconds) {
        Pair<SEAction, Integer> newAction = new Pair<>(action, seconds);
        actions.add(newAction);
        actions.sort((pair1, pair2) -> pair1.getValue().compareTo(pair2.getValue()));
        updateActionsDisplay();
    }

    private void removeActionButtonAction() {
        int[] selectedRows = actionsTable.getSelectedRows(); 

      //Check if at least one actions is selected
        if (selectedRows.length > 0) {
            StringBuilder confirmationMessage = new StringBuilder("Are you sure you want to remove the following actions?\n");

            //Check if all the actions selected are not empty and create message
            for (int row : selectedRows) {
                if (isEmptyRow(row)) {
                    JOptionPane.showMessageDialog(null, "Please select a valid action to remove.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Pair<SEAction, Integer> actionToRemove = getActionFromRow(row);
                confirmationMessage.append("\n"+actionToRemove.getKey().toString()+"\nTime: "+formatTime(actionToRemove.getValue())+"\n");
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    confirmationMessage.toString(),
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
               
            	 // Remove the selected actions, iterating backward
                for (int i = selectedRows.length - 1; i >= 0; i--) { 
                    int row = selectedRows[i]; 
                    Pair<SEAction, Integer> actionToRemove = getActionFromRow(row);
                    removeAction(actionToRemove.getKey());
                }
                
                updateActionsDisplay(); 
                actionsTable.clearSelection();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select actions to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    
    private void updateActionsDisplay() {
        tableModel.setRowCount(0);

        for (Pair<SEAction, Integer> action : actions) {
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

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public JPanel getScenarioPanel() {
        return scenarioPanel;
    }

    private Pair<SEAction, Integer> getActionFromRow(int row) {
        int rowCount = 0;

        for (Pair<SEAction, Integer> action : actions) {
            int lines = action.getKey().toString().split("\n").length + 1; 

            if (rowCount == row) {
                return action; 
            }
            if (rowCount + lines > row) {
                return action; 
            }
            rowCount += lines; 
        }
        return null; 
    }


    private void removeAction(SEAction actionToRemove) {
        actions.removeIf(pair -> pair.getKey().equals(actionToRemove));
    }
    
    private boolean isEmptyRow(int row) {
        Object actionValue = tableModel.getValueAt(row, 0);
        return actionValue == null || actionValue.toString().trim().isEmpty();
    }
}
