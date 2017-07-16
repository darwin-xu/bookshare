package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import com.bookshare.domain.Session;
import com.bookshare.domain.User;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test(timeOut = timeout_ms)
    public void userWithEmptyName() throws Exception {
        User user = new User();

        // This will generate a random verifycode and send it to user by SMS.
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isBadRequest(), null);
    }

    @Test(timeOut = timeout_ms)
    public void userWithName() throws Exception {
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo1");
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo1");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);

        // Use username and password to login.
        user = new User();
        user.setUsername("TestNo1");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), null);

        // Use oldPassword to change the password.
        user = new User();
        user.setUsername("TestNo1");
        user.setOldPassword("papawfwf");
        user.setPassword("qtqtqt");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);

        // Use username and password to login.
        user = new User();
        user.setUsername("TestNo1");
        user.setPassword("qtqtqt");
        Cookie cookie = perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);

        perform(mockMvc, Method.POST, "/sessions/logout", cookie, null, status().isOk(), null);
    }

    @Test(timeOut = timeout_ms)
    public void userWithWrongVerifyCode() throws Exception {
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo2");
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);

        // Try to use a wrong verifyCode to modify.
        user = new User();
        user.setUsername("TestNo2");
        user.setVerifyCode("11xx33");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isForbidden(), null);
    }

    @Test(timeOut = timeout_ms)
    public void userWithWrongPassword() throws Exception {
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo3");
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo3");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);

        // Use username and a wrong password to login.
        user = new User();
        user.setUsername("TestNo3");
        user.setPassword("hahah");
        perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isForbidden(), null);
    }

    @Test(timeOut = timeout_ms)
    public void userWithWrongOldPassword() throws Exception {
        // Create a user for test.
        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("TestNo4");
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("TestNo4");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);

        // Use username and password to login.
        user = new User();
        user.setUsername("TestNo4");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), null);

        // Use wrong oldPassword to change the password.
        user = new User();
        user.setUsername("TestNo4");
        user.setOldPassword("papawfwft");
        user.setPassword("qtqtqt");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isForbidden(), null);
    }

    @Test(timeOut = timeout_ms)
    public void logoutWithWrongCookie() throws Exception {
        // Create a nonexistent cookie
        Cookie cookie = new Cookie("session", new Session().getSessionID());

        // Logout for nonexistent cookie should be failed.
        perform(mockMvc, Method.POST, "/sessions/logout", cookie, null, status().isUnauthorized(), null);
    }

    @Test(timeOut = timeout_ms)
    public void checkSession() throws Exception {
        // Check session without cookie.
        perform(mockMvc, Method.POST, "/sessions/check", null, null, status().is4xxClientError(), null);
        // mockMvc.perform(MockMvcRequestBuilders.post("/sessions/check")).andExpect(status().is4xxClientError());

        User user;

        // This will generate a random verifycode and send it to user by SMS.
        user = new User();
        user.setUsername("checkSessionUser1");
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("checkSessionUser1");
        user.setVerifyCode("112233");
        user.setPassword("papawfwf");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);

        // Use username and password to login.
        user = new User();
        user.setUsername("checkSessionUser1");
        user.setPassword("papawfwf");
        Cookie cookie1 = perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);

        // Check this cookie, it should valid.
        perform(mockMvc, Method.GET, "/sessions/check", cookie1, null, status().isOk(), null);

        // Use username and password to login again
        user = new User();
        user.setUsername("checkSessionUser1");
        user.setPassword("papawfwf");
        Cookie cookie2 = perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);

        // Check this cookie2, it should be valid.
        perform(mockMvc, Method.GET, "/sessions/check", cookie2, null, status().isOk(), null);

        // Check this cookie1, it should be invalid.
        perform(mockMvc, Method.GET, "/sessions/check", cookie1, null, status().isUnauthorized(), null);
    }

}
