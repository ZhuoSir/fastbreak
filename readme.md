## CircuitBreaker

### AbstractCircuitBreaker

CircuitBreaker的核心思想实现，
属性circuitBreakerPolicy 为CircuitBreaker熔断逻辑代理类，
属性circuitBreakerFallBack 为熔断处理的回调接口；

入口方法为execute方法，也是CircuitBreaker的主要调用方法；


### BaseCircuitBreaker

BaseCircuitBreaker是AbstractCircuitBreaker实体实现；
BaseCircuitBreakerPolicy是BaseCircuitBreaker类的实现代理，
当服务执行失败时，会进入统计策略，统计在thresholdWindow时间内是否达到了失败的阈值tripThreshold。
如果达到则触发熔断逻辑；
并且在halfOpenTimeout时间后，熔断器会进入半开状态，会开放服务，如果在thresholdInHalfOpen
时间内没有触发半开阈值内的失败次数则关闭状态，反之，则重新开启；

测试用例：
```java

        final Calculate calculate = new Calculate();

        final BaseCircuitBreaker<Integer> baseCircuitBreaker = new BaseCircuitBreaker<>(10, 3 * 1000, 5 * 1000, 5,new CircuitBreakerFallBack<Integer>() {
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
```

### SimpleCircuitBreaker

SimpleCircuitBreaker是AbstractCircuitBreaker简单实现；
相比BaseCircuitBreakerPolicy，SimpleCircuitBreakerPolicy只会在CLOSED和OPEN状态中切换；
在一定频次内达到阈值，则进入OPEN状态，并在固定时间内会重新回到CLOSED状态；

比如以下：在近50次内，达到10次失败，打开熔断，熔断时间为5ms
```java

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

```


