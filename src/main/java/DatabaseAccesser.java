import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.List;


public class DatabaseAccesser {

    private static final Logger logger = LogManager.getLogger(DatabaseAccesser.class);

    protected void store(List<Bilidm.DanmakuElem> dmElemList) {
        Connection dbConnection = null;
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;

        int totalNum = dmElemList.size();
        int updatedNum = 0;

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
            for (Bilidm.DanmakuElem dmElem:dmElemList) {
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
                updatedNum += prepareStatement.executeUpdate();
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
        logger.info(String.format("Total num: %d Updated Num: %d", totalNum, updatedNum));
    }
}
