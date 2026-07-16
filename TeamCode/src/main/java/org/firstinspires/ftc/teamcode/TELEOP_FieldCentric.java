package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.*;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Teleop Red", group ="Teleop Red")
public class TELEOP_FieldCentric extends LinearOpMode {

     public RobotContainer m_robot;
     private GamepadEx m_driverOp;
     private GamepadEx m_toolOp;

     private static final ElapsedTime m_runTime = new ElapsedTime();

     public void initialize() {
          telemetry.clearAll();
          telemetry.addData("init complete", true);

          m_runTime.reset();
     }

     @Override
     public void runOpMode() throws InterruptedException {
          initializeSubsystems();

          while (!opModeIsActive() && !isStopRequested()) {
               telemetry.update();
          }

          m_runTime.reset();
          while (!isStopRequested() && opModeIsActive()) {
               m_robot.run(); // run the scheduler

               m_robot.drivetrain.update();
               Pose2d poseEstimate = m_robot.drivetrain.getPoseEstimate();
               telemetry.addData("ODM","x[%3.2f] y[%3.2f] heading(%3.2f)", poseEstimate.getX(),
                       poseEstimate.getY(), Math.toDegrees(poseEstimate.getHeading()));

               telemetry.addData("end heading", GlobalVariables.m_autoEndHeading);
               telemetry.update();
          }

          endOfOpMode();
          m_robot.reset();
     }

     public void endOfOpMode() {}

     public void initializeSubsystems() {
          m_robot = new RobotContainer(this);
          m_driverOp = new GamepadEx(gamepad1);
          m_toolOp = new GamepadEx(gamepad2);

          m_robot.drivetrain.setFieldCentric(true);
          m_robot.drivetrain.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
          m_robot.drivetrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
          m_robot.drivetrain.setDefaultCommand(new RR_MecanumDriveDefault(m_robot.drivetrain, m_driverOp,0.03));

         setSide();
         GlobalVariables.m_red = m_robot.getRedSide();

          if (!Constants.ShooterConstants.kTuningMode)
              m_robot.m_shooter.setDefaultCommand(new CMD_ShooterDefault(m_robot.m_shooter, m_robot.m_intake, m_robot.m_kicker));

          configureButtonBindings();
     }

     public void configureButtonBindings() {
         AddTriggerCommand(m_driverOp, GamepadKeys.Trigger.RIGHT_TRIGGER, new SequentialCommandGroup(
             new InstantCommand(()-> m_robot.m_shooter.setShootingVelocity(Constants.ShooterConstants.kPreRev))
             ,new CMD_AutoAlignCheck(m_robot.drivetrain, m_robot.m_vision)
             ,new CMD_Shoot(m_robot.m_shooter, m_robot.m_kicker, m_robot.m_intake)));

         AddTriggerToggleCommand(m_driverOp, GamepadKeys.Trigger.LEFT_TRIGGER,
              new InstantCommand(()-> m_robot.m_intake.setMotorPower(Constants.IntakeConstants.kIntakeOn))
                  .andThen(new InstantCommand(()-> m_robot.m_shooter.setStopperClosed()))
              ,new InstantCommand(()-> m_robot.m_intake.setMotorPower(Constants.IntakeConstants.kIntakeOff))
                 .andThen(new InstantCommand(()-> m_robot.m_shooter.setStopperOpen()))
         );

         AddButtonCommand(m_driverOp, GamepadKeys.Button.RIGHT_BUMPER,
             new CMD_ShootInterrupt(m_robot.drivetrain, m_robot.m_shooter, m_robot.m_vision, m_robot.m_intake));

         AddButtonToggleCommand(m_driverOp, GamepadKeys.Button.LEFT_BUMPER,
             new InstantCommand(()-> m_robot.m_intake.setMotorPower(Constants.IntakeConstants.kIntakeReverse))
             ,new InstantCommand(()-> m_robot.m_intake.setMotorPower(Constants.IntakeConstants.kIntakeOff)));

         AddButtonCommand(m_driverOp, GamepadKeys.Button.Y, new CMD_AlignTarget(m_robot.drivetrain, m_robot.m_vision));

         // Operator
         AddButtonCommand(m_driverOp, GamepadKeys.Button.START,
             new InstantCommand(()-> m_robot.drivetrain.setPoseEstimate(new Pose2d(0, 0, 0))));

         AddButtonToggleCommand(m_driverOp, GamepadKeys.Button.X,
             new InstantCommand(()-> m_robot.m_shooter.unjam())
             ,new InstantCommand(()-> m_robot.m_shooter.setShooterStop()));

         AddButtonCommand(m_driverOp, GamepadKeys.Button.A, new SequentialCommandGroup(
             new InstantCommand(()-> m_robot.m_shooter.setShootingVelocity(Constants.ShooterConstants.kMidFieldVel))
             ,new CMD_ShootAuto(m_robot.m_shooter, m_robot.m_kicker, m_robot.m_intake)
         ));

         AddButtonCommand(m_driverOp, GamepadKeys.Button.B, new SequentialCommandGroup(
                 new InstantCommand(()-> m_robot.m_shooter.setShootingVelocity(Constants.ShooterConstants.kFarVel))
                 ,new CMD_ShootAuto(m_robot.m_shooter, m_robot.m_kicker, m_robot.m_intake)
         ));

         AddButtonCommand(m_driverOp, GamepadKeys.Button.DPAD_UP, new SequentialCommandGroup(
                 new InstantCommand(()-> m_robot.m_shooter.setShootingVelocity(Constants.ShooterConstants.kFarthestVel))
                 ,new CMD_ShootAuto(m_robot.m_shooter, m_robot.m_kicker, m_robot.m_intake)
         ));
     }

     public void setSide() {
          m_robot.setRedSide();
     }

     public void AddButtonCommand(GamepadEx gamepad, GamepadKeys.Button button, Command command) {
          new GamepadButton(gamepad, button).whenPressed(command);
     }

     public void AddButtonToggleCommand(GamepadEx gamepad, GamepadKeys.Button button, Command onTrue, Command onFalse){
          new GamepadButton(gamepad, button).whenPressed(onTrue).whenReleased(onFalse);
     }

     public void AddTriggerCommand(GamepadEx gamepad, GamepadKeys.Trigger trigger, Command command){
          new Trigger(()-> gamepad.getTrigger(trigger) >= .5).whenActive(command);
     }

     public void AddTriggerToggleCommand(GamepadEx gamepad, GamepadKeys.Trigger trigger, Command onTrue, Command onFalse){
          new Trigger(()-> gamepad.getTrigger(trigger) >= .5).whenActive(onTrue).whenInactive(onFalse);
     }
}