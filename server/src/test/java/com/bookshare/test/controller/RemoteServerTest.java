package com.bookshare.test.controller;

import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import com.bookshare.domain.User;

@Test
public class RemoteServerTest extends AbstractMockMvcTest {

    @Test(timeOut = int_timeout_ms)
    public void userWithName() {
        User user = new User();
        user.setUsername("TestNo11");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity("http://112.213.117.196:8080/bookshare/users/getVerifyCode", user, User.class);
    }

}
