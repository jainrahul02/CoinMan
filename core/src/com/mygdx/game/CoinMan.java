package com.mygdx.game;



import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.sun.org.apache.xerces.internal.parsers.IntegratedParserConfiguration;


import java.util.ArrayList;
import java.util.Random;

import javax.xml.soap.Text;

import static com.badlogic.gdx.Input.Keys.R;

public class CoinMan extends ApplicationAdapter {

	SpriteBatch batch;
	//texture is used to add image
	Texture background;
	//four frames for running
	Texture[] run;
	int runState=0;
	int pause=0;
	//to control the fall of the man
	float gravity =0.2f;
	float velocity=0;
	int manYPosition=0;
	Rectangle manRectangle;
	Texture coin;
	int coinCount=0;
	ArrayList<Integer> coinXPosition = new ArrayList<Integer>();
	ArrayList<Integer> coinYPosition = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();

	Texture bomb;
	int bombCount=0;
	ArrayList<Integer> bombXPosition = new ArrayList<Integer>();
	ArrayList<Integer> bombYPosition = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();

	Random random;
	int score=0 ;
	//to end the game we need to check the gamestate
	int gameState =0;
	Texture dizzy;

	//to display the score
	BitmapFont bitmapFont;

	Music music;
	Music hit;

	@Override
	public void create () {
		music= Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true);
		music.play();

		batch = new SpriteBatch();
		background = new Texture("bg.png");
		dizzy=new Texture("dizzyy.png");
		run = new Texture[4];
		run[0] = new Texture("frame-1.png");
		run[1] = new Texture("frame-2.png");
		run[2] = new Texture("frame-3.png");
		run[3] = new Texture("frame-4.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		//initially the man is at the center of the screen
		manYPosition = Gdx.graphics.getHeight()/2;
		random = new Random();

		bitmapFont = new BitmapFont();
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.getData().setScale(10);
	}
	//to make coins appear in the screen
	public void showCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYPosition.add((int) height);
		//xpos wil remain constant
		coinXPosition.add(Gdx.graphics.getWidth());
	}
	//to make bombs appear in the screen
	public void showBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYPosition.add((int) height);
		//xpos wil remain constant
		bombXPosition.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//gameStates
		if(gameState==0){
			//GAME NOT STARTED
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}else if(gameState==1){
			//GAME IS RUNNING
			//COINS
			if(coinCount<100)
				coinCount++;
			else{
				coinCount=0;
				//make a new coin to show
				showCoin();
			}
			coinRectangles.clear();
			for(int i=0;i<coinXPosition.size();i++){
				//draw the coin;
				batch.draw(coin,coinXPosition.get(i),coinYPosition.get(i));
				coinXPosition.set(i,coinXPosition.get(i)-4);
				coinRectangles.add(new Rectangle(coinXPosition.get(i),coinYPosition.get(i),coin.getWidth(),coin.getHeight()));
			}


			//BOMBS
			if(bombCount<250)
				bombCount++;
			else{
				bombCount=0;
				//make a new coin to show
				showBomb();
			}
			bombRectangles.clear();
			for(int i=0;i<bombXPosition.size();i++){
				//draw the coin;
				batch.draw(bomb,bombXPosition.get(i),bombYPosition.get(i));
				bombXPosition.set(i,bombXPosition.get(i)-8);
				bombRectangles.add(new Rectangle(bombXPosition.get(i),bombYPosition.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//whenever we touch the screen
			if(Gdx.input.justTouched()){
				//to jump up velocity is negative
				velocity =-10;
			}



			//to control the speed of running
			if(pause<8)
				pause++;
			else {
				pause=0;
				//to make it run
				if (runState < 3)
					runState++;
				else
					runState = 0;
			}
			velocity+=gravity;
			manYPosition-=velocity;
			//to keep it in the bottom of the screen
			if(manYPosition<=0)
				manYPosition=0;

		}else if(gameState==2){
		
			//BOMB collision ,restart
			if(Gdx.input.justTouched()){
				hit.stop();
				try {
					Thread.currentThread().sleep( 500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				gameState=1;
				music.play();
				//since initially the man should be at the center of the screen
				manYPosition = Gdx.graphics.getHeight()/2;
				score=0;
				coinXPosition.clear();
				coinYPosition.clear();
				coinCount=0;
				coinRectangles.clear();
				velocity=0;
				bombXPosition.clear();
				bombYPosition.clear();
				bombCount=0;
				bombRectangles.clear();


			}


		}

		if(gameState==2){
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - run[runState].getWidth() / 2, manYPosition);
			music.stop();
			hit= Gdx.audio.newMusic(Gdx.files.internal("hit.mp3"));
			//hit.setLooping(true);
			hit.play();

		}else {
			//to allign the man at the center of the screen
			batch.draw(run[runState], Gdx.graphics.getWidth() / 2 - run[runState].getWidth() / 2, manYPosition);
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - run[runState].getWidth()/2,manYPosition,run[runState].getWidth(),run[runState].getHeight());

		//to check if man collide with coins
		for(int i=0;i<coinRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				score++;
				coinRectangles.remove(i);
				coinXPosition.remove(i);
				coinYPosition.remove(i);
				break;
			}
		}

		//to check if man collide with bombs
		for(int i=0;i<bombRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
			//	Gdx.app.log("Bomb","Collision");
				gameState=2;
			}
		}

		//display score
		bitmapFont.draw(batch,String.valueOf(score),100,200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
