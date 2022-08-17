package org.bankmanagement.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Custom result matcher. Call static method responseBody()
 */
public class ResponseResultMatcher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This method verifies that response contains certain object of certain type
     *
     * @param expectedObject object of expected type to validate
     * @param tClass         class of expection class
     * @param <T>            expecting type
     * @return new ResultMatcher that will be used in response validation
     */
    public <T> ResultMatcher containsObject(T expectedObject, Class<T> tClass) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, tClass);
            assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        };
    }

    /**
     * This method verifies that response contains list of certain type with certain values
     *
     * @param expectedList list of expecting type with verified values
     * @param tClass       class of expecting type
     * @param <T>          expecting type
     * @return new ResultMatcher that will be used in response validation
     */
    public <T> ResultMatcher containsList(List<T> expectedList, Class<T> tClass) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, tClass);
            List<T> actualValue = objectMapper.readValue(json, collectionType);
            assertThat(actualValue).usingRecursiveComparison().isEqualTo(expectedList);
        };
    }

    /**
     * This method verifies that response contains message that starts with expected prefix
     *
     * @param expectedMessagePrefix String prefix that you expect from response message
     * @return new ResultMatcher that will be used in response validation
     */
    public ResultMatcher messageStartsWith(String expectedMessagePrefix) {
        return result -> {
            String response = result.getResponse().getContentAsString();
            assertThat(response).startsWith(expectedMessagePrefix);
        };
    }

    /**
     * Call this static method to get access to validation methods for mvc results
     *
     * @return new instance of this class
     */
    public static ResponseResultMatcher responseBody() {
        return new ResponseResultMatcher();
    }
}
