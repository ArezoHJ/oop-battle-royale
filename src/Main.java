import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        List<Character> fighters = new ArrayList<>(List.of(
                new Warrior("Thor"),
                new Mage("Gandalf"),
                new Archer("Robin"),
                new Tank("Atlas")
        ));

        Battle.fight(fighters);
    }
}

abstract class Character {
    protected static final Random RANDOM = new Random();

    private final String name;
    private final int maxHealth;
    private final int attackDamage;
    private int health;

    protected Character(String name, int health, int attackDamage) {
        this.name = name;
        this.maxHealth = health;
        this.health = health;
        this.attackDamage = attackDamage;
    }

    public abstract void attack(Character target);

    public void takeDamage(int damage) {
        if (!isAlive()) {
            return;
        }

        int actualDamage = Math.max(0, damage);
        health = Math.max(0, health - actualDamage);
        System.out.println(name + " takes " + actualDamage + " damage. "
                + health + "/" + maxHealth + " HP remaining.");

        if (!isAlive()) {
            System.out.println(name + " has been defeated!");
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    protected boolean canAttack(Character target) {
        return target != null && target != this && isAlive() && target.isAlive();
    }

    protected int randomDamage(int minimum, int maximum) {
        return RANDOM.nextInt(minimum, maximum + 1);
    }

    protected String getName() {
        return name;
    }

    protected int getAttackDamage() {
        return attackDamage;
    }

    public int getHealth() {
        return health;
    }
}

class Warrior extends Character {
    Warrior(String name) {
        super(name, 145, 25);
    }

    @Override
    public void attack(Character target) {
        if (!canAttack(target)) {
            return;
        }

        boolean powerStrike = RANDOM.nextInt(100) < 30;
        int damage = powerStrike
                ? getAttackDamage() + randomDamage(12, 22)
                : randomDamage(getAttackDamage() - 5, getAttackDamage() + 5);

        System.out.println(getName() + (powerStrike
                ? " uses a POWER STRIKE on "
                : " swings at ") + target.getName() + "!");
        target.takeDamage(damage);
    }
}

class Mage extends Character {
    Mage(String name) {
        super(name, 105, 29);
    }

    @Override
    public void attack(Character target) {
        if (!canAttack(target)) {
            return;
        }

        int damage = randomDamage(getAttackDamage() - 6, getAttackDamage() + 9);
        System.out.println(getName() + " casts a FIREBALL at " + target.getName() + "!");
        target.takeDamage(damage);
    }
}

class Archer extends Character {
    Archer(String name) {
        super(name, 115, 22);
    }

    @Override
    public void attack(Character target) {
        if (!canAttack(target)) {
            return;
        }

        boolean criticalHit = RANDOM.nextInt(100) < 25;
        int damage = randomDamage(getAttackDamage() - 4, getAttackDamage() + 4);
        if (criticalHit) {
            damage *= 2;
        }

        System.out.println(getName() + (criticalHit ? " lands a CRITICAL HIT on " : " shoots ")
                + target.getName() + "!");
        target.takeDamage(damage);
    }
}

class Tank extends Character {
    private static final int ARMOR = 7;

    Tank(String name) {
        super(name, 175, 18);
    }

    @Override
    public void attack(Character target) {
        if (!canAttack(target)) {
            return;
        }

        int damage = randomDamage(getAttackDamage() - 4, getAttackDamage() + 4);
        System.out.println(getName() + " slams " + target.getName() + " with a heavy strike!");
        target.takeDamage(damage);
    }

    @Override
    public void takeDamage(int damage) {
        int incomingDamage = Math.max(0, damage);
        int blockedDamage = Math.min(ARMOR, incomingDamage);
        int reducedDamage = incomingDamage - blockedDamage;
        if (isAlive()) {
            System.out.println(getName() + "'s armor blocks " + blockedDamage + " damage.");
        }
        super.takeDamage(reducedDamage);
    }
}

class Battle {
    private Battle() {
    }

    public static void fight(List<Character> fighters) {
        if (fighters == null || fighters.size() < 2) {
            throw new IllegalArgumentException("A battle needs at least two fighters.");
        }

        System.out.println("=== BATTLE ROYALE START ===");
        for (Character fighter : fighters) {
            System.out.println(fighter.getClass().getSimpleName() + " enters with "
                    + fighter.getHealth() + " HP.");
        }

        int round = 1;
        while (livingCount(fighters) > 1) {
            System.out.println("\n--- Round " + round++ + " ---");
            for (Character attacker : fighters) {
                if (!attacker.isAlive() || livingCount(fighters) <= 1) {
                    continue;
                }

                List<Character> targets = new ArrayList<>();
                for (Character fighter : fighters) {
                    if (fighter.isAlive() && fighter != attacker) {
                        targets.add(fighter);
                    }
                }

                Character target = targets.get(Character.RANDOM.nextInt(targets.size()));
                attacker.attack(target);
            }
        }

        for (Character fighter : fighters) {
            if (fighter.isAlive()) {
                System.out.println("\n=== " + fighter.getName() + " WINS THE BATTLE ROYALE! ===");
                return;
            }
        }

        System.out.println("\nNobody survived the Battle Royale!");
    }

    private static int livingCount(List<Character> fighters) {
        int count = 0;
        for (Character fighter : fighters) {
            if (fighter.isAlive()) {
                count++;
            }
        }
        return count;
    }
}
