package frc.robot.ComponentsControl;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax.ControlType;
import frc.robot.Components;
import frc.robot.ControlInputs;
import frc.robot.SensorInputs;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ComponentsControlV2 extends ComponentsControl {

    @Override
    public void runComponents(Components components, ControlInputs controlInputs, SensorInputs sensorInputs) 
    {    
        Double intakeRollerMotorPower = controlInputs.runIntake ? -1.0 : 0.0;
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
                    intakeBeltMotorPower = 0.4;
                }
            }
        }

        if ( controlInputs.shootLow || controlInputs.shootHigh)
        {
            final double lowShotTargetVelocity = 3000;
            final double highShotTargetVelocity = 6500;
            double targetVelocity = 0;
            double firstPIDLoopVelocityTargetOffset = 50;
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
                components.shooterMotorPIDController.setReference(
                    targetVelocity-firstPIDLoopVelocityTargetOffset, ControlType.kVelocity, 0);
                shotInProgress = true;
                firstShooterSpinupCompleted = false;
            }
            else
            {
                final double motorVelocity = components.shooterMotorEncoder.getVelocity();
                SmartDashboard.putNumber("Shooter Motor Vel", motorVelocity);
                if (motorVelocity >= ( targetVelocity - firstPIDLoopVelocityTargetOffset) )
                {
                    firstShooterSpinupCompleted = true;
                }
                if (firstShooterSpinupCompleted)
                {
                    if (!secondShooterSpinupInProcess)
                    {
                        components.shooterMotorPIDController.setReference(targetVelocity, ControlType.kVelocity, 2);
                        secondShooterSpinupInProcess = true;
                    }
                }
                if (secondShooterSpinupInProcess)
                {
                    double targetVelocityTolerance = 20;
                    int cycleCountThreshold = 10;
                    if ( (motorVelocity >= targetVelocity - targetVelocityTolerance) && 
                         (motorVelocity <= targetVelocity + targetVelocityTolerance) )
                    {
                        if (shooterVelWithinToleranceCycleCount >= cycleCountThreshold)
                        {
                            upperBeltPowerAccum = upperBeltPowerAccum+0.05;
                            transferBeltMotorPower = Math.min(upperBeltPowerAccum,1.0);
                            if (!sensorInputs.upperBallPresent)
                            {
                                lowerBeltPowerAccum = lowerBeltPowerAccum+0.05;
                                intakeBeltMotorPower = Math.min(lowerBeltPowerAccum,1.0);
                            }
                        }
                        else
                        {
                            shooterVelWithinToleranceCycleCount++;
                            lowerBeltPowerAccum = 0.0;
                            upperBeltPowerAccum = 0.0;
                        }
                    }
                    else
                    {
                        SmartDashboard.putBoolean("DB/LED 0", true);
                        shooterVelWithinToleranceCycleCount = 0;
                    }
                }
            }
        }
        else
        {
            SmartDashboard.putBoolean("DB/LED 0", false);
            if (shotInProgress)
            {
                shotInProgress = false;
                firstShooterSpinupCompleted = false;
                secondShooterSpinupInProcess = false;
                shooterVelWithinToleranceCycleCount = 0;
                upperBeltPowerAccum = 0.0;
                lowerBeltPowerAccum = 0.0;
            }
            components.shooterMotor.set(0.0);
        }
        components.intakeArmControl.set(controlInputs.deployIntake);

        components.intakeRollerMotor.set(intakeRollerMotorPower);
        components.intakeBeltMotor.set(ControlMode.PercentOutput, intakeBeltMotorPower);
        components.transferBeltMotor.set(ControlMode.PercentOutput, transferBeltMotorPower);
    }
    
}
