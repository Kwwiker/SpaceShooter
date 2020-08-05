package ru.kirusha.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class SpaceShooter extends ApplicationAdapter {
	public static final int SCR_WIDTH = 420, SCR_HEIGHT = 750; // размеры экрана
	long lastEnemySpawnTime; // время появления последнего врага
	long enemySpawnInterval = 2000; // интервал между появлением врагов
	long lastShootTime; // время последнего выстрела
	long shootInterval = 1000; // интервал между выстрелами

	SpriteBatch batch;
	OrthographicCamera camera; // для масштабирования под все разрешения экранов
	Vector3 touch; // обрабатывает касания

	// текстуры и звуки
	Texture imgShip;
	Texture imgEnemyShip;
	Texture imgShootUp;
	Texture imgShootDown;
	Texture imgSky;
	Sound sndBoom;
	Sound sndShoot;

	Array<Stars> stars = new Array<>(); // фон
	Ship ship; // наш самолёт
	Array<ShipEnemy> shipEnemies = new Array<>(); // враги
	Array<Shoot> shoots = new Array<>(); // выстрелы
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();
		loadResources();
		stars.add(new Stars(0));
		stars.add(new Stars(SCR_HEIGHT));
		ship = new Ship();
	}

	@Override
	public void render () {
		actions();
		camera.update(); // обновляем камеру
		batch.setProjectionMatrix(camera.combined); // перерасчитывает размеры объектов
		batch.begin();
		for (int i = 0; i < stars.size; i++)
			batch.draw(imgSky, stars.get(i).x, stars.get(i).y, stars.get(i).width, stars.get(i).height);

		for (int i = 0; i < shipEnemies.size; i++)
			batch.draw(imgEnemyShip, shipEnemies.get(i).x, shipEnemies.get(i).y, shipEnemies.get(i).width, shipEnemies.get(i).height);

		for (int i = 0; i < shoots.size; i++)
			batch.draw(imgShootUp, shoots.get(i).x, shoots.get(i).y, shoots.get(i).width, shoots.get(i).height);

		if (ship.isAlive)
			batch.draw(imgShip, ship.x, ship.y, ship.width, ship.height);
		batch.end();
	}

	private void actions() {
		for (int i = 0; i < stars.size; i++) stars.get(i).move(); // небо

		// обработка касаний
		if (Gdx.input.isTouched()) {
			touch.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(touch);
			ship.x += (touch.x - (ship.x + ship.width / 2)) / 20;
		}

		// выстрелы
		if (ship.isAlive)
			if (TimeUtils.millis() - lastShootTime > shootInterval)
				spawnShoot();

		// перемещение выстрелов
		for (int i = 0; i <shoots.size; i++) {
			shoots.get(i).move();
			// проверить попадание
			for (int j = 0; j < shipEnemies.size; j++) {
				if (shoots.get(i).overlaps(shipEnemies.get(j))) {
					shoots.get(i).isAlive = false;
					shipEnemies.get(j).isAlive = false;
					sndBoom.play();
				}
			}
			if (!shoots.get(i).isAlive) shoots.removeIndex(i); // удаляем из списка мёртвые выстрелы
		}

		// рождение врагов
		if (TimeUtils.millis() - lastEnemySpawnTime > enemySpawnInterval) spawnEnemy();

		// перемещение врагов
		for (int i = 0; i < shipEnemies.size; i++) {
			shipEnemies.get(i).move();
			if (shipEnemies.get(i).y < 0 && ship.isAlive) gameOver(); // если пробрались за край
			if (!shipEnemies.get(i).isAlive) shipEnemies.removeIndex(i); // удаление мёртвых врагов
		}
	}

	// создание выстрела
	void spawnShoot() {
		shoots.add(new Shoot(ship));
		lastShootTime = TimeUtils.millis();
		sndShoot.play();
	}

	// создание врага
	void spawnEnemy() {
		shipEnemies.add(new ShipEnemy());
		lastEnemySpawnTime = TimeUtils.millis();
	}

	// конец игры
	void gameOver() {
		ship.isAlive = false;
		sndBoom.play();
	}

	private void loadResources() {
		// загружаем текстуры
		imgShip = new Texture("our_ship.png");
		imgEnemyShip = new Texture("enemies_ship.png");
		imgShootUp = new Texture("shoot_our.png");
		imgShootDown = new Texture("shoot_down.png");
		imgSky = new Texture("sky.jpg");

		// загружаем звуки
		sndBoom = Gdx.audio.newSound(Gdx.files.internal("explode_sound.mp3"));
		sndShoot = Gdx.audio.newSound(Gdx.files.internal("shoot_sound.mp3"));
	}

	@Override
	public void dispose () {
		batch.dispose();
		imgShip.dispose();
		imgEnemyShip.dispose();
		imgShootUp.dispose();
		imgShootDown.dispose();
		imgSky.dispose();
		sndBoom.dispose();
		sndShoot.dispose();
	}




}
