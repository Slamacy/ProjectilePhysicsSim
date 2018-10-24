package PhysicsSim;

/**
 * @authors 
 * Patricia Bere - D00193593
 * Oisin Murphy - D00191700
 */

public class Substance {
    private String name;
    private double density;
    
    public Substance() {
        this.name = "";
        this.density = 0.0;
    }
    
    public Substance(String name, double density) {
        this.name = name;
        this.density = density;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }
}
