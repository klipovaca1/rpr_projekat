package ba.unsa.etf.rpr.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * A Java Bean that represents one table of the database used for the purposes of this application
 * This name was chosen for the class because "Case" is a reserved keyword in MySQL (and also in Java)
 */
public class Lawsuit implements Idable {
    private int id;
    private String title;
    private long uin;
    private User judge;
    private User defendant;
    private User lawyer;
    private User prosecutor;
    private String verdict;

    public Lawsuit() {
    }
    public Lawsuit(int id, String title, long uin, User judge, User defendant, User lawyer, User prosecutor, String verdict) {
        this.id = id;
        this.title = title;
        this.uin = uin;
        this.judge = judge;
        this.defendant = defendant;
        this.lawyer = lawyer;
        this.prosecutor = prosecutor;
        this.verdict = StringUtils.capitalize(verdict.toLowerCase());
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getUin() {
        return uin;
    }
    public void setUin(long uin) {
        this.uin = uin;
    }
    public User getJudge() {
        return judge;
    }
    public void setJudge(User judge) {
        this.judge = judge;
    }
    public User getDefendant() {
        return defendant;
    }
    public void setDefendant(User defendant) {
        this.defendant = defendant;
    }
    public User getLawyer() {
        return lawyer;
    }
    public void setLawyer(User lawyer) {
        this.lawyer = lawyer;
    }
    public User getProsecutor() {
        return prosecutor;
    }
    public void setProsecutor(User prosecutor) {
        this.prosecutor = prosecutor;
    }
    public String getVerdict() {
        return verdict;
    }
    public void setVerdict(String verdict) {
        this.verdict = StringUtils.capitalize(verdict.toLowerCase());
    }

    @Override
    public String toString() {
        return "\nID = " + id + ", \"" + title + "\", UIN = " + uin + judge + defendant + lawyer + prosecutor + "\nFinal verdict - " + verdict;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Lawsuit lawsuit = (Lawsuit)o;
        return id == lawsuit.id && uin == lawsuit.uin && title.equals(lawsuit.title) && judge.equals(lawsuit.judge) && defendant.equals(lawsuit.defendant) && lawyer.equals(lawsuit.lawyer) && prosecutor.equals(lawsuit.prosecutor) && verdict.equals(lawsuit.verdict);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, title, uin, judge, defendant, lawyer, prosecutor, verdict);
    }
}