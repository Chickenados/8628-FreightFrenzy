package TestBot.Mechanisms.Auto.StatePrograms;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import TestBot.Mechanisms.FreightBotInfo;

/*
 * This is an example LinearOpMode that shows how to use
 * a REV Robotics Touch Sensor.
 *
 * It assumes that the touch sensor is configured with a name of "sensor_digital".
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 */

@Autonomous(name = "RDepotFarPark", group = "Sensor")

public class DRFarPark extends LinearOpMode {
    /* Copyright (c) 2017 FIRST. All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without modification,
     * are permitted (subject to the limitations in the disclaimer below) provided that
     * the following conditions are met:
     *
     * Redistributions of source code must retain the above copyright notice, this list
     * of conditions and the following disclaimer.
     *
     * Redistributions in binary form must reproduce the above copyright notice, this
     * list of conditions and the following disclaimer in the documentation and/or
     * other materials provided with the distribution.
     *
     * Neither the name of FIRST nor the names of its contributors may be used to endorse or
     * promote products derived from this software without specific prior written permission.
     *
     * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
     * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
     * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
     * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
     * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
     * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
     * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
     * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
     * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
     * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
     * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

    /**
     * The REV Robotics Touch Sensor
     * is treated as a digital channel.  It is HIGH if the button is unpressed.
     * It pulls LOW if the button is pressed.
     * <p>
     * Also, when you connect a REV Robotics Touch Sensor to the digital I/O port on the
     * Expansion Hub using a 4-wire JST cable, the second pin gets connected to the Touch Sensor.
     * The lower (first) pin stays unconnected.*
     */
    FreightBotInfo robot = new FreightBotInfo();
    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_MOTOR_REV = 537.6;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 1.0;
    static final double DRIVE_SLOW = 0.2;
    static final double TURN_SPEED = 0.5;

    int robotPosition = 0;

    //OUR ORIGINAL touch sensor program for the red side, RED LEFT -
    @Override
    public void runOpMode() {


        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0", "Starting at %7d :%7d",
                robot.backRight.getCurrentPosition(),
                robot.backLeft.getCurrentPosition(),
                robot.frontRight.getCurrentPosition(),
                robot.frontLeft.getCurrentPosition());
        telemetry.update();
        telemetry.addData("running route",robotPosition);

        // wait for the start button to be pressed.
        waitForStart();

        //Move servo forward


        // while the op mode is active, loop and read the light levels.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {

            // send the info back to driver station using telemetry function.
            // if the digital channel returns true it's HIGH and the button is unpressed.
            robot.digitalTouchLeft.setState(true);
            robot.digitalTouchRight.setState(true);
            encoderDrive(.6, 16, 16, 3); //go to touch sensor
            robot.touchServoRight.setPosition(robot.RIGHT_DOWN);
            robot.touchServoLeft.setPosition(robot.LEFT_DOWN);

            sleep(500);
            telemetry.addData("left state",robot.digitalTouchLeft.getState());
            telemetry.addData("right state",robot.digitalTouchRight.getState());
            telemetry.update();
            sleep(2000);

            if(robot.digitalTouchLeft.getState() == false){
                //    at position 3, run top one
                telemetry.addData("Left Digital Touch", "Is Pressed");
                telemetry.update();
                sleep(500);
                //Move servo backward
                robot.touchServoLeft.setPosition(robot.LEFT_UP);
                robot.touchServoRight.setPosition(robot.RIGHT_UP);
                robotPosition = 1;
                runTwo();
                //sleep(20000);
            } else if(robot.digitalTouchRight.getState() == false) {
                //    at position 3, run top one
                telemetry.addData("Right Digital Touch", "Is Pressed");
                telemetry.update();
                sleep(500);
                //Move servo backward
                robot.touchServoLeft.setPosition(robot.LEFT_UP);
                robot.touchServoRight.setPosition(robot.RIGHT_UP);
                robotPosition = 2;
                runOne();
                //sleep(20000);
            } else {
                //       at position 1, run bottom one
                telemetry.addData(" Neither Digital Touch", "not pressed");
                telemetry.update();
                sleep(500);
                robot.touchServoLeft.setPosition(robot.LEFT_UP);
                robot.touchServoRight.setPosition(robot.RIGHT_UP);
                robotPosition = 3;
                runThree();

                //sleep(20000);
            }
        }
        //telemetry.addData("PARKING NOW", robotPosition);


        telemetry.update();
    }


