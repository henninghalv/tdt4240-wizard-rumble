package com.progark.group2.wizardrumble.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapHandler {

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public MapHandler(){
        maploader = new TmxMapLoader();
        map = maploader.load("map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
    }
    // Might be used to add new maps.
    public void generateMapFromFile(String file){
    }

    // Places map to fit camera position
    public void setView(OrthographicCamera camera){
        renderer.setView(camera);
    }

    public void render(){
        renderer.render();
    }
}
