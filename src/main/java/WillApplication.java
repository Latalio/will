import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class WillApplication {
    public static void main(String[] args) throws IOException{
        Map<String, List<String>> headerFileds = new HashMap<String, List<String>>();

        FileWriter log;

        // HttpURLConnection的实现和加载原理

        // 0. 初始化（资源准备）
        // assert file does exists.
        try {
            log = new FileWriter("./src/main/java/log/log.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        log.write("[INFO]: Test line 0.");
        log.write("[INFO]: Test line 1.");


        // 1. 下载资源
        HttpURLConnection connection = null;
        InputStream protoIn = null;
        FileInputStream fileInputStream = null;
        try {
            // 1.1 配置并启动连接
            URL url = new URL("https://api.bilibili.com/x/v2/dm/web/seg.so?type=1&oid=265498641&pid=713118359&segment_index=1");
            connection = (HttpURLConnection) url.openConnection();  // connection并未实际发生，除非调用connection.connect();
            // 设置文件头
            // 发起
            connection.connect();
            if (connection.getResponseCode() == -1) {
                log.write("[ERROR]: connection is null");
                return ;
            }

            // 1.2 获取protobuf二进制输入流
            protoIn = connection.getInputStream();
            if (protoIn == null) {
                log.write("[ERROR]: Input stream obtaining failed!");
                return ;
            }

            File seg = new File("./src/main/resources/seg_online.so");
            FileOutputStream segOut = new FileOutputStream(seg);
            byte[] bytesBuffer = new byte[2048];
            int readLen;
            while ((readLen=protoIn.read(bytesBuffer)) != -1) {
                segOut.write(bytesBuffer,0,readLen);
            }
            segOut.close();

            fileInputStream = new FileInputStream(seg);

            if (protoIn.read() == -1) {
                System.out.println("File tail reached!");
                // TODO: 停止下载循环 to stop the download cycle
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }


        // 2. 解析二进制文件
        Bilidm.DmSegMobileReply bilidm = null;
        try {
//            protoIn = new FileInputStream("./src/main/resources/seg.so");

//            bilidm  = Bilidm.DmSegMobileReply.parseFrom(protoIn);
            bilidm  = Bilidm.DmSegMobileReply.parseFrom(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Bilidm.DanmakuElem> dmList = bilidm.getElemsList();
        if (dmList == null) {
            log.write("[ERROR]: Danmu list obtaining failed!");
        }


        // 3. 存储到数据库
        Connection dbConnection = null;
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            // 3.1 连接数据库
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://192.168.77.10:3306/bilidm";
            String user = "root";
            String password = "root";
            dbConnection = DriverManager.getConnection(url, user, password);

            // 3.2 存储数据
//            String sql = "select * from videodm_test";
            String sql =
                    "INSERT IGNORE INTO " +
                    "videodm_test (id,progress,mode,fontsize,color,midHash,content,ctime,weight,pool,idStr) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            prepareStatement = dbConnection.prepareStatement(sql);
            for (Bilidm.DanmakuElem dmElem:dmList) {
                // 设置参数
                prepareStatement.setLong(1, dmElem.getId());
                prepareStatement.setInt(2, dmElem.getProgress());
                prepareStatement.setInt(3, dmElem.getMode());
                prepareStatement.setInt(4, dmElem.getFontsize());
                prepareStatement.setInt(5, dmElem.getColor());
                prepareStatement.setString(6, dmElem.getMidHash());
                prepareStatement.setString(7, dmElem.getContent());
                prepareStatement.setLong(8, dmElem.getCtime());
                prepareStatement.setInt(9, dmElem.getWeight());
                prepareStatement.setInt(10, dmElem.getPool());
                prepareStatement.setString(11, dmElem.getIdStr());
                // 执行更新
                prepareStatement.executeUpdate();
                // 处理结果？？
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3.3 释放资源
            // 关闭连接，释放资源
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
        }
    }
    }
}
