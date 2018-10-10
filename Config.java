import java.util.ArrayList;

public class Config {
    public ArrayList<Integer> requestSequence;
    public ArrayList<Integer> bitArray;
    public ArrayList<Integer> listState;
    public boolean opt;
    public int length;
    public int numTimes;
    public int reqSeqLength;
    
    public Config() {
        requestSequence = new ArrayList<Integer>();
        bitArray = new ArrayList<Integer>();
        listState = new ArrayList<Integer>();
        opt = false;
        length = 0;
        numTimes = 0;
        reqSeqLength = 0;
    }
}
