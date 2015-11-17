package wmx2obj;

import java.util.ArrayList;

public class Material {
    
    /* For each material user has created, assign
    correct texture page in program (drop-down menu)
    */
    
    private ArrayList<String> materialNames;
    private ArrayList<Double> textureCoordinates;
    private ArrayList<Integer> textureIndices;

    
    public Material(ArrayList<String> materials, ArrayList<Double>
                    textureCord, ArrayList<Integer> textureIndices ){
        materialNames = materials;
        textureCoordinates = textureCord;
        this.textureIndices = textureIndices;
        
    }
    public void texturePage(){
        
        
        for (int i = 0; i < textureIndices.size(); i++){
            
        }
        
    }
}
