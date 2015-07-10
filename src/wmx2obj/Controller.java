package wmx2obj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

public class Controller {

    private final ArrayList<Segment> segments = new ArrayList();
    
    private int start;
    private int end;
    private int vt=1;
    private String PrevPage="";

    private byte[] data;
   
    public void importFile(File file) throws IOException {
        // read bytes from imported file to an array
        data = Files.readAllBytes(file.toPath());
    }

    public void generateSegments() {
        // initialize segment instances
        segments.clear();
        for (int segment = start - 1; segment < end; segment++) {
            segments.add(new Segment(segment));
        }
        
        // generate block instances for each segment
        int vertexIndexOffset = 0;
        int textureIndexOffset = 6;
        for (Segment segment : segments) {
            segment.generateBlocks(data, vertexIndexOffset, textureIndexOffset);
            vertexIndexOffset = segment.getFaceIndexOffset();
        }
    }
    
    public void exportMtl(File file) throws IOException {
        try (PrintWriter printWriterMTL = new PrintWriter(file)) {
            for (int n = 0; n < 16;n++){
                printWriterMTL.println("newmtl " + n);
                printWriterMTL.println("Kd 1.0 1.0 1.0");
                printWriterMTL.println("illum 2");
                printWriterMTL.println("map_Kd " + n +".png");
                printWriterMTL.println();
            }
            printWriterMTL.println("newmtl rails");
            printWriterMTL.println("Kd 1.0 1.0 1.0");
            printWriterMTL.println("illum 2");
            printWriterMTL.println("map_Kd rails.png");
            printWriterMTL.println();
            printWriterMTL.println("newmtl water");
            printWriterMTL.println("Kd 1.0 1.0 1.0");
            printWriterMTL.println("illum 2");
            printWriterMTL.println("map_Kd water.png");
            printWriterMTL.println();
            printWriterMTL.println("newmtl road");
            printWriterMTL.println("Kd 1.0 1.0 1.0");
            printWriterMTL.println("illum 2");
            printWriterMTL.println("map_Kd road.png");
            printWriterMTL.close();
        }
        
    }
    
    public void exportFile(File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(file);     
        printWriter.println("# Converted to Wavefront .obj with wmx2obj");
        printWriter.println("# wmx2obj \u00A9 2015 Aleksanteri Hirvonen & Simo "+"Halfer"+" Ollonen");
        printWriter.println("# Contact @ simoollonen@gmail.com");
        printWriter.println("mtllib wmxtextures.mtl");
        printWriter.println();
        writeVertices(printWriter);
        writeTextures(printWriter);
        writeFaces(printWriter);
        printWriter.close();
    }

    private void writeVertices(PrintWriter printWriter) {
        // take the coordinate values of each vertex instance
        // and write to file
        printWriter.println("# List of geometric vertices");
        for (Segment segment : segments) {
            for (Block block : segment.getBlocks()) {
                for (Vertex vertex : block.getVertices()) {
                    printWriter.println(
                            "v " 
                            + vertex.getCoordinate(Vertex.X) + " "
                            + -vertex.getCoordinate(Vertex.Y) + " "
                            + -vertex.getCoordinate(Vertex.Z)
                    );
                }
            }
        }
    }
    
    private void writeTextures(PrintWriter printWriter) {
        
        printWriter.println("#List of texture coordinates");
        
        for (Segment segment : segments) {
            for (Block block : segment.getBlocks()) {
                for (Texture texture : block.getTextures()) {
                    
                    printWriter.println("vt " + (texture.getUV(Texture.U0))
                     + " " + (1-texture.getUV(Texture.V0)));
                    printWriter.println("vt " + (texture.getUV(Texture.U1))
                     + " " + (1-texture.getUV(Texture.V1)));
                    printWriter.println("vt " + (texture.getUV(Texture.U2))
                     + " " + (1-texture.getUV(Texture.V2)));                  
                }
            }
        }
    }

    private void writeFaces(PrintWriter printWriter) {
        // take the vertex index values of each face instance
        // and write to file
        printWriter.println("# Polygonal face elements");
        for (Segment segment : segments) {
            for (Block block : segment.getBlocks()) {
                for (Face face : block.getFaces()) {
                    
                    if (PrevPage != face.getPage()){
                    printWriter.println("usemtl " + face.getPage());
                    PrevPage = face.getPage();
                    }
                    printWriter.print("f ");
                    for (Integer faceIndex : face.getFaceIndices()) {                                                                       
                        printWriter.print(faceIndex + "/" + vt + " ");
                        vt += 1;
                        
                    }
                    printWriter.println();
                }
            }
        }
    }
    
    private void setExportRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    public boolean isExportRangeValid(String startString, String endString) {
        try {
            int start = Integer.parseInt(startString);
            int end = Integer.parseInt(endString);
            setExportRange(start, end);
            return start >= 1 && end <= 835 && start <= end;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}