package com.progark.group2.wizardrumble.tools;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Random;

public class SoundManager extends ApplicationAdapter {
    private static SoundManager instance = null;

    private Music currentMusic;
    private Music menuMusic, battlemusic, gameEndMusic;
    private ArrayList<Sound> fireSounds, iceSounds, fireCastSounds, iceCastSounds;

    public SoundManager() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu.wav"));
        battlemusic = Gdx.audio.newMusic(Gdx.files.internal("battle.wav"));

        menuMusic.setLooping(true);
        battlemusic.setLooping(true);

        importSounds();

        // Starting with menu music
        currentMusic = menuMusic;
        currentMusic.play();
    }

    public static SoundManager getInstance(){
        if (instance == null){
            instance = new SoundManager();
        }
        return instance;
    }

    private void importSounds(){

        // Fire
        fireSounds = new ArrayList<Sound>();
        FileHandle[] files = Gdx.files.internal("sounds/fire").list();
        for(FileHandle file: files) {
            Sound sound = Gdx.audio.newSound(file);
            fireSounds.add(sound);
        }
        // Ice
        iceSounds = new ArrayList<Sound>();
        files = Gdx.files.internal("sounds/ice").list();
        for(FileHandle file: files) {
            Sound sound = Gdx.audio.newSound(file);
            iceSounds.add(sound);
        }
        // Fire cast from wizard
        fireCastSounds = new ArrayList<Sound>();
        files = Gdx.files.internal("sounds/fireCast").list();
        for(FileHandle file: files) {
            Sound sound = Gdx.audio.newSound(file);
            fireCastSounds.add(sound);
        }
        // Ice cast from wizard
        iceCastSounds = new ArrayList<Sound>();
        files = Gdx.files.internal("sounds/iceCast").list();
        for(FileHandle file: files) {
            Sound sound = Gdx.audio.newSound(file);
            iceCastSounds.add(sound);
        }
    }

    public void playSound(SoundType type, float volume){
        switch (type){
            case FIRE:
                fireSounds.get(new Random().nextInt(fireSounds.size())).play(volume);
                break;
            case ICE:
                iceSounds.get(new Random().nextInt(iceSounds.size())).play(volume);
                break;
            case FIRECAST:
//                fireCastSounds.get(new Random().nextInt(fireCastSounds.size())).play(volume);
                break;
            case ICECAST:
//                iceCastSounds.get(new Random().nextInt(iceCastSounds.size())).play(volume);
                break;

        }
    }

    public void switchMusic(String type){
        currentMusic.stop();
        if(type.equals("battle")){
            currentMusic = battlemusic;
            currentMusic.play();
        } else{
            currentMusic = menuMusic;
            currentMusic.play();
        }
    }


}
