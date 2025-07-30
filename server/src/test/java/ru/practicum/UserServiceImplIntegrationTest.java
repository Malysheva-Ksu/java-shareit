package ru.practicum;

import org.springframework.test.annotation.DirtiesContext;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.user.User;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserUpdateDto;
import ru.practicum.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    private User userToUpdate;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        userToUpdate = User.builder()
                .name("name")
                .email("name@example.com")
                .build();

        anotherUser = User.builder()
                .name("name2")
                .email("name2@example.com")
                .build();

        entityManager.persist(userToUpdate);
        entityManager.persist(anotherUser);
        entityManager.flush();
    }

    @Test
    void updateUser_whenNameAndEmailAreValid_shouldUpdateUserInDatabase() {
        UserUpdateDto updateDto = new UserUpdateDto("name", "name.new@example.com");

        UserResponseDto responseDto = userService.updateUser(userToUpdate.getId(), updateDto);

        assertNotNull(responseDto);
        assertEquals(userToUpdate.getId(), responseDto.getId());
        assertEquals("name", responseDto.getName());
        assertEquals("name.new@example.com", responseDto.getEmail());

        entityManager.flush();
        entityManager.clear();
        User updatedUserFromDb = entityManager.find(User.class, userToUpdate.getId());

        assertNotNull(updatedUserFromDb);
        assertEquals("name", updatedUserFromDb.getName());
        assertEquals("name.new@example.com", updatedUserFromDb.getEmail());
    }

    @Test
    @Commit
    void updateUser_whenOnlyNameIsProvided_shouldUpdateOnlyName() {
        UserUpdateDto updateDto = new UserUpdateDto("nameUpdate", null);

        userService.updateUser(userToUpdate.getId(), updateDto);

        entityManager.flush();
        entityManager.clear();

        User updatedUserFromDb = entityManager.find(User.class, userToUpdate.getId());

        assertNotNull(updatedUserFromDb);
        assertEquals("nameUpdate", updatedUserFromDb.getName());
        assertEquals("name@example.com", updatedUserFromDb.getEmail());
    }

    @Test
    void updateUser_whenEmailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        UserUpdateDto updateDto = new UserUpdateDto("name", "name2@example.com");

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(userToUpdate.getId(), updateDto);
        });

        entityManager.clear();
        User notUpdatedUser = entityManager.find(User.class, userToUpdate.getId());
        assertEquals("name", notUpdatedUser.getName());
        assertEquals("name@example.com", notUpdatedUser.getEmail());
    }
}