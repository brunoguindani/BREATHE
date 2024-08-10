/* Distributed under the Apache License, Version 2.0.
   See accompanying NOTICE file for details.*/

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Events.eEvent;
import com.kitware.pulse.cdm.bind.Patient.PatientData.eSex;
import com.kitware.pulse.cdm.bind.PatientActions.HemorrhageData.eCompartment;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.engine.SEActiveEvent;
import com.kitware.pulse.cdm.engine.SEEventHandler;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.cdm.patient.actions.SEHemorrhage;
import com.kitware.pulse.cdm.patient.actions.SESubstanceCompoundInfusion;
import com.kitware.pulse.cdm.patient.assessments.SECompleteBloodCount;
import com.kitware.pulse.cdm.patient.conditions.SEChronicAnemia;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.LengthUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.MassUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PowerUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.cdm.properties.SEScalarTime;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.utilities.LogListener;
import com.kitware.pulse.utilities.JNIBridge;

/**
 * How to use the Java interface to the Pulse Physiology Engine
 */
public class Main
{
  // Create a listener that will catch any messages logged in C++
  // This class will take the messages and add them to the log created in Java
  protected static class MyListener extends LogListener
  {
    protected MyListener()
    {
      super();
      // Since we are just pushing this data into the log
      // We set listen to false, when you create a LogListener
      // is will automatically register itself with the static Log class,
      // and whenever a Log even class is called, this will be called
      // and since this just calls the Log to log you will get into a recursive infinite loop.
      // This is just because I use the LogListener interface to listen to any log messages coming
      // from C++. Technically it is a LogListener as it is 'listening' to log events in C++.
      // At any rate the Java side will receive log events from C++, and you create your own 
      // LogListener so you can do as you see fit with those messages. BUT if you do want to 
      // push those messages to the Java Log, you will need this class to not listen so you
      // don't get into an infinite recursive loop
      listen(false);
    }    
    @Override public void handleDebug(String msg) { Log.debug(msg); }
    @Override public void handleInfo(String msg)  { Log.info(msg); }
    @Override public void handleWarn(String msg)  { Log.warn(msg); }
    @Override public void handleError(String msg) { Log.error(msg); }
    @Override public void handleFatal(String msg) { Log.fatal(msg); }
  }

  // Another listener example
  // Create a listener that will catch any messages logged in C++
  // This class will take the messages and forward them to slf4j,
  // so they can be logged with whatever logger a user may want to use
  protected static class ForwardToSlf4jListener extends LogListener
  {
    private static final Logger LOG = LoggerFactory.getLogger("CppPulseEngine"); // select a name that makes it clear where these logs are coming from

    @Override public void handleDebug(String msg) { LOG.debug(msg); }
    @Override public void handleInfo(String msg)  { LOG.info(msg); }
    @Override public void handleWarn(String msg)  { LOG.warn(msg); }
    @Override public void handleError(String msg) { LOG.error(msg); }
    @Override public void handleFatal(String msg) { LOG.error("FATAL: {}", msg); }
  }

  protected static class MyEventHandler implements SEEventHandler
  {
    @Override
    public void handleEvent(eEvent e, boolean active, SEScalarTime time)
    {
      // Here is how you test against an event of interest and do something
      if(e == eEvent.CardiacArrest && active)
        Log.info("Patient is in cardiac arrest "+time);
    }
  }

  public enum InitializationType { PatientObject, PatientFile, StateFile };

  public static void main(String[] args)
  {
    example();
  }

