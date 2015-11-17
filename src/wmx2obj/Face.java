package wmx2obj;

import java.util.ArrayList;

public class Face {
    public static final int p = 0;
    
    private static final int FACE_INDEX_COUNT = 3;
    private static final int FACE_UV_COUNT = 6;
    
    private final int id;
    
    private final ArrayList<Integer> faceIndices = new ArrayList();
    private final ArrayList<Double> textureIndices = new ArrayList();
    private final ArrayList<String> texturepages = new ArrayList();
    
    public Face(int id) {
        this.id = id;
    }
    
    public void addFaceIndex(int faceIndex) {
            faceIndices.add(faceIndex);
    }
    
    public void addTextureIndex(double textureIndex) {
            textureIndices.add(textureIndex);
    }
    
    public void findFaceIndices(byte[] data, int offset, 
                                int faceIndexOffset) {
        // find vertex indices of the face
        for (int faceIndex = 0; 
                faceIndex < FACE_INDEX_COUNT; 
                faceIndex++) {
            int index = data[offset + faceIndex] & 0xff;
            index += faceIndexOffset + 1;
            addFaceIndex(index);
        }
    }
    
    public void findTexturePage(byte[] data, int offset, int faceIndexOffset,
                                int textureIndexOffset){
        
        int page = data[offset + FACE_UV_COUNT + textureIndexOffset] & 0xff;
        int isWater = data[offset + FACE_UV_COUNT + textureIndexOffset + 2] 
                & 0xff;
        
        /* if value of isWater is 64, 96, 192 or 224 then texture page is
        changed from default page*/
        /*
        pageNumber = page/16
        
        for (int i = 0; i<16;i++){
            if (page >= 16 * i && page < 16*(i+1)){
                pageNumber = page / 16 + 1;
                addPage(pageNumber.toString());
        //erikoistapaukset!!!
        */
        
        if(page >= 0 && page < 16 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("0");
        }else if (page >= 16 && page < 32 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("1");
        }else if (page >= 32 && page < 48 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("2");
        }else if (page >= 48 && page < 64 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("3");
        }else if (page >= 64 && page < 80 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("4");
        }else if (page >= 80 && page < 96 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("5");
        }else if (page >= 96 && page < 112 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("6");
        }else if (page >= 112 && page < 128 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("7");
        }else if (page >= 128 && page < 144 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("8");
        }else if (page >= 144 && page < 160 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("9");
        }else if (page >= 160 && page < 176 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("10");
        }else if (page >= 176 && page < 192 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("11");
        }else if (page >= 192 && page < 208 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("12");
        }else if (page >= 208 && page < 224 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("13");
        }else if (page >= 224 && page < 240 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("14");
        }else if (page >= 240 && page < 256 && isWater != 64 && isWater != 96
                && isWater != 192 && isWater != 224){
            texturepages.add("15");
        }else if (isWater==64 || isWater==192) {
            texturepages.add("water");
        }else if (isWater==96){
            texturepages.add("rails");
        }else if (isWater==224){
            texturepages.add("road");
        }
        
    }
    
    public int getId() {
        return id;
    }
    
    public ArrayList<Integer> getFaceIndices() {
        return faceIndices;
    }
    
    public ArrayList<Double> getTextureIndices() {
        return textureIndices;
    }
    
    public String getPage(){
        return texturepages.get(texturepages.size()-1);
    }
    public ArrayList<String> print(){
        return texturepages;
    }
}
