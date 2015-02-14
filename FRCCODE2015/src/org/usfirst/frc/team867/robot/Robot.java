
package org.usfirst.frc.team867.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.BuiltInAccelerometer; //allows use of accelerometer on RobotRio
import edu.wpi.first.wpilibj.Joystick; //creates joysticks (gamepad)
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; //allows output of data
import edu.wpi.first.wpilibj.Preferences; //allows for preferences table (setting values)
import edu.wpi.first.wpilibj.RobotDrive; //motor set up and control
import edu.wpi.first.wpilibj.Timer; //allows for timing (delay)
import edu.wpi.first.wpilibj.Jaguar; //allows control of the arm and winch motors

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
 * @version 0.6
 * 
 * REMEMBER TO PLUG IN DRIVER JOYSTICK FIRST (PORT 0)
 * 
 */

public class Robot extends IterativeRobot 
{
	//robot variables
	RobotDrive myRobotForward; //robotdrive forward -- JTTR_BUG
	boolean driveReverse; //store reverse toggle
	double driveRotation; //rotation for the robot
	double driveSlow; //robot speed
	boolean compressorstate; //whether or not compressor is on
	boolean clampstate; //whether or not the lift-arms are clamped
	
	//create independent motors controllers
	Jaguar extruder;
	Jaguar winch;
	Jaguar miniwheels;
	
	//driver controls
	Joystick driverJoy; //driver gamepad
	double driverJoyxaxis; // driver gamepad left x axis
	double driverJoyyaxis; // driver gamepad left y axis
	double driverJoyslow; // driver gamepad z axis (set speed)
	boolean driverJoygo; //safety trigger (1)
	boolean driverJoyccw; //pivot counter clockwise (4)
	boolean driverJoycw; //pivot clockwise (5)
	boolean driverJoyreverse; //reverse directions (forward is now backwards) (2)
	
	//manipulator controls
	Joystick manipJoy;
	double manipJoyLEFTxaxis; // manipulator gamepad left x axis
	double manipJoyLEFTyaxis; // manipulator gamepad left y axis
	double manipJoyRIGHTxaxis; // manipulator gamepad right x axis
	double manipJoyRIGHTyaxis; // manipulator gamepad right y axis
	boolean manipJoycompressor; //toggles compressor (8)
	boolean manipJoyEXTRcylinder; //activates extruder "pusher"
	boolean manipJoyEXTRout; //noodle out
	boolean manipJoyEXTRin; //noodle in
	boolean manipJoyLIFTcylinder; //clamp lift
	boolean manipJoypurge1; //empty tanks (open exposed solenoid)
	boolean manipJoypurge2; //empty tanks (open exposed solenoid)
	
	
	BuiltInAccelerometer accel; // builtin accel
	
	Preferences prefs = Preferences.getInstance(); //preferences menu
	 
	Compressor compressor; //allows for compressor control
	Solenoid LIFTextendSol; //first solenoid
	Solenoid LIFTretractSol; //second solenoid
	Solenoid EXTRextendSol; //third solenoid
	Solenoid EXTRretractSol; //fourth solenoid
	Solenoid purgeSol; //disconnected solenoid (to release air)
	
