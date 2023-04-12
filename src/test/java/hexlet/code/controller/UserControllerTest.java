package hexlet.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.domain.dto.UserRequestDTO;
import hexlet.code.domain.dto.UserResponseDTO;
import hexlet.code.domain.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    private final UserRequestDTO userRequestDTO1 = new UserRequestDTO(
        "email1@gmail.com",
        "TestFirstName1",
        "TestLastName1",
        "pass1"
    );

    private final UserRequestDTO userRequestDTO2 = new UserRequestDTO(
        "email2@gmail.com",
        "TestFirstName2",
        "TestLastName2",
        "pass2"
    );

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void testGetAllUsers() throws Exception {
        mvc.perform(post("/api/users")
            .content(toJson(userRequestDTO1))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mvc.perform(post("/api/users")
            .content(toJson(userRequestDTO2))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        MockHttpServletResponse response = mvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andReturn().getResponse();

        List<UserResponseDTO> userDTOList = toObject(response.getContentAsString(), new TypeReference<>() { });
        int expectedDtoCount = 2;
        int actual = userDTOList.size();

        assertEquals(expectedDtoCount, actual);
    }

    @Test
    void testGetUserById() throws Exception {
        mvc.perform(post("/api/users")
            .content(toJson(userRequestDTO1))
            .contentType(MediaType.APPLICATION_JSON));

        User existedUser = userRepository.findAll().get(0);
        long userId = existedUser.getId();

        MockHttpServletResponse response = mvc.perform(get("/api/users/%d".formatted(userId)))
            .andExpect(status().isOk())
            .andReturn().getResponse();

        UserResponseDTO userDTO = toObject(response.getContentAsString(), new TypeReference<>() { });

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

        mvc.perform(post("/api/users")
            .content(toJson(userRequestDTO1))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email", is("email1@gmail.com")));

        expectedCountInDB = 1;
        actual = userRepository.count();

        assertEquals(expectedCountInDB, actual);
    }

    @Test
    void testUpdateUser() throws Exception {
        mvc.perform(post("/api/users")
            .content(toJson(userRequestDTO1))
            .contentType(MediaType.APPLICATION_JSON));

        User userToUpdate = userRepository.findAll().get(0);
        long userId = userToUpdate.getId();

        UserRequestDTO userRequestDTO = new UserRequestDTO(
            "email1@gmail.com",
            "TestFirstName1",
            "NewLastName",
            "pass1"
        );

        MockHttpServletResponse response = mvc.perform(put("/api/users/%d".formatted(userId))
            .content(toJson(userRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(userToUpdate.getId()), Long.class))
            .andExpect(jsonPath("$.lastName", is("NewLastName")))
            .andReturn().getResponse();

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findUserByLastName("TestLastName1").orElse(null));
        assertNotNull(userRepository.findUserByLastName("NewLastName").orElse(null));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(post("/api/users")
            .content(toJson(userRequestDTO1))
            .contentType(MediaType.APPLICATION_JSON));

        User userToDelete = userRepository.findAll().get(0);
        long userId = userToDelete.getId();

        mvc.perform(delete("/api/users/%d".formatted(userId)))
            .andExpect(status().isOk());

        assertFalse(userRepository.existsById(userId));
    }

    private String toJson(Object object) throws JsonProcessingException {
        return JSON_MAPPER.writeValueAsString(object);
    }

    private <T> T toObject(String json, TypeReference<T> type) throws JsonProcessingException {
        return JSON_MAPPER.readValue(json, type);
    }
}
