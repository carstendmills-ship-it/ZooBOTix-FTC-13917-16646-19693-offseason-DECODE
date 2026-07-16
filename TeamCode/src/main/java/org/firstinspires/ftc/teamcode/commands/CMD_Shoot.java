package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.subsystems.SUB_Intake;
import org.firstinspires.ftc.teamcode.subsystems.SUB_Kicker;
import org.firstinspires.ftc.teamcode.subsystems.SUB_Shooter;

public class CMD_Shoot extends SequentialCommandGroup {
    public CMD_Shoot(SUB_Shooter p_shooter, SUB_Kicker p_lift, SUB_Intake p_intake){
        addRequirements(p_shooter, p_intake);

        addCommands(
            new CMD_AdjustTargetVel(p_shooter)
            ,new InstantCommand(p_shooter::setStopperOpen)
            ,new CMD_GetShooterAtVelocity(p_shooter)
            ,new InstantCommand(p_lift::kickLeft)
            ,new WaitCommand(250)
            ,new InstantCommand(p_lift::homeLeft)
            ,new InstantCommand(p_lift::kickRight)
            ,new WaitCommand(250)
            ,new InstantCommand(p_lift::home)
            ,new InstantCommand(()-> p_intake.setMotorPower(Constants.IntakeConstants.kIntakeAuto))
            ,new WaitCommand(500)
            ,new InstantCommand(p_lift::kick)
            ,new WaitCommand(250)
            ,new InstantCommand(p_lift::home)
                ,new InstantCommand(p_shooter::setStopperClosed)
        );
    }
}
