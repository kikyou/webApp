package org.baidu.ecom.conCurrent;

/**
 * Created by baidu on 2017/1/24.
 */
public class Counter {
    private int count;

    synchronized public int getCount() {
        return count;
    }

    synchronized public void setCount(int value) {
        count = value;
    }

    public static void main(String[] args) throws Exception {
        Counter c = new Counter();
        c.setCount(10);

        synchronized (c) {
            int v = c.getCount();
            c.setCount(v + 20);
        }

        System.out.println(c.getCount());
    }
}
