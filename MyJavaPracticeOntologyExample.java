import java.util.ArrayList;
import java.util.List;

// Person class representing the ontology's Person class
class Person {
    private String name;
    private int age;
    private String city;

    // Constructor
    public Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", city='" + city + "'}";
    }
}

// Main class to create and manage individuals
public class MyJavaPracticeOntologyExample {
    public static void main(String[] args) {
        // Create individuals (instances of Person)
        Person alice = new Person("Alice", 25, "New York");
        Person bob = new Person("Bob", 30, "Los Angeles");
        Person charlie = new Person("Charlie", 35, "Chicago");
        Person david = new Person("David", 40, "Houston");

        // Add individuals to a list
        List<Person> people = new ArrayList<>();
        people.add(alice);
        people.add(bob);
        people.add(charlie);
        people.add(david);

        // Print details of each person
        for (Person person : people) {
            System.out.println(person);
        }
    }
}
