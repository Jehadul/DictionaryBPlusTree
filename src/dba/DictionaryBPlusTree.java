package dba;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class DictionaryBPlusTree {
    private static final String FILE_PATH = "src/dba/BengaliDictionary.txt";

    public static void main(String[] args) {
        try {
            BPlusTree dictionary = new BPlusTree(4); // order = 4
            loadFromFile(dictionary, FILE_PATH);
            System.out.println("Loaded dictionary into B+ Tree.");

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n--- English-Bangla Dictionary (B+ Tree) ---");
                System.out.println("1️. Search");
                System.out.println("2️. Insert");
                System.out.println("3. Delete");
                System.out.println("4. Display All");
                System.out.println("5️. Save and Exit");
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter English word: ");
                        String eng = sc.nextLine();
                        String result = dictionary.search(eng);
                        if (result != null)
                            System.out.println("Meaning: " + result);
                        else
                            System.out.println("Not found!");
                    }
                    case 2 -> {
                        System.out.print("Enter English word: ");
                        String eng = sc.nextLine();
                        System.out.print("Enter Bangla meaning: ");
                        String ban = sc.nextLine();
                        dictionary.insert(eng, ban);
                        System.out.println("Word added!");
                    }
                    case 3 -> {
                        System.out.print("Enter English word to delete: ");
                        String eng = sc.nextLine();
                        dictionary.delete(eng);
                        System.out.println("Word deleted.");
                    }

                    case 4 -> dictionary.display();
                    case 5 -> {
                        saveToFile(dictionary, FILE_PATH);
                        System.out.println("Dictionary saved. Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadFromFile(BPlusTree tree, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No dictionary file found. Starting empty.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    String[] parts = line.split(",", 2);
                    tree.insert(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }
    
    private static void saveToFile(BPlusTree tree, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {

            BPlusTreeNode node = tree.getRoot();

            // Go to leftmost leaf
            while (!node.isLeaf) 
            	node = node.children.get(0);

            // Traverse all leaves
            while (node != null) {
                for (int i = 0; i < node.keys.size(); i++) {
                    bw.write(node.keys.get(i) + "," + node.values.get(i));
                    bw.newLine();
                }
                node = node.next;
            }

        } catch (IOException e) {
            System.out.println("Error saving dictionary: " + e.getMessage());
        }
    }

    
}