package app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitware.pulse.cdm.properties.CommonUnits.*;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;
import com.kitware.pulse.cdm.bind.Enums.eDriverWaveform;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;

import java.io.*;
import java.util.*;

public class DecisionTree {
  private Node root;
  private Node cursor;

  // Current action state (accumulates changes)
  private SEMechanicalVentilatorVolumeControl currentSettings;

  // Mapping from JSON tree codes -> descriptive names
  private static final Map<String, String> CODE_TO_LONGNAME = new HashMap<>();
  static {
    CODE_TO_LONGNAME.put("cd", "CarbonDioxide");
    CODE_TO_LONGNAME.put("hr", "HeartRate");
    CODE_TO_LONGNAME.put("os", "OxygenSaturation");
    CODE_TO_LONGNAME.put("rr", "RespirationRate");
    CODE_TO_LONGNAME.put("tv", "TidalVolume");
    CODE_TO_LONGNAME.put("FiO2", "FractionInspiredOxygen");
    CODE_TO_LONGNAME.put("Flow", "Flow");
    CODE_TO_LONGNAME.put("IP", "InspiratoryPeriod");
    CODE_TO_LONGNAME.put("PEEP", "PositiveEndExpiratoryPressure");
    CODE_TO_LONGNAME.put("RR", "RespirationRate");
    CODE_TO_LONGNAME.put("TV", "TidalVolume");
  }

  private static final Map<String, Double> TREE_TO_SIM_CONVERSIONS = new HashMap<>();
  static {
    TREE_TO_SIM_CONVERSIONS.put("OxygenSaturation", 0.01);
	TREE_TO_SIM_CONVERSIONS.put("TidalVolume", 59.0);  // patient weight
  }

  public DecisionTree(String jsonFilePath) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(new File(jsonFilePath));
    this.root = parseNode(rootNode);

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


