import com.chen.fastbreak.breaker.BaseCircuitBreaker;
import com.chen.fastbreak.breaker.CircuitBreakerRunner;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class CircuitBreakerTest {

    @Test
    public void test() throws Throwable {

        CountDownLatch latch = new CountDownLatch(1);

        final Calculate calculate = new Calculate();

        final BaseCircuitBreaker<Integer> baseCircuitBreaker = new BaseCircuitBreaker<>(10, 10, 3, new CircuitBreakerRunner<Integer>() {
            @Override
            public Integer run() throws Exception {
                return calculate.calculate();
            }

            @Override
            public void fallBack() {
                System.out.println("执行失败....");
            }

            @Override
            public void fallBack(Exception e) {
                System.out.println("执行失败....");
                e.printStackTrace();
            }
        });

        for (int i = 1; i <= 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int ret = baseCircuitBreaker.execute();
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

    class Calculate {

        int value = 1;

        public int calculate() throws Exception {
            int ret = value % 10;
            value++;
            if (ret > 0) {
                System.out.println("计算成功" + (value - 1));
            } else {
                throw new Exception("计算错误" + (value - 1));
            }
            return ret;
        }
    }
}
