package com.Fritz;

import java.util.ArrayList;

public class Simulation
{
	/*
	0 - M
	1 - A
	2 - G
	3 - F
	4 - L
	*/
	
	//STATE DATA
	private double[][] current;                                     // [device][state] = xxx (mA)
	private double[][][] changeTime;                                // [device][stateFrom][stateTo] = xxx (ms)
	
	//SIMULATION SEQUENCE
	private String[][] sequence;                                    // [row][0]	= device		|| [row][1] = instruction
	private int[][] simulation_states;                              // [device][0] = on_state 	|| [device][1] = off_state
	
	//CURRENT SIMULATION VARIABLES
	private int[] state = new int[]{0, 0, 0, 0, 0};                 // [device] = current state
	private ArrayList<String[]> log;
	private double totalTime = 0;
	
	public void loadData(String dataFile)
	{
		System.out.println("Loading data from: " + dataFile);
		current = new double[5][30];
		changeTime = new double[5][15][30];
		
		String[][] dataFromFile = FileManager.loadCSV(dataFile);
		
		for (int row = 0; row < dataFromFile.length; row++)
		{
			String val0 = "";
			try
			{
				val0 = dataFromFile[row][0];
			} catch (Exception e)
			{
			}
			
			int device = - 1;
			switch (val0)
			{
				case "M":
					device = 0;
					break;
				case "A":
					device = 1;
					break;
				case "G":
					device = 2;
					break;
				case "F":
					device = 3;
					break;
				case "L":
					device = 4;
					break;
				default:
					device = - 1;
					break;
			}
			if (device != - 1)
			{
				int state = - 1;
				double currentValue = 0;
				try
				{
					state = Integer.valueOf(dataFromFile[row][1]);
					currentValue = Double.parseDouble(dataFromFile[row][2]);
				} catch (Exception e)
				{
				}
				
				//System.out.print(val0);
				//System.out.print("," + "\tstate = " + state);
				//System.out.print("," + "\tcurrent = " + currentValue);
				current[device][state] = currentValue;
				
				if (state != - 1)
				{
					double[] times = new double[]{0};
					try
					{
						if (dataFromFile[row].length > 3)
						{
							times = new double[dataFromFile[row].length - 1];
							for (int column = 3; column < dataFromFile[row].length; column++)
							{
								times[column - 3] = Double.parseDouble(dataFromFile[row][column]);
								//System.out.print("," + "\ttime = " + times[column - 3]);
							}
						}
						
					} catch (Exception e)
					{
					}
					changeTime[device][state] = times;
				}
				
			}
			//System.out.println();
		}
		
		/*
		for (int i = 0; i < current.length; i++)
		{
			for (int j = 0; j < current[i].length; j++)
			{
				if (current[i][j] != 0)
				{
					System.out.println("device " + i + " - state " + j + " - current = " + current[i][j]);
				}
			}
		}
		
		for (int i = 0; i < changeTime.length; i++)
		{
			for (int j = 0; j < changeTime[i].length; j++)
			{
				for (int k = 0; k < changeTime[i][j].length; k++)
				{
					if (changeTime[i][j][k] != 0)
					{
						System.out.println("device " + i + " - from " + j + " - to " + k + " - time = " + changeTime[i][j][k]);
					}
				}
			}
		}
		*/
	}
	
	public void setStateChoices(int[][] _simulation_states)
	{
		simulation_states = _simulation_states;
	}
	
	public int[][] loadStateChoices(String sequenceFile)
	{
		System.out.println("Loading state choices from: " + sequenceFile);
		String[][] dataFromFile = FileManager.loadCSV(sequenceFile);
		
		int sequencePos = 0;
		int[][] simulation_states = new int[5][3];
		
		for (int row = 0; row < dataFromFile.length; row++)
		{
			String[] line;
			try
			{
				line = dataFromFile[row];
			} catch (Exception e){ continue; }
			
			if (! line[0].contains("#"))
			{
				switch (line[0])
				{
					case "M_ON":    simulation_states[0][0] = Integer.valueOf(line[1]); break;
					case "M_OFF":   simulation_states[0][1] = Integer.valueOf(line[1]); break;
					case "A_ON":    simulation_states[1][0] = Integer.valueOf(line[1]); break;
					case "A_OFF":   simulation_states[1][1] = Integer.valueOf(line[1]); break;
					case "G_ON":    simulation_states[2][0] = Integer.valueOf(line[1]); break;
					case "G_OFF":   simulation_states[2][1] = Integer.valueOf(line[1]); break;
					case "F_ON":    simulation_states[3][0] = Integer.valueOf(line[1]); break;
					case "F_OFF":   simulation_states[3][1] = Integer.valueOf(line[1]); break;
					case "L_RX":    simulation_states[4][0] = Integer.valueOf(line[1]); break;
					case "L_TX":    simulation_states[4][2] = Integer.valueOf(line[1]); break;
					case "L_OFF":   simulation_states[4][1] = Integer.valueOf(line[1]); break;
				}
			}
		}
		return simulation_states;
	}
	
