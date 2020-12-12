import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class WillApplication {

    private static final Logger logger = LogManager.getLogger(WillApplication.class);

    public static void main(String[] args) throws IOException{
        Map<String, List<String>> headerFileds = new HashMap<String, List<String>>();

        // 0. 初始化（资源准备）
        logger.info("START.");

        // 1. 下载弹幕文件（protobuf）
        int oid = 265498641;
        Downloader downloader = new Downloader();
        List<FileInputStream> protoFileInList = downloader.download(oid);
        logger.info("Protobuf file downloading done.");

        // 2. 解析二进制文件
        Parser parser = new Parser();
        List<Bilidm.DanmakuElem> dmElemList = parser.parse(protoFileInList);
        logger.info("Protobuf parsing done.");

        // 3. 存储到数据库
        DatabaseAccesser accesser = new DatabaseAccesser();
        accesser.store(dmElemList);
        logger.info("Danmu file storage done.");

        logger.info("COMPLETED.");

    }


}
