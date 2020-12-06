package com.marcruble.generators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class BalancedLatinSquareGenerator {

    /**
     * Generates a balanced latin square with given number of conditions
     * and saves it at given filename.
     * As explained here:
     * Martyn Shuttleworth (May 8, 2009). Counterbalanced Measures Design.
     * Retrieved Dec 06, 2020 from Explorable.com:
     * https://explorable.com/counterbalanced-measures-design
     *
     * @param size number of conditions
     * @param fileName name of file
     */
    public static void Generate(int size, String fileName)
    {
        // first generate a 2D array, then transform to string
        int[][] square = new int[size][size];
        StringBuilder text = new StringBuilder();

        // generate first line of balanced latin square
        int forward = 2;
        int backward = 0;

        for (int i = 0; i < size; i++)
        {
            if (i == 0)
                square[0][i] = 1;
            else if (i % 2 != 0)
                square[0][i] = forward++;
            else
                square[0][i] = size - (backward++);
        }

        // now generate the following lines by adding one to all
        for (int i = 1; i < size; i++) //rows
        {
            for (int j = 0; j < size; j++) //columns
            {
                int next = square[i-1][j] + 1;
                if (next > size)
                    square[i][j] = next % size;
                else
                    square[i][j] = next;
            }
        }

        // if size is odd, we need a mirrored version too
        int[][] mirrored = new int[size][size];

        if (size % 2 != 0)
        {
            for (int i = 0; i < size; i++) //rows
            {
                for (int j = 0; j < size; j++) //columns
                {
                    mirrored[i][j] = square[i][size - j - 1];
                }
            }
        }

        // convert to string
        for (int i = 0; i < size; i++) //rows
        {
            for (int j = 0; j < size; j++) //columns
            {
                text.append(square[i][j]);
                text.append(" ");
            }
            text.append("\n");
        }

        if (size % 2 != 0)
        {
            // add mirrored version also
            for (int i = 0; i < size; i++) //rows
            {
                for (int j = 0; j < size; j++) //columns
                {
                    text.append(mirrored[i][j]);
                    text.append(" ");
                }
                text.append("\n");
            }
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

        // read number of conditions
        System.out.print("Enter number of possible conditions: ");
        int size = scanner.nextInt();
        scanner.nextLine();

        // read file name
        System.out.println("Enter name of file to store result: ");
        String fileName = scanner.next();

        Generate(size, fileName);
    }
}
