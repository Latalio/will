package DBPoolTest;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.List;

public class Database extends Thread {
    private final int MAX_CONNECTION_NUM = 10;

    // TODO: 设置ArrayList的容量上限
    // NOTE: List适用于空间优先的结果，不复用connections
    List<PipedReader> inList = new ArrayList<>();

    int connect(PipedWriter out) {
        // 建立连接
        if (out == null) {
            throw new NullPointerException("out is null!");
        }

        int handle = inList.size();
        if (handle >= MAX_CONNECTION_NUM) {
            System.out.println("[INFO]: Maximum number of connections reached\n");
            return -1;
        } else {
            try {
                inList.add(new PipedReader(out));
            } catch (IOException e) {
                inList.remove(handle);
                System.out.println("[ERROR]: Connection failed!\n");
                e.printStackTrace();
            }
            return handle;
        }
    }

    void disconnect(int handle) {
        // 断开连接
        // 1. handle检查
        inList.remove(handle);
    }

    @Override
    public void run() {
        super.run();
        // 对有新消息的管道进行遍历，打印（存储）消息
    }
}
