package com.progark.group2.wizardrumble.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.progark.group2.wizardrumble.entities.Obstacle;

public class B2WorldCreator {
    public B2WorldCreator(TiledMap map){
        // The number 3 is the index of the object layer in the TiledMap.
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(MapObject.class)) {
            if(object instanceof RectangleMapObject){
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                new Obstacle(rect);
            }
        }
    }
}
