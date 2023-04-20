package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.domain.dto.LogInRequestDTO;
import hexlet.code.domain.dto.UserRequestDTO;
import hexlet.code.domain.dto.UserResponseDTO;
import hexlet.code.domain.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static hexlet.code.utils.TestUtils.TOKEN_PREFIX;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Bootstrap the full application context, we can @Autowire any bean.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // Test with a real http server
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String TEST_EMAIL_1 = "email.1@testmail.com";
    private static final String TEST_EMAIL_2 = "email.2@testmail.com";
    private static final String TEST_FIRST_NAME = "TEST_FirstName";
    private static final String TEST_LAST_NAME = "TEST_LastName";
    private static final String TEST_UPDATED_LAST_NAME = "TEST_UpdatedLastName";
    private static final String TEST_PASS = "pass";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private TestUtils utils;

    @AfterEach
    void afterEach() {
        statusRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetAllUsers() throws Exception {
        registerUser(TEST_EMAIL_1)
            .andExpect(status().isOk());

        registerUser(TEST_EMAIL_2)
            .andExpect(status().isOk());

        MockHttpServletResponse response = mvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andReturn().getResponse();

        List<UserResponseDTO> userDTOList = utils.jsonToObject(
            response.getContentAsString(UTF_8),
            new TypeReference<>() { }
        );
        int expectedDtoCount = 2;
        int actual = userDTOList.size();

        assertEquals(expectedDtoCount, actual);
        assertEquals(TEST_EMAIL_1, userDTOList.get(0).getEmail());
        assertEquals(TEST_EMAIL_2, userDTOList.get(1).getEmail());
    }

    @Test
    void testGetUserById() throws Exception {
        registerUser(TEST_EMAIL_1)
            .andExpect(status().isOk());

        MockHttpServletResponse signInResponse = signIn(TEST_EMAIL_1)
            .andExpect(status().isOk())
            .andReturn().getResponse();

        String jwtToken = signInResponse.getContentAsString();

        User existedUser = userRepository.findAll().get(0);
        long userId = existedUser.getId();

        MockHttpServletResponse response = mvc.perform(get("/api/users/%d".formatted(userId))
                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + jwtToken))
            .andExpect(status().isOk())
            .andReturn().getResponse();

        UserResponseDTO userDTO = utils.jsonToObject(response.getContentAsString(UTF_8), new TypeReference<>() { });

        assertEquals(existedUser.getId(), userDTO.getId());
        assertEquals(existedUser.getEmail(), userDTO.getEmail());
        assertEquals(existedUser.getFirstName(), userDTO.getFirstName());
        assertEquals(existedUser.getLastName(), userDTO.getLastName());
    }

    @Test
    void testRegisterUser() throws Exception {
        long expectedCountInDB = 0;
        long actual = userRepository.count();

        assertEquals(expectedCountInDB, actual);

        registerUser(TEST_EMAIL_1)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email", is(TEST_EMAIL_1)));

        expectedCountInDB = 1;
        actual = userRepository.count();

        assertEquals(expectedCountInDB, actual);
    }

    @Test
    void testUpdateUser() throws Exception {
        registerUser(TEST_EMAIL_1)
            .andExpect(status().isOk());

        MockHttpServletResponse signInResponse = signIn(TEST_EMAIL_1)
            .andExpect(status().isOk())
            .andReturn().getResponse();

        String jwtToken = signInResponse.getContentAsString(UTF_8);

        User userToUpdate = userRepository.findAll().get(0);
        String previousLastName = userToUpdate.getLastName();
        UserRequestDTO dto = buildUpdateLastNameDTO(TEST_EMAIL_1, TEST_UPDATED_LAST_NAME);
        long userId = userToUpdate.getId();

        mvc.perform(put("/api/users/%d".formatted(userId))
                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + jwtToken)
                .content(utils.toJson(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(userId), Long.class))
            .andExpect(jsonPath("$.lastName", is(TEST_UPDATED_LAST_NAME)))
            .andReturn().getResponse();

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findUserByLastName(previousLastName).orElse(null));
        assertNotNull(userRepository.findUserByLastName(TEST_UPDATED_LAST_NAME).orElse(null));
    }

    @Test
    void testDeleteUser() throws Exception {
        registerUser(TEST_EMAIL_1)
            .andExpect(status().isOk());

        MockHttpServletResponse signInResponse = signIn(TEST_EMAIL_1)
            .andExpect(status().isOk())
            .andReturn().getResponse();

        String jwtToken = signInResponse.getContentAsString();

        User userToDelete = userRepository.findAll().get(0);
        long userId = userToDelete.getId();

        mvc.perform(delete("/api/users/%d".formatted(userId))
                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + jwtToken))
            .andExpect(status().isOk());

        assertFalse(userRepository.existsById(userId));
    }

    private ResultActions registerUser(String email) throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
            email,
            TEST_FIRST_NAME,
            TEST_LAST_NAME,
            TEST_PASS
        );

        return mvc.perform(post("/api/users")
            .content(utils.toJson(userRequestDTO))
            .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions signIn(String email) throws Exception {
        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(email, TEST_PASS);

        return mvc.perform(post("/api/login")
            .content(utils.toJson(logInRequestDTO))
            .contentType(MediaType.APPLICATION_JSON));
    }

    private UserRequestDTO buildUpdateLastNameDTO(String email, String newLastName) {
        return new UserRequestDTO(
            email,
            TEST_FIRST_NAME,
            newLastName,
            TEST_PASS
        );
    }
}
