import java.util.ArrayList;
/**
 * Created by Yaskovich Dmitry on 15/12/2017.
 */
public class CardData implements Comparable<CardData> {
    private String id;
    private Integer dbfId;
    private String nameEN;
    private String nameRU;
    private String text;
    private String flavor;
    private String artist;
    private Integer attack;
    private String cardClass;
    private boolean collectible;
    private Integer cost;
    private Integer elite;
    private String faction;
    private Integer health;
    private ArrayList<String> mechanics;
    private String rarity;
    private String set;
    private String type;
    private int cardID;
    private boolean wild;
    private double popularityStandard;
    private double winrateStandard;
    private double copiesStandard;
    private double ratingStandard;
    private double popularityWild;
    private double winrateWild;
    private double copiesWild;
    private double ratingWild;
    private double ratingOverall;
    private double popularityArena;
    private double winrateArena;
    private double copiesArena;
    private double ratingArena;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDbfId() {
        return dbfId;
    }

    public void setDbfId(Integer dbfId) {
        this.dbfId = dbfId;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameRU() {
        return nameRU;
    }

    public void setNameRU(String nameRU) {
        this.nameRU = nameRU;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public String getCardClass() {
        return cardClass;
    }

    public void setCardClass(String cardClass) {
        this.cardClass = cardClass;
    }

    public boolean isCollectible() {
        return collectible;
    }

    public void setCollectible(boolean collectible) {
        this.collectible = collectible;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getElite() {
        return elite;
    }

    public void setElite(Integer elite) {
        this.elite = elite;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public ArrayList<String> getMechanics() {
        return mechanics;
    }

    public void setMechanics(ArrayList<String> mechanics) {
        this.mechanics = mechanics;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public boolean isWild() {
        return wild;
    }

    public void setWild(boolean wild) {
        this.wild = wild;
    }

    public double getPopularityStandard() {
        return popularityStandard;
    }

    public void setPopularityStandard(double popularityStandard) {
        this.popularityStandard = popularityStandard;
    }

    public double getWinrateStandard() {
        return winrateStandard;
    }

    public void setWinrateStandard(double winrateStandard) {
        this.winrateStandard = winrateStandard;
    }

    public double getCopiesStandard() {
        return copiesStandard;
    }

    public void setCopiesStandard(double copiesStandard) {
        this.copiesStandard = copiesStandard;
    }

    public double getRatingStandard() {
        return ratingStandard;
    }

    public void setRatingStandard(double ratingStandard) {
        this.ratingStandard = ratingStandard;
    }

    public double getPopularityWild() {
        return popularityWild;
    }

    public void setPopularityWild(double popularityWild) {
        this.popularityWild = popularityWild;
    }

    public double getWinrateWild() {
        return winrateWild;
    }

    public void setWinrateWild(double winrateWild) {
        this.winrateWild = winrateWild;
    }

    public double getCopiesWild() {
        return copiesWild;
    }

    public void setCopiesWild(double copiesWild) {
        this.copiesWild = copiesWild;
    }

    public double getRatingWild() {
        return ratingWild;
    }

    public void setRatingWild(double ratingWild) {
        this.ratingWild = ratingWild;
    }

    public double getRatingOverall() {
        return ratingOverall;
    }

    public void setRatingOverall(double ratingOverall) {
        this.ratingOverall = ratingOverall;
    }

    public double getPopularityArena() {
        return popularityArena;
    }

    public void setPopularityArena(double popularityArena) {
        this.popularityArena = popularityArena;
    }

    public double getWinrateArena() {
        return winrateArena;
    }

    public void setWinrateArena(double winrateArena) {
        this.winrateArena = winrateArena;
    }

    public double getCopiesArena() {
        return copiesArena;
    }

    public void setCopiesArena(double copiesArena) {
        this.copiesArena = copiesArena;
    }

    public double getRatingArena() {
        return ratingArena;
    }

    public void setRatingArena(double ratingArena) {
        this.ratingArena = ratingArena;
    }

    @Override
    public String toString() {
        return "CardData{" +
                "id='" + id + '\'' +
                ", dbfID='" + dbfId + '\'' +
                ", cardID='" + cardID + '\'' +
                ", nameEN='" + nameEN + '\'' +
                ", nameRU='" + nameRU + '\'' +
                ", rarity='" + rarity + '\'' +
                ", set='" + set + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", wild=" + wild +
                ", popularityStandard=" + popularityStandard +
                ", winrateStandard=" + winrateStandard +
                ", copiesStandard=" + copiesStandard +
                ", ratingStandard=" + ratingStandard +
                ", popularityWild=" + popularityWild +
                ", winrateWild=" + winrateWild +
                ", copiesWild=" + copiesWild +
                ", ratingWild=" + ratingWild +
                ", ratingOverall=" + ratingOverall +
                ", popularityArena=" + popularityArena +
                ", winrateArena=" + winrateArena +
                ", copiesArena=" + copiesArena +
                ", ratingArena=" + ratingArena +
                '}';
    }

    @Override
    public int compareTo(CardData o) {
        return (int) (getRatingOverall() * 1000000) - (int) (o.getRatingOverall() * 1000000);
    }

}
