package ba.unsa.etf.rpr.business;

import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagerTest {
    private final String incorrectName = "IncorrectNameFormat";
    private final String correctName = "Correct name format";
    private final String incorrectPassword = "shortpw";
    private final String correctPassword = "normal password";
    private UserManager userManager;
    private List<User> users;

    @BeforeEach
    public void initializeObjects() {
        userManager = Mockito.mock(UserManager.class);
        users = new ArrayList<>();
        users.add(new User(1, "Kemal Lipovaca", "moze10pls", "Defendant"));
        users.add(new User(2, "Minela Sultanovic", "mozeimeni6pls", "Lawyer"));
        users.add(new User(3, "Ajna Balesic", "nemoguvamjapomoc", "Prosecutor"));
        users.add(new User(4, "Dino Keco", "maaferim", "Judge"));
    }
    @Test
    public void validateCredentialsMockingTest() throws CourtroomException {
        Mockito.doCallRealMethod().when(userManager).validateCredentials(incorrectName, incorrectPassword);
        assertThrows(CourtroomException.class, () -> userManager.validateCredentials(incorrectName, incorrectPassword));

        Mockito.doCallRealMethod().when(userManager).validateCredentials(correctName, incorrectPassword);
        assertThrows(CourtroomException.class, () -> userManager.validateCredentials(correctName, incorrectPassword));

        Mockito.doCallRealMethod().when(userManager).validateCredentials(incorrectName, correctPassword);
        assertThrows(CourtroomException.class, () -> userManager.validateCredentials(incorrectName, correctPassword));

        Mockito.doCallRealMethod().when(userManager).validateCredentials(correctName, correctPassword);
        assertDoesNotThrow(() -> userManager.validateCredentials(correctName, correctPassword));
    }
    @Test
    public void addUserMockingTest() throws CourtroomException {
        Mockito.doCallRealMethod().when(userManager).addUser(users.get(0).getName(), users.get(0).getUserType());
        assertThrows(CourtroomException.class, () -> userManager.addUser(users.get(0).getName(), users.get(0).getUserType()));
        Mockito.verify(userManager).addUser(users.get(0).getName(), users.get(0).getUserType());
    }
    @Test
    public void deleteUserKindOfMockingTest() throws CourtroomException {
        Mockito.when(userManager.deleteUser(0)).thenReturn(users.get(0));
        assertEquals(userManager.deleteUser(0), users.get(0));
        Mockito.verify(userManager).deleteUser(0);
    }
    @Test
    public void getAllUsersKindOfMockingTest() throws CourtroomException {
        Mockito.when(userManager.getAllUsers()).thenReturn(users);
        assertEquals(userManager.getAllUsers(), users);
        Mockito.verify(userManager).getAllUsers();
    }
    @Test
    public void validateCredentialsFailedTest() {
        userManager = UserManager.getInstance();
        assertThrows(CourtroomException.class, () -> userManager.validateCredentials(incorrectName, incorrectPassword));
        assertThrows(CourtroomException.class, () -> userManager.validateCredentials(correctName, incorrectPassword));
        assertThrows(CourtroomException.class, () -> userManager.validateCredentials(incorrectName, correctPassword));
    }
    @Test
    public void validateCredentialsSuccessTest() {
        userManager = UserManager.getInstance();
        assertDoesNotThrow(() -> userManager.validateCredentials(correctName, correctPassword));
    }
}