package engine.object.ui;

import engine.object.Ability;

import java.util.ArrayList;

public class PlayerAbilityBar {

    public static final int SIZE = 8;

    private final static ArrayList<Ability> abilities = new ArrayList<>(SIZE);

    public static ArrayList<Ability> getAbilities() {
        return abilities;
    }

}
