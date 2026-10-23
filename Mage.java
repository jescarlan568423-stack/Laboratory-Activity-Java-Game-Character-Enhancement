package CCE105;

import java.awt.Color;

class Mage extends GameCharacter {
 public Mage(String name) {
     super(name, 80);
 }

 @Override
 public int attack(GameCharacter target) {
     int damage = 12 + random.nextInt(7);   
     target.takeDamage(damage);
     gainEnergy(25);                       
     return damage;
 }

 @Override
 public int specialMove(GameCharacter target) {
     spendEnergy(SPECIAL_COST);
     int damage = 30 + random.nextInt(11);  
     target.takeDamage(damage);
     return damage;
 }

 @Override
 public String getAttackMessage() {
     return name + " casts a fireball!";
 }

 @Override
 public String getSpecialMessage() {
     return name + " uses Lightning Storm!";
 }

 @Override
 public String getType() {
     return "Mage";
 }

 @Override
 public Color getColor() {
     return new Color(110, 80, 200);
 }
}