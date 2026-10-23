package CCE105;

import java.awt.Color;

class Warrior extends GameCharacter {
 public Warrior(String name) {
     super(name, 120);
 }

 @Override
 public int attack(GameCharacter target) {
     int damage = 10 + random.nextInt(7);   
     target.takeDamage(damage);
     gainEnergy(20);
     return damage;
 }

 @Override
 public int specialMove(GameCharacter target) {
     spendEnergy(SPECIAL_COST);
     int damage = 20 + random.nextInt(9);   
     target.takeDamage(damage);
     heal(8);
     return damage;
 }

 @Override
 public String getAttackMessage() {
     return name + " swings a mighty sword!";
 }

 @Override
 public String getSpecialMessage() {
     return name + " uses Shield Bash and recovers 8 HP!";
 }

 @Override
 public String getType() {
     return "Warrior";
 }

 @Override
 public Color getColor() {
     return new Color(200, 70, 60);
 }
}