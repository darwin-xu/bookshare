package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bookshare.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractMockMvcTest extends AbstractTestNGSpringContextTests {

    protected final int ext_timeout_ms = 60000;

    protected final int int_timeout_ms = 2000;

    private ObjectMapper mapper = new ObjectMapper();

    public enum Method {
        GET, PUT, POST, PATCH, DELETE
    };

    public <T> T perform(MockMvc mvc, Method method, String url, Cookie cookie, Object json, ResultMatcher result,
            Class<T> valueType) throws Exception {
        MockHttpServletRequestBuilder builder;

        // Select the right method.
        switch (method) {
        case GET:
            builder = MockMvcRequestBuilders.get(url);
            break;
        case PUT:
            builder = MockMvcRequestBuilders.put(url);
            break;
        case POST:
            builder = MockMvcRequestBuilders.post(url);
            break;
        case PATCH:
            builder = MockMvcRequestBuilders.patch(url);
            break;
        case DELETE:
            builder = MockMvcRequestBuilders.delete(url);
            break;
        default:
            builder = MockMvcRequestBuilders.get(url);
            break;
        }

        // Attach cookie in the request.
        if (cookie != null) {
            builder = builder.cookie(cookie);
        }

        // Add content in the request.
        if (json != null) {
            builder = builder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(json));
        }

        if (valueType == null) {
            mvc.perform(builder).andExpect(result);
            return null;
        } else if (valueType.equals(Cookie.class)) {
            return valueType.cast(mvc.perform(builder).andExpect(result).andReturn().getResponse().getCookies()[0]);
        } else {
            return mapper.readValue(
                    mvc.perform(builder).andExpect(result).andReturn().getResponse().getContentAsString(), valueType);
        }
    }

    protected Cookie createAndLogin(MockMvc mvc, String username) throws Exception {
        User user = new User();
        user.setUsername(username);
        perform(mvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);
        user.setVerifyCode("112233");
        user.setPassword("123");
        perform(mvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);
        return perform(mvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);
    }

    protected List<String> getBooks(char mask[], String bookArray[]) {
        List<String> isbns = new ArrayList<String>();
        for (int i = 0; i < mask.length; ++i)
            if ('0' < mask[i] && mask[i] < '9')
                for (int c = 0; c < mask[i] - '0'; ++c)
                    isbns.add(bookArray[i]);
            else if (mask[i] != ' ')
                isbns.add(bookArray[i]);
        return isbns;
    }

    protected void preGetBooks(MockMvc mvc) throws Exception {
        String isbns[] = { "9787514610307", "9787550284340", "9787550217454", "9787030324672", "9787569914061",
                "9787505417731", "9787508622545", "9787301150894", "9787516810941", "9787509766989", "9787553805900",
                "9787550278998", "9787508665450", "9787500648192" };
        for (String isbn : isbns) {
            perform(mvc, Method.GET, "/books/" + isbn, null, null, status().isOk(), null);
        }
    }

}
