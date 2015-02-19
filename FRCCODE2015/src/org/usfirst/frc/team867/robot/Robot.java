
package org.usfirst.frc.team867.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.BuiltInAccelerometer; //allows use of accelerometer on RobotRio
import edu.wpi.first.wpilibj.Joystick; //creates joysticks (gamepad)
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; //allows output of data
import edu.wpi.first.wpilibj.Preferences; //allows for preferences table (setting values)
import edu.wpi.first.wpilibj.RobotDrive; //motor set up and control
import edu.wpi.first.wpilibj.Timer; //allows for timing (delay)
import edu.wpi.first.wpilibj.Jaguar; //allows control of the arm and winch motors

//pneumatic imports
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

/**
 * 
 * @author Aaron
 * @author Andrew
 * @version 0.8
 * 
 * JOYSTICKS ARE ALPHABETICAL ORDER
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
	 
	Compressor comp1; //allows for compressor control
	Solenoid LIFTextendSol; //first solenoid
	Solenoid LIFTretractSol; //second solenoid
	Solenoid EXTRextendSol; //third solenoid
	Solenoid EXTRretractSol; //fourth solenoid
	
	
    public void robotInit() //initialization code; period independent
    {     	  	
    	//initialize joysticks
    	driverJoy = new Joystick(1);
    	manipJoy = new Joystick(0);
    	
      	//initialize motors
    	myRobotForward = new RobotDrive(2, 1, 3, 0); //frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor
    	
    	//initialize solenoids
    	LIFTextendSol = new Solenoid(0);
    	LIFTretractSol = new Solenoid(1);
    	EXTRextendSol = new Solenoid(3); //this is 3
    	EXTRretractSol = new Solenoid(2); // this is 2
    	
    	
    	//initialize compressor
    	comp1 = new Compressor();
    	
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
    	compressorstate = true;
    	clampstate = false;
    }
    
    public void teleopPeriodic() //teleoperated period (loops)
    {
     	//get joystick inputs
    	
    	//driver values
    	driverJoyccw = driverJoy.getRawButton(4);
		driverJoycw = driverJoy.getRawButton(5);
    	driverJoygo = driverJoy.getRawButton(1);
    	driverJoyreverse = driverJoy.getRawButton(2);
    	driverJoyxaxis = driverJoy.getRawAxis(0);
		driverJoyyaxis = driverJoy.getRawAxis(1);
		driverJoyslow = driverJoy.getRawAxis(2);
		
		//manipulator values
		manipJoyLEFTxaxis = manipJoy.getRawAxis(0);
		manipJoyLEFTyaxis = manipJoy.getRawAxis(1);
		manipJoyRIGHTxaxis = manipJoy.getRawAxis(4);
		manipJoyRIGHTyaxis = manipJoy.getRawAxis(5);
		manipJoycompressor = manipJoy.getRawButton(8);
		manipJoyEXTRcylinder = manipJoy.getRawButton(6);
		manipJoyEXTRout = manipJoy.getRawButton(2); 
		manipJoyEXTRin = manipJoy.getRawButton(1);
		manipJoyLIFTcylinder  = manipJoy.getRawButton(5); 
		
    	
    	//debug
    	
    	SmartDashboard.putBoolean("Compressor", compressorstate);
    	SmartDashboard.putBoolean("LiftPneumatics", clampstate);
    	SmartDashboard.putNumber("driverxaxis", driverJoyxaxis);
    	SmartDashboard.putNumber("driverJoyslow", driverJoyslow);
    	SmartDashboard.putBoolean("driverJoyreverse", driverJoyreverse);
    	SmartDashboard.putBoolean("go", driverJoygo);
		SmartDashboard.putBoolean("driverJoyccw", driverJoyccw);
		SmartDashboard.putNumber("driveRotation", driveRotation);
    	
    	
    	
    	//drive buttonstates
    	
    	//"slow" factor
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
    	
    	//drive when go button is held
    	if(driverJoygo)
    	{
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
    	
    	//manipulator buttonstates
   
    	//code to toggle variable
    	if (manipJoycompressor)
    	{
    		compressorstate = !compressorstate;
    		Timer.delay(0.3);
    	}
    	
    	//code to de/activate compressor
    	if (compressorstate)
    	{
    		comp1.start();
    	}
    	else
    	{
    		comp1.stop();
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
    
    public void testPeriodic() //test period
    {
    	//left blank on purpose
    }
 
}
