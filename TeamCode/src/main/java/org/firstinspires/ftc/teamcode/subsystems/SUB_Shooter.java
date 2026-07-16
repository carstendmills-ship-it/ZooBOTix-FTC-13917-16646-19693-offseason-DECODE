package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants;

@Config
public class SUB_Shooter extends SubsystemBase {
    private final LinearMotion m_shooter;
    private final FtcDashboard m_dashboard;
    private final OpMode m_opMode;
    
    public static double kP, kD, kF, vel = 0;
    private double lastP, lastD, lastF;

    private final Servo m_stopperLeft, m_stopperRight;

    public SUB_Shooter(OpMode p_opMode){
        m_opMode = p_opMode;

        m_stopperLeft = m_opMode.hardwareMap.get(Servo.class, "stopperLeft");
        m_stopperRight = m_opMode.hardwareMap.get(Servo.class, "stopperRight");

        m_stopperRight.setDirection(Servo.Direction.FORWARD);
        m_stopperLeft.setDirection(Servo.Direction.REVERSE);

        this.m_shooter = new LinearMotion(
                "shooter",
                new DcMotorEx[]{
                    m_opMode.hardwareMap.get(DcMotorEx.class,"shooterMotorLeft"),
                    m_opMode.hardwareMap.get(DcMotorEx.class, "shooterMotorRight")
                },
                new boolean[]{false, true},
                m_opMode.hardwareMap.get(DcMotorEx.class,"shooterMotorLeft"),
                false,
                Constants.ShooterConstants.kMaxVel,
                Constants.ShooterConstants.kP,
                0.0,
                0.0,
                Constants.ShooterConstants.kF
        );

        m_dashboard = FtcDashboard.getInstance();
        m_dashboard.setTelemetryTransmissionInterval(20);
    }

    public void periodic() {
        m_shooter.periodic();

        m_opMode.telemetry.addData("Target Vel", "%.0f RPM", m_shooter.getTargetVelocity());

        m_opMode.telemetry.addData("Vel", "%.0f RPM", m_shooter.getCurrentVelocityRaw());
        m_opMode.telemetry.addData("Power", "%.3f", m_shooter.getOutputPower());


        m_opMode.telemetry.addData("ready to launch?", isReadyToLaunch());

        if(Constants.ShooterConstants.kTuningMode){
            if(kP != lastP || kD != lastD || kF != lastF){
                m_shooter.setPIDF(kP, 0, kD, kF);

                lastP = kP;
                lastD = kD;
                lastF = kF;
            }

            m_shooter.setTargetVelocity(vel);
        }

        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Target Vel", m_shooter.getTargetVelocity());

        packet.put("Vel", m_shooter.getCurrentVelocity());
        packet.put("Output", m_shooter.getOutputPower());
        packet.put("RPM", m_shooter.getCurrentVelocityRaw());


        m_dashboard.sendTelemetryPacket(packet);
    }

    public void runPower(double power){
        m_shooter.runPower(power);
    }

    public void setShooterStop(){
        m_shooter.setMotorsStop();
    }

    public void unjam(){
        m_shooter.unjam();
    }

    public void setShootingVelocity(double velocity){
        m_shooter.setTargetVelocity(velocity);
    }

    public boolean isReadyToLaunch(){
        return m_shooter.getCurrentVelocity() >= (m_shooter.getTargetVelocity() - Constants.ShooterConstants.kTolerance);
    }

    public void setStopperClosed(){
        m_stopperLeft.setPosition(250.0/300.0);
        m_stopperRight.setPosition(250.0/300.0);
    }

    public void setStopperOpen(){
        m_stopperLeft.setPosition(0/300.0);
        m_stopperRight.setPosition(0/300.0);
    }
}