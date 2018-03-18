package io.github.spair.byond.message;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByondMessageTest {

    @Test
    public void getMessageAsTopic() {
        final String controlString = "?test;data";

        ByondMessage byondMessage1 = new ByondMessage(null, "test;data");
        ByondMessage byondMessage2 = new ByondMessage(null, "?test;data");
        ByondMessage byondMessage3 = new ByondMessage(null, null);

        assertNotEquals(controlString, byondMessage1.getMessage());
        assertEquals(controlString, byondMessage1.getMessageAsTopic());

        assertEquals(controlString, byondMessage2.getMessage());
        assertEquals(controlString, byondMessage2.getMessageAsTopic());

        assertNull(byondMessage3.getMessageAsTopic());
    }
}