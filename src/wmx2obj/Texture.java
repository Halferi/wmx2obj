package wmx2obj;

import java.util.ArrayList;

public class Texture {
    public static final int COORDINATE_COUNT = 6;
    public static final int COORDINATE_BYTES = 1;
    public static final int U0=0,V0=1,U1=2,V1=3,U2=4,V2=5;
    public static final int p = 0;
    
    private final int id;
    private final ArrayList<Double> coordinates = new ArrayList();
    private final ArrayList<Integer> texturepages = new ArrayList();
    
    public Texture(int id){
        this.id = id;
    }
    
    public void addCoordinate(double coordinate){
        if (coordinates.size() < COORDINATE_COUNT) {
            coordinates.add(coordinate);
        }
    }
    
     
    public void findTextureIndices(byte[] data, int offset){       
        for (int textureIndex = 0; textureIndex < COORDINATE_COUNT;
                textureIndex++){
            double index = data[offset + textureIndex + COORDINATE_COUNT] & 0xff;           
            addCoordinate((index+0.5)/256);
            
        }
        
    }
    
    public int getId(){
        return id;
    }
    
    public double getUV(int UV){
        return coordinates.get(UV);
    }
    
    public int getPage(){
        return texturepages.get(texturepages.size()-1);
    }
    public void print(){
        System.out.print(texturepages);
    }
}
