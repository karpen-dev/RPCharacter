package me.arahis.rpcharacter.models;

public class Character {
    private Long id;
    private String ownerName;
    private String ownerUUID;
    private String charRole;
    private String charName;
    private String propertyName;
    private String propertyValue;
    private String propertySignature;

    public Character(Long id, String ownerName, String ownerUUID, String charRole, String charName, String propertyName, String propertyValue, String propertySignature) {
        this.id = id;
        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
        this.charRole = charRole;
        this.charName = charName;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.propertySignature = propertySignature;
    }

    public Character(String ownerName, String ownerUUID, String charRole, String charName, String propertyName, String propertyValue, String propertySignature) {
        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
        this.charRole = charRole;
        this.charName = charName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getCharRole() {
        return charRole;
    }

    public void setCharRole(String charRole) {
        this.charRole = charRole;
    }

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertySignature() {
        return propertySignature;
    }

    public void setPropertySignature(String propertySignature) {
        this.propertySignature = propertySignature;
    }
}