//3980 for position 2
//7350 for dump position
//3940 safe to go from dump to load
// 3251 go from up and down to load
//1477 down position

    public void runOne(){
        //backwards
        encoderDrive(DRIVE_SPEED,-10,-10,2.0);
        //turn right
        turnLeft(TURN_SPEED,.9);
        //go to cupcake holder (shipping hub)
        //encoderDrive(DRIVE_SPEED,6,6,3.0); //Drive forward 6
        telemetry.addData("Running route", "1");
        telemetry.update();
        encoderDrive(TURN_SPEED, -40,40,4.0); //strafe right- extra far to get rid of team scoring element
        encoderDrive(TURN_SPEED, 3,-3,4.0); //strafe back
        encoderDrive(DRIVE_SPEED, 11,11,2);// drive forward
        encoderDrive(DRIVE_SLOW, 4,4,2);// drive forward

        liftXRail(2800);
        //pivot box all the way
        robot.liftServo.setPosition(1);
        sleep(1000);
        robot.liftServo.setPosition(.4);
        sleep(1000);
        liftXRail(2500);
        robot.liftServo.setPosition(.2);
        sleep(1000);
        liftXRail(100);
        robot.elevatorServo.setPosition(.5);

        //go back
        encoderDrive(DRIVE_SPEED,-36,-36,2.0);
        robot.elevatorServo.setPosition(.5);
        sleep(500);
        encoderDrive(DRIVE_SLOW,-3,-3,2.0);
        //strafe right
        encoderDrive(DRIVE_SPEED,30,-30,5.0);
        encoderDrive(DRIVE_SLOW,7,-7,3.0);
        encoderDrive(DRIVE_SLOW,-2.5,-2.5,2.0);
        //spin carousel
        runtime.reset();
        while(2.5>runtime.seconds()&&opModeIsActive()){           //SPIN DUCKY
            robot.duckyMover.setPower(-.5);
        }
        robot.duckyMover.setPower(0);

        encoderDrive(DRIVE_SPEED, 20,20,2.0);

        encoderDrive(DRIVE_SPEED,20,-20,4.0); //this was the park in depot movement (reversed for 25 in)
        encoderDrive(DRIVE_SPEED,45,45,2.0);
        encoderDrive(DRIVE_SLOW,3,-3,4.0);
        encoderDrive(DRIVE_SPEED,35,35,2.0);
        turnLeft(TURN_SPEED,1.5);
        sleep(20000);
    }
    public void runTwo(){
        //backwards
        encoderDrive(DRIVE_SPEED,-10,-10,2.0);
        //turn right
        turnLeft(TURN_SPEED,.9);
        //go to cupcake holder (shipping hub)
        //encoderDrive(DRIVE_SPEED,6,6,3.0); //Drive forward 6
        telemetry.addData("Running route", "2");
        telemetry.update();
        encoderDrive(TURN_SPEED, -42,42,4.0); //strafe right- extra far to get rid of team scoring element
        encoderDrive(TURN_SPEED, 3,-3,4.0); //strafe back
        robot.elevatorServo.setPosition(.7);
        encoderDrive(DRIVE_SPEED, 10.5,10.5,2);// drive forward
        encoderDrive(DRIVE_SLOW, 5,5,2);// drive forward

        liftXRail(3200);
        //pivot box all the way
        robot.liftServo.setPosition(1);
        sleep(1000);
        robot.liftServo.setPosition(.4);
        liftXRail(2500);
        robot.liftServo.setPosition(.2);
        sleep(1000);
        liftXRail(100);
        robot.elevatorServo.setPosition(.5);

        //go back
        encoderDrive(DRIVE_SPEED,-36,-36,2.0);
        robot.elevatorServo.setPosition(.5);
        sleep(500);
        encoderDrive(DRIVE_SLOW,-3,-3,2.0);
        //strafe right
        encoderDrive(DRIVE_SPEED,33,-33,5.0);
        encoderDrive(DRIVE_SLOW,6,-6,3.0);
        encoderDrive(DRIVE_SLOW,-2,-2,2.0);
        //spin carousel
        runtime.reset();
        while(2.5>runtime.seconds()&&opModeIsActive()){           //SPIN DUCKY
            robot.duckyMover.setPower(-.3);
        }
        robot.duckyMover.setPower(0);

        encoderDrive(DRIVE_SPEED, 20,20,2.0);

        encoderDrive(DRIVE_SPEED,20,-20,4.0); //this was the park in depot movement (reversed for 25 in)
        encoderDrive(DRIVE_SPEED,45,45,2.0);
        encoderDrive(DRIVE_SLOW,3,-3,4.0);
        encoderDrive(DRIVE_SPEED,35,35,2.0);
        turnLeft(TURN_SPEED,1.5);

        sleep(20000);

    }

