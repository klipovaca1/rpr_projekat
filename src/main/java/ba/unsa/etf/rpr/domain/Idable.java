package ba.unsa.etf.rpr.domain;

/**
 * An interface that restricts to classes to have an ID parameter
 */
public interface Idable {
    int getId();
    void setId(int id);
}