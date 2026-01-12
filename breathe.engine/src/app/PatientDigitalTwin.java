package app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitware.pulse.cdm.bind.Enums.eDriverWaveform;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PatientDigitalTwin {
  // STATE
  // Integer tuples representing the current and previous state of the system
  private List<Integer> currState;
  private List<Integer> prevState;
  // List and size of state variables names
  private String[] stateVarsNames;
  private int stateDimension;
  // Indexes of patient and doctor location in the state
  private int patientLocIndex;
  private int doctorLocIndex;
  // Names of locations for patient (mapped to integers for the state) and doctor
  private Map<String, Integer> patientLocNamesToInts;
  private String[] doctorLocNames;
  
  // INPUT METRICS
  // Maps state tuples to action channels, representing the strategy
  private Map<List<Integer>, String> regressorsMap;
  // List of metrics names (normal and long version)
  private List<String> metricNames = Arrays.asList("hr", "tv", "rr", "cd", "ox");
  private List<String> metricLongNames = Arrays.asList("HeartRate", "TidalVolume", "RespirationRate", "CarbonDioxide", "OxygenSaturation");
  // Maps metric names to safe operating range
  private Map<String, double[]> metricBounds;
  
  // Current action object (accumulates changes)
  private SEMechanicalVentilatorVolumeControl currentSettings;

  // Table of transitions with source and destination nodes, read from file
  private String patientSHAFile = "../../breathe_stratego/patient_transitions.csv";  // !! TODO
  private List<String> patientTransitions;

  public PatientDigitalTwin(Path strategyFile) throws IOException {
	// Starting values: doctor loc = 0 (init), patient loc = 9 (init), five ok flags = 1, vent_on = 0
	currState = new ArrayList<>(Arrays.asList(0,9,1,1,1,1,1,0));
	// Null previous state
	prevState = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0));
	
    // initialize lower and upper bounds of metrics
    metricBounds = new HashMap<>();
    metricBounds.put("hr", new double[] {65, 90});       // HeartRate
    metricBounds.put("tv", new double[] {300, 500});     // TidalVolume
    metricBounds.put("rr", new double[] {10, 17});       // RespirationRate
    metricBounds.put("cd", new double[] {40, 65});       // CarbonDioxide
    metricBounds.put("ox", new double[] {0.915, 0.99});  // OxygenSaturation

    // Initialize the accumulated action
    currentSettings = new SEMechanicalVentilatorVolumeControl();
    currentSettings.getFlow().setValue(60, VolumePerTimeUnit.L_Per_min);
    currentSettings.getFractionInspiredOxygen().setValue(0.21);
    currentSettings.getInspiratoryPeriod().setValue(1.0, TimeUnit.s);
    currentSettings.getPositiveEndExpiratoryPressure().setValue(6, PressureUnit.cmH2O);
    currentSettings.getRespirationRate().setValue(12, FrequencyUnit.Per_min);
    currentSettings.getTidalVolume().setValue(400, VolumeUnit.mL);
    currentSettings.setInspirationWaveform(eDriverWaveform.Square);
    currentSettings.setConnection(eSwitch.On);

    loadStrategyFromFile(strategyFile);
    patientTransitions = Files.readAllLines(Path.of(patientSHAFile));
  }

  public SEMechanicalVentilatorVolumeControl getCurrentSettings() {
    return currentSettings;
  }

  public void printStates() {
    System.out.println("Previous state: " + prevState);
    System.out.println("Current state:  " + currState);
    for (int i = 0; i < stateVarsNames.length; i++)
      System.out.print(stateVarsNames[i] + "=" + currState.get(i) + " ");
    // System.out.println("\n" + getCurrentSettings());
    System.out.println("\n");
  }
  
  private int getStateIndex(String name) {
    return Arrays.asList(stateVarsNames).indexOf(name);
  }
  
  private void setState(List<Integer> newState) {
    prevState = currState;
    currState = newState;
  }
  
  private String setBoolAndComputeEvent(String metricName, double metricValue) {
    // compute new boolean
    double low  = metricBounds.get(metricName)[0];
    double high = metricBounds.get(metricName)[1];
    int newOk = (low <= metricValue && metricValue <= high) ? 1 : 0;
    // set new boolean
    int stateIndex = getStateIndex(metricName + "_ok");
    currState.set(stateIndex, newOk);
    
    if (prevState.get(stateIndex) != currState.get(stateIndex)) {
      String eventChannel = metricName;
      if (metricValue < low)
        eventChannel += "1";
      else if (metricValue > high)
        eventChannel += "3";
      else
        eventChannel += "2";
      return eventChannel;
    }
    else {
      return null;
    }
  }
  
  private void updateStateFromPatient(Map<String, Double> metrics) {
    // State has 8 components
    prevState = new ArrayList<>(currState);

    // Change the 5 booleans
    String patientEvent = null;
    for (String name : metricNames) {
      double metric = metrics.get(metricLongNames.get(metricNames.indexOf(name)));
      String event = setBoolAndComputeEvent(name, metric);
      if (patientEvent == null && event != null)
        patientEvent = event;
    }
    // Change patient location
    System.out.println("Patient event: " + patientEvent);
    changePatientLocWithAction(patientEvent);

    // Set ventilator flag (supposed to be fixed) just in case
    // currState.set(getStateIndex("vent_on"), 1);
    
    // Last component (doctor location) cannot change directly after reading the metrics,
    // it must be triggered by an event
  }
  
  private String chooseActionString() {
    return regressorsMap.get(currState);  // can also be null
  }

  // implements doctor SHA
  private void changeDoctorLocWithAction(String a) {
    if (a == null)
      return;

    // TODO technically one could implement a CSV-based method similar to changePatientLocWithAction
    // but this requires a class refactoring and I can't be bothered rn

    // Single-state doctor automaton
    return;

    // Three-state doctor automaton
//    int doctorLoc = currState.get(doctorLocIndex);
//    if (doctorLoc == 0) {
//      if (a.equals("rera3"))
//        currState.set(doctorLocIndex, 1);
//      else if (a.equals("tvol3"))
//        currState.set(doctorLocIndex, 2);
//    }
  }

  // implements patient SHA
  private void changePatientLocWithAction(String a) {
    if (a == null)
      return;

    for (String line : patientTransitions) {
        // split row on comma
        String[] parts = line.split(",");
        if (parts.length != 3)
          continue;

        String startLoc = parts[0];
        String tableAction = parts[1];
        String endLoc = parts[2];

        // check that both the action and the location is the same
        if (tableAction.equals(a) && patientLocNamesToInts.get(startLoc) == currState.get(patientLocIndex)) {
          // update location
          currState.set(patientLocIndex, patientLocNamesToInts.get(endLoc));
          System.out.println("Patient transition: " + startLoc + " " + tableAction + " " + endLoc);
          return;
        }
      }

    System.out.println("No patient transition");
  }
  
  private void chooseAndApplyDoctorAction() {
    String actionString = chooseActionString();
    System.out.println("Doctor event: " + actionString);
    if (actionString == null)
      return;

    // apply parameter change corresponding to the event
    switch (actionString) {
      case "rera3":
        int currentRR = (int) currentSettings.getRespirationRate().getValue(FrequencyUnit.Per_min);
        currentSettings.getRespirationRate().setValue(currentRR + 1, FrequencyUnit.Per_min);
      break;
      case "tvol3":
        int currentTV = (int) currentSettings.getTidalVolume().getValue(VolumeUnit.mL);
        currentSettings.getTidalVolume().setValue(currentTV + 20, VolumeUnit.mL);
      break;
    }
    
    // both locations can change
    changeDoctorLocWithAction(actionString);
    changePatientLocWithAction(actionString);
  }

  // !! All-in-one update method (public interface)
  public SEMechanicalVentilatorVolumeControl updateAndGetAction(Map<String, Double> metrics) {
    // System.out.println("Calling updateAndGetAction()");
    updateStateFromPatient(metrics);
    chooseAndApplyDoctorAction();
    // System.out.println("Done");
    return getCurrentSettings();
  }

  private void loadStrategyFromFile(Path file) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(Files.newBufferedReader(file));

    // ---- actions ----
    Map<Integer, String> actionsMap = new HashMap<>();
    JsonNode actionsNode = root.path("actions");
    Iterator<String> actionKeys = actionsNode.fieldNames();
    while (actionKeys.hasNext()) {
      String k = actionKeys.next();
      String v = actionsNode.path(k).asText();

      String extracted;
      if ("WAIT".equals(v)) {
        extracted = null;
      } else {
        int idx = v.indexOf('!');
        if (idx >= 0) {
          int start = v.lastIndexOf(' ', idx) + 1;
          extracted = v.substring(start, idx);
        } else {
          extracted = null;
        }
      }
      actionsMap.put(Integer.parseInt(k), extracted);
    }
    

    // ---- statevars ----
    JsonNode stateVarsNode = root.path("statevars");
    stateVarsNames = new String[stateVarsNode.size()];
    for (int i = 0; i < stateVarsNode.size(); i++) {
      stateVarsNames[i] = stateVarsNode.get(i).asText();
    }
    stateDimension = stateVarsNames.length;
    doctorLocIndex = getStateIndex("doctor.location");
    patientLocIndex = getStateIndex("patient.location");

    // ---- locationnames ----
    JsonNode locNames = root.path("locationnames");

    JsonNode doctorNode = locNames.path("doctor.location");
    doctorLocNames = new String[doctorNode.size()];
    {
      int idx = 0;
      Iterator<String> it = doctorNode.fieldNames();
      while (it.hasNext()) {
        doctorLocNames[idx++] = it.next();
      }
    }

    // ---- patient location name -> integer index map ----
    JsonNode patientLocMapNode = locNames.path("patient.location");
    patientLocNamesToInts = new HashMap<>();

    Iterator<String> patientLocFieldNames = patientLocMapNode.fieldNames();
    while (patientLocFieldNames.hasNext()) {
      String key = patientLocFieldNames.next();
      String name = patientLocMapNode.path(key).asText();

      patientLocNamesToInts.put(name, Integer.parseInt(key));
    }
    
    // ---- regressors ----
    regressorsMap = new HashMap<>();
    JsonNode regressorsNode = root.path("regressors");
    Iterator<String> regKeys = regressorsNode.fieldNames();

    while (regKeys.hasNext()) {
      String key = regKeys.next();

      String trimmed = key.trim();
      if (!trimmed.startsWith("(") || !trimmed.endsWith(")")) {
        throw new IOException("Invalid regressor key format: " + key);
      }
      String inside = trimmed.substring(1, trimmed.length() - 1);
      String[] parts = inside.split(",");
      if (parts.length != stateDimension) {
        throw new IOException("Regressor key does not match state dimension: " + key);
      }

      List<Integer> tuple = new ArrayList<>(stateDimension);
      for (String p : parts) {
        tuple.add(Integer.parseInt(p.trim()));
      }

      JsonNode regObj = regressorsNode.path(key);
      int minimize = regObj.path("minimize").asInt(0);

      JsonNode regSub = regObj.path("regressor");
      Iterator<String> subKeys = regSub.fieldNames();

      String chosenAction = null;
      Double chosenValue = null;

      while (subKeys.hasNext()) {
        String subk = subKeys.next();
        double val = regSub.path(subk).asDouble();

        if (chosenValue == null) {
          chosenValue = val;
          chosenAction = subk;
        } else {
          if (minimize == 0) {
            if (val > chosenValue) {
              chosenValue = val;
              chosenAction = subk;
            }
          } else {
            if (val < chosenValue) {
              chosenValue = val;
              chosenAction = subk;
            }
          }
        }
      }

      int chosenActionInt = Integer.parseInt(chosenAction);
      regressorsMap.put(tuple, actionsMap.get(chosenActionInt));
    }
  }

  public static void main(String[] args) {
    try {
      PatientDigitalTwin twin = new PatientDigitalTwin(Path.of("../../breathe_stratego/strat.json"));

      System.out.println("Initialization");
      System.out.println("State size: " + twin.stateDimension + ", variables: " + String.join(",", twin.stateVarsNames));
      System.out.println("Patient locations: " + twin.patientLocNamesToInts);
      System.out.println("Strategy: " + twin.regressorsMap);
      twin.printStates();

      System.out.println("Test setState");
      twin.setState(new ArrayList<>(Arrays.asList(0,9,1,1,1,1,1,1)));
      twin.printStates();

      System.out.println("Send patient data (start, all metrics ok)");
      Map<String, Double> data = new HashMap<>();
      data.put("HeartRate", 72.0);
      data.put("TidalVolume", 400.0);
      data.put("RespirationRate", 12.0);
      data.put("CarbonDioxide", 41.0);
      data.put("OxygenSaturation", 0.975);
      twin.updateAndGetAction(data);  // = null
      twin.printStates();

      System.out.println("Send low CD (event cd1)");
      data.put("CarbonDioxide", 35.0);
      twin.updateAndGetAction(data);  // = tvol3
      twin.printStates();

      System.out.println("Send low TV (event tv1)");
      data.put("TidalVolume", 250.0);
      twin.updateAndGetAction(data);  // = rera3
      twin.printStates();

      System.out.println("Send normal CD (event cd2)");
      data.put("CarbonDioxide", 41.0);
      twin.updateAndGetAction(data);  // = rera3
      twin.printStates();

      System.out.println("Send normal TV (event tv2)");
      data.put("TidalVolume", 400.0);
      twin.updateAndGetAction(data);  // = tvol3
      twin.printStates();

      System.out.println(twin.getCurrentSettings());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
