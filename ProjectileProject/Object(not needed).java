
package PhysicsSim;

/**
 * @authors 
 * Patricia Bere - D00193593
 * Oisin Murphy - D00191700
 */
public class Object {
    private String name;
    private double weight;
    public double radius;
    
    public Object()
    {
        this.name = "";
        this.weight = 0.0;
        this.radius = 0.0;
    }
    
    public Object(String name, double weight, double radius)
    {
        this.name = name;
        this.weight = weight;
        this.radius = radius;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public double getWeight() 
    {
        return weight;
    }

    public void setWeight(double weight) 
    {
        this.weight = weight;
    }

    public double getRadius() 
    {
        return radius;
    }

    public void setRadius(double radius) 
    {
        this.radius = radius;
    }
}
