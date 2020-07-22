package test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMsgSender {

    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    private OutputStream out;

    public ClientMsgSender(OutputStream out) {
        this.out = out;
    }

    public void stop() {
        scheduler.shutdown();
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                String msg = new String("Test Msg");
                byte[] msgBytes = TcpClient.getMessageBytes(msg);
                if (msgBytes != null) {
                    try {
                        out.write(msgBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 3, 1, TimeUnit.SECONDS);
    }
}
