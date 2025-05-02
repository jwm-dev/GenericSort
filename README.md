# GenericSort

A modular Java project demonstrating both generic sorting through insertion sort and object-oriented design with inheritance.

---

## Project Structure & File Purpose

- **Main.java**  
  *Entry point.*  
  Handles:  
  - Generating random data files (`GenerateFile`)
  - Reading/writing binary files
  - Sorting arrays (ints, doubles, strings, and `Person` objects) using `GenericSort`
  - Configuring and applying flexible sorting logic for `Person` objects

- **src/Person.java**  
  *Base class for people.*  
  Stores first name, last name, and date of birth. Used as the superclass for all person types.

- **src/RegisteredPerson.java**  
  *Extends `Person`.*  
  Adds a government ID field. Represents people who are registered.

- **src/OCCCPerson.java**  
  *Extends `RegisteredPerson`.*  
  Adds a student ID field. Represents OCC students.

- **src/GenerateFile.java**  
  *Random data generator.*  
  Creates a binary file with randomized ints, doubles (with random decimal places), strings, and a mix of `Person`, `RegisteredPerson`, and `OCCCPerson` objects.

- **src/GenericSort.java**  
  *Generic insertion sort implementation.*  
  Provides a static method to sort arrays of any type using a provided comparator.

- **build.sh**  
  *Build and run script.*  
  - Compiles all `.java` files
  - Places all `.class` files in `/bin`
  - Runs the program with `java -cp bin Main`

- **bin/**  
  *Output directory for compiled `.class` files and input/output files.*  
  Created/used by `build.sh`.

---

## Usage

```sh
./build.sh
```
- Compiles and runs the project.
- Requires Java 17+ and a POSIX shell.

---

## Key Features

- **Modular design:** Clear separation of base and derived classes.
- **Flexible sorting:** Easily change sorting/grouping logic for `Person` objects via enums in `Main.java`.
- **Randomized test data:** Robustly tests sorting with realistic, generated test-data.
- **No external dependencies:** No Maven/Gradle required.

---

## Customization

- To change how `Person` objects are sorted, edit the `PERSON_COMPARATOR` assignment in `Main.java` and choose different enum values for primary and secondary sorting.

---

## Example Output

![1/2](screenshots/example1_2.png)
![2/2](screenshots/example2_2.png)

---
