/*

This is an example of how to use the simulator.

- Create a new project
- Copy the the 3 files from the src/com/Fritz folder
- Write your own app you could use the following main method as an example.

*** note that the following was written as pseudocode

** note that you can remove the package declarations from the provided code



*/

static void main(String[] args)
{
	// If you want to load sequence from a CSV file use the following line.
	//String[][] sequence = s.loadSequence("simulate_sequence.csv");
	
	// sequence of events including time pauses etc.
	String[][] sequence;				
	sequence[0][0] = "device";
	sequence[0][1] = "on or off?";
	sequence[1][0] = "device";
	sequence[1][1] = "on or off?";
	sequence[2][0] = "device";
	sequence[2][1] = "on or off?";
	sequence[3][0] = "device";
	sequence[3][1] = "on or off?";
	
	
	// if you want to load the state choice from a csv file use the following line:
	//int[][] states = s.loadStateChoices("simulate_sequence.csv");
	
	// linking the "on" and "off" states to data to data from the data.csv file.
	// state[device][0] - on state (RX state - LoRa only)
	// state[device][1] - off state
	// state[device][2] - TX state (LoRa only)
	int[][] states;			
	states[0][0] = 0;
	states[0][1] = 0;
	states[1][0] = 0;
	states[1][1] = 0;
	states[2][0] = 0;
	states[2][1] = 0;
	
	
	
	// "s" is the simulation object
	Simulation s = new Simulation();
	
	// the simulation needs to load the dataset - containing the current draw, timing etc for each state of each device.
	// the filename includes the relative path to the location of the file
	s.loadData("data.csv");
	
	// the simulation needs to know what the simulation sequence looks like. This is simply a String[][]
	s.setSequence(sequence);
	
	// the simulation needs to know for each on/off condition in the sequence - what state this refers to in the dataset.
	s.setStateChoices(states);
	
	// calling the member method "simulate" returns a double with total energy use in mJoules
	double energyUse = s.simulate();
	System.out.println("energy use = " + energyUse);
	
	// the simulation stores a log in memory. you can save this to a file, by uncommenting the following line:
	//s.saveLog("simulate_out.csv");			
}