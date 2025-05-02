package src;
// Jeffrey Gregory OCCC Mar 15 2025
import java.util.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String firstName, lastName;
    //private OCCCDate dob = new OCCCDate();
    private Date dob = new Date(0); // using the Date class from java.util as placeholder for OCCCDate
                                    // default to beginning of unix time if no date is provided
    // constructors
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    //public Person(String firstName, String lastName, OCCCDate dob) {
    public Person(String firstName, String lastName, Date dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }
    public Person(Person p) { // copy constructor
        this.firstName = p.firstName;
        this.lastName = p.lastName;
        this.dob = p.dob;
    }

    // getters
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    // setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // age and date functions
    public Date getDOB() {
        return dob;
    }
    //public OCCCDate getDOB() {
        //return dob;
    //}
    public int getAge() {
        Date now = new Date();
        long diff = now.getTime() - dob.getTime();
        return (int)(diff / 1000 / 60 / 60 / 24 / 365.25); // returns age in years
        //return Year.now().getValue() - dob.getYear();
    }

    // toString
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); // format Date object to MM/dd/yyyy
        return lastName + ", " + firstName + " (" + sdf.format(dob) + ")";
        //return lastName + ", " + firstName + " " + dob.toString();

    }

    // equals
    public boolean equals(Person p) {
        return this.firstName.equals(p.firstName) && this.lastName.equals(p.lastName) && this.dob.equals(p.dob);
    }

    // actions
    public void eat() {
        System.out.println(getClass().getSimpleName() + " is eating...");
    }
    public void sleep() {
        System.out.println(getClass().getSimpleName() + " is sleeping...");
    }
    public void play() {
        System.out.println(getClass().getSimpleName() + " is playing...");
    }
    public void run() {
        System.out.println(getClass().getSimpleName() + " is running...");
    }
}