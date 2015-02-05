
package org.usfirst.frc.team867.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot 
{
	RobotDrive myRobotForward; //robotdrive forward
	Joystick driverJoy; //driver gamepad
	BuiltInAccelerometer accel; // builtin accel
	double driverJoyxaxis; // driver gamepad left x axis
	double driverJoyyaxis; // driver gamepad left y axis
	double driverJoyslow; // driver gamepad z axis (set speed)
	boolean driverJoysafety; //safety trigger (1)
	boolean driverJoyccw; //pivot counter clockwise (4)
	boolean driverJoycw; //pivot clockwise (5)
	boolean driverJoyreverse; //reverse directions (forward is now backwards)(2)
	boolean driveReverse; //store reverse toggle
	double driveRotation;
	double driveSlow;
	
	
	
    public void robotInit() //initialization code; period independent
    { 
    	driverJoy = new Joystick(0);
    	myRobotForward = new RobotDrive(2, 1, 3, 0); //frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor
    	accel = new BuiltInAccelerometer();
    }

    public void autonomousInit() //initialization code; autonomous
    {

    }
    
    public void autonomousPeriodic() //autonomous period 
    {

    }

    public void teleopInit() //initialization code; teleop
    {
    	driveReverse=false;
    }
    
    public void teleopPeriodic() //teleoperated period
    {
    	
    	SmartDashboard.putNumber("Zaxis", accel.getZ());
    	
    	//"slow" factor
    	driverJoyslow = driverJoy.getRawAxis(2);
    	driveSlow = ((driverJoyslow + 1) * 4.5) + 1;
    	
    	SmartDashboard.putNumber("driverJoyslow", driverJoyslow);
    	
    	//reverse toggle
    	driverJoyreverse = driverJoy.getRawButton(2);
    	SmartDashboard.putBoolean("driverJoyreverse", driverJoyreverse);
    	if(driverJoyreverse)
    	{
    		driveReverse = !driveReverse;
    		Timer.delay(0.3);
    		
    	}
    	SmartDashboard.putBoolean("driveReverse", driveReverse);
    	
    	//safety
    	driverJoysafety = driverJoy.getRawButton(1);
    	SmartDashboard.putBoolean("driverJoysafety", driverJoysafety);
    	
    	
    	//forward driving
    	if(!driveReverse && driverJoysafety)
    	{
    		driverJoyxaxis = driverJoy.getRawAxis(0);
    		driverJoyyaxis = driverJoy.getRawAxis(1);
    		driverJoyccw = driverJoy.getRawButton(4);
    		driverJoycw = driverJoy.getRawButton(5);
    		
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
    		
    		//print out drive values
    		SmartDashboard.putNumber("driveSlow", driveSlow);
    		SmartDashboard.putNumber("driverJoyxaxis", driverJoyxaxis / driveSlow);
    		SmartDashboard.putNumber("driverJoyyaxis", driverJoyyaxis / driveSlow);
    		SmartDashboard.putBoolean("driverJoycw", driverJoycw);
   			SmartDashboard.putBoolean("driverJoyccw", driverJoyccw);
   			SmartDashboard.putNumber("driveRotation", driveRotation);
   			myRobotForward.mecanumDrive_Cartesian(driverJoyxaxis / driveSlow , driverJoyyaxis / driveSlow, driveRotation, 0);
    		
    		
    		
    	}
    	
    	//reverse driving
    	if(driveReverse && driverJoysafety)
    	{
    		driverJoyxaxis = driverJoy.getRawAxis(0);
    		driverJoyyaxis = driverJoy.getRawAxis(1);
    		driverJoyccw = driverJoy.getRawButton(4);
    		driverJoycw = driverJoy.getRawButton(5);
    		
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
    		
    		SmartDashboard.putNumber("driveSlow", driveSlow);
    		SmartDashboard.putNumber("driverJoyxaxis", driverJoyxaxis / driveSlow);
    		SmartDashboard.putNumber("driverJoyyaxis", driverJoyyaxis / driveSlow);
   			SmartDashboard.putBoolean("driverJoyccw", driverJoyccw);
   			SmartDashboard.putNumber("driveRotation", driveRotation);
   			myRobotForward.mecanumDrive_Cartesian( -1 * driverJoyxaxis / driveSlow, -1 * driverJoyyaxis / driveSlow, driveRotation, 0);
    		
    	}
        
    }
    
    public void testPeriodic() //test period
    {
    }
    
}