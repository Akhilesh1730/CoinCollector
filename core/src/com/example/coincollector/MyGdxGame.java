package com.example.coincollector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;

	Texture[] coinMan;

	Texture dizzy;
	Texture coin;

	Texture bomb;
	int manState = 0;
	int pause = 0;
	float gravity = 1f;
	float velocity = 0;
	float manY = 0;

	Rectangle manRectangle;

	//Coins
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();

	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	int coinCount = 0;

	//Bomb
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();

	ArrayList<Rectangle> bombsRectangle = new ArrayList<>();
	int bombCount = 0;
	Random random;

	BitmapFont font;

	int gameState = 0;
	int score = 0;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		coinMan = new Texture[4];
		coinMan[0] = new Texture("frame-1.png");
		coinMan[1] = new Texture("frame-2.png");
		coinMan[2] = new Texture("frame-3.png");
		coinMan[3] = new Texture("frame-4.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");
		manY = Gdx.graphics.getHeight()/2;
		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void makeCoin() {
		int height = (int) (random.nextFloat()*(Gdx.graphics.getHeight()));
		coinYs.add(height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		int height = (int) (random.nextFloat()*(Gdx.graphics.getHeight()));
		bombYs.add(height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for (int i = 0; i < coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i , coinXs.get(i) - 4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if (bombCount < 120) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			bombsRectangle.clear();
			for (int i = 0; i < bombXs.size(); i++) {
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i , bombXs.get(i) - 16);
				bombsRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -25;
			}
			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity += gravity;
			manY -= velocity;

			if (manY<=0) {
				manY = 0;
			}

		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else {
			if (Gdx.input.justTouched()) {
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				coinCount = 0;
				coinRectangles.clear();
				coinXs.clear();
				coinYs.clear();
				bombsRectangle.clear();
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
				gameState = 1;
			}
		}

		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth()/2 - coinMan[manState].getWidth()/2, manY);
		} else {
			batch.draw(coinMan[manState], Gdx.graphics.getWidth()/2 - coinMan[manState].getWidth()/2, manY);
		}

		 manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - coinMan[manState].getWidth()/2,
				 manY,
				 coinMan[manState].getWidth(),
				 coinMan[manState].getHeight());

		 for (int i = 0; i < coinRectangles.size(); i++) {
			 if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				 Gdx.app.log("Coin!", "Collision");
				 score++;
				 coinRectangles.remove(i);
				 coinXs.remove(i);
				 coinYs.remove(i);
				 break;
			 }
		 }

		for (int i = 0; i < bombsRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombsRectangle.get(i))) {
				Gdx.app.log("Coin!", "Collision");
				bombsRectangle.remove(i);
				bombXs.remove(i);
				bombYs.remove(i);
				gameState = 2;
				break;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);
		 batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
