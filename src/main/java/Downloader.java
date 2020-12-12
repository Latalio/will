import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Downloader {

    private static final Logger logger = LogManager.getLogger(Downloader.class);

    protected List<FileInputStream> download(int oid) {

        List<FileInputStream> protoFileInList = new LinkedList<>();

        short segmentIndex = 1;
        while (true) {

            try {
                // 1.1 配置并启动连接
                URL url = new URL(String.format("https://api.bilibili.com/x/v2/dm/web/seg.so?type=1&oid=%d&segment_index=%d", oid, segmentIndex));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();  // connection并未实际发生，除非调用connection.connect();
                // 设置文件头
                // 发起
                connection.connect();
                if (connection.getResponseCode() == -1) {
                    logger.error("Connection failed!");
                    return null;
                }

                // 1.2 获取protobuf二进制输入流: conn--conn#InStream-->bytesBuf-->File#OutStream--File--File#InStream-->
                InputStream protoIn = connection.getInputStream();
                if (protoIn == null) {
                    logger.error("Input stream obtaining failed!");
                    return null;
                }

                File seg = new File(String.format("./src/main/resources/seg_dump_%d.so", segmentIndex)); // 存储到本地文件中
                FileOutputStream segOut = new FileOutputStream(seg);

                byte[] bytesBuffer = new byte[2048];
                int readLen;
                if ((readLen = protoIn.read(bytesBuffer)) != -1) { //最后一段时，连接会成功，但不会返回任何消息，因此此处返回值是-1
                    segOut.write(bytesBuffer,0,readLen);
                } else {
                    break;
                }

                while ((readLen = protoIn.read(bytesBuffer)) != -1) {
                    segOut.write(bytesBuffer,0,readLen);
                }
                segOut.close();

                protoFileInList.add(new FileInputStream(seg));

            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                // TODO: how to handle nesting Exception?
            }

            segmentIndex++;
        }

        return protoFileInList;
    }
}
