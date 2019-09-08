package com.Fritz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class FileManager
{
	public static void main(String[] args)
	{

		String[][] data = loadCSV("testFile.csv");

		writeCSV(data, "new.csv");



	}

	public static void writeCSV(String[][] data, String filename)
	{
		try
		{
			FileWriter csvWriter = new FileWriter(filename);

			for (int row = 0; row < data.length; row++) {
				csvWriter.append(String.join(",", data[row]));
				csvWriter.append(System.lineSeparator());
			}

			csvWriter.flush();
			csvWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String[][] loadCSV(String filename)
	{
		int numberOfLines = getNumberOfLines(filename);
		String[][] data = new String[numberOfLines][];
		try
		{
			BufferedReader csvReader = new BufferedReader(new FileReader(filename));
			String rowData;
			int row = 0;
			//System.out.println("[row][column] = data");
			while ((rowData = csvReader.readLine()) != null) {
				//System.out.print("y[" + y + "] ");
				data[row] = rowData.split(",");
				// do something with the data
				for (int column = 0; column < data[row].length; column++)
				{
					//System.out.print("[" + row + "]" + "[" + column + "] = " + data[row][column] + "\t \t");
					//System.out.print(data[row][column] + ", \t");
				}
				//System.out.println();
				row++;
			}
			csvReader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return data;
	}

	public static int getNumberOfLines(String filename)
	{
		int lines = 0;

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while (reader.readLine() != null) lines++;
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return lines;
	}

}
