package sandrakorpi.csnfribeloppapi.Security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    /** Tidsstyrd variant så att testet inte behöver vänta 15 minuter. */
    private static class TestClockService extends LoginAttemptService {
        long time = 1_000_000L;

        @Override
        protected long now() {
            return time;
        }
    }

    @Test
    void blocksAfterTooManyFailures() {
        TestClockService service = new TestClockService();

        for (int i = 0; i < LoginAttemptService.MAX_FAILURES - 1; i++) {
            service.recordFailure("sandra");
        }
        assertFalse(service.isBlocked("sandra"));

        service.recordFailure("sandra");
        assertTrue(service.isBlocked("sandra"));
        assertFalse(service.isBlocked("någon-annan"));
    }

    @Test
    void successfulLoginClearsTheCounter() {
        TestClockService service = new TestClockService();
        for (int i = 0; i < LoginAttemptService.MAX_FAILURES; i++) {
            service.recordFailure("sandra");
        }
        assertTrue(service.isBlocked("sandra"));

        service.recordSuccess("sandra");

        assertFalse(service.isBlocked("sandra"));
    }

    @Test
    void blockExpiresAfterTheWindow() {
        TestClockService service = new TestClockService();
        for (int i = 0; i < LoginAttemptService.MAX_FAILURES; i++) {
            service.recordFailure("sandra");
        }
        assertTrue(service.isBlocked("sandra"));

        service.time += LoginAttemptService.WINDOW_MS + 1;

        assertFalse(service.isBlocked("sandra"));
    }
}
