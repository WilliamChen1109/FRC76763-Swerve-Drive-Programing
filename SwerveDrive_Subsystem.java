public void drive (double x1, double y1, double x2) {
    double r = Math.sqrt ((L * L) + (W * W));
    y1 *= -1;

    double a = x1 - x2 * (L / r);
    double b = x1 + x2 * (L / r);
    double c = y1 - x2 * (W / r);
    double d = y1 + x2 * (W / r);

    double backRightSpeed = Math.sqrt ((a * a) + (d * d));
    double backLeftSpeed = Math.sqrt ((a * a) + (c * c));
    double frontRightSpeed = Math.sqrt ((b * b) + (d * d));
    double frontLeftSpeed = Math.sqrt ((b * b) + (c * c));

    double backRightAngle = Math.atan2 (a, d) / Math.pi;
    double backLeftAngle = Math.atan2 (a, c) / Math.pi;
    double frontRightAngle = Math.atan2 (b, d) / Math.pi;
    double frontLeftAngle = Math.atan2 (b, c) / Math.pi;
}

private Jaguar angleMotor;
private Jaguar speedMotor;
private PIDController pidController;

public WheelDrive (int angleMotor, int speedMotor, int encoder) {
    this.angleMotor = new Jaguar (angleMotor);
    this.speedMotor = new Jaguar (speedMotor);
    pidController = new PIDController (1, 0, 0, new AnalogInput (encoder), this.angleMotor);

    pidController.setOutputRange (-1, 1);
    pidController.setContinuous ();
    pidController.enable ();
}

private final double MAX_VOLTS = 4.95;

public void drive (double speed, double angle) {
    speedMotor.set (speed);

    double setpoint = angle * (MAX_VOLTS * 0.5) + (MAX_VOLTS * 0.5); // Optimization offset can be calculated here.
    if (setpoint < 0) {
        setpoint = MAX_VOLTS + setpoint;
    }
    if (setpoint > MAX_VOLTS) {
        setpoint = setpoint - MAX_VOLTS;
    }

    pidController.setSetpoint (setpoint);
}

private WheelDrive backRight;
private WheelDrive backLeft;
private WheelDrive frontRight;
private WheelDrive frontLeft;

public SwerveDrive (WheelDrive backRight, WheelDrive backLeft, WheelDrive frontRight, WheelDrive frontLeft) {
    this.backRight = backRight;
    this.backLeft = backLeft;
    this.frontRight = frontRight;
    this.frontLeft = frontLeft;
}

    backRight.drive (backRightSpeed, backRightAngle);
    backLeft.drive (backLeftSpeed, backLeftAngle);
    frontRight.drive (frontRightSpeed, frontRightAngle);
    frontLeft.drive (frontLeftSpeed, frontLeftAngle);

private Joystick joystick = new Joystick (0);

public void teleopPeriodic () {
    swerveDrive.drive (joystick.getRawAxis (1), joystick.getRawAxis (0), joystick.getRawAxis (4));
}
