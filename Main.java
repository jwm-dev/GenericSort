import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import src.GenerateFile;
import src.Person;
import src.RegisteredPerson;
import src.GenericSort;
import src.OCCCPerson;

public class Main {
    // Sorting option Enums
    enum PrimaryPersonSort {
        BY_TYPE,           // Person, RegisteredPerson, OCCCPerson
        BY_REGISTERED,     // Registered vs Unregistered
        NONE               // No grouping
    }
    
    enum SecondaryPersonSort {
        BY_NAME,
        BY_DOB,
        BY_TO_STRING,
        BY_GID,     // RegisteredPerson and OCCCPerson only (Person defaults to toString)
        BY_SID      // OCCCPerson only (Person and RegisteredPerson defaults to toString)
    }

    // File paths
    private static final String INPUT_FILE = "bin/io/data.bin";
    private static final String OUTPUT_FILE = "bin/io/data_sorted.bin";

    // Fallback sorting type for objects without GID/SID (for use in cases such as BY_GID and BY_SID, where some objects may not have these IDs)
    private static final SecondaryPersonSort FALLBACK_SORT = SecondaryPersonSort.BY_TO_STRING;
    // Person comparator (Default: BY_TYPE, BY_NAME)
    private static final Comparator<Person> PERSON_COMPARATOR = getPersonComparator(  // Modify this to change sorting behavior of Person objects
        PrimaryPersonSort.BY_TYPE,
        SecondaryPersonSort.BY_NAME
    );

