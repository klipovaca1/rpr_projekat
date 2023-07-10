package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.domain.Lawsuit;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.sql.*;
import java.util.*;

/**
 * The class that bridges the court case information stored in the database and code that uses the stored court case information
 * This class represents the implementation of the LawsuitDAO interface using MySQL
 */
public class LawsuitDAOSQLImplementation extends AbstractDAO<Lawsuit> implements LawsuitDAO {
    private static LawsuitDAOSQLImplementation instance = null;
    public LawsuitDAOSQLImplementation() {
        super("Lawsuit");
    }
    /**
     * This method represents the implementation of the Singleton Design Pattern in this application
     * Since this class is going to be used in multiple different places it would be wise to allow only the existence of one instance of this class at a given time
     * @return an instance of this class
     */
    public static LawsuitDAOSQLImplementation getInstance() {
        if (instance == null) {
            instance = new LawsuitDAOSQLImplementation();
        }
        return instance;
    }
    @Override
    public Lawsuit tableRowToObject(ResultSet resultSet) throws CourtroomException {
        Lawsuit lawsuit = new Lawsuit();
        try {
            lawsuit.setId(resultSet.getInt("id"));
            lawsuit.setTitle(resultSet.getString("title"));
            lawsuit.setUin(resultSet.getLong("uin"));
            lawsuit.setJudge(DAOFactory.getUserDAO().getById(resultSet.getInt("judgeId")));
            lawsuit.setDefendant(DAOFactory.getUserDAO().getById(resultSet.getInt("defendantId")));
            lawsuit.setLawyer(DAOFactory.getUserDAO().getById(resultSet.getInt("lawyerId")));
            lawsuit.setProsecutor(DAOFactory.getUserDAO().getById(resultSet.getInt("prosecutorId")));
            lawsuit.setVerdict(resultSet.getString("verdict"));
        }
        catch (SQLException error) {
            throw new CourtroomException(error.getMessage(), error);
        }
        return lawsuit;
    }
    @Override
    public Map<String,Object> objectToTableRow(Lawsuit lawsuit) {
        Map<String,Object> tableRow = new LinkedHashMap<>();
        tableRow.put("id", lawsuit.getId());
        tableRow.put("title", lawsuit.getTitle());
        tableRow.put("uin", lawsuit.getUin());
        tableRow.put("judgeId", lawsuit.getJudge().getId());
        tableRow.put("defendantId", lawsuit.getDefendant().getId());
        tableRow.put("lawyerId", lawsuit.getLawyer().getId());
        tableRow.put("prosecutorId", lawsuit.getProsecutor().getId());
        tableRow.put("verdict", lawsuit.getVerdict());
        return tableRow;
    }
    @Override
    public List<Lawsuit> searchByUser(User user) throws CourtroomException {
        return searchResult("SELECT * FROM Lawsuit WHERE judgeId = ? OR defendantId = ? OR lawyerId = ? OR prosecutorId = ?", new Object[]{user.getId(), user.getId(), user.getId(), user.getId()});
    }
}