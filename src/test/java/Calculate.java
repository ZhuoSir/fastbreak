public class Calculate {

    int value = 1;

    public int calculate() throws Exception {
        int ret = value % 3;
        value++;
        if (ret > 0) {
            System.out.println("计算成功" + (value - 1));
        } else {
            throw new Exception("计算错误" + (value - 1));
        }
        return ret;
    }

}
