package com.bookshare;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bookshare.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void userWithEmptyName() throws Exception {
        User user = new User();

        // This will generate a random verifycode and send it to user by SMS.
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void userWithName() throws Exception {
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo1");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo1");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use username and password to login.
        user = new User();
        user.setUsername("TestNo1");
        user.setPassword("papawfwf");
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
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        // Get Cookie from response.
        Cookie cookie = result.getResponse().getCookies()[0];
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sessions/logout").cookie(cookie).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void userWithWrongVerifyCode() throws Exception {
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo2");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

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
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo3");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo3");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use username and a wrong password to login.
        user = new User();
        user.setUsername("TestNo3");
        user.setPassword("hahah");
        mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void userWithWrongOldPassword() throws Exception {
        // Create a user for test.
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo4");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo4");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use username and password to login.
        user = new User();
        user.setUsername("TestNo4");
        user.setPassword("papawfwf");
        mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Use wrong oldPassword to change the password.
        user = new User();
        user.setUsername("TestNo1");
        user.setOldPassword("papawfwft");
        user.setPassword("qtqtqt");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
