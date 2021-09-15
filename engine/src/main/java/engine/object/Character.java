package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;

public class Character extends GameObject {

    private String characterId;
    private String characterName;
    private final CharacterStats characterStats;

    public Character(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderKey) {
        super(primitiveMeshShape, shaderKey);
        this.characterStats = new CharacterStats();
    }

    public CharacterStats getCharacterStats() {
        return this.characterStats;
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
}
