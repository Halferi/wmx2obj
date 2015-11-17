package wmx2obj;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Reader {
    
    /*
    For triangles last 4 bytes use format:
    (First byte = 4-bit texturepage, 4-bit texturenumber)
    (Second byte = 10 , solid type)
    (Third byte = 00, if not water, rail or road texture)
    (Fourth byte = FF, for walkability and landability)
    */
    
    private static final String objVertex = "v ";
    private static final String objTexture = "vt ";
    private static final String objNormal = "vn ";
    private static final String objFace = "f ";
    private static final String objMaterial = "usemtl ";
    
    private List<String> info;
    private List<String> line;
    
    private final ArrayList<Double> coordinatesV = new ArrayList();
    private final ArrayList<BigDecimal> coordinatesVclone = new ArrayList();
    private final ArrayList<Short> coordinatesVBytes = new ArrayList();
    private final ArrayList<Double> coordinatesVt = new ArrayList();
    private final ArrayList<Double> coordinatesVn = new ArrayList();
    private final ArrayList<Integer> coordinatesF = new ArrayList();
    private final ArrayList<Integer> textureIndices = new ArrayList();
    private final ArrayList<Byte> textureCoordinatesAsBytes = new ArrayList();
    private final ArrayList<String> materialNames = new ArrayList();
    
    private ArrayList<Byte> writerBytes;
    private byte[] processedBytes;
    
    public Reader (List<String> info){
        this.info = info;
    }
    public void addCoordinateV(double coordinate){
        coordinatesV.add(coordinate);
    }
    public void addCoordinateVt(double coordinate){
        coordinatesVt.add(coordinate);
    }
    public void addCoordinateVn(double coordinate){
        coordinatesVn.add(coordinate);
    }
    public void addCoordinateF(int coordinate){
        coordinatesF.add(coordinate);
    }
    public void addTextureIndice(int indice){
        textureIndices.add(indice);
    }
    
    public void identify(){
        for (int iterator = 0; iterator < info.size();iterator++){
            if (info.get(iterator).startsWith(objVertex)){
                //System.out.println("vertex");
                readVertices(info.get(iterator));
                
            }else if(info.get(iterator).startsWith(objTexture)){
                //System.out.println("texture");
                readTextures(info.get(iterator));
                
            }else if(info.get(iterator).startsWith(objNormal)){
                //System.out.println("normal");
                //readNormals(info.get(iterator));
                
            }else if(info.get(iterator).startsWith(objFace)){
                //System.out.println("face");
                readFaces(info.get(iterator));
                //System.out.println(coordinatesF);
            }else if(info.get(iterator).startsWith(objMaterial)){
                //readMaterial(info.get(iterator));
            }else{
                //System.out.println("something else");
            }
        }
        setVertexRange(coordinatesV);
        setTextureScale(coordinatesVt);
        listToBytes(coordinatesVclone);
        Writer w = new Writer(coordinatesVBytes,textureCoordinatesAsBytes,coordinatesVn,
                                coordinatesF,textureIndices);
        w.orderArrays();
        writerBytes = w.getProcessedBytes();
        processedBytes = convertArrayToList(writerBytes);
        
    }
    public void readMaterial(String line){
        Scanner scanner = new Scanner(line);
        scanner.skip("usemtl ");
        String name = scanner.next();
        System.out.println(name);
        materialNames.add(name);
    }
    public void readVertices(String line){
        Scanner scanner = new Scanner(line);
        scanner.skip("v");
        while (scanner.hasNext()){
            //byte b = Byte.decode(scanner.next());
            double v = Double.parseDouble(scanner.next());
            addCoordinateV(v);
        }
    }
    public void readTextures(String line){
        Scanner scanner = new Scanner(line);
        scanner.skip("vt");
        while(scanner.hasNext()){
            double vt = Double.parseDouble(scanner.next());
            addCoordinateVt(vt);
        }
    }
    public void readNormals(String line){
        Scanner scanner = new Scanner(line);
        scanner.skip("vn");
        while (scanner.hasNext()){
            //byte b = Byte.decode(scanner.next());
            double vn = Double.parseDouble(scanner.next());
            addCoordinateVn(vn);
        }
    }
    public void readFaces(String line){
        Scanner scanner = new Scanner(line);
        scanner.skip("f");
        String s = "/";
        
        char slash = s.charAt(0);
        while(scanner.hasNext()){
            String check = scanner.next();
            String sumVt = "";
            String sumF = "";
            for (int iterator = 0; iterator < check.length();iterator++){
                if (check.charAt(iterator) != slash){
                    char c = check.charAt(iterator);
                    sumF += c; 
                }else if(check.charAt(iterator) == slash){
                    char c = check.charAt(iterator+1);
                    sumVt += c;
                    if (check.charAt(iterator+2) != slash){
                        char c2 = check.charAt(iterator+2);
                        sumVt += c2;
                    }
                    int t = Integer.parseInt(String.valueOf(sumVt));
                    addTextureIndice(t);
                    iterator = check.length();
                }
                
            }
            int f = Integer.parseInt(String.valueOf(sumF));
            addCoordinateF(f-1);
        }
        
        /*String check = scanner.next();        // for textures indices
            for (int iterator = 0; iterator < check.length();iterator++){
                if (check.charAt(iterator) == slash){
                    char c = check.charAt(iterator+1);
                    System.out.println(c);
                    double t = Double.parseDouble(String.valueOf(c));
                    addCoordinateVt(t);
                }
            }*/
    }
    
    public void setVertexRange(ArrayList<Double> coordinates){
        BigDecimal absmin = new BigDecimal(Collections.min(coordinates));
        absmin = absmin.abs();
        BigDecimal max = new BigDecimal(Collections.max(coordinates));
        max = max.abs(new MathContext(6, RoundingMode.HALF_UP));
        max = max.add(absmin);
        BigDecimal targetmax = new BigDecimal(2048);
        BigDecimal multiplierFix;
        for (Double number : coordinates){
            BigDecimal decimal = new BigDecimal(number);
            decimal = decimal.add(absmin);
            decimal = decimal.round(new MathContext(5, RoundingMode.HALF_UP));

            multiplierFix = targetmax.divide(max,new MathContext(6, RoundingMode.HALF_UP));

            decimal = decimal.multiply(multiplierFix);
            //number += absmin;
            //number = number*(targetmax/max);
            //System.out.println(number);
            coordinatesVclone.add(decimal);
        }
    }
    
    public void setTextureScale(ArrayList<Double> coordinates){
        for (Double indice : coordinates){
            indice = indice * 256;
            textureCoordinatesAsBytes.add(indice.byteValue());
        }
    }
    
    public void listToBytes(ArrayList<BigDecimal> coordinates){
        for (BigDecimal number : coordinates){
            coordinatesVBytes.add(number.shortValue());
        }
    }
    
    public byte[] getProcessedBytes(){
        return processedBytes;
    }
    
    public byte[] convertArrayToList(ArrayList<Byte> array) {
        processedBytes = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            processedBytes[i] = array.get(i);
        }

        return processedBytes;
    }
}
