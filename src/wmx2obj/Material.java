package wmx2obj;

import java.util.ArrayList;

public class Material {
    
    /* For each material user has created, assign
    correct texture page in program (drop-down menu)
    */
    
    private ArrayList<String> materialNames;
    private ArrayList<Double> textureCoordinates;
    private ArrayList<Integer> textureIndices;

    private ArrayList<Integer> cluts;
    
    public Material(ArrayList<String> materials, ArrayList<Double>
                    textureCord, ArrayList<Integer> textureIndices ){
        materialNames = materials;
        textureCoordinates = textureCord;
        this.textureIndices = textureIndices;
        
    }
    public void clutID(){
        
        
        for (int i = 0; i < textureIndices.size(); i++){
            if(i%3==0){
                if(textureIndices.get(i) >= 0){
                    //blaah
                }
            }
        }
        
    }
}
