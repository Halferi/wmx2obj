package wmx2obj;

import java.util.ArrayList;

public class Vertex {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    private final static int COORDINATE_COUNT = 3;
    private final static int COORDINATE_BYTES = 2;
    
    private final int id;

    private final ArrayList<Double> coordinates = new ArrayList();
    
    public Vertex(int id) {
        this.id = id;
    }

    public void addCoordinate(double coordinate) {
        if (coordinates.size() < COORDINATE_COUNT) {
            coordinates.add(coordinate);
        }
    }

    public void findCoordinates(byte[] data, int offset, 
            int blockX, int blockZ,
            int segmentX, int segmentZ) {
        // find coordinate values for every dimension of the vertex
        for (int coordinate = 0; coordinate < COORDINATE_COUNT; coordinate++) {
            // convert the little-endian byte combination to an integer
            String coordinateString = "";
            for (int b = 0; b < COORDINATE_BYTES; b++) {
                coordinateString = 
                        String.format(
                                "%02x", 
                                data[offset 
                                        + coordinate * COORDINATE_BYTES 
                                        + b] & 0xff
                        ) + coordinateString;
            }
            int coordinateValue = Integer.parseInt(coordinateString, 16);
            
            // convert found coordinate value to an unsigned 16-bit value
            int[] b = {coordinateValue & 0xff, (coordinateValue >>> 8) & 0xff};
            short int16 = (short) (((b[1] & 0xff) << 8) | (b[0] & 0xff));
            
            // assign found coordinate value to the vertex
            // note that increments are needed for x and z dimensions
            // in order to find the correct position of the vertex
            if (coordinate == Vertex.X) {
                addCoordinate((int16 + blockX + segmentX) / 1000.0);
            } else if (coordinate == Vertex.Y) {
                addCoordinate(int16 / 1000.0);
            } else if (coordinate == Vertex.Z) {
                addCoordinate((int16 + blockZ + segmentZ) / 1000.0);
            }
        }
    }
    
    public int getId() {
        return id;
    }
    
    public double getCoordinate(int coordinate) {
        return coordinates.get(coordinate);
    }

}
