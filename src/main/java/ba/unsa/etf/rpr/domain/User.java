package ba.unsa.etf.rpr.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * A Java Bean that represents one table of the database used for the purposes of this application
 */
public class User implements Idable {
    private int id;
    private String name;
    private String password;
    private String userType;

    public User() {
    }
    public User(int id, String name, String password, String userType) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.userType = StringUtils.capitalize(userType.toLowerCase());
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = StringUtils.capitalize(userType.toLowerCase());
    }
    @Override
    public String toString() {
        return '\n' + userType + ' ' + name + " with ID of " + id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User)o;
        return id == user.id && name.equals(user.name) && password.equals(user.password) && userType.equals(user.userType);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, userType);
    }
}