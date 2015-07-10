package wmx2obj;

import java.util.ArrayList;

public class Segment {

    public static final int OFFSET_BYTES = 4;
    
    private static final int LENGTH = 0x9000;
    private static final int BLOCK_COUNT = 0x10;
    private static final int SIZE = 8192;
    private static final int ROWS_PER_COLUMN = 32;

    private final int id;
    private final int offsetX;
    private final int offsetZ;

    private final ArrayList<Block> blocks = new ArrayList();
    
    private int faceIndexOffset;
    private int textureIndexOffset;

    public Segment(int id) {
        this.id = id;
        
        // adjust the segment to its correct position
        offsetX = SIZE * (id % ROWS_PER_COLUMN);
        offsetZ = -SIZE * (id / ROWS_PER_COLUMN);
    }

    public void addBlock(Block block) {
        if (blocks.size() < BLOCK_COUNT) {
            blocks.add(block);
        }
    }

    public void generateBlocks(byte[] data, int faceIndexOffset, 
            int textureIndexOffset) {
        // find correct offsets for every block and create the block instances
        for (int block = 0; block < BLOCK_COUNT; block++) {
            // convert the little-endian byte combination to an integer
            String offsetString = "";
            for (int b = 0; b < OFFSET_BYTES; b++) {
                offsetString = String.format(
                        "%02x",
                        data[id * LENGTH + (1 + block) 
                                * OFFSET_BYTES + b] & 0xff
                ) + offsetString;
            }
            int offset = Integer.parseInt(offsetString, 16) + id * LENGTH;
            
            // create a block instance using the found offset value
            addBlock(new Block(
                    block, 
                    offset + OFFSET_BYTES,
                    data[offset] & 0xff,
                    data[offset + 1] & 0xff
            ));
        }
        
        // generate the face elements and vertices of each block
        for (Block block : blocks) {
            block.generateFaces(data, faceIndexOffset, textureIndexOffset);
            block.generateTextures(data, faceIndexOffset, textureIndexOffset);
            block.generateVertices(data, offsetX, offsetZ);
            for (Face face : block.getFaces()) {
                for (Integer faceIndex : face.getFaceIndices()) {
                    if (faceIndex > faceIndexOffset) {
                        faceIndexOffset = faceIndex;
                    }
                }
            }
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
