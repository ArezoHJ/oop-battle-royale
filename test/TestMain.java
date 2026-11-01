import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class TestMain {
    private static int testsPassed;

    public static void main(String[] args) {
        runTest("characters start with expected health", TestMain::testStartingHealth);
        runTest("damage reduces health and cannot revive a defeated character",
                TestMain::testTakingDamage);
        runTest("all character types deal damage in their expected ranges",
                TestMain::testAttackDamageRanges);
        runTest("tank armor blocks damage and ignores negative damage",
                TestMain::testTankArmor);
        runTest("battle rejects invalid participant lists", TestMain::testInvalidBattles);
        runTest("battle ends with one winner", TestMain::testBattleHasWinner);

        System.out.println("\nAll " + testsPassed + " tests passed.");
    }

    private static void testStartingHealth() {
        check(new Warrior("Test Warrior").getHealth() == 145, "Warrior should start with 145 HP.");
        check(new Mage("Test Mage").getHealth() == 105, "Mage should start with 105 HP.");
        check(new Archer("Test Archer").getHealth() == 115, "Archer should start with 115 HP.");
        check(new Tank("Test Tank").getHealth() == 175, "Tank should start with 175 HP.");
    }

    private static void testTakingDamage() {
        TestFighter fighter = new TestFighter("Test Fighter", 100, 10, 30);

        runSilently(() -> fighter.takeDamage(35));
        check(fighter.getHealth() == 65, "Taking 35 damage should leave 65 HP.");
        check(fighter.isAlive(), "A character with HP remaining should be alive.");

        runSilently(() -> fighter.takeDamage(100));
        check(fighter.getHealth() == 0, "Health should stop at zero.");
        check(!fighter.isAlive(), "A character with zero HP should be defeated.");

        runSilently(() -> fighter.takeDamage(5));
        check(fighter.getHealth() == 0, "A defeated character should not take more damage.");
    }

    private static void testAttackDamageRanges() {
        checkAttackDamage("Warrior", new Warrior("Warrior"), 20, 47);
        checkAttackDamage("Mage", new Mage("Mage"), 23, 38);
        checkAttackDamage("Archer", new Archer("Archer"), 18, 52);
        checkAttackDamage("Tank", new Tank("Tank"), 14, 22);
    }

    private static void testTankArmor() {
        Tank tank = new Tank("Armored Tank");

        runSilently(() -> tank.takeDamage(10));
        check(tank.getHealth() == 172, "Armor should reduce 10 damage to 3.");

        runSilently(() -> tank.takeDamage(7));
        check(tank.getHealth() == 172, "Armor should block an attack of 7 completely.");

        runSilently(() -> tank.takeDamage(-5));
        check(tank.getHealth() == 172, "Negative damage should not heal or hurt the tank.");
    }

    private static void testInvalidBattles() {
        expectIllegalArgument(() -> Battle.fight(null), "A null fighter list should be rejected.");
        expectIllegalArgument(
                () -> Battle.fight(List.of()),
                "An empty fighter list should be rejected."
        );
        expectIllegalArgument(
                () -> Battle.fight(List.of(new Warrior("Only Fighter"))),
                "A battle with fewer than two fighters should be rejected."
        );
    }

    private static void testBattleHasWinner() {
        TestFighter first = new TestFighter("Reliable Winner", 100, 10, 100);
        TestFighter second = new TestFighter("Reliable Opponent", 100, 10, 100);

        String output = captureOutput(() -> Battle.fight(List.of(first, second)));

        check(!first.isAlive() ^ !second.isAlive(),
                "Exactly one fighter should survive this deterministic battle.");
        check(output.contains("WINS THE BATTLE ROYALE!"),
                "The battle should print a winner.");
    }

    private static void checkAttackDamage(String type, Character attacker, int minimum, int maximum) {
        TestFighter target = new TestFighter("Target", 1_000, 1, 0);
        int startingHealth = target.getHealth();

        runSilently(() -> attacker.attack(target));

        int damage = startingHealth - target.getHealth();
        check(damage >= minimum && damage <= maximum,
                type + " damage should be between " + minimum + " and " + maximum
                        + ", but was " + damage + ".");
    }

    private static void expectIllegalArgument(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError(message);
    }

    private static void runTest(String name, Runnable test) {
        test.run();
        testsPassed++;
        System.out.println("PASS: " + name);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void runSilently(Runnable action) {
        captureOutput(action);
    }

    private static String captureOutput(Runnable action) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream replacementOutput = new PrintStream(capturedOutput)) {
            System.setOut(replacementOutput);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return capturedOutput.toString();
    }

    private static final class TestFighter extends Character {
        private final int fixedDamage;

        private TestFighter(String name, int health, int attackDamage, int fixedDamage) {
            super(name, health, attackDamage);
            this.fixedDamage = fixedDamage;
        }

        @Override
        public void attack(Character target) {
            if (canAttack(target)) {
                target.takeDamage(fixedDamage);
            }
        }
    }
}