  public static void example()
  {
    // As Pulse is a C++ library, a JNI bridge is necessary to pass data between C++ and Java
    // Pulse is contained in a single dynamic/shared library, that will need to be loaded into the JVM
    // By default, an instantiation of the PulseEngine will look for and load the Pulse library in the java.library.path
    System.out.println("Using Pulse version " + PulseEngine.version() + "-" + PulseEngine.hash());

    // If you are using this Pulse API for other things (such as Scalars) (not instantiating an engine right away),
    // you can manually load the native Pulse library like so
    JNIBridge.initialize();
    // If the library is not found in java.library.path, it will look in a the user.dir
    // if you would like to specify the directory where it should look, you can with this call:
    //JNIBridge.initialize("/path/to/pulse/library");
    // If you would like to run against debug library (PulseJNId), you can do initialize like this
    //JNIBridge.initializeDebug();

    // Create a Pulse Engine
    PulseEngine pe = new PulseEngine();

    // By default, no log file is written
    pe.setLogFilename("./test_results/howto/HowTo_EngineUse.java.log");

    // The Pulse API provides a Log class
    // It can be used to write to easily forward messages to the console and any LogListener
    // So messages from this example, and the Pulse Engine will both goto the same slf4j logger
    // If you use the Log (like this example), it by default outputs logged messages to the console
    // We are using a slf4j listener that is also going to write to the console.
    // We are going to turn off console writing by the Log class, and it will only
    // direct log messages to our Listener, which will write them to the console
    Log.output2Console = false;

    // Create a listener that will get any log messages that come from the engine.
    // (Info, Warnings, Errors, Fatal Errors)

    // This forwards log messages to an SLF4J logger
    // Note that pulse comes with the logback logger so slf4j will print something for this example
    // If you would rather use a different logger, you can delete the logback jars and substitute something else
    pe.setLogListener(new ForwardToSlf4jListener());

    // If you want to know whenever the engine enters and exits a particular state
    pe.setEventHandler(new MyEventHandler());

    // Here is how to specify the data to get back from the engine
    SEDataRequestManager dataRequests = new SEDataRequestManager();
    dataRequests.createPhysiologyDataRequest("HeartRate", FrequencyUnit.Per_min);
    dataRequests.createPhysiologyDataRequest("TotalLungVolume", VolumeUnit.mL);
    dataRequests.createPhysiologyDataRequest("RespirationRate", FrequencyUnit.Per_min);
    dataRequests.createPhysiologyDataRequest("BloodVolume", VolumeUnit.mL);
    dataRequests.createPatientDataRequest("BasalMetabolicRate",PowerUnit.kcal_Per_day);
    // In addition to getting this data back via this API
    // You can have Pulse write the data you have requested to a CSV file
    dataRequests.setResultsFilename("./test_results/HowTo_EngineUse.java.csv");
    // Create a reference to a double[] that will contain the data returned from Pulse
    List<Double> dataValues;
    // data_values[0] is ALWAYS the simulation time in seconds
    // The rest of the data values are in order of the data_requests list provided

    InitializationType initType = InitializationType.StateFile;
    // INITIALIZE THE ENGINE WITH A PATIENT
    switch(initType)
    {
      case PatientObject:
      {
        // Set up your patient
        // This is the minimum and recommended data to set for a patient
        // But you can get more complicated, consult our Patient Variability Documentation for more details
        SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
        SEPatient patient = patient_configuration.getPatient();
        patient.setName("Standard");
        patient.setSex(eSex.Male);
        patient.getAge().setValue(44,TimeUnit.yr);
        patient.getWeight().setValue(170,MassUnit.lb);
        patient.getHeight().setValue(71,LengthUnit.in);
        patient.getBodyFatFraction().setValue(0.21);
        patient.getHeartRateBaseline().setValue(72,FrequencyUnit.Per_min);
        patient.getDiastolicArterialPressureBaseline().setValue(72.,PressureUnit.mmHg);
        patient.getSystolicArterialPressureBaseline().setValue(114.,PressureUnit.mmHg);
        patient.getRespirationRateBaseline().setValue(16,FrequencyUnit.Per_min);
        patient.getBasalMetabolicRate().setValue(1600,PowerUnit.kcal_Per_day);
        

        // Optionally add conditions to the patient_configuration
        // Optionally, specify where you would like Pulse to look for files it needs on disk
        patient_configuration.setDataRootDir("./");// Default is "./"


        // If I wanted to make set a condition on the patient
        // Note that while you can have multiple conditions on a patient
        // It is more than likely not tested and the engine may or may not converge
        List<SECondition> conditions = new ArrayList<>();
        SEChronicAnemia anemia = new SEChronicAnemia();// One of these days I will prefix these with SE...
        anemia.getReductionFactor().setValue(0.3);
        conditions.add(anemia);

        // Initialize an engine 
        // Optionally, initializeEngine can take in a directory where Pulse looks for its required data files on disk
        pe.initializeEngine(patient_configuration, dataRequests);
        // This method will block while the engine stabilizes to meet the defined patient parameters
        break;
      }
      case PatientFile:
      {
        SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
        patient_configuration.setPatientFile("./patients/StandardMale.json");
        // Optionally add conditions to the patient_configuration

        // Allocate an engine
        pe.initializeEngine(patient_configuration, dataRequests);
        // This method will block while the engine stabilizes to meet the defined patient parameters
        break;
      }
      case StateFile:
      {
        pe.serializeFromFile("./states/StandardMale@0s.json", dataRequests);
        // This method method sets the engine to the provided state instantaneously and you are ready to process actions/advance time
        break;
      }
    }

    // You can get the initial patient at any time
    // But it will not change, so once is good
    // All values will be set to what the engine stabilized to
    SEPatient initialPatient = new SEPatient();
    pe.getInitialPatient(initialPatient);
    Log.info("Sex " + initialPatient.getSex());
    Log.info("Age(yr) " + initialPatient.getAge().getValue(TimeUnit.yr));
    Log.info("Height(cm) " + initialPatient.getHeight().getValue(LengthUnit.cm));
    Log.info("Weight(lb) " + initialPatient.getWeight().getValue(MassUnit.lb));
    Log.info("BasalMetabolicRate(kcal) " +  initialPatient.getBasalMetabolicRate().getValue(PowerUnit.kcal_Per_day));
    
    // You can pull the condition manager from the engine to see what, if any, conditions are applied to this patient
    // Note this will not change, its based on how the patient was initially created
    List<SECondition> conditions = new ArrayList<SECondition>();
    pe.getConditions(conditions);
    for(SECondition any : conditions)
    {
      Log.info(any.toString());
    }

    // Now we can start telling the engine what to do
    // All the same concepts apply from the C++ HowTo files, so look there if you want to see more examples

    if(!pe.advanceTime()) // Advance one engine time step
    {
      Log.info("Something bad happened");
      return;
    }

    dataValues = pe.pullData();
    dataRequests.writeData(dataValues);
    
    SEScalarTime time = new SEScalarTime(0, TimeUnit.s);
    time.setValue(1, TimeUnit.s);
    Log.info("Advancing "+time+"...");
    if(!pe.advanceTime(time)) // Simulate one second
    {
      Log.info("Something bad happened");
      return;
    }

    dataValues = pe.pullData();
    dataRequests.writeData(dataValues);

    // Let's do something to the patient, you can either send actions over one at a time, or pass in a List<SEAction>
    
    SEHemorrhage h = new SEHemorrhage();
    h.setCompartment(eCompartment.RightLeg);
    h.getSeverity().setValue(0.8);
    
    // Optionally, You can set the flow rate of the hemorrhage,
    // This needs to be provided the proper flow rate associated with the anatomy
    // This is implemented as a flow source, this rate will be constant, and will not be affected by dropping blood pressures
    // It is intended to interact with sensors or with something continuously monitoring physiology and updating the flow
    //h.getFlowRate().setValue(75,VolumePerTimeUnit.mL_Per_min);// Change this to 750 if you want to see how engine failures are handled!!
    if(!pe.processAction(h))
    {
      Log.error("Engine was unable to process requested actions");
      return;
    }
    
    // At any point you can also pull the current active actions from the engine
    Log.info("Here are the current active actions in the engine");
    List<SEAction> actions = new ArrayList<SEAction>();
    pe.getActiveActions(actions);
    for(SEAction any : actions)
    {
      Log.info(any.toString());
    }

    for(int i=1; i<=2; i++)
    {
      time.setValue(1,TimeUnit.min);
      Log.info("Advancing "+time+"...");
      if(!pe.advanceTime(time)) // Simulate one minute
      {
        Log.error("Engine was unable to stay within modeling parameters with requested actions");
        return;
      }
      dataValues = pe.pullData();
      dataRequests.writeData(dataValues);
    }

    /*
    // Stop the hemorrhage
    h.getSeverity().setValue(0);
    //h.getFlowRate().setValue(0,VolumePerTimeUnit.mL_Per_min);
    if(!pe.processAction(h))
    {
      Log.error("Engine was unable to process requested actions");
      return;
    }*/

    for(int i=1; i<=1; i++)
    {
      time.setValue(1,TimeUnit.min);
      Log.info("Advancing "+time+"...");
      if(!pe.advanceTime(time)) // Simulate one minute
      {
        Log.error("Engine was unable to stay within modeling parameters with requested actions");
        return;
      }
      dataValues = pe.pullData();
      dataRequests.writeData(dataValues);
    }

    // Infuse some fluids
    SESubstanceCompoundInfusion ivFluids= new SESubstanceCompoundInfusion();
    ivFluids.setSubstanceCompound("Saline");
    ivFluids.getBagVolume().setValue(500,VolumeUnit.mL);
    ivFluids.getRate().setValue(100,VolumePerTimeUnit.mL_Per_min);
    if(!pe.processAction(ivFluids))
    {
      Log.error("Engine was unable to process requested actions");
      return;
    }

    for(int i=1; i<=5; i++)
    {
      time.setValue(1,TimeUnit.min);
      Log.info("Advancing "+time+"...");
      if(!pe.advanceTime(time)) // Simulate one minute
      {
        Log.error("Engine was unable to stay within modeling parameters with requested actions");
        return;
      }
      dataValues = pe.pullData();
      dataRequests.writeData(dataValues);
    }

    // You can explicitly check if the patient is in a specific state/event
    List<SEActiveEvent> activeEvents = new ArrayList<SEActiveEvent>();
    pe.getActiveEvents(activeEvents);
    for(SEActiveEvent ae : activeEvents)
      Log.info(ae.type+" has been active for "+ae.duration);

    // Let's get an assessment from the engine
    // Assessments can involve extra calculation to generate the data necessary for the specified assessment
    SECompleteBloodCount cbc = new SECompleteBloodCount();
    if(!pe.getPatientAssessment(cbc))
      Log.error("Failed to get patient assessment");
    Log.info("Red Blood Count "+cbc.getRedBloodCellCount());
    Log.info("White Blood Count "+cbc.getWhiteBloodCellCount());
    
    // Release any files
    // We can keep using this engine if we want
    // We just need to reinitialize it
    pe.clear();

    // Be nice to your memory and deallocate the native memory associated with this engine if you are done with it
    pe.cleanUp();
    // Note you can now run a static (scenario) or another dynamic engine with the pe object, it will allocate and manage a new C++ engine 
  }
}
