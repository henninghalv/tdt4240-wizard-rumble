package com.progark.group2.wizardrumble.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class MapHandler {

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Vector2 mapSize;

    public MapHandler(){
        maploader = new TmxMapLoader();
        map = maploader.load("map_with_borders.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        mapSize = new Vector2(2240,2240);
    }
    // Might later be used to add new maps.
    public void generateMapFromFile(String file){
    }

    // Places map to fit camera position
    public void setView(OrthographicCamera camera){
        renderer.setView(camera);
    }

    public TiledMap getMap(){
        return map;
    }

    public Vector2 getMapSize(){
        return mapSize;
    }

    public void render(){
        renderer.render();
    }
}
