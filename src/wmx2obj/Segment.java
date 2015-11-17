package wmx2obj;

import java.util.ArrayList;

public class Segment {

    public static final int        OFFSET_BYTES = 4;
    private static final int       LENGTH = 0x9000;
    private static final int       BLOCK_COUNT = 0x10;
    private static final int       SIZE = 8192;
    private static final int       COLUMNS_PER_ROW = 32;
    private final int              id;
    private final int              offsetX;
    private final int              offsetZ;
    private final ArrayList<Block> blocks = new ArrayList();
    private int                    faceIndexOffset;
    private int                    textureIndexOffset;

    public Segment(int id) {
        this.id = id;
        
        // adjust the segment to its correct position
        offsetX = SIZE * (id % COLUMNS_PER_ROW);
        offsetZ = -SIZE * (id / COLUMNS_PER_ROW);
    }

    public void addBlock(Block block) {
            blocks.add(block);
    }

    public void generateBlocks(byte[] data, int faceIndexOffset, 
                               int textureIndexOffset) {
        // find correct offsets for every block and create the block instances
        for (int block = 0; block < BLOCK_COUNT; block++) { 
            // convert the little-endian byte combination to an integer
            String offsetString = "";
            for (int b = 0; b < OFFSET_BYTES; b++) {
                int dataIndex   = id * LENGTH + (1 + block) * OFFSET_BYTES + b;
                String nextByte = String.format("%02x", data[dataIndex] & 0xff);
                offsetString    = nextByte + offsetString;
            }
            int offset = Integer.parseInt(offsetString, 16) + id * LENGTH;
            
            // create a block instance using the found offset value
            Block b = new Block(block, offset + OFFSET_BYTES, 
                                data[offset] & 0xff, data[offset + 1] & 0xff,
                                data[offset + 2] & 0xff);
            
            b.generateFaces(data, faceIndexOffset, textureIndexOffset);
            b.generateTextures(data, faceIndexOffset, textureIndexOffset);
            b.generateVertices(data, offsetX, offsetZ);
            b.generateNormals(data, faceIndexOffset);
            
            for (Face face : b.getFaces()) {
                for (Integer vertexIndex : face.getFaceIndices()) {
                    if (vertexIndex > faceIndexOffset) {
                        faceIndexOffset = vertexIndex;
                    }
                }
            }
            addBlock(b);
        }
        this.faceIndexOffset = faceIndexOffset;
        this.textureIndexOffset = textureIndexOffset;
    }
    
    public ArrayList<Block> getBlocks() {
        return blocks;
    }
    
    public int getFaceIndexOffset() {
        return faceIndexOffset;
    }
    public int getTextureIndexOffset(){
        return textureIndexOffset;
    }

}
