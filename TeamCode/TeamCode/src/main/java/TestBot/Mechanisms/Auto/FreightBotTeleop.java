package TestBot.OpModes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.R;

import TestBot.Mechanisms.FreightBotInfo;
import TestBot.Mechanisms.MecanumDriveBase;

@TeleOp(name = "FreightBotTeleop")
public class FreightBotTeleop extends OpMode {
    FreightBotInfo robot = new FreightBotInfo();
    private ElapsedTime runtime = new ElapsedTime();
    boolean singleButtonPress = false;
    int stage;
    int goal;
    int liftEncoderGoal;
    @Override
    public void init(){
        robot.init(hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addData("Our Heading", robot.getHeading(AngleUnit.DEGREES));
        telemetry.addData("XRAIL POSITION", robot.liftMotor.getCurrentPosition());



        //mecanum drive w/ precision mode
        if(gamepad1.left_bumper){
            robot.mecanumDrive(0.5*gamepad1.right_stick_y, -0.5*gamepad1.right_stick_x, -0.5*gamepad1.left_stick_x);
        } else if(gamepad1.right_bumper){
            robot.mecanumDrive(0.25*gamepad1.right_stick_y, -0.25*gamepad1.right_stick_x, -0.25*gamepad1.left_stick_x);
        } else{
            robot.mecanumDrive(gamepad1.right_stick_y, -gamepad1.right_stick_x, -gamepad1.left_stick_x);
        }
        //start


        //GAMEPAD 1 - JONAH
        // lift servo (BOX)
        if (gamepad2.dpad_up) {
            robot.liftServo(1); //dumps stuff out
        } else if (gamepad2.dpad_left) {
            robot.liftServo(0); //
        } else if (gamepad2.dpad_down) {
            robot.liftServo(0.2); //straight up and down
        } else if(gamepad2.dpad_right){
            robot.liftServo(0.4); //tilt
        }

        //ELEVATOR SERVO
        if (gamepad1.left_trigger>0.1) {
            robot.elevatorServo.setPosition(.7); //horizontal
        } else if (gamepad1.right_trigger>0.1) {
            robot.elevatorServo.setPosition(.5); //vertical
        }

        if (gamepad1.dpad_up){
            singleButtonPress=true;
            stage=1;
            goal=0;
            liftEncoderGoal=-1600;
        } else if(gamepad1.dpad_down){
            singleButtonPress=true;
            stage=1;
            goal=1;
            liftEncoderGoal=-1600;
        }



        if(singleButtonPress&&(robot.liftMotor.getCurrentPosition()>liftEncoderGoal)){

            robot.liftMotor.setPower(1);

        } else if(singleButtonPress&&robot.liftMotor.getCurrentPosition()<liftEncoderGoal){
            // Stop all motion;
            robot.liftMotor.setPower(0);
            if(stage==1) {
                robot.liftServo(0.3); //middle
                stage++;
                if (stage == 2 && goal == 0) {
                    liftEncoderGoal = -2500;
                } else if (stage == 2 && goal == 1) {
                    liftEncoderGoal = -4900;
                }
            } else if(stage==2){
                singleButtonPress=false;
                stage=0;
                liftEncoderGoal=-300;
            }
        }



        if(gamepad2.a){
            robot.elevatorServo.setPosition(.7); //horizontal
        } else if (gamepad2.b) {
            robot.elevatorServo.setPosition(.3); //vertical
        }

        //ducky mover
        if(gamepad2.right_bumper) {
            robot.duckyMover.setPower(-0.05); //spain without the a in red
        }else if(gamepad2.left_bumper){
            robot.duckyMover.setPower(0.05); //spain without the a in blue
        } else {
            robot.duckyMover.setPower(0); //freeze
        }

        //intake motor
        if (gamepad1.left_trigger>.001) {
            robot.intakeMotor.setPower(.1); //take it back now y'all
        } else if (gamepad1.right_trigger>.001){
            robot.intakeMotor.setPower(-.1); //outoutout
        } else {
            robot.intakeMotor.setPower(0);
        }

        //XRAIL
        if (gamepad2.right_stick_y>.01) {
            robot.liftMotor.setPower(1); //intake
        } else if (gamepad2.right_stick_y<-.01){
            robot.liftMotor.setPower(-1); //un-intake
        } else {
            robot.liftMotor.setPower(0);
        }

        //touch sensor
        if(gamepad1.dpad_left){
            robot.touchServoLeft.setPosition(0); //horizontal
        } else if (gamepad1.dpad_right) {
            robot.touchServoLeft.setPosition(0.5); //vertical
        }






    }



}





