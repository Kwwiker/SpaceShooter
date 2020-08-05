package ru.kirusha.spaceshooter;

import com.badlogic.gdx.math.MathUtils;

public class SpaceObject {
    float x, y;
    int width, height;
    boolean isAlive = true;
    float vx, vy; // скорость

    // метод перемещения
    void move() {
        x += vx;
        y += vy;
        // проверка вылета за экран
        if (x < 0 - width || x > SpaceShooter.SCR_WIDTH || y < 0 - height || y > SpaceShooter.SCR_HEIGHT) {
            isAlive = false;
        }
    }

    // метод определения пересечения 2-x объектов
    boolean overlaps(SpaceObject object) {
        return (x > object.x && x < object.x + object.width || object.x > x && object.x < x + width) &&
                (y > object.y && x < object.y + object.height || object.y > y && object.y < y + height);
    }

}

// класс выстрела
class  Shoot extends SpaceObject {
    Shoot(SpaceObject ship) {
        width = 16;
        height = 16;
        x = ship.x + ship.width / 2 - 8;
        y = ship.y + ship.height / 2 - 8;
        vy = 8;
    }
}

// класс врага
class ShipEnemy extends SpaceObject {
    ShipEnemy() {
        width = 64;
        height = 64;
        // случайное появление
        x = MathUtils.random(0, SpaceShooter.SCR_WIDTH - width);
        y = SpaceShooter.SCR_HEIGHT;
        vy = MathUtils.random(-5, -2);
    }
}

// класс самолёта
class Ship extends SpaceObject {
    Ship() {
        width = 64;
        height = 64;
        x = SpaceShooter.SCR_WIDTH / 2f - width / 2;
        y = 20;
    }
}

// класс звёздного неба
class Stars extends SpaceObject {
    Stars(float y) {
        width = SpaceShooter.SCR_WIDTH;
        height = SpaceShooter.SCR_HEIGHT;
        vy = -1;
        this.y = y;
    }

    @Override
    void move() {
        super.move();
        if (y <= -height) y = height;
    }
}
