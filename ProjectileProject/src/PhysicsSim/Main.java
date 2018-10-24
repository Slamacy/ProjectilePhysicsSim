package PhysicsSim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

/**
 * @authors 
 * Patricia Bere - D00193593
 * Oisin Murphy - D00191700
 */

public class Main {
    private static Scanner in = new Scanner(System.in);
    private static File output = new File("output.txt");
    private static final double GRAVITY = 9.81;
    private static Vector<Double> position = new Vector<>(),
                                  velocity = new Vector<>(),
                                  acceleration = new Vector<>(),
                                  omega = new Vector<>(),
                                  apparentVelocity = new Vector<>(),
                                  forceGravity = new Vector<>(),
                                  forceMagnus = new Vector<>(),
                                  forceDrag = new Vector<>(),
                                  forceNet = new Vector<>(),
                                  kHat = new Vector<>(),
                                  rotation = new Vector<>(),
                                  RxVa = new Vector<>();
                                  
                                  //all vectors that will be used
    private static double time;
    private static double finalTime;
    private static double currentTime;
    private static double mass;
    private static double h = 0.1;
    //h == 1/60 to match a 60fps game refresh rate
    private static Substance[] substances = new Substance[5];
    private static double Va; //apparent velocity magnitude
    private static double Cl; // (radius * w)/ Va; 
    private static double Cd = 0.1; //drag coeffecient
    private static double area; //area of the object dependant on the object;
    private static double w; //flow rate of fluid
    private static double radius; //radius of sphere
    private static double ballDensity; //density
    private static double fluidDensity; //TEMP AIR VAL FOR TESTING
    private static String fluid; //name of fluid
    private static boolean isFirst = true;
    
    public static void main(String[] args) {
        substances[0] = new Substance("Water", 1000);
        substances[1] = new Substance("Honey", 1420);
        substances[2] = new Substance("Olive oil", 918);
        substances[3] = new Substance("Air", 1.161);
        substances[4] = new Substance("Hydrogen", 0.082);
        
        kHat.add(0, 0.0);
        kHat.add(1, 0.0);
        kHat.add(2, 1.0);
        
        initialize();
        
        for(double i = time; i <= finalTime; i += h) {
            //***get apparent velocity for future use***
            currentTime = i;
            double Va0 = velocity.get(0) - omega.get(0);
            double Va1 = velocity.get(1) - omega.get(1);
            double Va2 = velocity.get(2) - omega.get(2);
        
            apparentVelocity.add(0, Va0);   //
            apparentVelocity.add(1, Va1);   //setting the values of the vector
            apparentVelocity.add(2, Va2);   //
            //******************************************
            
            //*****************get Va*******************
            Va = Math.sqrt((apparentVelocity.get(0) * apparentVelocity.get(0)) +
                           (apparentVelocity.get(1) * apparentVelocity.get(1)) +
                           (apparentVelocity.get(2) * apparentVelocity.get(2)));
            //******************************************
            
            //**************get w (omega)***************
            w = Math.sqrt((rotation.get(0) * rotation.get(0)) 
                        + (rotation.get(1) * rotation.get(1))
                        + (rotation.get(2) * rotation.get(2)));
            //******************************************
            
            gravForce();
            magnusForce();
            dragForce();
            netForce();
            getAcceleration();
            writeResultsToFile();
            updatePosition();
            updateVelocity();
        }
        //loop to go through the algorithm a number of times adding up to a second.
        System.out.println("Results written to " + output);
    }
    
    //Patricia
    public static void gravForce() {
        // Fg = -mgk
        double Fg0 = (mass * GRAVITY * kHat.get(0)) * -1;
        double Fg1 = (mass * GRAVITY * kHat.get(1)) * -1;
        double Fg2 = (mass * GRAVITY * kHat.get(2)) * -1;
       
        forceGravity.add(0, Fg0);
        forceGravity.add(1, Fg1);
        forceGravity.add(2, Fg2);
    }
    
