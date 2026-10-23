package CCE105;

import java.awt.Color;
import java.util.Random;

abstract class GameCharacter {
    protected static final int MAX_ENERGY = 100;
    protected static final int SPECIAL_COST = 50;
    protected static final int START_ENERGY = 20;

    protected final Random random = new Random();
    protected final String name;
    protected final int maxHp;
    protected int hp;
    protected int energy;

    public GameCharacter(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        resetForBattle();
    }
   
    public abstract int attack(GameCharacter target);       
    public abstract int specialMove(GameCharacter target);  
    public abstract String getAttackMessage();
    public abstract String getSpecialMessage();
    public abstract String getType();
    public abstract Color getColor();

    public String showName() {
        return "Character: " + name;
    }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void gainEnergy(int amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    public void spendEnergy(int amount) {
        energy = Math.max(0, energy - amount);
    }

    public boolean canUseSpecial() {
        return energy >= SPECIAL_COST;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void resetForBattle() {
        hp = maxHp;
        energy = START_ENERGY;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getEnergy() { return energy; }
}