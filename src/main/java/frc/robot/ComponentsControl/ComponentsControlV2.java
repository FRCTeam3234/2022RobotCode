package frc.robot.ComponentsControl;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax.ControlType;
import frc.robot.Components;
import frc.robot.ControlInputs;
import frc.robot.SensorInputs;

public class ComponentsControlV2 extends ComponentsControl {

    @Override
    public void runComponents(Components components, ControlInputs controlInputs, SensorInputs sensorInputs) 
    {    
        Double intakeRollerMotorPower = controlInputs.runIntake ? 1.0 : 0.0;
        Double intakeBeltMotorPower = 0.0;
        Double transferBeltMotorPower = 0.0;

        if (controlInputs.runIntake)
        {
            if (!sensorInputs.upperBallPresent)
            {
                intakeBeltMotorPower = 1.0;
                transferBeltMotorPower = 0.25;

            }
            else
            {
                if (!sensorInputs.lowerBallPresent)
                {
                    intakeBeltMotorPower = 1.0;
                }
            }
        }

        if ( controlInputs.shootLow || controlInputs.shootHigh)
        {
            final double lowShotTargetVelocity = 3000;
            final double highShotTargetVelocity = 5000;
            double targetVelocity = 0;
            if (controlInputs.shootLow)
            {
                targetVelocity = lowShotTargetVelocity;
            }
            if (controlInputs.shootHigh)
            {
                targetVelocity = highShotTargetVelocity;
            }
            
            if (!shotInProgress)
            {
                components.shooterMotorPIDController.setReference(targetVelocity, ControlType.kVelocity, 0);
            }
            else
            {
                double motorVelocity = components.shooterMotorEncoder.getVelocity();
                double velocityTolerance = 5;
                if ( (motorVelocity > targetVelocity - velocityTolerance)  &&
                    (motorVelocity < targetVelocity + velocityTolerance) )
                {
                    transferBeltMotorPower = 1.0;
                }
            }
        }
        else
        {
            if (shotInProgress)
            {
                shotInProgress = false;
            }
            components.shooterMotorPIDController.setReference(0, ControlType.kVelocity, 0);
            intakeBeltMotorPower = 0.0;
        }
        components.intakeArmControl.set(controlInputs.deployIntake);

        components.intakeRollerMotor.set(intakeRollerMotorPower);
        components.intakeBeltMotor.set(ControlMode.PercentOutput, intakeBeltMotorPower);
        components.transferBeltMotor.set(ControlMode.PercentOutput, transferBeltMotorPower);
    }
    
}