    //Patricia
    public static void magnusForce() {
    //FmHat = (1 / |RotationBar x ApparentVelocityBar|) * RotationBar x ApparentVelocityBar
    //Fm = (1/2*(rho * area * Cl * (Va * Va)));
    //FmBar = Fm * FmHat
    //CL = radius * omega / va
    //Area (cross-sectional area)
        Cl = (w * radius) / Va;
        
        double Fm = (0.5 *(fluidDensity * area * Cl * (Va * Va)));
        double i = ((rotation.get(1) * apparentVelocity.get(2)) - (apparentVelocity.get(1) * rotation.get(2)));
        double j = -((rotation.get(0) * apparentVelocity.get(2)) - (apparentVelocity.get(0) * rotation.get(2)));
        double k = ((rotation.get(0) * apparentVelocity.get(1)) - (apparentVelocity.get(0) * rotation.get(1)));
        //crossProduct = i - j + k;

        RxVa.add(0, i);
        RxVa.add(1, j);
        RxVa.add(2, k);

        double RxVaLength = Math.sqrt((RxVa.get(0) * RxVa.get(0)) 
                                    + (RxVa.get(1) * RxVa.get(1)) 
                                    + (RxVa.get(2) * RxVa.get(2)));

        //double FmHat = (1/ crossProduct) * crossProduct;
        //double FmBar = Fm * FmHat;

        forceMagnus.add(0, (Fm * ((1 / RxVaLength) * RxVa.get(0))));
        forceMagnus.add(1, (Fm * ((1 / RxVaLength) * RxVa.get(1)))); //placeholder values
        forceMagnus.add(2, (Fm * ((1 / RxVaLength) * RxVa.get(2))));
    }
    
    //Oisin
    public static void dragForce() {
        //FdBar = -1/2(density)(A)(dragCoeffecient)(Va)(VaBar)
        double Fd0 = -(0.5) * (fluidDensity * area * Cd * Va * apparentVelocity.get(0)); //i
        double Fd1 = -(0.5) * (fluidDensity * area * Cd * Va * apparentVelocity.get(1)); //j
        double Fd2 = -(0.5) * (fluidDensity * area * Cd * Va * apparentVelocity.get(2)); //k
        
        forceDrag.add(0, Fd0); //i
        forceDrag.add(1, Fd1); //j
        forceDrag.add(2, Fd2); //k
    }
    
    //Oisin
    public static void netForce() {
        //Fnet = Fg + Fm + Fd
        double Fnet0 = forceGravity.get(0) + forceMagnus.get(0) + forceDrag.get(0);
        double Fnet1 = forceGravity.get(1) + forceMagnus.get(1) + forceDrag.get(1);
        double Fnet2 = forceGravity.get(2) + forceMagnus.get(2) + forceDrag.get(2);
        
        forceNet.add(0, Fnet0);
        forceNet.add(1, Fnet1);
        forceNet.add(2, Fnet2);
    }
    
    //Patricia
    public static void getAcceleration() 
    {
        // F = ma
        // FHat = maHat
        // aHat = FHat / m
        double aHat0 = forceNet.get(0) / mass;
        double aHat1 = forceNet.get(1) / mass;
        double aHat2 = forceNet.get(2) / mass;
        
        acceleration.add(0, aHat0);
        acceleration.add(1, aHat1);
        acceleration.add(2, aHat2);
    }
    
    //Patricia
    public static void updatePosition() 
    {
        //Position = Position + h*(Velocity)
        position.set(0, position.get(0) + (h * velocity.get(0)));
        position.set(1, position.get(1) + (h * velocity.get(1)));
        position.set(2, position.get(2) + (h * velocity.get(2)));   
    }
    
    //Oisin
    public static void updateVelocity() {
        //Velocity = Velocity + h(acceleration)
        velocity.set(0, velocity.get(0) + (h * acceleration.get(0)));
        velocity.set(1, velocity.get(1) + (h * acceleration.get(1)));
        velocity.set(2, velocity.get(2) + (h * acceleration.get(2)));
    }
    
