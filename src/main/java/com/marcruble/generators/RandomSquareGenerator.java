package com.marcruble.generators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.Scanner;

public class RandomSquareGenerator {

    /**
     * Generates a random square with given number of rows
     * and columns and saves it at given filename.
     * @param rows number of rows
     * @param columns number of columns
     * @param conditions number of conditions
     * @param fileName name of file
     */
    public static void Generate(int rows, int columns, int conditions, String fileName, Random random)
    {
        // first generate a 2D array, then transform to string
        int[][] square = new int[rows][columns];
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                // generate random number
                square[i][j] = random.nextInt(conditions) + 1;
            }
        }

        // convert to string
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                text.append(square[i][j]);
                text.append(" ");
            }
            text.append("\n");
        }

        // make sure file has correct ending
        if (!fileName.endsWith(".txt"))
            fileName += ".txt";

        // save file in results folder
        Path path = Paths.get("results/" + fileName);
        try {
            new File("results").mkdir(); // make sure results folder exists
            Files.writeString(path, text.toString().strip(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("[Error] occured while saving Balanced Latin Square");
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        // read parameters
        System.out.println("Enter seed for random numbers: ");
        int seed = scanner.nextInt();
        System.out.print("Enter number of rows: ");
        int rows = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter number of columns: ");
        int columns = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter number of possible conditions: ");
        int conditions = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter number of files: ");
        int files = scanner.nextInt();
        scanner.nextLine();

        // read file name
        System.out.println("Enter starting name of file to store result: ");
        String startName = scanner.next();

        // generate files
        Random random = new Random(seed);

        for (int i = 0; i < files; i++)
        {
            String fileName = startName + (i+1);
            Generate(rows, columns, conditions, fileName, random);
        }
    }
}
