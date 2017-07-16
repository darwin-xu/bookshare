package com.bookshare.test.controller;

import javax.servlet.http.Cookie;

import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractMockMvcTest extends AbstractTestNGSpringContextTests {

    protected final int timeout_ms = 10000;
    
    private ObjectMapper mapper = new ObjectMapper();

    public enum Method {
        GET, PUT, POST, PATCH, DELETE
    };

    public <T> T perform(MockMvc mvc, Method method, String url, Cookie cookie, Object obj, ResultMatcher result,
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
        if (obj != null) {
            builder = builder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(obj));
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

}
