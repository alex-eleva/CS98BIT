import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Config c = new Config(); //a new config object is created that will store the data of the list publicly
        int optimal = -1; //initializes the optimal cost to -1 to begin.
        String bitConfig = "";
        int listLength = 6; //initializes the length of the list
        for(int i = 0; i < listLength/2; i++) { //this for loop creates a bit configuration that is half 0s and half 1s
            bitConfig = "0 " + bitConfig + "1 ";
            
        }
        //saves information to a file that will then be read, using the saveToFile method
        try {
           saveToFile("test.txt", listLength, bitConfig, listLength, 4, "Opt");
        }
        catch(IOException e) {  
        }
        //reads the information from a file to manipulate, using the readFile method
        try{
            readFile("test.txt", c);
        }
        catch(Exception e) {
        }
        //checks if the request Sequence stored in c is null. 
        //If this is true, a sequence of all 1s will be created, and all request Sequence configurations will be taken into account
        if(c.requestSequence == null) {
            c.requestSequence = new ArrayList<Integer>(Collections.nCopies(c.reqSeqLength, 1));
            boolean header = true; //this boolean simply checks if the titles of each table column should be printed
            do {
                //checks if the optimal needs to be calculated
                if(c.opt) {
                    Bit opt = new Bit(c.length);
                    optimal = opt.getOpt(c.requestSequence);
                }
                
                header = createRecord(c, optimal, header);
                
            } 
            while((c.requestSequence = nextSequence(c.length, c.requestSequence)) != null);
        }
        //if the request Sequence stored in c is defined, then the cost of the sequence will be calculated accordingly.
        else {
            //checks if the optimal needs to be calculated
            if(c.opt) {
                Bit opt = new Bit(c.length);
                optimal = opt.getOpt(c.requestSequence);
            }
            createRecord(c, optimal, true); 
        }
    }
    
    public static void readFile(String fileName, Config c) throws IOException {
        
        FileInputStream inputFile = null;
        Scanner sc = null; 
        try {
            inputFile = new FileInputStream(fileName); 
            sc = new Scanner(inputFile); 
            c.length = sc.nextInt(); //#1 gets the length of the list
            sc.nextLine();
            String line = sc.nextLine();
            
            if(line.isEmpty()) { //#2 checks if a fixed bit array was given
                c.bitArray = null;
            }
            else {
                String [] bitNumbers = line.split(" ");
                for(int i = 0; i < bitNumbers.length; i++) {
                    c.bitArray.add(Integer.parseInt(bitNumbers[i]));
                }
            }
            if(c.bitArray != null && c.bitArray.size() != c.length) {
                throw new RuntimeException("Length of bit array does not match.");
            }
            c.reqSeqLength = sc.nextInt(); //#3 request sequence length
            sc.nextLine();
            line = sc.nextLine();
            if(line.isEmpty()) {  // #4: checks if a request sequence is given
                c.requestSequence = null;
            }
            else {
                String [] requestNumbers = line.split(" ");
                for(int i = 0; i < requestNumbers.length; i++) {
                    c.requestSequence.add(Integer.parseInt(requestNumbers[i]));
                }
            }
            //makes sure the given request sequence has the same length as it is supposed to in the file
            if(c.requestSequence != null && c.requestSequence.size()!= c.reqSeqLength) {
                throw new RuntimeException("Length of Request Seqence does not match.");
            }

            c.numTimes = sc.nextInt(); //#5 gets the number of times the cost should be calculated
            sc.nextLine();
            line = sc.nextLine();
            c.opt = line.equals("Opt"); //#6 checks if Opt need to be calculated

        }
        catch(FileNotFoundException e) { //catches a FileNotFoundException, with a message
            System.out.println("Exception: File '" + fileName + "' not found.");
        }
        catch(RuntimeException r) {
            System.out.println("Runtime Exception was thrown. Unable to properly parse configuration file");
            System.out.println(r.getMessage());
            r.printStackTrace();
            
        }
        finally {
            if(inputFile != null) {
                inputFile.close();
                sc.close();
            }
            else {
                //throws IOException if there are issues when closing the file.
                throw new IOException(); 
            }
        }
         
    }
    
    public static String buildStringSequence(ArrayList<Integer> list) {
        String line = "";
        for(int i = 0; i < list.size(); i++) {
            line += list.get(i) + " "; //simply adds a space between each element of an arrayList
        }
        line = line.trim();
        return line; //returns the string representation of the arrayList
    }

    //this method will return the next requestSequence, when given the current requestSequence and the length of said list
    public static ArrayList<Integer> nextSequence(int length, ArrayList<Integer>curr) {
        ArrayList<Integer> next = new ArrayList<Integer>(); //creates an empty arrayList that will be modified and returned
        boolean flag = true; //a flag that checks if the new arrayList needs to be manipulated further
        for(int i = 0; i < curr.size(); i++) {
            if(flag) {
                if(curr.get(i) < length) {
                    next.add(curr.get(i) + 1); //increments 1 to the element
                    flag = false; //sets flag to false
                }
                else {
                    next.add(1);
                }
            }
            else {
                next.add(curr.get(i)); //if flag is false, the original element value is copied into the new array
            }
        }
        if(flag) {
            
            return null;
        }
        
        return next;
    }

    public static boolean createRecord(Config c, int optCost, boolean header) {
        ArrayList<Record> recordList = new ArrayList<Record>(); //an arrayList of records is created to store all of the times the algorithm is calculated
        Bit test;
        int costSum = 0;
        double costAvg = 0;
        for(int i = 0; i < c.numTimes; i++) {
            Record r = new Record();
            if(c.bitArray == null) {
                test = new Bit(c.length);
            }
            else {
                test = new Bit(c.length, c.bitArray);
            }
            //the contents of the record are filled in order to 
            r.bitArray = buildStringSequence(test.getBitArray());
            r.cost = test.getCost(c.requestSequence);
            //the sum of the cost is continually added
            costSum += r.cost;
            r.requestSequence = buildStringSequence(c.requestSequence);
            r.newBitArray = buildStringSequence(test.getBitArray());
            r.listState = buildStringSequence(test.getListState());
            //each new Record is added into the arrayList
            recordList.add(r);
        }
        
        //cost average is calculated by dividing the costSum by the number of times the algorithm was run
        costAvg = (double)costSum/c.numTimes;
        
        //these lines simply configure the table to 
        int reqLength = findMax("Request Sequence", recordList.get(0).requestSequence) + 5;
        int bitLength = findMax("BIT Configuration", recordList.get(0).bitArray) + 5;
        int endLength = findMax("Ending Configuration", recordList.get(0).listState) + 5;
        int newBitLength = findMax("New BIT Config", recordList.get(0).newBitArray) + 5;
        int maxCost = recordList.get(0).listState.length()*recordList.get(0).requestSequence.length() + 5;
        
        if(header) {
            System.out.printf("%-"+ reqLength +"s%-" + bitLength + "s%-"+ endLength + "s%-" + newBitLength + "s%-" + maxCost + "s%-" + maxCost + "s%-" + maxCost + "s\n", 
                "Request Sequence", "BIT Configuration", "Ending Configuration", "New BIT Config", "Cost", "OPT Cost", "Ratio");
        }
        
        //prints out the results in the proper formatting, for each calculation of the algorithm 
        for(int i = 0; i <recordList.size(); i++) {
            System.out.printf("%-"+ reqLength +"s%-" + bitLength + "s%-"+ endLength + "s%-" + newBitLength + "s%-" + maxCost + 
                "s%-" + maxCost + "s%-" + maxCost + "s\n", recordList.get(i).requestSequence, recordList.get(i).bitArray, recordList.get(i).listState, recordList.get(i).newBitArray, 
                recordList.get(i).cost, optCost, optCost == 0 ? "NaN" : "" + (double)recordList.get(i).cost/optCost);
        }
        //these lines print out a final summary of the calculations of the algorithm
        if(recordList.size() > 1) {
            System.out.println("*********************************************");
            System.out.println("Average Cost: " + costAvg);
            System.out.println("OPT Cost: " + optCost);
            System.out.println("Average Ratio: " + (double)costAvg/optCost);
            System.out.println("*********************************************");
        }
        
        return recordList.size() > 1;
    }
    
    public static int findMax(String a, String b) {
        int aLength = a.length();
        int bLength = b.length();
        if(aLength > bLength) {
            return aLength;
        }
        return bLength;
    }

    //this method saves the contents of the algorithm parameters into a file.
    //int l is used to create the request sequence
    public static void saveToFile(String fileName, int listLength, String bitConfig, int l, int numTimes, String opt) throws IOException {
        double requestLength = 20 * (9 * l);
        PrintWriter pw = null;
        FileOutputStream fout = null;
        
        try {
            fout = new FileOutputStream(fileName);
            pw =  new PrintWriter(fout);
            //the list length is printed on the first line of the file
            pw.println(listLength);
            //the bit configuration is printed on the next line of the file
            pw.println(bitConfig);
            //the request sequence length i printed on the next line file
            pw.println((int)requestLength);
            for(int i = 0; i < 20; i++) {
                //the step through helper method adds the request sequence, with a specific pattern. 
                stepThrough(l, false, 1, pw);
                stepThrough(l, false, 4, pw);
                stepThrough(l, false, 1, pw);
                stepThrough(l, false, 3, pw);
            }
            pw.println();
            //the number of times the algorithm should be calculated is printed on the next line of the file
            pw.println(numTimes);
            //whether or not Opt should be calculated is printed on the next line of the file
            pw.println(opt);
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found.");
        }
        finally {
            if(pw != null) {
                pw.close();
                fout.flush();
                fout.close();
            }
            else {
                //throws an IOexception if there is an issue with closing the file.
                throw new IOException(); 
            }
        }
    }
    
    //the step through method used a particular pattern to create the request sequence
    //if the boolean for ascending order is true, then the request sequence is appended as such:
    //1 is printed "exponent" number of times, 2 is printed "exponent" number of times, all the way to l
    //if the boolean for descending order is fale, then the request sequence is appended as such:
    //l is printed "exponent" number of times, l-1 is printed "exponent" times, all the way to 1.
    public static void stepThrough(int l, boolean asc, int exponent, PrintWriter pw) {
        if(asc) {
            for(int i = 1; i <= l; i++) {
                for(int j = 0; j < exponent; j++) {
                    pw.print(i + " ");
                }
            } 
        }
        else {
            for(int i = l; i > 0; i--) {
                for(int j = 0; j < exponent; j++) {
                    pw.print(i + " ");
                } 
            } 
        }
    }

}

