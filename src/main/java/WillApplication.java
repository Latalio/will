import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class WillApplication {
    public static void main(String[] args) {
        Map<String, List<String>> headerFileds = new HashMap<String, List<String>>();

        FileWriter log

        // HttpURLConnection的实现和加载原理

        // 0. 初始化（资源准备）
        // assert file does exists.
        try {
            log = new FileWriter("./log/log.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        // 1. 下载资源
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.bilibili.com/x/v2/dm/web/seg.so?type=1&oid=252499515&pid=372697442&segment_index=1");
            connection = (HttpURLConnection) url.openConnection();  // connection并未实际发生，除非调用connection.connect();
            connection.connect();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if (null == connection) {
            try {
                log.write("[ERROR]: connection is null");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return ;
            }
        }


        // 2. 解析二进制文件
        Bilidm.DmSegMobileReply bilidm = null;
        try {
            InputStream in = connection.getInputStream();
            if (in.read() == -1) {
                System.out.println("File tail reached!");
            }
//            FileInputStream fileIn = new FileInputStream("./src/main/resources/seg.so");
            bilidm  = Bilidm.DmSegMobileReply.parseFrom(in);
//            Bilidm.DanmakuElem bilidmElem = bilidm.getElems(0);
//            System.out.println(bilidm.getElemsCount());
//            System.out.println(bilidm.getElems(100).getAction());

        } catch (IOException e) {
            e.printStackTrace();
        }


        // 3. 存储到数据库
        List<Bilidm.DanmakuElem> dmList = bilidm.getElemsList();
        // 3.1 连接数据库
        // 3.2 存储资源
        // 3.3 释放资源

    }
}
