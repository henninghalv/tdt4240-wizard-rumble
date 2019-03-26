package com.progark.group2.wizardrumble.network.requests;

import java.util.HashMap;

public class PlayersHealthStatusRequest extends Request {

    private HashMap<Integer, Integer> map;

    public HashMap<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, Integer> map) {
        this.map = map;
    }
}
