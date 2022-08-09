package org.bankmanagement.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseResultMatcher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public<T> ResultMatcher containsObject(Object expectedObject, Class<T> tClass) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, tClass);
            assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        };
    }

    public static ResponseResultMatcher responseBody() {
        return new ResponseResultMatcher();
    }
}
