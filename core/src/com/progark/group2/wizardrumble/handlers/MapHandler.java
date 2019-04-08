package com.progark.group2.wizardrumble.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import static com.progark.group2.wizardrumble.Application.SCALE;

public class MapHandler {

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Vector2 mapSize;
    private TiledMapTileLayer ground;
    private TiledMapTileLayer walkables;
    private TiledMapTileLayer treesAndRocks;
    private SpriteBatch spriteBatch;

    public MapHandler(SpriteBatch spriteBatch){
        this.spriteBatch = spriteBatch;
        maploader = new TmxMapLoader();
        map = maploader.load("map2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, SCALE, spriteBatch);



        MapProperties prop = map.getProperties();

        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get("height", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);
        int tilePixelHeight = prop.get("tileheight", Integer.class);

        float mapPixelWidth = mapWidth * tilePixelWidth * SCALE;
        float mapPixelHeight = mapHeight * tilePixelHeight * SCALE;

        mapSize = new Vector2(mapPixelWidth, mapPixelHeight);
        ground = (TiledMapTileLayer) map.getLayers().get(0);
        walkables = (TiledMapTileLayer) map.getLayers().get(1);
        treesAndRocks = (TiledMapTileLayer) map.getLayers().get(3);

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

    public void renderGround(){
        this.spriteBatch.begin();
        renderer.renderTileLayer(ground);
        renderer.renderTileLayer(walkables);
        this.spriteBatch.end();
    }

    public void renderTreesAndRocks(){
        this.spriteBatch.begin();
        renderer.renderTileLayer(treesAndRocks);
        this.spriteBatch.end();
    }
}
