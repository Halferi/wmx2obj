package wmx2obj;

import java.util.ArrayList;

public class Block {
    
    private static final int FACE_BYTES = 16;
    private static final int VERTEX_BYTES = 8;
    private static final int SIZE = 2048;
    private static final int ROWS_PER_COLUMN = 4;
    
    private final int offset;
    private final int faceCount;
    private final int vertexCount;
    private final int offsetX;
    private final int offsetZ;
    
    private final ArrayList<Face> faces = new ArrayList();
    private final ArrayList<Vertex> vertices = new ArrayList();
    private final ArrayList<Texture> textures = new ArrayList();
    
    public Block(int id, int offset, int faceCount, int vertexCount) {
        this.offset = offset;
        this.faceCount = faceCount;
        this.vertexCount = vertexCount;
        offsetX = SIZE * (id % ROWS_PER_COLUMN);
        offsetZ = -SIZE * (id / ROWS_PER_COLUMN);
    }
    
    public void addFace(Face face) {
        if (faces.size() < faceCount) {
            faces.add(face);
        }
    }
    
    public void addVertex(Vertex vertex) {
        if (vertices.size() < vertexCount) {
            vertices.add(vertex);
        }
    }
    
    public void addTexture(Texture texture) {
        if (textures.size() < faceCount){
            textures.add(texture);
        }
    }
    
    public void generateTextures(byte[] data, int faceIndexOffset,
            int textureIndexOffset){
        for(int texture = 0; texture < faceCount; texture++){
            addTexture(new Texture(texture));
        }
        for (Texture texture : textures){    
            texture.findTextureIndices(
                    data,
                    offset + texture.getId() * FACE_BYTES                  
            );
            
        }    
    }
    
    public void generateFaces(byte[] data, int faceIndexOffset,
            int textureIndexOffset) {
        // initialize face instances
        for (int face = 0; face < faceCount; face++) {
            addFace(new Face(face));
        }
        
        // find vertex indices of the face instances
        for (Face face : faces) {
            face.findFaceIndices(data, 
                    offset + face.getId() * FACE_BYTES,
                    faceIndexOffset
            );
            face.findTexturePage(data, offset, faceIndexOffset,
                    textureIndexOffset);
            textureIndexOffset += 16;
        }
    }
    
    public void generateVertices(byte[] data, int segmentX, int segmentZ) {
        // initialize vertex instances
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            addVertex(new Vertex(vertex));
        }
        
        // find vertex coordinates of the vertex instances
        for (Vertex vertex : vertices) {
            vertex.findCoordinates(
                    data, 
                    offset + faceCount * FACE_BYTES 
                            + vertex.getId() * VERTEX_BYTES,
                    offsetX,
                    offsetZ,
                    segmentX,
                    segmentZ
            );
        }
    }
    
    
    public ArrayList<Face> getFaces() {
        return faces;
    }
    
    public ArrayList<Vertex> getVertices() {
        return vertices;
    }
    
    public ArrayList<Texture> getTextures() {
        return textures;
    }
    
    public int getfaceCount(){
        return faceCount;
    }
}