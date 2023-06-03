package me.arahis.rpcharacter.models;

public class RPPlayer {

    private String uuid;
    private String name;
    private int amountOfChars;
    private int selectedChar;

    public RPPlayer(String uuid, String name, int amountOfChars, int selectedChar) {
        this.uuid = uuid;
        this.name = name;
        this.amountOfChars = amountOfChars;
        this.selectedChar = selectedChar;
    }

    public RPPlayer(String name, int amountOfChars, int selectedChar) {
        this.name = name;
        this.amountOfChars = amountOfChars;
        this.selectedChar = selectedChar;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmountOfChars() {
        return amountOfChars;
    }

    public void setAmountOfChars(int amountOfChars) {
        this.amountOfChars = amountOfChars;
    }

    public int getSelectedChar() {
        return selectedChar;
    }

    public void setSelectedChar(int selectedChar) {
        this.selectedChar = selectedChar;
    }
}