    // Add initial node pointing to root
    Node initial = new Node();
    Branch rootBranch = new Branch();
    rootBranch.thenNode = this.root;
    initial.branches.add(rootBranch);
    this.root = initial;
    this.cursor = root;
  }

  // --- Node classes ---
  static class Node {
    List<ActionSpec> actions = new ArrayList<>();
    List<Branch> branches = new ArrayList<>();
    Node elseNode;
  }

  static class ActionSpec {
    String op;     // set / inc / dec
    String param;
    double value;

    ActionSpec(String op, String param, double value) {
      this.op = op;
      this.param = param;
      this.value = value;
    }
  }

  static class Interval {
    double low, high;

    Interval(double low, double high) {
      this.low = low;
      this.high = high;
    }

    boolean contains(double v) {
      return v >= low && v <= high;
    }
  }

  static class Condition {
    String metric;
    Interval interval;

    Condition(String metric, Interval interval) {
      this.metric = metric;
      this.interval = interval;
    }

    boolean matches(Map<String, Double> metrics) {
      return metrics.containsKey(metric) && interval.contains(metrics.get(metric));
    }
  }

  static class Branch {
    List<Condition> guard = new ArrayList<>();
    Node thenNode;
  }

  public enum AdvanceStatus {
    ADVANCED,
    DID_NOT_ADVANCE,
    TREE_ENDED
  }

  // --- Parsing ---
  private Node parseNode(JsonNode json) {
    Node node = new Node();

    if (json.has("actions")) {
      for (JsonNode a : json.get("actions")) {
        if(!a.get("value").isNull()) {
        	String param = CODE_TO_LONGNAME.get(a.get("param").asText());
            double value = a.get("value").asDouble();
            if (TREE_TO_SIM_CONVERSIONS.containsKey(param)) {
            	value *= TREE_TO_SIM_CONVERSIONS.get(param);
            }
            String op = a.get("op").asText();
            node.actions.add(new ActionSpec(op, param, value));
        }
      }
    }

    if (json.has("branches")) {
      for (JsonNode b : json.get("branches")) {
        Branch branch = new Branch();
        for (JsonNode g : b.get("guard")) {
          String metric = CODE_TO_LONGNAME.get(g.get("metric").asText());
          double low = g.get("interval").get("low").asDouble();
          double high = g.get("interval").get("high").asDouble();
          if (TREE_TO_SIM_CONVERSIONS.containsKey(metric)) {
        	  low  *= TREE_TO_SIM_CONVERSIONS.get(metric);
        	  high *= TREE_TO_SIM_CONVERSIONS.get(metric);
          }
          branch.guard.add(new Condition(metric, new Interval(low, high)));
        }
        branch.thenNode = parseNode(b.get("then"));
        node.branches.add(branch);
      }
    }

    if (json.has("else")) {
      node.elseNode = parseNode(json.get("else"));
    }

    return node;
  }

  // --- Cursor Methods ---
  public SEMechanicalVentilatorVolumeControl getCurrentSettings() {
    return currentSettings;
  }

  // --- Pending Conditions Reporter ---
  public String getPendingConditions() {
    StringBuilder sb = new StringBuilder();

    if (cursor.branches.isEmpty() && cursor.elseNode == null) {
      sb.append("(leaf node)");
      return sb.toString();
    }

    int branchIndex = 1;
    for (Branch b : cursor.branches) {
      sb.append("Branch ").append(branchIndex).append(": ");
      if (b.guard.isEmpty()) {
        sb.append("(no conditions)");
      } else {
        List<String> conds = new ArrayList<>();
        for (Condition c : b.guard) {
          conds.add(c.metric + " in [" + c.interval.low + ", " + c.interval.high + "]");
        }
        sb.append(String.join(" AND ", conds));
      }
      sb.append("\n");
      branchIndex++;
    }

    if (cursor.elseNode != null) {
      sb.append("else branch\n");
    }

    return sb.toString().trim();
  }

  public void applyCursorActions() {
    for (ActionSpec a : cursor.actions) {
        switch (a.param) {
          case "TidalVolume":
            int currentTV = (int) currentSettings.getTidalVolume().getValue(VolumeUnit.mL);
            if (a.op.equals("set")) currentSettings.getTidalVolume().setValue((int) a.value, VolumeUnit.mL);
            else if (a.op.equals("inc")) currentSettings.getTidalVolume().setValue(currentTV + (int) a.value, VolumeUnit.mL);
            else if (a.op.equals("dec")) currentSettings.getTidalVolume().setValue(currentTV - (int) a.value, VolumeUnit.mL);
            break;
          case "RespirationRate":
            int currentRR = (int) currentSettings.getRespirationRate().getValue(FrequencyUnit.Per_min);
            if (a.op.equals("set")) currentSettings.getRespirationRate().setValue((int) a.value, FrequencyUnit.Per_min);
            else if (a.op.equals("inc")) currentSettings.getRespirationRate().setValue(currentRR + (int) a.value, FrequencyUnit.Per_min);
            else if (a.op.equals("dec")) currentSettings.getRespirationRate().setValue(currentRR - (int) a.value, FrequencyUnit.Per_min);
            break;
          case "PositiveEndExpiratoryPressure":
            int currentPEEP = (int) currentSettings.getPositiveEndExpiratoryPressure().getValue(PressureUnit.cmH2O);
            if (a.op.equals("set")) currentSettings.getPositiveEndExpiratoryPressure().setValue((int) a.value, PressureUnit.cmH2O);
            else if (a.op.equals("inc")) currentSettings.getPositiveEndExpiratoryPressure().setValue(currentPEEP + (int) a.value, PressureUnit.cmH2O);
            else if (a.op.equals("dec")) currentSettings.getPositiveEndExpiratoryPressure().setValue(currentPEEP - (int) a.value, PressureUnit.cmH2O);
            break;
          case "FractionInspiredOxygen":
            double currentFiO2 = currentSettings.getFractionInspiredOxygen().getValue();
            if (a.op.equals("set")) currentSettings.getFractionInspiredOxygen().setValue(a.value);
            else if (a.op.equals("inc")) currentSettings.getFractionInspiredOxygen().setValue(currentFiO2 + a.value);
            else if (a.op.equals("dec")) currentSettings.getFractionInspiredOxygen().setValue(currentFiO2 - a.value);
            break;
        }
    }
  }

  public AdvanceStatus maybeAdvance(Map<String, Double> metrics) {
    if (cursor.branches.isEmpty() && cursor.elseNode == null) {
      return AdvanceStatus.TREE_ENDED;
    }

    for (Branch b : cursor.branches) {
      boolean allMatch = true;
      for (Condition c : b.guard) {
        if (!c.matches(metrics)) {
          allMatch = false;
          break;
        }
      }
      if (allMatch) {
	    // Advance cursor of current node and apply actions
        cursor = b.thenNode;
        applyCursorActions();
        return AdvanceStatus.ADVANCED;
      }
    }

    if (cursor.elseNode != null) {
	    // Advance cursor of current node and apply actions
    	cursor = cursor.elseNode;
      applyCursorActions();
      return AdvanceStatus.ADVANCED;
    }

    return AdvanceStatus.DID_NOT_ADVANCE;
  }

  public void reset() {
    cursor = root;
  }

  // --- State Reader ---
  public static Map<String, Double> readState(String filePath) throws IOException {
    Map<String, Double> metrics = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || !line.contains(":")) continue;
        String[] parts = line.split(":", 2);
        if (parts.length != 2) continue;
        try {
          double value = Double.parseDouble(parts[1].trim());
          metrics.put(parts[0].trim(), value);
        } catch (NumberFormatException ignored) {}
      }
    }
    return metrics;
  }

  public String printTree() {
	  StringBuilder sb = new StringBuilder();
	  Map<Node, Integer> ids = new HashMap<>();
	  assignIds(root, ids, new int[]{0});  // assign unique IDs

	  printNodeStructure(root, ids, sb, new HashSet<>());

	  return sb.toString();
	}

	// --- Node structure ---
  private void printNodeStructure(Node node, Map<Node, Integer> ids, StringBuilder sb, Set<Node> visited) {
	  if (node == null || visited.contains(node)) return;
	  visited.add(node);

	  int id = ids.get(node);
	  sb.append("Node ").append(id).append("\n");

	  // Print actions for this node
	  sb.append(printNode(node));

	  // Print all outgoing branches
	  for (Branch b : node.branches) {
	    int targetId = ids.get(b.thenNode);
	    sb.append("  -> Branch to Node ").append(targetId).append(" if ");
	    if (b.guard.isEmpty()) {
	      sb.append("[no conditions]");
	    } else {
	      for (int i = 0; i < b.guard.size(); i++) {
	        Condition c = b.guard.get(i);
	        sb.append(c.metric).append(" in [")
	          .append(c.interval.low).append(", ")
	          .append(c.interval.high).append("]");
	        if (i < b.guard.size() - 1) sb.append(" AND ");
	      }
	    }
	    sb.append("\n");
	  }

	  // Print ELSE if exists
	  if (node.elseNode != null) {
	    int elseId = ids.get(node.elseNode);
	    sb.append("  -> ELSE to Node ").append(elseId).append("\n");
	  }

	  // Recurse into all children (branches + else)
	  for (Branch b : node.branches) {
	    printNodeStructure(b.thenNode, ids, sb, visited);
	  }
	  if (node.elseNode != null) {
	    printNodeStructure(node.elseNode, ids, sb, visited);
	  }
	}

	private void assignIds(Node node, Map<Node, Integer> ids, int[] counter) {
	  if (node == null || ids.containsKey(node)) return;
	  ids.put(node, counter[0]++);
	  for (Branch b : node.branches) {
	    assignIds(b.thenNode, ids, counter);
	  }
	  assignIds(node.elseNode, ids, counter);
	}

	private String printNode(Node no) {
	  if (no == null || no.actions.isEmpty()) {
	    return "[no actions]";
	  }

	  StringBuilder sb = new StringBuilder();
	  sb.append("Actions:\n");

	  for (ActionSpec a : no.actions) {
	    sb.append("  - ").append(a.op)
	      .append(" ").append(a.param)
	      .append(" ").append(a.value)
	      .append("\n");
	  }

	  return sb.toString();
	}
	
	public String printCurrentNode() {
		return printNode(cursor);
	}
	
}
