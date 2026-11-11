package sandrakorpi.csnfribeloppapi.Security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Enkel begränsning av inloggningsförsök: efter MAX_FAILURES misslyckade försök inom
 * WINDOW_MS spärras användarnamnet tills tidsfönstret har gått ut. Nyckeln är medvetet bara
 * användarnamnet, eftersom en IP-adress från X-Forwarded-For går att förfalska.
 *
 * Datan ligger i minnet, så begränsningen gäller per serverinstans och nollställs vid omstart.
 * Kör man flera instanser bakom en lastbalanserare behövs en gemensam lagring, till exempel Redis.
 */
@Component
public class LoginAttemptService {

    static final int MAX_FAILURES = 10;
    static final long WINDOW_MS = 15 * 60 * 1000L;
    private static final int CLEANUP_THRESHOLD = 10_000;

    private record Entry(int failures, long firstFailureAt) {
    }

    private final ConcurrentHashMap<String, Entry> attempts = new ConcurrentHashMap<>();

    /** Överskuggas i tester som behöver styra tiden. */
    protected long now() {
        return System.currentTimeMillis();
    }

    public boolean isBlocked(String key) {
        Entry entry = attempts.get(key);
        if (entry == null) {
            return false;
        }
        if (now() - entry.firstFailureAt() > WINDOW_MS) {
            attempts.remove(key, entry);
            return false;
        }
        return entry.failures() >= MAX_FAILURES;
    }

    public void recordFailure(String key) {
        long current = now();
        attempts.compute(key, (k, entry) ->
                entry == null || current - entry.firstFailureAt() > WINDOW_MS
                        ? new Entry(1, current)
                        : new Entry(entry.failures() + 1, entry.firstFailureAt()));

        if (attempts.size() > CLEANUP_THRESHOLD) {
            attempts.entrySet().removeIf(e -> current - e.getValue().firstFailureAt() > WINDOW_MS);
        }
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }
}
