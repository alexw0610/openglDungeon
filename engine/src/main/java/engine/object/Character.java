package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;

public class Character extends GameObject {

    private String characterId;
    private String characterName;
    private short characterLevel;
    private float characterHealth;

    public Character(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderKey) {
        super(primitiveMeshShape, shaderKey);
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public short getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(short characterLevel) {
        this.characterLevel = characterLevel;
    }

    public float getCharacterHealth() {
        return characterHealth;
    }

    public void setCharacterHealth(float characterHealth) {
        this.characterHealth = characterHealth;
    }
}