	public void setSequence(String[][] _seq)
	{
		sequence = _seq;
	}
	
	public String[][] loadSequence(String sequenceFile)
	{
		System.out.println("Loading sequence from: " + sequenceFile);
		String[][] dataFromFile = FileManager.loadCSV(sequenceFile);
		
		int sequencePos = 0;
		String[][] sequence = new String[100][2];
		
		for (int row = 0; row < dataFromFile.length; row++)
		{
			String[] line;
			try
			{
				line = dataFromFile[row];
			} catch (Exception e){ continue; }
			
			if (line[0].equals("M") || line[0].equals("A") || line[0].equals("G") || line[0].equals("F") || line[0].equals("L"))
			{
				//System.out.println(line[1]);
				sequence[sequencePos][0] = line[0];
				sequence[sequencePos][1] = line[1];
				sequencePos++;
			}
			
			if (line[0].equals("T"))
			{
				//System.out.println(line[1]);
				sequence[sequencePos][0] = line[0];
				sequence[sequencePos][1] = line[1];
				sequencePos++;
			}
		}
		
		String[][] sequenceTrimmed = new String[sequencePos][2];
		System.arraycopy(sequence, 0, sequenceTrimmed, 0, sequencePos);
		sequence = sequenceTrimmed;
		return sequence;
	}
	
