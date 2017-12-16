/**
 * Created by Yaskovich Dmitry on 15/12/2017.
 */
public class CardData implements Comparable<CardData> {
    private String nameEN, nameRU, rarity, gameSet, type;
    int id;
    boolean wild;
    private double popularityStandard, winrateStandard, copiesStandard, ratingStandard,
            popularityWild, winrateWild, copiesWild, ratingWild, ratingOverall;

    public CardData(String nameEN, int id) {
        this.nameEN = nameEN;
        this.id = id;
        this.wild=true;
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

    public double getRatingStandard() {
        return ratingStandard;
    }

    public void setRatingStandard(double ratingStandard) {
        this.ratingStandard = ratingStandard;
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

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getGameSet() {
        return gameSet;
    }

    public void setGameSet(String gameSet) {
        this.gameSet = gameSet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CardData{" +
                "nameEN='" + nameEN + '\'' +
                ", nameRU='" + nameRU + '\'' +
                ", rarity='" + rarity + '\'' +
                ", gameSet='" + gameSet + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", popularityStandard=" + popularityStandard +
                ", winrateStandard=" + winrateStandard +
                ", copiesStandard=" + copiesStandard +
                ", ratingStandard=" + ratingStandard +
                ", popularityWild=" + popularityWild +
                ", winrateWild=" + winrateWild +
                ", copiesWild=" + copiesWild +
                ", ratingWild=" + ratingWild +
                ", ratingOverall=" + ratingOverall +
                '}';
    }

    @Override
    public int compareTo(CardData o) {
        return (int)(getRatingOverall()*1000000)- (int)(o.getRatingOverall()*1000000);
    }

}