	//vision 
	int camerasession;
	Image  dcamera;
	
	
    public void robotInit() //initialization code; period independent
    {     	  	
    	//initialize joysticks
    	driverJoy = new Joystick(0);
    	manipJoy = new Joystick(1);
    	
    	//initialize joystick inputs
    	driverJoyccw = driverJoy.getRawButton(4);
		driverJoycw = driverJoy.getRawButton(5);
    	driverJoygo = driverJoy.getRawButton(1);
    	driverJoyreverse = driverJoy.getRawButton(2);
    	driverJoyxaxis = driverJoy.getRawAxis(0);
		driverJoyyaxis = driverJoy.getRawAxis(1);
		
		manipJoyLEFTxaxis = manipJoy.getRawAxis(0);
		manipJoyLEFTyaxis = manipJoy.getRawAxis(1);
		manipJoyRIGHTxaxis = manipJoy.getRawAxis(4);
		manipJoyRIGHTyaxis = manipJoy.getRawAxis(5);
		manipJoycompressor = manipJoy.getRawButton(8);
		manipJoyEXTRcylinder = manipJoy.getRawButton(6);
		manipJoyEXTRout = manipJoy.getRawButton(2); 
		manipJoyEXTRin = manipJoy.getRawButton(1);
		manipJoyLIFTcylinder  = manipJoy.getRawButton(5); 
		manipJoypurge1 = manipJoy.getRawButton(4); 
		manipJoypurge1 = manipJoy.getRawButton(3);
		
		
    	//initialize motors
    	myRobotForward = new RobotDrive(2, 1, 3, 0); //frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor
    	
    	//initialize solenoids
    	LIFTextendSol = new Solenoid(0);
    	LIFTretractSol = new Solenoid(1);
    	EXTRextendSol = new Solenoid(2);
    	EXTRretractSol = new Solenoid(3);
    	purgeSol = new Solenoid(4);
    	
    	
    	//initialize compressor
    	compressor = new Compressor();
    	
    	//initialize accel   	   	
    	accel = new BuiltInAccelerometer();
    	
    	//initialize vision
    	dcamera = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	camerasession = NIVision.IMAQdxOpenCamera("cam0",NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(camerasession);
        
        //initialize independent motors
        extruder = new Jaguar(4);
        miniwheels = new Jaguar(5);
        winch = new Jaguar(6);
       
        
    }

    public void autonomousInit() //initialization code; autonomous
    {

    }
    
    public void autonomousPeriodic() //autonomous period (loops)
    {

    }

    public void teleopInit() //initialization code; teleop
    {
    	driveReverse = false;
    	compressorstate = false;
    	clampstate = false;
    }
    
    public void teleopPeriodic() //teleoperated period (loops)
    {
    	//drive when go button is held
    	if(driverJoygo)
    	{
    		drive();
    	}
    	
    	manipulate();
    	
    	    	
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
 
    private void manipulate()
    {
    	//code to toggle variable
    	if (manipJoycompressor)
    	{
    		compressorstate = !compressorstate;
    		Timer.delay(0.3);
    	}
    	
    	//code to de/activate compressor
    	if (compressorstate)
    	{
    		compressor.start();
    	}
    	else
    	{
    		compressor.stop();
    	}
    	
    	//purge tanks
    	if(manipJoypurge1 && manipJoypurge2)
    	{
    		purgeSol.set(true);
    	}
    	else
    	{
    		purgeSol.set(false);
    	}
    	
    	//code to toggle variable
    	if (manipJoyLIFTcylinder)
    	{
    		clampstate = !clampstate;
    		Timer.delay(0.3);
    	}
    	
    	//code to de/activate clamp
    	if (clampstate) //clamped (pneumatics retracted)
    	{
    		LIFTextendSol.set(false);
    		LIFTretractSol.set(true);
    	}
    	else
    	{
    		LIFTretractSol.set(false);
    		LIFTextendSol.set(true);
    	}
    	
    	//code to control extruder pneumatics
    	if(manipJoyEXTRcylinder)
    	{
    		EXTRextendSol.set(false);
    		EXTRextendSol.set(true);
    	}
    	else
    	{
    		EXTRextendSol.set(false);
    		EXTRextendSol.set(true);
    	}
    	
    	//winch control
    	winch.set(manipJoyLEFTyaxis);
    	
    	//extruder in/out control
    	if(manipJoyEXTRout)
    	{
    		extruder.set(-0.5);
    	}
    	else if(manipJoyEXTRin)
    	{
    		extruder.set(0.5);
    	}
    	else
    	{
    		extruder.set(0.0);
    	}
    	
    	//mini-wheel control
    	miniwheels.set(manipJoyRIGHTyaxis);
    	
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
