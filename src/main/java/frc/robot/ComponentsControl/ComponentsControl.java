package frc.robot.ComponentsControl;

import frc.robot.Components;
import frc.robot.ControlInputs;
import frc.robot.SensorInputs;


public abstract class ComponentsControl {

    protected boolean shotInProgress = false;
    protected boolean firstShooterSpinupCompleted = false;
    protected boolean secondShooterSpinupInProcess = false;
    protected Integer shooterVelWithinToleranceCycleCount = 0;
    public abstract void runComponents(Components components, ControlInputs controlInputs, SensorInputs sensorInputs);
}
