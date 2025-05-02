package src;

import java.io.*;
import java.util.*;

public class GenerateFile {
    /**
     * Generates a binary file in the project root containing:
     * - 20 ints
     * - 20 doubles
     * - 20 strings
     * - 20 Person objects
     * Data is written efficiently in binary format.
     *
     * @param filename Path to the output file (relative to project root)
     * @throws IOException if an I/O error occurs
     */
    public static void generate(String filename) throws IOException {
        try (
            FileOutputStream fos = new FileOutputStream(filename);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(bos);
            ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            Random rand = new Random();

            // Prepare and shuffle 20 ints
            List<Integer> intList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                // Generate a random integer between -1000 and 1000
                intList.add(rand.nextInt(2001) - 1000);
            }
            Collections.shuffle(intList, rand);
            for (int i : intList) dos.writeInt(i);

            // Prepare and shuffle 20 doubles
            List<Double> doubleList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                // Generate a random double between -1000.0 and 1000.0
                double raw = -1000.0 + rand.nextDouble() * 2000.0;
                // Random number of decimal places between 0 and 8
                int decimals = rand.nextInt(9);
                double scale = Math.pow(10, decimals);
                double rounded = Math.round(raw * scale) / scale;
                doubleList.add(rounded);
            }
            Collections.shuffle(doubleList, rand);
            for (double d : doubleList) dos.writeDouble(d);

            // Prepare and shuffle 20 random strings
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                stringList.add(randomString(rand, 4, 14));
            }
            Collections.shuffle(stringList, rand);
            for (String s : stringList) dos.writeUTF(s);

            dos.flush(); // flush DataOutputStream before writing objects

            // Prepare and shuffle 20 Person/RegisteredPerson/OCCCPerson objects with randomized names and dates
            List<Person> personList = new ArrayList<>();
            long startMillis = new GregorianCalendar(1950, Calendar.JANUARY, 1).getTimeInMillis();
            long endMillis = new GregorianCalendar(2010, Calendar.DECEMBER, 31).getTimeInMillis();
            for (int i = 0; i < 20; i++) {
                String firstName = randomString(rand, 4, 9);
                String lastName = randomString(rand, 5, 12);
                long randomMillis = startMillis + (long) (rand.nextDouble() * (endMillis - startMillis));
                Date dob = new Date(randomMillis);

                int type = rand.nextInt(3); // 0: Person, 1: RegisteredPerson, 2: OCCCPerson
                if (type == 0) {
                    personList.add(new Person(firstName, lastName, dob));
                } else if (type == 1) {
                    String govID = randomID(rand, 6, 10);
                    personList.add(new RegisteredPerson(firstName, lastName, dob, govID));
                } else {
                    String govID = randomID(rand, 6, 10);
                    RegisteredPerson rp = new RegisteredPerson(firstName, lastName, dob, govID);
                    String studentID = randomID(rand, 6, 10);
                    personList.add(new OCCCPerson(rp, studentID));
                }
            }
            Collections.shuffle(personList, rand);
            for (Person p : personList) oos.writeObject(p);

            oos.flush();
        }
    }

    /**
     * Generates a random string with a length between minLen and maxLen.
     * Alternates consonants and vowels, and occasionally inserts double letters or legal English blends/diphthongs.
     */
    private static String randomString(Random rand, int minLen, int maxLen) {
        int len = rand.nextInt(maxLen - minLen + 1) + minLen;
        StringBuilder sb = new StringBuilder();

        // Helper to check if a char is a vowel
        java.util.function.Predicate<Character> isVowel = c -> "aeiouy".indexOf(Character.toLowerCase(c)) >= 0;

        // Start with a capital consonant
        char c = (char) ('a' + rand.nextInt(26));
        while (isVowel.test(c)) c = (char) ('a' + rand.nextInt(26));
        sb.append(Character.toUpperCase(c));
        boolean useVowel = true;

        while (sb.length() < len) {
            if (useVowel) {
                // 20% chance for a vowel diphthong (double vowel)
                if (rand.nextDouble() < 0.2 && sb.length() < len - 1) {
                    char v1 = "aeiouy".charAt(rand.nextInt(6));
                    char v2 = "aeiouy".charAt(rand.nextInt(6));
                    sb.append(v1).append(v2);
                } else {
                    char v = "aeiouy".charAt(rand.nextInt(6));
                    sb.append(v);
                }
            } else {
                // 15% chance for a consonant blend (double consonant or common English blend)
                if (rand.nextDouble() < 0.15 && sb.length() < len - 1) {
                    char c1, c2;
                    do {
                        c1 = (char) ('a' + rand.nextInt(26));
                    } while (isVowel.test(c1));
                    // For blends, allow some common English patterns
                    if (rand.nextBoolean()) {
                        // Double consonant (e.g., "ll", "tt")
                        c2 = c1;
                    } else {
                        // Try to make a legal blend: r, l, h, w, y after a consonant
                        char[] blendFollowers = {'r', 'l', 'h', 'w', 'y'};
                        c2 = blendFollowers[rand.nextInt(blendFollowers.length)];
                        if (c1 == c2) c2 = 'r';
                    }
                    sb.append(c1).append(c2);
                } else {
                    char cons;
                    do {
                        cons = (char) ('a' + rand.nextInt(26));
                    } while (isVowel.test(cons));
                    sb.append(cons);
                }
            }
            useVowel = !useVowel;
        }
        return sb.substring(0, len);
    }

    /**
     * Generates a random alphanumeric string with only uppercase letters, for IDs.
     */
    private static String randomID(Random rand, int minLen, int maxLen) {
        int len = rand.nextInt(maxLen - minLen + 1) + minLen;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
}