	public double simulate()
	{
		totalTime = 0;
		System.out.println("Running sequence simulation");
		int rows = sequence.length;
		log = new ArrayList<>();
		//log = new String[100][8];
		
		int logRow = 0;
		String[] entry = new String[8];
		entry[0]    = "Time (mS)";
		entry[1]    = "Total current (mA)";
		entry[2]    = "Accumulative energy (mJoules)";
		entry[3]    = "MCU current (mA)";
		entry[4]    = "Accelo. current (mA)";
		entry[5]    = "GPS current (mA)";
		entry[6]    = "Flash current (mA)";
		entry[7]    = "LoRa current (mA)";
		log.add(entry);
		logRow++;
		
		for (int i = 0; i < state.length; i ++)
		{
			state[i] = simulation_states[i][1];  // setting all device to off_state
		}
		
		entry = new String[8];
		entry[0]    = String.valueOf(0);
		entry[1]    = String.valueOf(current());
		entry[2]    = String.valueOf(0);;
		entry[3]    = String.valueOf(current[0][state[0]]);
		entry[4]    = String.valueOf(current[1][state[1]]);
		entry[5]    = String.valueOf(current[2][state[2]]);
		entry[6]    = String.valueOf(current[3][state[3]]);
		entry[7]    = String.valueOf(current[4][state[4]]);
		log.add(entry);
		logRow++;
		
		
		double totalCost = 0;
		double step_time = 0;
		double step_current = 0;
		
		
		
		for (int row = 0; row < rows; row++)
		{
			String device = sequence[row][0];
			String instruction = sequence[row][1];
			
			
			int deviceNumber = -1;
			int state_from = -1;
			int state_to = -1;
			int state_power = 0;
			
			try
			{
				if (instruction.equals("ON"))
				{
					state_power = 0;
				}
				else if (instruction.equals("OFF"))
				{
					state_power = 1;
				}
				else if (instruction.equals("RX"))
				{
					state_power = 0;
				}
				else if (instruction.equals("TX"))
				{
					state_power = 2;
				}
			} catch (Exception e){continue;}
			
			
			switch (device)
			{
				case "M":   deviceNumber = 0;   break;
				case "A":   deviceNumber = 1;   break;
				case "G":   deviceNumber = 2;   break;
				case "F":   deviceNumber = 3;   break;
				case "L":   deviceNumber = 4;   break;
				case "T":   step_time = Double.parseDouble(instruction);    break;
				default:    break;
			}
			
			if (deviceNumber != -1)
			{
				state_from = state[deviceNumber];
				state_to = simulation_states[deviceNumber][state_power];
				state[deviceNumber] = state_to;
				step_time = changeTime[deviceNumber][state_from][state_to];
			}
			else
			{
				step_current = current();
			}
			
			if (instruction.equals("ON")) step_current = current();
			
			entry = new String[8];
			entry[0]    = String.valueOf(totalTime);
			entry[1]    = String.valueOf(step_current);
			entry[2]    = String.valueOf((totalCost/1000)*3.3);
			entry[3]    = String.valueOf(current[0][state[0]]);
			entry[4]    = String.valueOf(current[1][state[1]]);
			entry[5]    = String.valueOf(current[2][state[2]]);
			entry[6]    = String.valueOf(current[3][state[3]]);
			entry[7]    = String.valueOf(current[4][state[4]]);
			log.add(entry);
			logRow++;
			
			
			totalCost += step_current*step_time;
			if (instruction.equals("OFF")) step_current = current();
			
			totalTime += step_time;
			
			entry[0]    = String.valueOf(totalTime);
			entry[1]    = String.valueOf(step_current);
			entry[2]    = String.valueOf((totalCost/1000)*3.3);
			entry[3]    = String.valueOf(current[0][state[0]]);
			entry[4]    = String.valueOf(current[1][state[1]]);
			entry[5]    = String.valueOf(current[2][state[2]]);
			entry[6]    = String.valueOf(current[3][state[3]]);
			entry[7]    = String.valueOf(current[4][state[4]]);
			log.add(entry);
			logRow++;
		}
		
		//String[][] logTrimmed = new String[999][8];
		//System.arraycopy(log, 0, logTrimmed, 0, 999);
		//sequence = sequenceTrimmed;


		totalCost = (totalCost/1000)*3.3;        //conversion from mA*mS -> milli Joules (voltage is 3v3) (milliJoules is mW*S)
		return totalCost;
	}
	
	public double getSimTime()
	{
		return totalTime;
	}
	
	public double getIdleCost()
	{
		for (int i = 0; i < state.length; i ++)
		{
			state[i] = simulation_states[i][1];  // setting all device to off_state
		}
		return current();
	}
	
	private void saveLog(String filename)
	{
		String[][] logArr = log.toArray(new String[log.size()][]);
		
		FileManager.writeCSV(logArr, filename);
	}
	
	private String[][] convertArray(int[][] input)
	{
		String[][] arr = new String[0][0];
		try
		{
			arr = new String[input.length][input[0].length];
			for (int i = 0; i < input.length; i++)
			{
				for (int j = 0; j < 2; j ++)
				{
					arr[i][j] = String.valueOf(input[i][j]);
				}
			}
		} catch (Exception e){}
		return arr;
	}
	
	private String[][] convertArray(double[][] input)
	{
		String[][] arr = new String[0][0];
		try
		{
			arr = new String[input.length][input[0].length];
			for (int i = 0; i < input.length; i++)
			{
				for (int j = 0; j < 2; j ++)
				{
					arr[i][j] = String.valueOf(input[i][j]);
				}
			}
		} catch (Exception e){}
		return arr;
	}
	
	private double current()
	{
		double total = 0;
		//System.out.print("0");
		for (int i = 0; i < state.length; i ++)
		{
			//System.out.print(" + " + current[i][state[i]]);
			total += current[i][state[i]];
		}
		//System.out.println(" = " + total);
		return total;
	}
	
	public static void main(String[] args)
	{
		Simulation s = new Simulation();
		s.loadData("data.csv");
		
		String[][] seq = s.loadSequence("simulate_sequence.csv");
		int[][] choices = s.loadStateChoices("simulate_sequence.csv");
		
		long timPrev = System.currentTimeMillis();
		
		s.setSequence(seq);
		s.setStateChoices(choices);
		
		s.simulate();
		s.saveLog("simulate_out.csv");
		
		System.out.println(System.currentTimeMillis() - timPrev);
	}
	
}