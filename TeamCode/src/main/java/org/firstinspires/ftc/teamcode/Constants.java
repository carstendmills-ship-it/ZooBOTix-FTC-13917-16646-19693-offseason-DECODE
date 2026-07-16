package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Vector2d;

public class Constants {
    public static final class ShooterConstants {
        public static final boolean kTuningMode = false;
        public static final double kFarVel = 0.69;
        public static final double kFarthestVel = 0.70 ;
        public static final double kCloseVel = .70;
        public static final double kMidFieldVel = 0.6;
        public static final double kPreRev = .45;

        public static final double kTolerance = 0.01;
        public static final double kMaxVel = 2100;
        public static final double kP = 10;
        public static final double kF = 0.88;
    }

    public static final class IntakeConstants {
        public static final double kIntakeOn = 1;
        public static final double kIntakeAuto = 0.9;
        public static final double kIntakeOff = 0;
        public static final double kIntakeReverse = -0.67;
    }

    public static final class KickConstants {
        public static final double kKickDur = 0.15;

        public static final double kKickRight = 25.0 / 300.0;
        public static final double kKickLeft = 30.0 / 300.0;

        public static final double kLevelRight = 10.0 / 300.0;
        public static final double kLevelLeft = 15.0 / 300.0;

        public static final double kHome = 0.0;
    }

    public static final class AutoAlignConstants{
        public static final double kMidRangeThreshold = 45;
        public static final double kDistanceThreshold = 90;
        public static final double kFarthestThreshold = 130;

        public static Vector2d kBlueGoalPose = new Vector2d(72, 67);
        public static Vector2d kRedGoalPose = new Vector2d(72, -67);
    }

    public static class SystemConstants{
        public static final int kSysUpdateRateHz = 10;
    }
}


