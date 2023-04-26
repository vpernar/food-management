package rs.raf.domaci4;

import java.util.Objects;

public class Obrok {
    private String jelo;
    private String dan;
    private int broj;

    public Obrok(String jelo) {
        this.jelo = jelo;
        broj = 1;
    }

    public String getJelo() {
        return jelo;
    }

    public void setJelo(String naziv) {
        this.jelo = naziv;
    }

    public String getDan() {
        return dan;
    }

    public void setDan(String dan) {
        this.dan = dan;
    }

    public int getBroj() {
        return broj;
    }

    public void setBroj(int broj) {
        this.broj = broj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Obrok obrok = (Obrok) o;

        if (!Objects.equals(jelo, obrok.jelo)) return false;
        return Objects.equals(dan, obrok.dan);
    }

    @Override
    public int hashCode() {
        int result = jelo != null ? jelo.hashCode() : 0;
        result = 31 * result + (dan != null ? dan.hashCode() : 0);
        return result;
    }
}
