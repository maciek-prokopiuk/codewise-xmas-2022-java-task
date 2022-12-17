package pl.codewise.xmas.task;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class XMasCookieQueue implements CookieQueue {

    public static final int QUEUE_SIZE = 100;
    private final Cookie[] queue;
    private final AtomicInteger index;

    public XMasCookieQueue() {
        this.queue = new Cookie[QUEUE_SIZE];
        this.index = new AtomicInteger();
    }

    @Override
    public void add(Cookie cookie) {
        queue[index.getAndUpdate(value -> (value + 1) % QUEUE_SIZE)] = cookie;
    }

    @Override
    public synchronized Report getReport() {
        var now = Instant.now();
        var filteredCookies = Arrays.stream(queue).filter(Objects::nonNull).filter(cookie -> ChronoUnit.MINUTES.between(cookie.getCreateTime(), now) < 5).toList();
        return new Report(filteredCookies);
    }
}
