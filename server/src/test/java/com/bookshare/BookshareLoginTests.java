package com.bookshare;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bookshare.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookshareLoginTests {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void userWithEmptyName() throws Exception {
        // Create a user for test.
        User user = new User();

        // This will generate a random verifycode and send it to user by SMS.
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void userWithName() throws Exception {
        // Create a user for test.
        User user;
        user = new User();
        user.setUsername("TestNo1");

        // This will generate a random verifycode and send it to user by SMS.
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use modify to set a password for user.
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use username and password to login.
        mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use oldPassword to change the password.
        user = new User();
        user.setUsername("TestNo1");
        user.setOldPassword("papawfwf");
        user.setPassword("qtqtqt");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use username and password to login.
        user = new User();
        user.setUsername("TestNo1");
        user.setPassword("qtqtqt");
        mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void userWithWrongVerifyCode() throws Exception {
        // Create a user for test.
        User user;
        user = new User();
        user.setUsername("TestNo2");

        // This will generate a random verifycode and send it to user by SMS.
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Try to use a wrong verifyCode to modify.
        user = new User();
        user.setUsername("TestNo2");
        user.setVerifyCode("11xx33");
        user.setPassword("papawfwf");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void userWithWrongPassword() throws Exception {
        // Create a user for test.
        User user;
        user = new User();
        user.setUsername("TestNo1");

        // This will generate a random verifycode and send it to user by SMS.
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo1");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use username and a wrong password to login.
        user = new User();
        user.setUsername("TestNo1");
        user.setPassword("hahah");
        mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void userWithSession() {

    }

}
