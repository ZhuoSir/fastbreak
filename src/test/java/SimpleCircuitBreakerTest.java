import com.chen.fastbreak.BaseCircuitBreaker;
import com.chen.fastbreak.CircuitBreakerFallBack;
import com.chen.fastbreak.SimpleCircuitBreaker;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class SimpleCircuitBreakerTest {

    @Test
    public void test() throws Throwable {

        CountDownLatch latch = new CountDownLatch(1);

        final Calculate calculate = new Calculate();

        final BaseCircuitBreaker<Integer> baseCircuitBreaker = new BaseCircuitBreaker<>(10, 50, 5, 5,new CircuitBreakerFallBack<Integer>() {
            @Override
            public Integer call() throws Exception {
                return calculate.calculate();
            }

            @Override
            public void fallBack() {
                System.out.println("执行失败....");
            }
        });

        for (int i = 1; i <= 300; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer ret = baseCircuitBreaker.execute();
                        System.out.println("计算结果：" + ret);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(100);
        }

        latch.await();
    }


    @Test
    public void test1() throws Throwable {
        final Calculate calculate = new Calculate();

        final SimpleCircuitBreaker<Integer> baseCircuitBreaker = new SimpleCircuitBreaker<>(10, 50, 5, new CircuitBreakerFallBack<Integer>() {
            @Override
            public Integer call() throws Exception {
                return calculate.calculate();
            }

            @Override
            public void fallBack() {
                System.out.println("熔断中....");
            }
        });

        for (int i = 1; i <= 300; i++) {
            try {
                Integer ret = baseCircuitBreaker.execute();
                System.out.println("计算结果：" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
