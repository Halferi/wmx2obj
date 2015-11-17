package wmx2obj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final ArrayList<Segment> segments = new ArrayList();
    
    private int start;
    private int end;
    private int vt=1;
    private String PrevPage="";

    private byte[] data;
    private List<String> info;
    
    public void importOBJ(File file) throws IOException {
        info = Files.readAllLines(file.toPath());
    }
   
    public void importFile(File file) throws IOException {
        // read bytes from imported file to an array
        data = Files.readAllBytes(file.toPath());
    }
    
    public void readWavefront(){
        info.clear();
        for (int line = 0; line < 2; line++){
            System.out.println(info);
        }
    }

    public void generateSegments() {
        // initialize segment instances
        segments.clear();
        for (int segment = start - 1; segment < end; segment++) {
            segments.add(new Segment(segment));
        }
        
        // generate block instances for each segment
        int faceIndexOffset = 0;
        int textureIndexOffset = 6;
        for (Segment segment : segments) {
            segment.generateBlocks(data, faceIndexOffset, textureIndexOffset);
            faceIndexOffset = segment.getFaceIndexOffset();
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
        int segmentIterator = start;
        for (Segment segment : segments) {
            printWriter.println("# Segment " + segmentIterator);
            for (Block block : segment.getBlocks()) {
                writeVertices(block, printWriter);
                writeTextures(block, printWriter);
                writeNormals(block, printWriter);
                writeFaces(block, printWriter);
            }
            segmentIterator++;
        }
        printWriter.close();
    }
    
    public void exportFF8WM(File file ) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        
    }

    private void writeVertices(Block block, PrintWriter printWriter) {
        
        for (Vertex vertex : block.getVertices()) {
            printWriter.println(
                "v " 
                + vertex.getCoordinate(Vertex.X) + " "
                + -vertex.getCoordinate(Vertex.Y) + " "
                + -vertex.getCoordinate(Vertex.Z)
            );
        }
    }
    
    private void writeTextures(Block block, PrintWriter printWriter) {

        for (Texture texture : block.getTextures()) {
                    
            printWriter.println("vt " + (texture.getUV(Texture.U0))
             + " " + (1-texture.getUV(Texture.V0)));
            printWriter.println("vt " + (texture.getUV(Texture.U1))
            + " " + (1-texture.getUV(Texture.V1)));
            printWriter.println("vt " + (texture.getUV(Texture.U2))
             + " " + (1-texture.getUV(Texture.V2)));                  
        }
    }

    private void writeFaces(Block block, PrintWriter printWriter) {
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
    
    private void writeNormals(Block block, PrintWriter printWriter){
        for (Normals normal : block.getNormals()){
            
            printWriter.println("vn " + normal.getCoordinate(Normals.X)
            + " " + normal.getCoordinate(Normals.Y)
            + " " + normal.getCoordinate(Normals.Z)
            );
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
    
    public void exportToFF8(File file){
        
    }
    
    // Handler for importer
    public void test(File file) throws IOException{
        FileOutputStream fos = new FileOutputStream(file);
        Reader reader = new Reader(info);
        reader.identify();
        //System.out.println(info);
        fos.write(reader.getProcessedBytes());
        fos.close();
    }

}