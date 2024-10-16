package panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import app.App;

public class PatientConditionsPanel extends JPanel{

	 public PatientPanel patientPanel;
	 public ConditionsPanel conditionsPanel;
	 
	 private JPanel patientConditionPanel;
	 private CardLayout cardLayout;  
	 
	 public PatientConditionsPanel(App app) {
		 
		   patientPanel = new PatientPanel(app);
		   conditionsPanel = new ConditionsPanel(app);
		 	
		   JToggleButton patientRadioButton = new JToggleButton("Patient");
	       JToggleButton conditionsRadioButton = new JToggleButton("Conditions");
	       patientRadioButton.setPreferredSize(new Dimension(150, 30));
	       conditionsRadioButton.setPreferredSize(new Dimension(150, 30));
	        
	       ButtonGroup group = new ButtonGroup();
	       group.add(patientRadioButton);
	       group.add(conditionsRadioButton);
	       patientRadioButton.setSelected(true);  
	        
	       JPanel radioPanel = new JPanel();
	       radioPanel.setBackground(Color.LIGHT_GRAY); 
	       radioPanel.add(patientRadioButton);
	       radioPanel.add(conditionsRadioButton);
	
	       cardLayout = new CardLayout();
	       patientConditionPanel = new JPanel(cardLayout);
	       patientConditionPanel.add(patientPanel, "Patient");
	       patientConditionPanel.add(conditionsPanel, "Condition");
	
	       patientRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Patient"));
	       conditionsRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Condition"));
	       
	       setPreferredSize(new Dimension(550, 20));
	       setBackground(Color.LIGHT_GRAY);
	       add(radioPanel, BorderLayout.NORTH);
	       add(patientConditionPanel, BorderLayout.CENTER);

	 }
	 
	 public PatientPanel getPatientPanel() {
		 return patientPanel;
	 }
	 
	 public ConditionsPanel getConditionsPanel() {
		 return conditionsPanel;
	 }
}
