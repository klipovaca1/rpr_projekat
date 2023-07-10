package ba.unsa.etf.rpr.business;

import ba.unsa.etf.rpr.domain.Lawsuit;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LawsuitManagerTest {
    private final User defendant = new User(1, "Kemal Lipovaca", "moze10pls", "Defendant");
    private final User lawyer = new User(2, "Minela Sultanovic", "mozeimeni6pls", "Lawyer");
    private final User prosecutor = new User(3, "Ajna Balesic", "nemoguvamjapomoc", "Prosecutor");
    private final User judge = new User(4, "Dino Keco", "maaferim", "Judge");
    private Lawsuit lawsuit;
    private final LawsuitManager lawsuitManager = LawsuitManager.getInstance();

    @BeforeEach
    public void initializeObjects() {
        lawsuit = new Lawsuit(1, "test", 123, judge, defendant, lawyer, prosecutor, "Undecided");
    }
    @Test
    public void validateUserTypesFailedTest() {
        lawsuit.setJudge(defendant);
        assertThrows(CourtroomException.class, () -> lawsuitManager.validateUserTypes(lawsuit));
        lawsuit.setJudge(judge);
        lawsuit.setLawyer(judge);
        assertThrows(CourtroomException.class, () -> lawsuitManager.validateUserTypes(lawsuit));
        lawsuit.setLawyer(lawyer);
        lawsuit.setProsecutor(lawyer);
        assertThrows(CourtroomException.class, () -> lawsuitManager.validateUserTypes(lawsuit));
    }
    @Test
    public void validateUserTypesSuccessTest() {
        assertDoesNotThrow(() -> lawsuitManager.validateUserTypes(lawsuit));
    }
    @Test
    public void validateCaseMembersFailedTest() {
        lawsuit.setDefendant(judge);
        assertThrows(CourtroomException.class, () -> lawsuitManager.validateCaseMembers(lawsuit));
        lawsuit.setDefendant(lawyer);
        assertThrows(CourtroomException.class, () -> lawsuitManager.validateCaseMembers(lawsuit));
        lawsuit.setDefendant(prosecutor);
        assertThrows(CourtroomException.class, () -> lawsuitManager.validateCaseMembers(lawsuit));
    }
    @Test
    public void validateCaseMembersSuccessTest() {
        assertDoesNotThrow(() -> lawsuitManager.validateCaseMembers(lawsuit));
    }
}