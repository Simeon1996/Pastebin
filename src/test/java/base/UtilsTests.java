package base;

import base.Helpers.PasteRequestBody;
import org.junit.jupiter.api.Test;
import base.Helpers.Utils;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTests {

    @Test
    public void testContentValidation() {
        // @TODO
    }

    @Test
    public void testPasteIdGeneration() {
        for (int i = 0; i < 100; i++) {
            String randomString = Utils.generateRandomString(5);

            assertEquals(5, randomString.length());
            assertTrue(randomString.matches("[a-zA-Z]+"));
        }
    }

    @Test
    public void testCreateValidationWithEmptyContent() {
        RootService service = new RootService();

        PasteRequestBody requestBody = new PasteRequestBody();

        requestBody.setContent("");

        Exception exception = assertThrows(InvalidParameterException.class, () -> {
            service.create(requestBody, "127.0.0.1");
        });

        assertEquals("Invalid content.", exception.getMessage());
    }

    @Test
    public void testCreateValidationWithNullContent() {
        RootService service = new RootService();

        PasteRequestBody requestBody = new PasteRequestBody();

        Exception exception = assertThrows(InvalidParameterException.class, () -> {
            service.create(requestBody, "127.0.0.1");
        });

        assertEquals("Invalid content.", exception.getMessage());
    }

    @Test
    public void testCreateValidationWithUndefinedExpirirationTime() {
        PasteRequestBody requestBody = new PasteRequestBody();

        requestBody.setContent("test");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            requestBody.setExpireAfter(ExpirationTime.valueOf("M_12"));
        });

        assertEquals("No enum constant base.ExpirationTime.M_12", exception.getMessage());
    }

    @Test
    public void testCreateValidationWithInvalidExpirirationTime() {
        PasteRequestBody requestBody = new PasteRequestBody();

        requestBody.setContent("test");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            requestBody.setExpireAfter(ExpirationTime.valueOf("12 minutes"));
        });

        assertEquals("No enum constant base.ExpirationTime.12 minutes", exception.getMessage());
    }

    @Test
    public void testCreateValidationWithValidExpirationTimes() {
        ExpirationTime expireAfter10Minutes = ExpirationTime.MI_10;
        ExpirationTime expireAfter30Minutes = ExpirationTime.MI_30;
        ExpirationTime expireAfter1Hour = ExpirationTime.H_1;
        ExpirationTime expireAfter10Hours = ExpirationTime.H_10;
        ExpirationTime expireAfter1Day = ExpirationTime.D_1;
        ExpirationTime expireAfter5Days = ExpirationTime.D_5;
        ExpirationTime expireAfter10Days = ExpirationTime.D_10;
        ExpirationTime expireAfter1Month = ExpirationTime.MO_1;
        ExpirationTime expireAfter6Months = ExpirationTime.MO_6;
        ExpirationTime expireAfter1Year = ExpirationTime.Y_1;

        assertEquals("minutes", expireAfter10Minutes.getTimeUnit());
        assertEquals(10, expireAfter10Minutes.getTimeValue());

        assertEquals("minutes", expireAfter30Minutes.getTimeUnit());
        assertEquals(30, expireAfter30Minutes.getTimeValue());

        assertEquals("hours", expireAfter1Hour.getTimeUnit());
        assertEquals(1, expireAfter1Hour.getTimeValue());

        assertEquals("hours", expireAfter10Hours.getTimeUnit());
        assertEquals(10, expireAfter10Hours.getTimeValue());

        assertEquals("days", expireAfter10Days.getTimeUnit());
        assertEquals(10, expireAfter10Days.getTimeValue());

        assertEquals("days", expireAfter1Day.getTimeUnit());
        assertEquals(1, expireAfter1Day.getTimeValue());

        assertEquals("days", expireAfter5Days.getTimeUnit());
        assertEquals(5, expireAfter5Days.getTimeValue());

        assertEquals("months", expireAfter1Month.getTimeUnit());
        assertEquals(1, expireAfter1Month.getTimeValue());

        assertEquals("months", expireAfter6Months.getTimeUnit());
        assertEquals(6, expireAfter6Months.getTimeValue());

        assertEquals("years", expireAfter1Year.getTimeUnit());
        assertEquals(1, expireAfter1Year.getTimeValue());
    }

    @Test
    public void testCreateValidationWithInvalidContent() {
        // @TODO
    }
}
