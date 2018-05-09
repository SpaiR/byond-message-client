package io.github.spair.byond.message;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ByondResponseTest {

    @Test
    public void testGetResponse() {
        ByondResponse byondResponse = new ByondResponse();
        byondResponse.setResponse("expected");
        assertNotNull(byondResponse.getResponse(String.class));
    }
}