# OOP Battle Royale

Ett litet terminalspel i Java där fyra figurer slåss tills bara en återstår. Projektet demonstrerar objektorienterad programmering med arv, overriding och polymorfism.

## Figurer

| Figur | HP | Egenskap |
|---|---:|---|
| Warrior | 145 | Kan göra en kraftattack |
| Mage | 105 | Kastar en eldboll med varierande skada |
| Archer | 115 | Har chans till en kritisk träff |
| Tank | 175 | Rustning minskar inkommande skada |

Måltavlor och vissa attackeffekter väljs slumpmässigt, så striden kan få olika resultat vid varje körning.

## Krav

- Java 17 eller senare

## Kör spelet

Kör kommandona från projektets rotmapp:

```bash
mkdir -p out
javac -d out src/Main.java
java -cp out Main
```

I IntelliJ IDEA kan du också köra `Main`.

## Kör testerna

Testerna använder bara Java och kräver inget separat testbibliotek:

```bash
mkdir -p out
javac -d out src/Main.java test/TestMain.java
java -cp out TestMain
```

Testerna kontrollerar startliv, skada, besegrade figurer, attackernas skadeintervall, Tanks rustning, ogiltiga strider och att en strid kan få en vinnare. Ett lyckat testresultat avslutas med `All 6 tests passed.`

## OOP i projektet

- **Inkapsling:** `Character` håller namn och hälsa privata och ger kontrollerad åtkomst via metoder.
- **Arv:** `Warrior`, `Mage`, `Archer` och `Tank` ärver från `Character`.
- **Overriding:** Varje figur har sin egen version av `attack()`. `Tank` ändrar också `takeDamage()`.
- **Polymorfism:** Striden arbetar med en lista av typen `List<Character>`. När `attack()` anropas väljer Java implementationen som hör till det faktiska objektet.

## Klassöversikt

Varje färg representerar en klass. Pilarna från `Character` visar arv; streckade pilar visar att `Battle` och `TestMain` använder eller testar spelets klasser.

```mermaid
flowchart TD
    C["Character — basklass<br/>namn, liv och grundskada<br/>attack(), takeDamage(), isAlive()"]
    W["Warrior<br/>kraftattack"]
    M["Mage<br/>eldboll"]
    A["Archer<br/>kritisk träff"]
    T["Tank<br/>rustning"]
    B["Battle<br/>väljer måltavlor<br/>och håller reda på rundorna"]
    X["TestMain<br/>kontrollerar reglerna<br/>och att striden fungerar"]

    C -->|"ärvs av"| W
    C -->|"ärvs av"| M
    C -->|"ärvs av"| A
    C -->|"ärvs av"| T
    B -.->|"använder"| C
    X -.->|"testar"| C
    X -.->|"testar"| B

    style C fill:#DBEAFE,stroke:#2563EB,color:#172554,stroke-width:2px
    style W fill:#FEF3C7,stroke:#D97706,color:#422006,stroke-width:2px
    style M fill:#EDE9FE,stroke:#7C3AED,color:#2E1065,stroke-width:2px
    style A fill:#DCFCE7,stroke:#16A34A,color:#052E16,stroke-width:2px
    style T fill:#FFE4E6,stroke:#E11D48,color:#4C0519,stroke-width:2px
    style B fill:#CFFAFE,stroke:#0891B2,color:#083344,stroke-width:2px
    style X fill:#F3F4F6,stroke:#4B5563,color:#111827,stroke-width:2px
```

## Stridens flöde

```mermaid
flowchart TD
    A["main skapar fyra figurer"] --> B["Battle.fight får listan"]
    B --> C{"Minst två figurer lever?"}
    C -- Nej --> F["Skriv ut vinnaren"]
    C -- Ja --> D["Nästa levande figur väljer en levande motståndare"]
    D --> E["Figurens egen attack körs"]
    E --> G["Måltavlan tar skada; Tank reducerar skadan"]
    G --> C

    classDef startEnd fill:#DBEAFE,stroke:#2563EB,color:#172554,stroke-width:2px
    classDef battleStep fill:#CFFAFE,stroke:#0891B2,color:#083344,stroke-width:2px
    classDef decision fill:#FEF3C7,stroke:#D97706,color:#422006,stroke-width:2px
    classDef attackStep fill:#EDE9FE,stroke:#7C3AED,color:#2E1065,stroke-width:2px
    classDef damageStep fill:#FFE4E6,stroke:#E11D48,color:#4C0519,stroke-width:2px

    class A,F startEnd
    class B battleStep
    class C decision
    class D,E attackStep
    class G damageStep
```

## Projektstruktur

```text
oop-battle-royale/
├── README.md
├── src/
│   └── Main.java
└── test/
    └── TestMain.java
```

Kompilerade `.class`-filer sparas i `out/`. Det är byggresultat som kan återskapas från källkoden; `.gitignore` gör att Git inte behöver lägga upp dem.