//3980 for position 2
//7350 for dump position
//3940 safe to go from dump to load
// 3251 go from up and down to load
//1477 down position

    public void runThree(){
        //backwards
        encoderDrive(DRIVE_SPEED,-10,-10,2.0);
        //turn right
        turnLeft(TURN_SPEED,.9);
        //go to cupcake holder (shipping hub)
        //encoderDrive(DRIVE_SPEED,6,6,3.0); //Drive forward 6
        telemetry.addData("Running route", "1");
        telemetry.update();
        encoderDrive(TURN_SPEED, -40,40,4.0); //strafe right- extra far to get rid of team scoring element
        encoderDrive(TURN_SPEED, 3,-3,4.0); //strafe back
        encoderDrive(DRIVE_SPEED, 9,9,2);// drive forward
        encoderDrive(DRIVE_SLOW, 6,6,2);// drive forward

        liftXRail(3250);
        //pivot box all the way
        robot.liftServo(0.4); //middle
        sleep(800);
        liftXRail(6500);
        sleep(1000);
        robot.liftServo.setPosition(1); //dump box
        sleep(1000);
        robot.liftServo.setPosition(.4);//middle
        sleep(1000);
        liftXRail(2500);
        robot.liftServo.setPosition(.2);
        sleep(1000);
        liftXRail(1100);
        robot.elevatorServo.setPosition(.5);

        //go back
        encoderDrive(DRIVE_SPEED,-36,-36,2.0);
        robot.elevatorServo.setPosition(.5);
        sleep(500);
        encoderDrive(DRIVE_SLOW,-3,-3,2.0);
        //strafe right
        encoderDrive(DRIVE_SPEED,33,-33,5.0);
        encoderDrive(DRIVE_SLOW,5,-5,3.0);
        encoderDrive(DRIVE_SLOW,-2,-2,2.0);
        //spin carousel
        runtime.reset();
        while(2.5>runtime.seconds()&&opModeIsActive()){           //SPIN DUCKY
            robot.duckyMover.setPower(-.4);
        }
        robot.duckyMover.setPower(0);

        encoderDrive(DRIVE_SPEED, 20,20,2.0);

        encoderDrive(DRIVE_SPEED,20,-20,4.0); //this was the park in depot movement (reversed for 25 in)
        encoderDrive(DRIVE_SPEED,45,45,2.0);
        encoderDrive(DRIVE_SLOW,3,-3,4.0);
        encoderDrive(DRIVE_SPEED,35,35,2.0);
        turnLeft(TURN_SPEED,1.5);
        liftXRail(100);
        sleep(20000);

    }



    public void liftXRail(int position){
        if (opModeIsActive()){
            robot.liftMotor.setTargetPosition(position);

            // Turn On RUN_TO_POSITION

            robot.liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.liftMotor.setPower(1);
            while (opModeIsActive() &&
                    (runtime.seconds() < 5.0) && (robot.liftMotor.isBusy())) {

                robot.liftMotor.getCurrentPosition();
                telemetry.update();
            }

            // Stop all motion;
            robot.liftMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    public void turnLeft (double power, double timeout){
        if (opModeIsActive()) {

            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < timeout) ){
                robot.backRight.setPower(power);
                robot.frontRight.setPower(power);
                robot.backLeft.setPower(-power);
                robot.frontLeft.setPower(-power);
            }
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.backRight.setPower(0);
        }
    }
    public void turnRight (double power, double timeout){
        if (opModeIsActive()) {

            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < timeout) ){
                robot.backRight.setPower(-power);
                robot.frontRight.setPower(-power);
                robot.backLeft.setPower(power);
                robot.frontLeft.setPower(power);
            }
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.backRight.setPower(0);
        }
    }
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newBackRightTarget;
        int newBackLeftTarget;
        int newFrontRightTarget;
        int newFrontLeftTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newBackRightTarget = robot.backRight.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newBackLeftTarget = robot.backLeft.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newFrontLeftTarget = robot.frontLeft.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newFrontRightTarget = robot.frontRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            robot.backRight.setTargetPosition(newBackRightTarget);
            robot.backLeft.setTargetPosition(newBackLeftTarget);
            robot.frontRight.setTargetPosition(newFrontRightTarget);
            robot.frontLeft.setTargetPosition(newFrontLeftTarget);

            // Turn On RUN_TO_POSITION
            robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.backRight.setPower(Math.abs(speed));
            robot.backLeft.setPower(Math.abs(speed));
            robot.frontLeft.setPower(Math.abs(speed));
            robot.frontRight.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.frontLeft.isBusy() && robot.frontRight.isBusy() && robot.backLeft.isBusy() && robot.backRight.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newBackLeftTarget, newBackRightTarget, newFrontLeftTarget, newFrontRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        robot.backLeft.getCurrentPosition(),
                        robot.backRight.getCurrentPosition(),
                        robot.frontRight.getCurrentPosition(),
                        robot.frontLeft.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.backRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }
}
