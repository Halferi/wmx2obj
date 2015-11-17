package wmx2obj;

import java.util.ArrayList;

public class Normals {
    
    private final int id;
    
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    private final static int COORDINATE_COUNT = 3;
    private final static int COORDINATE_BYTES = 2;
    
    private final ArrayList<Double> coordinates = new ArrayList();
    
    public Normals(int id){
        this.id = id;
    }
    
    public void addCoordinate(double coordinate) {
        coordinates.add(coordinate);
    }
    
    public void findCoordinates(byte[] data, int offset) {

        for (int coordinate = 0; coordinate < COORDINATE_COUNT; coordinate++) {
            
            String coordinateString = "";
            for (int b = 0; b < COORDINATE_BYTES; b++) {
                int dataIndex    = offset + coordinate * COORDINATE_BYTES + b;
                String nextByte  = String.format("%02x", 
                                                 data[dataIndex] & 0xff);
                coordinateString = nextByte + coordinateString;
            }
            
            int coordinateValue = Integer.parseInt(coordinateString, 16);
            int[] b = {coordinateValue & 0xff, (coordinateValue >>> 8) & 0xff};
            short int16 = (short) (((b[1] & 0xff) << 8) | (b[0] & 0xff));
            
            addCoordinate(int16);
            
            //System.out.println(offset);
        }
    }
    
    public int getId() {
        return id;
    }
    
    public double getCoordinate(int coordinate) {
        return coordinates.get(coordinate);
    }
}
