package com.Fritz;

import java.util.ArrayList;

public class Solver
{
	public static void main(String[] args)
	{
		Simulation s = new Simulation();
		s.loadData("data.csv");
		String[][] sequenceA = s.loadSequence("solver_sequenceA.csv");
		String[][] sequenceB = s.loadSequence("solver_sequenceB.csv");
		
		//load solver.csv:
		double interval_a = 300000;
		double interval_b = 3600000;
		//load all permutation options into an array
		System.out.println("Loading state choices from: " + "solver.csv");
		String[][] dataFromFile = FileManager.loadCSV("solver.csv");
		
		int[][] numberOfChoices = new int[5][3];
		int[][][] simulation_states = new int[5][3][];
		
		for (int row = 0; row < dataFromFile.length; row++)
		{
			String[] line;
			try
			{
				line = dataFromFile[row];
			} catch (Exception e){ continue; }
			
			if (! line[0].contains("#"))
			{
				boolean stop = false;
				int choices = 0;
				int[] values = new int[30];
				for (int i = 0; i < 30; i++)
				{
					if (stop){ continue; }
					try
					{
						values[i] = Integer.valueOf(line[i + 1]);
						choices = i + 1;
					} catch (Exception e)
					{
						stop = true;
					}
					
				}
				
				switch (line[0])
				{
					case "M_ON":    simulation_states[0][0] = values; numberOfChoices[0][0] = choices; break;
					case "M_OFF":   simulation_states[0][1] = values; numberOfChoices[0][1] = choices; break;
					case "A_ON":    simulation_states[1][0] = values; numberOfChoices[1][0] = choices; break;
					case "A_OFF":   simulation_states[1][1] = values; numberOfChoices[1][1] = choices; break;
					case "G_ON":    simulation_states[2][0] = values; numberOfChoices[2][0] = choices; break;
					case "G_OFF":   simulation_states[2][1] = values; numberOfChoices[2][1] = choices; break;
					case "F_ON":    simulation_states[3][0] = values; numberOfChoices[3][0] = choices; break;
					case "F_OFF":   simulation_states[3][1] = values; numberOfChoices[3][1] = choices; break;
					case "L_RX":    simulation_states[4][0] = values; numberOfChoices[4][0] = choices; break;
					case "L_TX":    simulation_states[4][2] = values; numberOfChoices[4][2] = choices; break;
					case "L_OFF":   simulation_states[4][1] = values; numberOfChoices[4][1] = choices; break;
					case "sequenceA":   interval_a = Double.parseDouble(line[1]);
					case "sequenceB":   interval_b = Double.parseDouble(line[1]);
				}
			}
		}
		
		//String[][] log = new String[1000000][12];
		
		ArrayList<String[]> log = new ArrayList<>();
		String[] entry = new String[12];
		entry[0]    = "Total day mJoules";
		entry[1]    = "M_ON";
		entry[2]    = "M_OFF";
		entry[3]    = "A_ON";
		entry[4]    = "A_OFF";
		entry[5]    = "G_ON";
		entry[6]    = "G_OFF";
		entry[7]    = "F_ON";
		entry[8]    = "F_OFF";
		entry[9]    = "L_RX";
		entry[10]   = "L_TX";
		entry[11]   = "L_OFF";
		log.add(entry);
		
		int logLine = 0;
		
		for (int m_on = 0; m_on < numberOfChoices[0][0]; m_on++)
		{
			for (int m_off = 0; m_off < numberOfChoices[0][1]; m_off++)
			{
				for (int a_on = 0; a_on < numberOfChoices[1][0]; a_on++)
				{
					for (int a_off = 0; a_off < numberOfChoices[1][1]; a_off++)
					{
						for (int f_on = 0; f_on < numberOfChoices[2][0]; f_on++)
						{
							for (int f_off = 0; f_off < numberOfChoices[2][1]; f_off++)
							{
								for (int g_on = 0; g_on < numberOfChoices[3][0]; g_on++)
								{
									for (int g_off = 0; g_off < numberOfChoices[3][1]; g_off++)
									{
										for (int l_rx = 0; l_rx < numberOfChoices[4][0]; l_rx++)
										{
											for (int l_tx = 0; l_tx < numberOfChoices[4][2]; l_tx++)
											{
												for (int l_off = 0; l_off < numberOfChoices[4][0]; l_off++)
												{
													System.out.println(logLine);
													
													int[][] state_choice = new int[5][3];   //permutation that we're testing...
													
													state_choice[0][0] = simulation_states[0][0][m_on];
													state_choice[0][1] = simulation_states[0][1][m_off];
													state_choice[1][0] = simulation_states[1][0][a_on];
													state_choice[1][1] = simulation_states[1][1][a_off];
													state_choice[2][0] = simulation_states[2][0][f_on];
													state_choice[2][1] = simulation_states[2][1][f_off];
													state_choice[3][0] = simulation_states[3][0][g_on];
													state_choice[3][1] = simulation_states[3][1][g_off];
													state_choice[4][0] = simulation_states[4][0][l_rx];
													state_choice[4][2] = simulation_states[4][2][l_tx];
													state_choice[4][1] = simulation_states[4][1][l_off];
													
													s.setStateChoices(state_choice);
													double idleCost = s.getIdleCost();
													
													s.setSequence(sequenceA);
													double cost_a = s.simulate();
													double time_a = s.getSimTime();
													
													s.setSequence(sequenceB);
													double cost_b = s.simulate();
													double time_b = s.getSimTime();
													
													
													double occurrences_a = (24*60*60*1000)/interval_a;
													double totalCost_a = occurrences_a*cost_a;
													double totalTime_a = occurrences_a*time_a;
													
													double occurrences_b = (24*60*60*1000)/interval_b;
													double totalCost_b = occurrences_b*cost_b;
													double totalTime_b = occurrences_b*time_b;
													
													double totalDayCost = totalCost_a + totalCost_b + (((24*60*60*1000) - totalTime_a - totalTime_b) *0.001 * idleCost);
													
													System.out.println("totalTimeA = " + time_a);
													System.out.println("totalTimeB = " + time_b);
													//System.out.println("totalCostA = " + totalCost_a);
													//System.out.println("totalCostB = " + totalCost_b);
													//System.out.println("((24*60*60*1000) - totalTime_a - totalTime_b) = " + ((24*60*60*1000) - totalTime_a - totalTime_b));
													//System.out.println("totalTimeB = " + totalTime_b);
													
													entry = new String[12];
													entry[0]    = String.valueOf(totalDayCost);
													entry[1]    = String.valueOf(state_choice[0][0]);
													entry[2]    = String.valueOf(state_choice[0][1]);
													entry[3]    = String.valueOf(state_choice[1][0]);
													entry[4]    = String.valueOf(state_choice[1][1]);
													entry[5]    = String.valueOf(state_choice[2][0]);
													entry[6]    = String.valueOf(state_choice[2][1]);
													entry[7]    = String.valueOf(state_choice[3][0]);
													entry[8]    = String.valueOf(state_choice[3][1]);
													entry[9]    = String.valueOf(state_choice[4][0]);
													entry[10]   = String.valueOf(state_choice[4][2]);
													entry[11]   = String.valueOf(state_choice[4][1]);
													
													log.add(entry);
													
													
													logLine++;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		String[][] logArr = log.toArray(new String[log.size()][]);
		
		FileManager.writeCSV(logArr, "solver_out.csv");
		
	}
	
	//private static void haxxxxxor(int m_on, int m_off, int a_on, int a_off, int f_on, int f_off, int g_on, int g_off, int l_rx, int l_tx, int l_off)
	//{
	//
	//}
}