    //Patricia
    public static void writeConditionsToFile() 
    {
        try(FileWriter fw = new FileWriter(output)) {
            PrintWriter print = new PrintWriter(new BufferedWriter(fw));
            if(output.length() == 0) {
                print.println("/----------- Initial Conditions -----------/");
                print.println("*************************************************\n");
            }
            print.println("Substance: " + fluid + " -> " + fluidDensity + "Kg/m^3");
            print.println("Radius of Object: " + radius + "m");
            print.println("Mass of Object: " + mass + "Kg");
            print.println("Density of Object: " + ballDensity + "Kg/m^3");
            print.println("Starting Time: " + time + "s");
            print.println("Substance flow rate: (i = " + omega.get(0) + ", j = " + omega.get(1) + ", k = " + omega.get(2) + ") m/s");
            print.println("Starting Position: (i = " + position.get(0) + ", j = " + position.get(1) + ", k = " + position.get(2) + ") m");
            print.println("Starting Velocity: (i = " + velocity.get(0) + ", j = " + velocity.get(1) + ", k = " + velocity.get(2) + ") m/s");
            print.println("*************************************************\n\n");
            print.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //Oisin
    public static void writeResultsToFile() {
        try(FileWriter fw = new FileWriter(output, true)) {
            PrintWriter print = new PrintWriter(new BufferedWriter(fw));
            if(isFirst) {
                print.println("/----------- Results -----------/");
                print.println("*************************************************\n");
                isFirst = false;
            }
            print.print("Time: ");
            print.printf("%.2f", currentTime);
            print.print("s\n");
            print.println("Position: i = " + position.get(0) + ", j = " + position.get(1) + ", k = " + position.get(2) + ") m");
            print.println("Velocity: i = " + velocity.get(0) + ", j = " + velocity.get(1) + ", k = " + velocity.get(2) + ") m/s");
            print.println("Fgravity: i = " + forceGravity.get(0) + ", j = " + forceGravity.get(1) + ", k = " + forceGravity.get(2) + ") m/s");
            print.println("Fdrag: i = " + forceDrag.get(0) + ", j = " + forceDrag.get(1) + ", k = " + forceDrag.get(2) + ") m/s");
            print.println("Fmagnus: i = " + forceMagnus.get(0) + ", j = " + forceMagnus.get(1) + ", k = " + forceMagnus.get(2) + ") m/s");
            print.println("Net Force: i = " + forceNet.get(0) + ", j = " + forceNet.get(1) + ", k = " + forceNet.get(2) + ") N");
            print.println("Acceleration: i = " + acceleration.get(0) + ", j = " + acceleration.get(1) + ", k = " + acceleration.get(2) + ") m/s^2");
            print.println("*************************************************\n");
            print.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void initialize() {
        //NEEDS: time, radius, density, mass, position, velocity, rotation, fluid choice, wind.
        System.out.print("Please enter the start time of the simulation in seconds: ");
        time = in.nextDouble();
        System.out.print("Please enter the duration of the simulation in seconds: ");
        double duration = in.nextDouble();
        finalTime = time + duration;
        
        System.out.println("Each step of the simulation will increment by 0.1 of a second.");
        
        System.out.print("Please enter the radius of sphere: ");
        radius = in.nextDouble();
        System.out.print("Please enter the density of the sphere: ");
        ballDensity = in.nextDouble();
        double volume = 4.0/3 * Math.PI * (radius * radius * radius);
        mass = ballDensity * volume;
        area = Math.PI * (radius * radius);
        //Finds mass of the ball based on inputs for this specific simulation.
        
        boolean done = false;
        while(done == false){
            System.out.println("");
            System.out.println("Please choose the fluid that the object will move through by"
                             + "\nentering the corresponding number.");
            for(int i = 0; i < substances.length; i++) {
                System.out.println(i + ". " + substances[i].getName() + " -> " + substances[i].getDensity() + "Kg/m^3");
            }
            int choice = in.nextInt();
            if(choice <= substances.length && choice >= 0) {
                fluidDensity = substances[choice].getDensity();
                fluid = substances[choice].getName();
                done = true;
            } else {
                System.out.println("Please enter one of the numbers assigned to the fluids.");
            }
        }
        System.out.println("");
        
        System.out.println("Please enter the three initial values for the position vector: ");
        System.out.print("i: " );
        double p1 = in.nextDouble();
        System.out.print("j: " );
        double p2 = in.nextDouble();
        System.out.print("k: " );
        double p3 = in.nextDouble();
        position.add(0, p1);
        position.add(1, p2);
        position.add(2, p3);
        System.out.println("");

        System.out.println("Please enter the three initial values for the velocity vector: ");
        System.out.print("i: " );
        double v1 = in.nextDouble();
        System.out.print("j: " );
        double v2 = in.nextDouble();
        System.out.print("k: " );
        double v3 = in.nextDouble();
        velocity.add(0, v1);
        velocity.add(1, v2);
        velocity.add(2, v3);
        System.out.println("");
        
        System.out.println("Please enter the three initial values for the rotation vector: ");
        System.out.print("i: " );
        double r1 = in.nextDouble();
        System.out.print("j: " );
        double r2 = in.nextDouble();
        System.out.print("k: " );
        double r3 = in.nextDouble();
        rotation.add(0, r1);
        rotation.add(1, r2);
        rotation.add(2, r3);
        System.out.println("");
        
        System.out.println("Please enter the three initial values for the substance flow rate vector (wind): ");
        System.out.print("i: " );
        double om1 = in.nextDouble();
        System.out.print("j: " );
        double om2 = in.nextDouble();
        System.out.print("k: " );
        double om3 = in.nextDouble();
        omega.add(0, om1);
        omega.add(1, om2);
        omega.add(2, om3);
        System.out.println("");
        
        writeConditionsToFile();
    }
}