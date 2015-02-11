
package org.usfirst.frc.team867.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.BuiltInAccelerometer; //allows use of accelerometer on RobotRio
import edu.wpi.first.wpilibj.Joystick; //creates joysticks (gamepad)
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; //allows output of data
import edu.wpi.first.wpilibj.Preferences; //allows for preferences table (setting values)
import edu.wpi.first.wpilibj.RobotDrive; //motor set up and control
import edu.wpi.first.wpilibj.Timer; //allows for timing (delay)

//image proccessing
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;
import edu.wpi.first.wpilibj.CameraServer;

//pneumatic imports
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

/**
 * 
 * @author Aaron
 * @author Andrew
 * @version 0.4
 * 
 * REMEMBER TO PLUG IN DRIVER JOYSTICK FIRST (PORT 0)
 * REBOOT ROBOTRIO AFTER CHANING PREFERENCES
 * 
 */

public class Robot extends IterativeRobot 
{
	RobotDrive myRobotForward; //robotdrive forward -- JTTR_BUG
	Joystick driverJoy; //driver gamepad
	BuiltInAccelerometer accel; // builtin accel
	Preferences prefs = Preferences.getInstance(); //preferences menu
	double driverJoyxaxis; // driver gamepad left x axis
	double driverJoyyaxis; // driver gamepad left y axis
	double driverJoyslow; // driver gamepad z axis (set speed)
	boolean driverJoygo; //safety trigger (1)
	boolean driverJoyccw; //pivot counter clockwise (4)
	boolean driverJoycw; //pivot clockwise (5)
	boolean driverJoyreverse; //reverse directions (forward is now backwards) (2)
	boolean driveReverse; //store reverse toggle
	double driveRotation; //rotation for the robot
	double driveSlow; //robot speed 
	Compressor compressor; //allows for compressor control
	Solenoid firstSol; //first solenoid
	Solenoid secondSol; //second solenoid
	Solenoid thirdSol; //third solenoid
	
	//vision 
	int camerasession;
	Image  dcamera;
	
	
    public void robotInit() //initialization code; period independent
    { 
    	//print preferences table
    	
    	//motor ports
    	prefs.putInt("MOTORfrontleft", 2);
    	prefs.putInt("MOTORrearleft", 1);
    	prefs.putInt("MOTORfrontright", 3);
    	prefs.putInt("MOTORrearright", 0);
    	
    	//driverjoy 
    	prefs.putInt("DRIVERreverse", 2);
    	prefs.putInt("DRIVERgo", 1);
    	prefs.putInt("DRIVERccw", 4);
    	prefs.putInt("DRIVERcw", 5);
    	
    	//pneumatics
    	prefs.putInt("SOLfirst", 0); //FIX DEFAULT VALUES
    	prefs.putInt("SOLsecond", 1);
    	prefs.putInt("SOLthird", 2);
    	
    	//initialize joysticks
    	driverJoy = new Joystick(0);
    	
    	//initialize joystick inputs
    	driverJoyccw = driverJoy.getRawButton(prefs.getInt("DRIVERccw", 4));
		driverJoycw = driverJoy.getRawButton(prefs.getInt("DRIVERcw", 5));
    	driverJoygo = driverJoy.getRawButton(prefs.getInt("DRIVERgo",1));
    	driverJoyreverse = driverJoy.getRawButton(prefs.getInt("DRIVERreverse", 2));
    	driverJoyxaxis = driverJoy.getRawAxis(0);
		driverJoyyaxis = driverJoy.getRawAxis(1);
		
    	//initialize motors
    	myRobotForward = new RobotDrive(prefs.getInt("frontleft", 2), prefs.getInt("rearleft", 1), prefs.getInt("frontright", 3), prefs.getInt("rearright", 0)); //frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor
    	
    	//initialize solenoids
    	firstSol = new Solenoid(prefs.getInt("SOLfirst", 0));
    	secondSol = new Solenoid(prefs.getInt("SOLsecond", 1));
    	thirdSol = new Solenoid(prefs.getInt("SOLthird", 2));
    	
    	//initialize compressor
    	compressor = new Compressor();
    	
    	//initialize accel   	   	
    	accel = new BuiltInAccelerometer();
    	
    	//initialize vision
    	dcamera = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	camerasession = NIVision.IMAQdxOpenCamera("cam0",NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(camerasession);
        
        
    }

    public void autonomousInit() //initialization code; autonomous
    {

    }
    
    public void autonomousPeriodic() //autonomous period (loops)
    {

    }

    public void teleopInit() //initialization code; teleop
    {
    	driveReverse=false;
    }
    
    public void teleopPeriodic() //teleoperated period (loops)
    {
    	//drive when go button is held
    	if(driverJoygo)
    	{
    		drive();
    	}
    	
    	//camera
    	NIVision.IMAQdxStartAcquisition(camerasession);
    	NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);
    	NIVision.IMAQdxGrab(camerasession, dcamera, 1);
        NIVision.imaqDrawShapeOnImage(dcamera, dcamera, rect,DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        CameraServer.getInstance().setImage(dcamera);
    	
    	
    }
    
    public void testPeriodic() //test period
    {
    	//left blank on purpose
    }
    
    private void drive()
    {
    	//"slow" factor
    	driverJoyslow = driverJoy.getRawAxis(2);
    	driveSlow = ((driverJoyslow + 1) * 4.5) + 1;
    	    	
    	//reverse toggle
    	if(driverJoyreverse)
    	{
    		driveReverse = !driveReverse;
    		Timer.delay(0.3);
    	}
    	
    	//rotation
		if(driverJoyccw)
		{
			driveRotation = -0.5;
		}
		else if(driverJoycw)
		{
			driveRotation = 0.5;
		}
		else
		{
			driveRotation = 0.0;
		}

    	//forward driving
    	if(!driveReverse)
    	{
   			myRobotForward.mecanumDrive_Cartesian(driverJoyxaxis / driveSlow , driverJoyyaxis / driveSlow, driveRotation, 0);    		
    	}
    	
    	//reverse driving
    	if(driveReverse)
    	{    		
    		myRobotForward.mecanumDrive_Cartesian( -1 * driverJoyxaxis / driveSlow, -1 * driverJoyyaxis / driveSlow, driveRotation, 0);
    	}
    }
}
