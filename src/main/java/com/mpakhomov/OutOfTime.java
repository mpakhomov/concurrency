package com.mpakhomov;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author: mpakhomov
 * created: 6/20/15
 */
public class OutOfTime {
    public static void main(String[] args) throws Exception {
        Timer timer = new Timer();
        timer.schedule(new ThrowTask(), 1);
        TimeUnit.SECONDS.sleep(1);
        timer.schedule(new ThrowTask(), 1);
        TimeUnit.SECONDS.sleep(5);
    }
    static class ThrowTask extends TimerTask {
        public void run() { throw new RuntimeException(); }
    }
}
