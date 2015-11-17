package wmx2obj;

import java.util.ArrayList;

public class Writer {
    
    private final ArrayList<Short> vertices;
    private final ArrayList<Byte> textures;
    private final ArrayList<Double> normals;
    private final ArrayList<Integer> faces;
    private final ArrayList<Integer> texIndices;
    private static final ArrayList<Byte> orderedBytes = new ArrayList();
    private static byte[] ret = new byte[2];
    private static short shortVal;
    
    public Writer(ArrayList<Short> Vertices, ArrayList<Byte> Textures
                  , ArrayList<Double> Normals, ArrayList<Integer> Faces
                  , ArrayList<Integer> textureIndices){
        vertices = Vertices;
        textures = Textures;
        normals = Normals;
        faces = Faces;
        texIndices = textureIndices;
    }
    
    //orders each byte to FF8 readable format, far from ready
    public void orderArrays(){
        orderedBytes.add((byte) (faces.size()/3));
        orderedBytes.add((byte) (vertices.size()/3));
        orderedBytes.add((byte) 0x01);
        orderedBytes.add((byte) 0x00);

        for (int iterator = 0; iterator < faces.size(); iterator++){
            
            if (iterator%3==0 && iterator != 0){
                orderedBytes.add((byte) 0x00);  //extra faces
                orderedBytes.add((byte) 0x00);
                orderedBytes.add((byte) 0x00);
                
                for (int i = iterator; i < iterator +3;i++){
                    orderedBytes.add((byte) textures.get((texIndices.get(i-3)-1)*2));
                    orderedBytes.add((byte) textures.get((texIndices.get(i-3)-1)*2+1));
                }
                /*
                For triangles last 4 bytes use format:
                (First byte = 4-bit texturepage, 4-bit clutid)
                (Second byte = 10 , solid type)
                (Third byte = 00, if not water, rail or road texture)
                (Fourth byte = FF, for walkability and landability)
                */
                orderedBytes.add((byte) 0x01);  //face properties
                orderedBytes.add((byte) 0x22);
                orderedBytes.add((byte) 0x40);
                orderedBytes.add((byte) 0x20);
            }
            orderedBytes.add((byte)Integer.parseInt(faces.get(iterator).toString()));
            if(iterator == faces.size()-1){
                orderedBytes.add((byte) 0x00);  //extra faces
                orderedBytes.add((byte) 0x00);
                orderedBytes.add((byte) 0x00);
                
                for (int i = iterator+1; i < iterator+1 +3;i++){
                    orderedBytes.add((byte) textures.get((texIndices.get(i-3)-1)*2));
                    orderedBytes.add((byte) textures.get((texIndices.get(i-3)-1)*2+1));
                }
                
                orderedBytes.add((byte) 0x01);  //face properties
                orderedBytes.add((byte) 0x22);
                orderedBytes.add((byte) 0x40);
                orderedBytes.add((byte) 0x20);
            }
            //System.out.println(Byte.parseByte(faces.get(iterator-1).toString()));
            
        }
        for (int iterator = 0; iterator < vertices.size(); iterator++){ //incomplete
            if(iterator%3==0 && iterator != 0){
                orderedBytes.add((byte) 0x00);
                orderedBytes.add((byte) 0x00);
                //System.out.println("w");
            }
            if(iterator%3==0){
                shortToBytes(vertices.get(iterator));
                orderedBytes.add(ret[0]);
                orderedBytes.add(ret[1]);
                //System.out.println("x");
            }else{
                shortToBytes(inverseValue(vertices,iterator));
                orderedBytes.add(ret[0]);
                orderedBytes.add(ret[1]);
                //System.out.println("y or z");
            }
            
        }
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);// last coordinates for vertice W, INCOMPLETE!!!
        
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0xF0);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        orderedBytes.add((byte) 0x00);
        
    }
    
    //Converts bytes to Little-Endian format
    public void shortToBytes(int value){ 
        ret[0] = (byte) (value & 0xff);
        ret[1] = (byte) (value >> 8 & 0xff);

    }
    
    public ArrayList<Byte> getProcessedBytes(){
        return orderedBytes;
    }
    
    //inverseValue of Short from list
    public short inverseValue (ArrayList<Short> list, int offset){  
        short x = list.get(offset);
        short invertedx = (short) (-x);
        return invertedx;
    }
}
