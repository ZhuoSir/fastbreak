import com.chen.fastbreak.factory.ProxyCallBack;
import com.chen.fastbreak.factory.ProxyFactory;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCase {

    @Test
    public void test() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        final AtomicInteger times = new AtomicInteger(0);

        ProxyFactory proxyFactory = new ProxyFactory();
        ProxyCallBack proxyCallBack = new ProxyCallBack() {

            public int beforeExecute() {
//                System.out.println("before...");
                int v = times.incrementAndGet();
                if (v > 30) {
                    return 1;
                }

                return 0;
            }

            public int afterExecute() {
//                System.out.println("after...");

                return 0;
            }

            public void exeception(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        Sampler sampler = proxyFactory.createProxyInstance(new Sampler(), proxyCallBack);

        for (int i = 0; i < 50; i++) {
            sampler.hello();
        }

        countDownLatch.await();
    }

}
