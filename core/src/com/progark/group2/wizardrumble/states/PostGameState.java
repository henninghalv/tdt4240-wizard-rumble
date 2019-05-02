package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;
import com.progark.group2.wizardrumble.states.resources.UIButton;
import com.progark.group2.wizardrumble.tools.SoundManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class PostGameState extends State {
    private static PostGameState instance = null;
    private NetworkController network;
    private Label.LabelStyle textStyle;

    private Table table;
    private ArrayList<Player> playerPlacements;

    public PostGameState(GameStateManager gameStateManager) throws IOException {
        super(gameStateManager);

        network = NetworkController.getInstance();

        table = new Table();
        table.setFillParent(true);
        table.center();

        // Sorted list of players according to placement
        playerPlacements = new ArrayList<Player>();

        playerPlacements.add(network.getPlayer());
        for(Player player : network.getPlayers().values()){
            playerPlacements.add(player);
        }

        Collections.sort(playerPlacements, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                if(player1.getTimeAliveInMilliseconds() > player2.getTimeAliveInMilliseconds()){
                    return -1;
                } else if(player1.getTimeAliveInMilliseconds() == player2.getTimeAliveInMilliseconds()){
                    if(player1.getKills() > player2.getKills()){
                        return -1;
                    } else if(player1.getKills() == player2.getKills()){
                        if(player1.isAlive()){
                            return -1;
                        } else if(player2.isAlive()){
                            return 1;
                        } else{
                            return 0;
                        }
                    } else {
                        return 1;
                    }
                } else{
                    return 1;
                }
            }
        });

        // Title
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        textStyle = new Label.LabelStyle(font, font.getColor());
        Label label = new Label("Game over!", textStyle);
        label.setAlignment(Align.center);
        label.setSize((float)WIDTH/2, (float)HEIGHT/4);
        table.add(label).size((float)WIDTH/2, (float)HEIGHT/4).colspan(4);
        table.row();

        Label placement = new Label("Placement ", textStyle);
        Label playerName = new Label("Player names", textStyle);
        Label timeAlive = new Label("Time alive", textStyle);
        Label playerKills = new Label("Kills", textStyle);
        table.add(placement).pad(1f);
        table.add(playerName).pad(1f);
        table.add(timeAlive).pad(1f);
        table.add(playerKills).pad(1f);
        table.row();

        for(int i = 0; i < playerPlacements.size(); i++){
            placement = new Label(i+1+".", textStyle);
            playerName = new Label(playerPlacements.get(i).getName(), textStyle);
            if(playerPlacements.get(i).getTimeAliveInMilliseconds() == 0){
                timeAlive = new Label("Error", textStyle);
            } else {
                timeAlive = new Label(playerPlacements.get(i).getTimeAliveInMilliseconds()/1000 + "s", textStyle);
            }
            playerKills = new Label(Integer.toString(playerPlacements.get(i).getKills()), textStyle);
            table.add(placement).pad(1f);
            table.add(playerName).pad(1f);
            table.add(timeAlive).pad(1f);
            table.add(playerKills).pad(1f);
            table.row();
        }

        Stack backButton = new UIButton(new Texture("UI/blue_button00.png"), "Go to Main menu").getButton();
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    GameStateManager.getInstance().set(MainMenuState.getInstance());
                    SoundManager.getInstance().switchMusic("menu");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        table.add(backButton).spaceTop(40f).pad(10f).colspan(3);
        table.row();

        this.stage.addActor(table);

        // Remove all players
        network.getPlayers().clear();
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
