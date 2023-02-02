package com.atilsamancioglu.survivorbirdstarter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

import sun.jvm.hotspot.ui.Inspector;

public class SurvivorBird extends ApplicationAdapter {

	 Random random;
	 SpriteBatch batch;
	 Texture background;
	 Texture bird;
	 Texture cactus;
	 Texture oldtree;
	 Texture shadytree;
	 Texture wood;
	 Texture bee1;
	 Texture bee2;
	 Texture bee3;
	 int gameState=0; // 1 --> Game on || 2 --> Game off
	 //ShapeRenderer shapeRenderer;
	 int score=0;
	 int scoredEnemy=0;
	 BitmapFont font; // score font
	 BitmapFont font2; // game over font

	 //sound effects
	 Sound hitSound;
	 Sound pointSound;
	 Sound wingSound;

	 //bird
	 float birdX=0;
	 float birdY=0;
	 float velocity=0;
	 float gravity=0.9f;
	 float birdWidth;
	 float birdHeight;
	 Circle birdCircle; // hitbox

	 //enemy
	 int numberOfEnemies=4;
	 float[] enemyX=new float[numberOfEnemies];
	 float[] enemyOffSet=new float[numberOfEnemies];
	 float[] enemyOffSet2=new float[numberOfEnemies];
	 float[] enemyOffSet3=new float[numberOfEnemies];
	 float distance=0; // distance between of bees
	 float enemyVelocity;
	 float beeWidth;
	 float beeHeight;
	 Circle[] beeCircle1;
	 Circle[] beeCircle2;
	 Circle[] beeCircle3;
	
	@Override
	public void create () {
		enemyVelocity=Gdx.graphics.getWidth()/154; // --> velocity=7
		random=new Random();
		batch=new SpriteBatch();
		background=new Texture("background.png");
		bird=new Texture("bird.png");
		cactus=new Texture("cactus.png");
		oldtree=new Texture("oldtree.png");
		shadytree=new Texture("shadytree.png");
		wood=new Texture("wood.png");
		bee1=new Texture("bee.png");
		bee2=new Texture("bee.png");
		bee3=new Texture("bee.png");
		//shapeRenderer=new ShapeRenderer();

		//sound effects
		hitSound=Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
		pointSound=Gdx.audio.newSound(Gdx.files.internal("point.mp3"));
		wingSound=Gdx.audio.newSound(Gdx.files.internal("wing.mp3"));

		//score font
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(7);

		//game over font
		font2=new BitmapFont();
		font2.setColor(Color.WHITE);
		font2.getData().setScale(8);

		//bird
		birdWidth=Gdx.graphics.getWidth()/14;
		birdCircle=new Circle();
		birdX=Gdx.graphics.getWidth()/3-Gdx.graphics.getWidth()/15;
		birdY=Gdx.graphics.getHeight()/2;
		birdHeight=Gdx.graphics.getHeight()/9;

		//bee
		beeCircle1=new Circle[numberOfEnemies]; //hitbox
		beeCircle2=new Circle[numberOfEnemies]; //hitbox
		beeCircle3=new Circle[numberOfEnemies]; //hitbox
		distance=Gdx.graphics.getWidth()/2; // distance between of bees
		beeWidth=Gdx.graphics.getWidth()/14;
		beeHeight=Gdx.graphics.getHeight()/9;

		for(int i=0;i<numberOfEnemies;i++){

			enemyOffSet[i] = (random.nextFloat())*(Gdx.graphics.getHeight());
			enemyOffSet2[i] = (random.nextFloat())*(Gdx.graphics.getHeight());
			enemyOffSet3[i] = (random.nextFloat())*(Gdx.graphics.getHeight());

			enemyX[i]=Gdx.graphics.getWidth()-bee1.getWidth()/2+i*distance;

			beeCircle1[i]=new Circle();
			beeCircle2[i]=new Circle();
			beeCircle3[i]=new Circle();
		}

	}

