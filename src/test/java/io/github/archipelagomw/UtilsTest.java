package io.github.archipelagomw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    public void textIsSafe()
    {
        assertEquals("Slay the Spire", Utils.getFileSafeName("Slay the Spire"));
    }

    @Test
    public void textIsBad()
    {

        assertEquals("Slay the Spire", Utils.getFileSafeName("Slay <>\"/?\\|:*the Spire"));
    }
}
