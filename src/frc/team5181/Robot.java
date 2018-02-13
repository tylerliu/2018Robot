package frc.team5181;

import edu.wpi.first.wpilibj.IterativeRobot;
import frc.team5181.actuators.*;
import frc.team5181.autonomous.AutonChooser;
import frc.team5181.tasking.Task;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
final public class Robot extends IterativeRobot {
	private Task autonCommand;
	private static int rFactor = 1;
	private static double speedFactor = 1.0;
	private static boolean isRVSE = false; // Means "is Reverse Mode Triggered"
	private static boolean isSNP = false;  // Means "is Sniping Mode Triggered"
	private static boolean isSolenoidForward = true;
	private SolenoidControl intakeSoleniod;
	private MotorControl intakeLeftMotor;
	private MotorControl intakeRightMotor;
	private MotorControl shooterLeft;
	private MotorControl shooterRight;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {

		AutonChooser.chooserInit();
        DriveTrain.init(Statics.DRIVE_LF,Statics.DRIVE_LB,Statics.DRIVE_RF,Statics.DRIVE_RB);
        Gamepad.init(Statics.XBOX_CTRL);
        intakeSoleniod = new SolenoidControl(Statics.INTAKE_COMPRESSOR,Statics.INTAKE_SOLENOID_FORWARD,Statics.INTAKE_SOLENOID_REVERSE);
        intakeLeftMotor = new MotorControl(Statics.INTAKE_LEFT_MOTOR,MotorControl.Model.VICTOR_SP,true);
        intakeRightMotor = new MotorControl(Statics.INTAKE_RIGHT_MOTOR,MotorControl.Model.VICTOR_SP,false);
        shooterLeft = new MotorControl(Statics.SHOOTER_LEFT_MOTOR,MotorControl.Model.VICTOR_SP, false);
        shooterRight = new MotorControl(Statics.SHOOTER_RIGHT_MOTOR,MotorControl.Model.VICTOR_SP, true);
	}

	/**
	 *
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autonCommand = AutonChooser.getAutonCommand();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		autonCommand.nextStep();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

        Gamepad.updateStatus();

        /**
         * Reverse Gear Trigger (Button Y, AKA "Triangle" in Dualshock 4)
         */
        if(Gamepad.LB_state) {
            if(Gamepad.current.LB) isRVSE = !isRVSE;
            rFactor = isRVSE ? -1 : 1;
        }

        /**
         *  Sniping Mode (First introduced for Team 11319 in FTC 2017 Relic Recovery)
         *  Use Right Bumper for toggling
         */
        if(Gamepad.RB_state) {
            if(Gamepad.current.RB) isSNP = !isSNP;
            speedFactor = isSNP ? Statics.LOW_SPD_FACTOR : Statics.FULL_SPD_FACTOR;

            DriveTrain.updateSpeedLimit(speedFactor);
        }

		/**
		 * Soleniod Control using "A" button
		 */
		if(Gamepad.B_state) {
        	if(Gamepad.current.B) isSolenoidForward = !isSolenoidForward;

        	intakeSoleniod.move(isSolenoidForward, !isSolenoidForward);
		}

		if(Gamepad.X_state || Gamepad.Y_state) {

			intakeLeftMotor.move(Gamepad.current.X,Gamepad.current.Y);
			intakeRightMotor.move(Gamepad.current.X,Gamepad.current.Y);
		}

		if(Gamepad.A_state) {
			shooterLeft.move(Gamepad.A_state,false);
			shooterRight.move(Gamepad.A_state,false);
		}

        /**
         *  NFS Drive Control. (R2 for forward, L2 for back, Left Joystick for turning)
         */
        if(Gamepad.RT_state || Gamepad.LT_state || Gamepad.jLeftX_state) {
            DriveTrain.tankDrive(rFactor*Gamepad.current.jLeftX,rFactor*(Gamepad.current.RT-Gamepad.current.LT));
        }

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

