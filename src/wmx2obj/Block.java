package wmx2obj;

import java.util.ArrayList;

public class Block {
    
    private static final int            FACE_BYTES = 16;
    private static final int            VERTEX_BYTES = 8;
    private static final int            NORMAL_BYTES = 8;
    private static final int            SIZE = 2048;
    private static final int            ROWS_PER_COLUMN = 4;
    private final int                   offset;
    private final int                   faceCount;
    private final int                   vertexCount;
    private final int                   normalCount;
    private final int                   offsetX;
    private final int                   offsetZ;   
    private final ArrayList<Face>       faces = new ArrayList();
    private final ArrayList<Vertex>     vertices = new ArrayList();
    private final ArrayList<Texture>    textures = new ArrayList();
    private final ArrayList<Normals>    normals = new ArrayList();
    
    public Block(int id, int offset, int faceCount, int vertexCount,
                 int normalCount) {
        this.offset = offset;
        this.faceCount = faceCount;
        this.vertexCount = vertexCount;
        this.normalCount = normalCount;
        offsetX = SIZE * (id % ROWS_PER_COLUMN);
        offsetZ = -SIZE * (id / ROWS_PER_COLUMN);
    }
    
    public void addFace(Face face) {
        faces.add(face);
    }
    
    public void addVertex(Vertex vertex) {
            vertices.add(vertex);
    }
    
    public void addTexture(Texture texture) {
            textures.add(texture);
    }
    
    public void addNormal(Normals normal){
            normals.add(normal);
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
            Face f = new Face(face);
            f.findFaceIndices(data, 
                              offset + f.getId() * FACE_BYTES,
                              faceIndexOffset);
            f.findTexturePage(data, offset, faceIndexOffset,
                              textureIndexOffset);
            textureIndexOffset += 16;
            addFace(f);
        }
    }
    
    public void generateVertices(byte[] data, int segmentX, int segmentZ) {
        // initialize vertex instances
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            Vertex v = new Vertex(vertex);
            int vertexOffset = offset + faceCount * FACE_BYTES + 
                               v.getId() * VERTEX_BYTES;
            v.findCoordinates(data, vertexOffset, 
                              offsetX, offsetZ, 
                              segmentX, segmentZ);
            addVertex(v);
        }
    }
    
    public void generateNormals(byte[] data, int offset){
        for(int normal = 0; normal < normalCount; normal++){
            Normals n = new Normals(normal);
            System.out.println(faceCount);
            System.out.println(vertexCount);
            System.out.println(normalCount);
            System.out.println(offset);
            
            n.findCoordinates(data,
                    offset + faceCount * FACE_BYTES 
                           + vertexCount * VERTEX_BYTES +
                            n.getId() * NORMAL_BYTES
            );
            addNormal(n);
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
    
    public ArrayList<Normals> getNormals(){
        return normals;
    }
    
    public int getfaceCount(){
        return faceCount;
    }
}