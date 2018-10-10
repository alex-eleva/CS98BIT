import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Bit {
    private int length; //indicates the length of the list
    private boolean fixed; //indicates if there is a fixed list of bits 
    private ArrayList<Integer> bitArray; //initializes the arrayList for the bits   
    private ArrayList<Integer> listState; //initializes the arrayList that orients the list
    private final static boolean DEBUG = false; //a static boolean that is used to debug
    
    //constructor for a Bit object, when only the length of the list is defined
    public Bit(int length) {
        Random randGen = new Random();
        this.bitArray = new ArrayList<Integer>();
        this.listState = new ArrayList<Integer>();
        this.fixed = false; 
        for(int i = 1; i <= length; i++) {
            listState.add(i); //creates the list in ascending order
            bitArray.add(randGen.nextInt(2));
            //assigns a random Bit for each value in the list
        }  
    }
    
    //constructor for a Bit object when a fixed Bit list is given.
    public Bit(int length, ArrayList<Integer> bitArray) {
        this.length = length;
        this.bitArray = new ArrayList<Integer>(bitArray.subList(0, length));
        this.listState = new ArrayList<Integer>();
        this.fixed = true; //takes
        for(int i = 1; i <= length; i++) {
           listState.add(i); //creates the initial list in ascending order
        }
    }
    
    public int getCost(ArrayList<Integer> requestSequence) {
        int cost = 0; 
        //iterates through each value of the request sequence
        for(int i = 0; i < requestSequence.size(); i++) {
            //finds the index of where the requested value is in the list
            int index = listState.indexOf(requestSequence.get(i));
            if(index == -1) {
                return -1;
            }
            //checks if the corresponding Bit value is a 0
            if(bitArray.get(index) == 0) {
                bitArray.set(index, 1); //sets the Bit value to a 1
                int toFront = listState.remove(index);
                int bitToFront = bitArray.remove(index);
                listState.add(0, toFront); //moves the value to the front of the list
                bitArray.add(0, bitToFront); 
            }
            else {
                bitArray.set(index, 0);    
            }
            cost += index + 1;  //adds to the cost during each iteration of the loop
        }
        return cost;
    }
    
    public int getOpt(ArrayList<Integer> requestSequence) {  
        //key = listState
        //value = cost
        //initializes a map that will hold all possible list states of the list, given the request sequence
        TreeMap<String, Integer> listStates = new TreeMap<String, Integer>();
        listStates.put(listState.toString(), 0); //puts the initial list state into the map
        //for loop that iterates through all of the values of the request sequence
        for(int i = 0; i < requestSequence.size(); i++) {
            //System.out.println(i + "of: " + requestSequence.size());
            if(DEBUG) {
                System.out.println("request:" + requestSequence.get(i));
            }
            //creates a map that holds all listStates for the particular request sequence
            TreeMap<String, Integer> newListStates = new TreeMap<String, Integer>();
            //for each loop that looks through every element of listStates
            for(Map.Entry<String, Integer> entry : listStates.entrySet()) {
                int index = buildList(entry.getKey()).indexOf(requestSequence.get(i));
                if(DEBUG) {
                    System.out.println("key " + entry.getKey());
                    System.out.println("index:" + index);
                }
                if(index == -1) {
                    return -1000;
                }
                //calculates the subset
                double sub = Math.pow(2, index);
                for(int j = 0; j < sub; j++) {
                    //for each subset, the cost is calculated
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    String key = entry.getKey();
                    int paidCost = bitOpt(buildList(key), newList, index, j); //calls the method bitOpt
                    int accessCost = newList.indexOf(requestSequence.get(i)) + 1;
                    int runningCost = paidCost + accessCost + entry.getValue();
                    if(DEBUG) {
                        System.out.println("new list: " + newList.toString());
                        System.out.println("paid cost: " + paidCost);
                        System.out.println("accessCost: " + accessCost);
                        System.out.println("running Cost: " + runningCost);
                    }

                    Integer check = newListStates.get(newList.toString());
                    if(check == null || check > runningCost) {
                        newListStates.put(newList.toString(), runningCost);
                    }
                }
            }
            listStates = newListStates;
        }
        int cost = -1;
        //goes through all listStates in the map, and checks for the minimum cost
        for(Map.Entry<String, Integer> entry : listStates.entrySet()) {
            if(cost == -1 || entry.getValue() < cost) {
                cost = entry.getValue();
            }
        }
        //returns the minimum cost between all of the different possible list states 
        return cost;
    }
    
    //this method simply returns an arrayList containing all elements of a list that are contained in a string
    public ArrayList<Integer> buildList(String key) {
        String [] temp = key.substring(1, key.length() - 1).split(", ");
        ArrayList<Integer> listState = new ArrayList<Integer>();
        for(String s: temp) {
            listState.add(Integer.parseInt(s));
        }
        
        return listState;
    }
    
    //method that calculates the Opt of a list, using subsets
    public int bitOpt(ArrayList<Integer> listState, ArrayList<Integer> newList, int reqIndex, int subset) {
        int counter = 0; //this counts the number of elements away from the requested index that a '1' bit is found
        int cost = 0;
        //if the requested index is 0, then the cost is 0
        if(reqIndex == 0) {
            newList.addAll(listState);
            return 0;
        }
        ArrayList<Integer> copy = new ArrayList<Integer>(listState);
        //converts the number of subsets into its binary form
        String binaryString = Integer.toBinaryString(subset);
        while(binaryString.length() < reqIndex) {
            binaryString = 0 + binaryString;
        }
        for(int k = binaryString.length() - 1; k >= 0; k--) {
            //each bit in the binary representation is checked to see if it is a 1
            if(binaryString.charAt(k) == '1') {
                //the cost is incremented with the counter + 1
                cost += counter + 1;
                counter = 0;
                Integer toMove = copy.remove(k);
                reqIndex--;
                //the element that corresponds with the '1' in the binary string is moved right behind the requested index
                copy.add(reqIndex + 1, toMove); 
            }
            else {
                //if the bit is a '0', then the counter is incremented
                counter++;
            }
        }
        newList.addAll(copy);
        return cost;
    }
    
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public ArrayList<Integer> getBitArray() {
        return bitArray;
    }

    public void setBitArray(ArrayList<Integer> bitArray) {
        this.bitArray = bitArray;
    }

    public ArrayList<Integer> getListState() {
        return listState;
    }

    public void setListState(ArrayList<Integer> listState) {
        this.listState = listState;
    }
 
}
