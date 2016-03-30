package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import textures.ModelTexture;

public class MainGameLoop {

    public static void main(String[] args) {
        DisplayManager.createDisplay();

        Loader loader = new Loader();

        /*
        float[] vertices = {            
                -0.5f,0.5f,0,   
                -0.5f,-0.5f,0,  
                0.5f,-0.5f,0,   
                0.5f,0.5f,0,        
                 
                -0.5f,0.5f,1,   
                -0.5f,-0.5f,1,  
                0.5f,-0.5f,1,   
                0.5f,0.5f,1,
                 
                0.5f,0.5f,0,    
                0.5f,-0.5f,0,   
                0.5f,-0.5f,1,   
                0.5f,0.5f,1,
                 
                -0.5f,0.5f,0,   
                -0.5f,-0.5f,0,  
                -0.5f,-0.5f,1,  
                -0.5f,0.5f,1,
                 
                -0.5f,0.5f,1,
                -0.5f,0.5f,0,
                0.5f,0.5f,0,
                0.5f,0.5f,1,
                 
                -0.5f,-0.5f,1,
                -0.5f,-0.5f,0,
                0.5f,-0.5f,0,
                0.5f,-0.5f,1
                 
        };
         
        float[] textureCoords = {
                 
                0,0,
                0,1,
                1,1,
                1,0,            
                0,0,
                0,1,
                1,1,
                1,0,            
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0
 
                 
        };
         
        int[] indices = {
                0,1,3,  
                3,1,2,  
                4,5,7,
                7,5,6,
                8,9,11,
                11,9,10,
                12,13,15,
                15,13,14,   
                16,17,19,
                19,17,18,
                20,21,23,
                23,21,22
 
        };
        

        RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("image")));
        */
        
        RawModel rawTreeModel = OBJLoader.loadObjModel("tree", loader);
        TexturedModel treeModel = new TexturedModel(rawTreeModel, new ModelTexture(loader.loadTexture("tree")));

        RawModel rawGrassModel = OBJLoader.loadObjModel("grassModel", loader);
        TexturedModel grassModel = new TexturedModel(rawGrassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        grassModel.getTexture().setHasTransparency(true);
        grassModel.getTexture().setUseFakeLighting(true);

        RawModel rawFernModel = OBJLoader.loadObjModel("fern", loader);
        TexturedModel fernModel = new TexturedModel(rawFernModel, new ModelTexture(loader.loadTexture("fern")));
        fernModel.getTexture().setHasTransparency(true);
        
        TexturedModel flowerModel = new TexturedModel(rawGrassModel, new ModelTexture(loader.loadTexture("flower")));
        flowerModel.getTexture().setHasTransparency(true);
        flowerModel.getTexture().setUseFakeLighting(true);

        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        RawModel rawDragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(),
                dragonData.getNormals(), dragonData.getIndices());
        TexturedModel dragonModel = new TexturedModel(rawDragonModel, new ModelTexture(loader.loadTexture("white")));
        dragonModel.getTexture().setReflectivity(1);
        dragonModel.getTexture().setShineDamper(8);

        Entity dragon = new Entity(dragonModel, new Vector3f(300, 0, -400), 0, 0, 0, 3);

        // build scene
        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for (int i = 0; i < 500; ++i) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * -800;
            entities.add(new Entity(treeModel, new Vector3f(x, 0, z), 0, 0, 0, 3));

            x = random.nextFloat() * 600 + 50;
            z = random.nextFloat() * -600 -50;
            entities.add(new Entity(grassModel, new Vector3f(x, 0, z), 0, 0, 0, 1));

            x = random.nextFloat() * 800;
            z = random.nextFloat() * -800;
            entities.add(new Entity(fernModel, new Vector3f(x, 0, z), 0, 0, 0, 0.6f));
            
            x = random.nextFloat() * 800;
            z = random.nextFloat() * -800;
            entities.add(new Entity(flowerModel, new Vector3f(x, 0, z), 0, 0, 0, 0.6f));
        }

        entities.add(dragon);

        Vector3f lightPos = new Vector3f(400,200,-400);
        float dy = 0.4f;
        Light light = new Light(lightPos, new Vector3f(1,1,1));
        
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");
        
        RawModel rawBunnyModel = OBJLoader.loadObjModel("bunny", loader);
        TexturedModel bunnyModel = new TexturedModel(rawBunnyModel, new ModelTexture(loader.loadTexture("real_white")));
        bunnyModel.getTexture().setShineDamper(5.0f);
        bunnyModel.getTexture().setReflectivity(1);
        
        Player player = new Player(bunnyModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.5f);
        
        Camera camera = new Camera(player);
        
        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            camera.move();
            player.move();
            
            if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
                renderer.setFog(true);
            } else if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
                renderer.setFog(false);
            }
            
            
            dragon.increaseRotation(0, 1, 0);
            
            renderer.processEntity(player);

            if (lightPos.y > 300) {
                dy = -dy;
                System.out.println("Light fall");
            }
            if (lightPos.y < 5) {
                dy = -dy;
                System.out.println("Light rise");
            }
            //lightPos.y += dy;

            light.setPosition(lightPos);
            renderer.processTerrain(terrain);

            for (Entity entity:entities)
                renderer.processEntity(entity);

            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