	@Override
	public void render () {

		batch.begin();
		//background
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.draw(wood,Gdx.graphics.getWidth()/3-Gdx.graphics.getWidth()/17,Gdx.graphics.getHeight()/9,Gdx.graphics.getWidth()/10,Gdx.graphics.getHeight()/5);
		batch.draw(cactus,Gdx.graphics.getWidth()/8,Gdx.graphics.getHeight()/9,Gdx.graphics.getWidth()/14,Gdx.graphics.getHeight()/5);
		batch.draw(oldtree,Gdx.graphics.getWidth()/2-Gdx.graphics.getWidth()/17,Gdx.graphics.getHeight()/9,Gdx.graphics.getWidth()/5,Gdx.graphics.getHeight()/2-Gdx.graphics.getHeight()/7);
		batch.draw(shadytree,Gdx.graphics.getWidth()-Gdx.graphics.getWidth()/3,Gdx.graphics.getHeight()/9,Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()/2-Gdx.graphics.getHeight()/8);

		font.draw(batch,String.valueOf(score),120,200);

		//bird
		batch.draw(bird,birdX,birdY,birdWidth,birdHeight);

		if(gameState==1){

			if(enemyX[scoredEnemy]+birdWidth<birdX){ // bird pass to bee
				//point sound effect
				long id=pointSound.play(1.0f);
				pointSound.setPitch(id,1);
				pointSound.setLooping(id,false);

				score++;
				if(scoredEnemy<numberOfEnemies-1){
					scoredEnemy++;
				}
				else{
					scoredEnemy=0;
				}
			}

			if(Gdx.input.justTouched()){
				//wing sound effect
				long id=wingSound.play(1.0f);
				wingSound.setPitch(id,1);
				wingSound.setLooping(id,false);

				velocity=-15;
			}

			for(int i=0;i<numberOfEnemies;i++){ //loop of bees

				if(enemyX[i]<Gdx.graphics.getWidth()/50){ // end of bees
					enemyX[i]=enemyX[i]+numberOfEnemies*distance;

					enemyOffSet[i] = (random.nextFloat()-0.8f)*(Gdx.graphics.getHeight()/5-800);
					enemyOffSet2[i] = (random.nextFloat()-0.8f)*(Gdx.graphics.getHeight()/5-800);
					enemyOffSet3[i] = (random.nextFloat()-0.8f)*(Gdx.graphics.getHeight()/5-800);
				}
				else{
					enemyX[i]=enemyX[i]-enemyVelocity; // move of bees
				}

				if((Gdx.graphics.getHeight()/5+enemyOffSet3[i])<Gdx.graphics.getHeight()){
					batch.draw(bee1,enemyX[i],(Gdx.graphics.getHeight()/5+enemyOffSet[i]),beeWidth,beeHeight);
					batch.draw(bee2,enemyX[i],(Gdx.graphics.getHeight()/5+enemyOffSet2[i]),beeWidth,beeHeight);
					batch.draw(bee3,enemyX[i],(Gdx.graphics.getHeight()/5+enemyOffSet3[i]),beeWidth,beeHeight);

					//hitbox of bees
					beeCircle1[i]=new Circle(enemyX[i]+beeWidth/2,(Gdx.graphics.getHeight()/5+enemyOffSet[i])+beeHeight/2,beeWidth/2-beeWidth/7);
					beeCircle2[i]=new Circle(enemyX[i]+beeWidth/2,(Gdx.graphics.getHeight()/5+enemyOffSet2[i])+beeHeight/2,beeWidth/2-beeWidth/7);
					beeCircle3[i]=new Circle(enemyX[i]+beeWidth/2,(Gdx.graphics.getHeight()/5+enemyOffSet3[i])+beeHeight/2,beeWidth/2-beeWidth/7);
				}
				else{
					enemyOffSet[i] = (random.nextFloat()-0.8f)*(Gdx.graphics.getHeight()/5-500);
					enemyOffSet2[i] = (random.nextFloat()-0.8f)*(Gdx.graphics.getHeight()/5-500);
					enemyOffSet3[i] = (random.nextFloat()-0.8f)*(Gdx.graphics.getHeight()/5-500);
				}
			}

			if(birdY>0){
				velocity=velocity+gravity;
				birdY=birdY-velocity;
			}
			else{
				//die sound effect
				long id=hitSound.play(1.0f);
				hitSound.setPitch(id,1);
				hitSound.setLooping(id,false);

				gameState=2;
			}
		}
		else if(gameState==0){
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}
		else if(gameState==2){

			font2.draw(batch,"Game Over! Tap To Play Again!",Gdx.graphics.getWidth()/10,Gdx.graphics.getHeight()/2);

			if(Gdx.input.justTouched()){
				gameState=1;

				for(int i=0;i<numberOfEnemies;i++){ //loop of bees

					enemyOffSet[i] = (random.nextFloat())*(Gdx.graphics.getHeight());
					enemyOffSet2[i] = (random.nextFloat())*(Gdx.graphics.getHeight());
					enemyOffSet3[i] = (random.nextFloat())*(Gdx.graphics.getHeight());

					enemyX[i]=Gdx.graphics.getWidth()-bee1.getWidth()/2+i*distance;

					beeCircle1[i]=new Circle();
					beeCircle2[i]=new Circle();
					beeCircle3[i]=new Circle();

				}
				scoredEnemy=0;
				score=0;
				velocity=0;
				birdY=Gdx.graphics.getHeight()/2;
			}
		}
		batch.end();

		birdCircle.set(birdX+birdWidth/2,birdY+birdHeight/2,birdWidth/2-birdWidth/7); // hitbox
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.circle(birdX+birdWidth/2,birdY+birdHeight/2,birdWidth/2-birdWidth/7);

		for(int i=0;i<numberOfEnemies;i++){
			/*shapeRenderer.circle(enemyX[i]+beeWidth/2,(Gdx.graphics.getHeight()/5+enemyOffSet[i])+beeHeight/2,beeWidth/2-beeWidth/7);
			shapeRenderer.circle(enemyX[i]+beeWidth/2,(Gdx.graphics.getHeight()/5+enemyOffSet2[i])+beeHeight/2,beeWidth/2-beeWidth/7);
			shapeRenderer.circle(enemyX[i]+beeWidth/2,(Gdx.graphics.getHeight()/5+enemyOffSet3[i])+beeHeight/2,beeWidth/2-beeWidth/7);*/

			if(Intersector.overlaps(birdCircle,beeCircle1[i]) || Intersector.overlaps(birdCircle,beeCircle2[i]) || Intersector.overlaps(birdCircle,beeCircle3[i])){
				gameState=2;
				birdX=Gdx.graphics.getWidth()/3-Gdx.graphics.getWidth()/15;
				birdY=Gdx.graphics.getHeight()/2;

				for(int j=0;j<numberOfEnemies;j++){
					enemyX[i]=0;
				}

				//die sound effect
				long id=hitSound.play(1.0f);
				hitSound.setPitch(id,1);
				hitSound.setLooping(id,false);
			}
		}
		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		hitSound.dispose();
		pointSound.dispose();
		wingSound.dispose();
	}
}
