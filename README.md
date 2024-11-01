# BREATHE – A Respiratory System Simulator for Mechanical Ventilator Testing and Training

BREATHE is a simulation software based on [Kitware's Pulse Physiology Engine](https://gitlab.kitware.com/physiology/engine), designed to test mechanical ventilators and provide training for students on how to operate these devices.  
The application includes both a Swing interface for testing external ventilators and a web-based interface.

## Components Overview
- **Engine**: The core simulation engine responsible for simulation.
- **Swing Interface**: Built with Java Swing for local testing of external ventilators.
- **Web Interface**: A web app designed specifically for training purposes.
- **ZeroMQ Client**: An external client simulating ventilator behavior, enabling seamless communication between BREATHE and external ventilator systems.

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/GionathaPirola/BREATHE.git
   cd BREATHE
   
2. Import the Maven dependencies for each project component (engine, swingGUI, web, zeroMQ) by running `mvn install`
3. Launch the application by running the main class of either the Swing or WebApp project, depending on the interface you wish to use.  
If you successfully launch the web app, you can access it at [http://localhost:8080](http://localhost:8080).


## Swing Interface Usage

The view is composed of the following elements:

- **Patient**: Displays all information about the patient, including name, age, weight, etc.
- **Conditions**: Shows all conditions applied to the patient at the start of the simulation.
- **Actions**: Lists all actions that can be applied to the patient during the simulation after it has started.
- **Ventilators**: Contains all ventilators that can be connected during the simulation. In addition to the standard ventilator, there is also a ZeroMQ server representing an external ventilator. When connected, the server will open and search for a connection (see ZeroMQ Client usage).
- **Output Panel**: Displays all output information related to the running simulation (e.g., ECG, current pressure, etc.).
- **Log Output**: Similar to the output panel, but provides the information about the simulation in written form.
- **Scenario**: This section allows you to create scenarios. By selecting a patient, you can add actions that will be applied at specific times during the simulation.

The simulation can be started using three methods:

1. **Standard Simulation**: Once all patient parameters and conditions are set, you can start the simulation. This process begins the stabilization of the patient, which may take a few minutes depending on the computer. After stabilization is complete, the patient will be exported to the `breathe.engine/states/` folder, and the simulation will start immediately.
   
2. **From File**: You can select a states file to start the simulation immediately. At any point during the simulation, you can export the current states and reload them in the future to resume from that point.

3. **From Scenario File**: You can choose a scenario file created from the scenario panel. The simulation will start immediately because scenario files use a states file.
