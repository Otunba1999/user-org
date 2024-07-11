package java.com.otunba.repository;

import com.otunba.models.User;
import com.otunba.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.otunba.TestModels.getUser1;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    public void tes_that_repository_will_save_user(){
        User user = new User();
        user.setEmail("test@otunba.com");
        user.setPassword("password");
        user.setFirstname("otunba");
        user.setLastname("otunba");

        var savedUser = userRepository.save(user);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
    }

    @Test
    public void test_that_repository_will_find_user_username_if_exist(){
        var savedUser = userRepository.save(getUser1());
        var foundUser = userRepository.findByEmail(savedUser.getEmail());
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    public void test_that_repository_will_not_find_user_username_if_not_exist(){
        var savedUser = userRepository.save(getUser1());
        var foundUser = userRepository.findByEmail("test@gmail.com");
        assertTrue(!foundUser.isPresent());
    }
}