# Laboratory-Activity-Java-Game-Character-Enhancement
# 🎮 Battle Arena: Data Abstraction in Java (DSA Lesson)

## 📌 What is Data Abstraction?

- **Abstraction** is the process of showing only the essential details and hiding the implementation.
- In **OOP with Java**, abstraction is usually implemented using **abstract classes** and **interfaces**.
- It lets us focus on _what_ an object does, not _how_ it does it.

---

## 🐾 Example: Game Characters with Abstraction

We define an **abstract class** `GameCharacter` that all characters must extend.  
It forces every character to implement `attack()` and `specialMove()`, but allows unique behaviors too.

This project upgrades the original console program into a **turn-based fighting game** with a Swing GUI, animations, and a tournament queue.

---

## 🚀 How to Run

1. Install **JDK 11 or newer**.
2. Put `Main.java`, `GameCharacter.java`, `Warrior.java`, and `Mage.java` in the same folder.
3. Compile:

```
javac *.java
```

4. Run:

```
java Main
```

---

## 🕹️ How to Play

1. Type a fighter name (1-12 letters, numbers, or spaces) and pick **Warrior** or **Mage**.
2. Click **Add Fighter**. Add at least 2 fighters.
3. Click **START BATTLE**.
4. On your turn, choose:
   - **Attack** – deals damage and builds energy.
   - **Special Move** – costs 50 energy and hits much harder.
5. The winner fights the next fighter in the queue until one **Champion** remains.

---

## ⚔️ Characters

| Class       | HP  | Attack | Special Move                              |
| ----------- | --- | ------ | ----------------------------------------- |
| 🛡️ Warrior  | 120 | 10-16  | Shield Bash (20-28 damage, heals 8 HP)    |
| 🔮 Mage     | 80  | 12-18  | Lightning Storm (30-40 damage)            |

The Mage charges energy faster, while the Warrior is tougher.

---

## 🖥️ Sample Battle Log

```
Character: Brader (Warrior)  VS  Character: GabMids (Mage)
Brader swings a mighty sword!  (13 damage)
GabMids casts a fireball!  (15 damage)
Brader swings a mighty sword!  (11 damage)
GabMids uses Lightning Storm!  (35 damage)
Brader uses Shield Bash and recovers 8 HP!  (24 damage)
```

---

## ✨ Added Features

- Swing GUI with a **setup screen** and an **animated battle arena**
- Animations: attack lunge, hit flash and shake, floating damage numbers, smooth HP bars
- Special move effects: ⚡ lightning for the Mage, 💥 shockwave for the Warrior
- Energy system and a live battle log
- Input validation:
  - empty name
  - invalid characters or longer than 12 characters
  - duplicate names
  - maximum of 8 fighters
  - at least 2 fighters needed to start

---

## 🧠 Concepts Applied

| Concept            | Where it is used                                                                                      |
| ------------------ | ----------------------------------------------------------------------------------------------------- |
| **Abstraction**    | `GameCharacter` is an abstract class. `Warrior` and `Mage` implement `attack()`, `specialMove()`, etc. |
| **ADT**            | A `Queue<GameCharacter>` (`LinkedList`) manages the fighter order: `offer()` to enqueue, `poll()` to dequeue. |
| **GUI**            | Java Swing with `CardLayout`, custom painting, and a `javax.swing.Timer` for animation.               |
| **User Input**     | Text field, combo box, and buttons, all validated.                                                    |

---

## 📁 Project Structure

```
Main.java           -> GUI, game flow, animations, queue (ADT)
GameCharacter.java  -> abstract base class
Warrior.java        -> concrete character
Mage.java           -> concrete character
```

---

## 📝 Summary

- **Abstraction** = “Define what, hide how.”
- Abstract classes/interfaces define the **rules**.
- Subclasses provide the **details**.
- Our game example makes it clear:
  - `GameCharacter` = abstract base
  - `Warrior`, `Mage` = concrete implementations
  - `Queue` = ADT that decides who fights next