    public static void main(String[] args) {
        try {
            // Generate the unsorted file
            GenerateFile.generate(INPUT_FILE);

            // Read data from the file
            FileInputStream fis = new FileInputStream(INPUT_FILE);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            ObjectInputStream ois = new ObjectInputStream(bis);

            int[] ints = new int[20];
            double[] doubles = new double[20];
            String[] strings = new String[20];
            Person[] persons = new Person[20];

            for (int i = 0; i < 20; i++) ints[i] = dis.readInt();
            for (int i = 0; i < 20; i++) doubles[i] = dis.readDouble();
            for (int i = 0; i < 20; i++) strings[i] = dis.readUTF();
            for (int i = 0; i < 20; i++) persons[i] = (Person) ois.readObject();

            dis.close();
            ois.close();

            // Output input file contents
            System.out.println("=== INPUT FILE CONTENTS ===");
            printData(ints, doubles, strings, persons);

            // Convert primitives to wrappers for sorting
            Integer[] intsBoxed = Arrays.stream(ints).boxed().toArray(Integer[]::new);
            Double[] doublesBoxed = Arrays.stream(doubles).boxed().toArray(Double[]::new);

            // Sort each array using GenericSort
            GenericSort.isort3(intsBoxed);
            GenericSort.isort3(doublesBoxed);
            GenericSort.isort3(strings);

            // Sort persons using the selected options
            GenericSort.isort3(persons, PERSON_COMPARATOR);

            // Convert back to primitives
            for (int i = 0; i < 20; i++) {
                ints[i] = intsBoxed[i];
                doubles[i] = doublesBoxed[i];
            }

            // Write sorted data to output file
            FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(bos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            for (int i = 0; i < 20; i++) dos.writeInt(ints[i]);
            for (int i = 0; i < 20; i++) dos.writeDouble(doubles[i]);
            for (int i = 0; i < 20; i++) dos.writeUTF(strings[i]);
            dos.flush();
            for (int i = 0; i < 20; i++) oos.writeObject(persons[i]);
            oos.flush();

            dos.close();
            oos.close();

            // Read and output sorted file contents
            FileInputStream fis2 = new FileInputStream(OUTPUT_FILE);
            BufferedInputStream bis2 = new BufferedInputStream(fis2);
            DataInputStream dis2 = new DataInputStream(bis2);
            ObjectInputStream ois2 = new ObjectInputStream(bis2);

            int[] intsSorted = new int[20];
            double[] doublesSorted = new double[20];
            String[] stringsSorted = new String[20];
            Person[] personsSorted = new Person[20];

            for (int i = 0; i < 20; i++) intsSorted[i] = dis2.readInt();
            for (int i = 0; i < 20; i++) doublesSorted[i] = dis2.readDouble();
            for (int i = 0; i < 20; i++) stringsSorted[i] = dis2.readUTF();
            for (int i = 0; i < 20; i++) personsSorted[i] = (Person) ois2.readObject();

            dis2.close();
            ois2.close();

            System.out.println("\n=== SORTED OUTPUT FILE CONTENTS ===");
            printData(intsSorted, doublesSorted, stringsSorted, personsSorted);

            System.out.println("\nSorting complete. Output written to " + OUTPUT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Comparator<Person> getPersonComparator(PrimaryPersonSort primary, SecondaryPersonSort secondary) {
        // Validate illegal combinations
        if (secondary == SecondaryPersonSort.BY_GID && primary == PrimaryPersonSort.NONE) {
            throw new IllegalArgumentException("BY_GID secondary sort requires a primary grouping.");
        }
        if (secondary == SecondaryPersonSort.BY_SID && primary != PrimaryPersonSort.BY_TYPE) {
            throw new IllegalArgumentException("BY_SID secondary sort requires BY_TYPE primary grouping.");
        }

        Comparator<Person> primaryComp = switch (primary) {
            case BY_TYPE -> Comparator.comparing((Person p) -> p instanceof OCCCPerson ? 2 : p instanceof RegisteredPerson ? 1 : 0);
            case BY_REGISTERED -> Comparator.comparing((Person p) -> (p instanceof RegisteredPerson || p instanceof OCCCPerson) ? 1 : 0);
            case NONE -> (_, _) -> 0;
        };

        Comparator<Person> fallbackComp;
        switch (FALLBACK_SORT) {
            case BY_NAME -> fallbackComp = Comparator.comparing(Person::getLastName).thenComparing(Person::getFirstName);
            case BY_DOB -> fallbackComp = Comparator.comparing(Person::getDOB);
            default -> fallbackComp = Comparator.comparing(Person::toString);
        }

        Comparator<Person> secondaryComp;
        switch (secondary) {
            case BY_NAME -> secondaryComp = Comparator.comparing(Person::getLastName).thenComparing(Person::getFirstName);
            case BY_DOB -> secondaryComp = Comparator.comparing(Person::getDOB);
            case BY_GID -> secondaryComp = (a, b) -> {
                boolean aHasGID = (a instanceof RegisteredPerson) || (a instanceof OCCCPerson);
                boolean bHasGID = (b instanceof RegisteredPerson) || (b instanceof OCCCPerson);
                if (aHasGID && bHasGID) {
                    String idA = (a instanceof RegisteredPerson) ? ((RegisteredPerson)a).getGovID() : ((OCCCPerson)a).getGovID();
                    String idB = (b instanceof RegisteredPerson) ? ((RegisteredPerson)b).getGovID() : ((OCCCPerson)b).getGovID();
                    return idA.compareTo(idB);
                } else if (!aHasGID && !bHasGID) {
                    return fallbackComp.compare(a, b);
                } else {
                    return aHasGID ? 1 : -1;
                }
            };
            case BY_SID -> secondaryComp = (a, b) -> {
                boolean aHasSID = a instanceof OCCCPerson;
                boolean bHasSID = b instanceof OCCCPerson;
                if (aHasSID && bHasSID) {
                    String idA = ((OCCCPerson)a).getStudentID();
                    String idB = ((OCCCPerson)b).getStudentID();
                    return idA.compareTo(idB);
                } else if (!aHasSID && !bHasSID) {
                    return fallbackComp.compare(a, b);
                } else {
                    return aHasSID ? 1 : -1;
                }
            };
            default -> secondaryComp = Comparator.comparing(Person::toString);
        }

        return primaryComp.thenComparing(secondaryComp);
    }

    private static void printData(int[] ints, double[] doubles, String[] strings, Person[] persons) {
        System.out.println("\nInts:");
        for (int i = 0; i < ints.length; i++) {
            System.out.printf("%6d%s", ints[i], ((i + 1) % 10 == 0 || i == ints.length - 1) ? "\n" : " ");
        }

        System.out.println("\nDoubles:");
        for (int i = 0; i < doubles.length; i++) {
            System.out.printf("%10.3f%s", doubles[i], ((i + 1) % 5 == 0 || i == doubles.length - 1) ? "\n" : " ");
        }

        System.out.println("\nStrings:");
        for (int i = 0; i < strings.length; i++) {
            System.out.printf("%-16s%s", strings[i], ((i + 1) % 5 == 0 || i == strings.length - 1) ? "\n" : " ");
        }

        System.out.println("\nPersons:");
        for (int i = 0; i < persons.length; i++) {
            Person p = persons[i];
            System.out.printf("[%2d] %-16s | %s\n", i, p.getClass().getSimpleName(), p);
        }
    }
}