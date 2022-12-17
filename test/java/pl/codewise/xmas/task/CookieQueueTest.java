package pl.codewise.xmas.task;

import org.junit.jupiter.api.Test;
import pl.codewise.xmas.task.Cookie.CookieType;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CookieQueueTest {

    @Test
    public void testEmptyQueue() {
        XMasCookieQueue xMasCookieQueue = new XMasCookieQueue();
        Report report = xMasCookieQueue.getReport();

        assertEquals(report.cookies().size(), 0);
    }

    @Test
    public void testSingleProducer() {
        XMasCookieQueue xMasCookieQueue = new XMasCookieQueue();
        for (int i = 0; i < 150; i++) {
            xMasCookieQueue.add(new Cookie(Integer.toString(i), CookieType.CHRISTMAS_TREE));
        }

        var cookies = xMasCookieQueue.getReport().cookies();
        assertEquals(100, cookies.size());

        assertTrue(cookies.stream().mapToInt(cookie -> Integer.parseInt(cookie.getLabel())).allMatch(x -> x >= 50 && x < 150));
    }

    @Test
    public void testMultipleProducers() throws InterruptedException {
        XMasCookieQueue xMasCookieQueue = new XMasCookieQueue();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            int index = i;
            executor.execute(() -> {
                for (int j = 0; j < 20; j++) {
                    xMasCookieQueue.add(new Cookie("Thread " + index, CookieType.CHRISTMAS_TREE, Instant.now().minus(Duration.ofMinutes(index * 2))));

                }
                latch.countDown();
            });
        }

        latch.await();

        var cookies = xMasCookieQueue.getReport().cookies();
        assertEquals(60, cookies.size());

        for (int i = 0; i < 3; i++) {
            int index = i;
            long threadCookies = cookies.stream().filter(cookie -> cookie.getLabel().equals("Thread " + index)).count();
            assertEquals(20, threadCookies);
        }
    }

    @Test
    public void testTimeCondition() {
        XMasCookieQueue xMasCookieQueue = new XMasCookieQueue();
        xMasCookieQueue.add(new Cookie("old", CookieType.CHRISTMAS_TREE, Instant.now().minus(Duration.ofMinutes(10))));
        xMasCookieQueue.add(new Cookie("new", CookieType.CHRISTMAS_TREE, Instant.now()));

        Report report = xMasCookieQueue.getReport();

        assertEquals(1, report.cookies().size());
        assertEquals("new", report.cookies().stream().findFirst().get().getLabel());
    }

    @Test
    public void testTimeCondition2() {
        XMasCookieQueue xMasCookieQueue = new XMasCookieQueue();
        xMasCookieQueue.add(new Cookie("old", CookieType.CHRISTMAS_TREE, Instant.now().minus(Duration.ofMinutes(4)).minus(Duration.ofSeconds(59))));
        xMasCookieQueue.add(new Cookie("new", CookieType.CHRISTMAS_TREE, Instant.now()));

        Report report = xMasCookieQueue.getReport();

        assertEquals(2, report.cookies().size());
    }
}
