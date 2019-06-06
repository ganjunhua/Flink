package com.holiday.flink.train.datastream.course05;


import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SinkToMySQL extends RichSinkFunction<Students> {
    Connection connection;
    PreparedStatement pstmt;

    private Connection getConnection() {

        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/holiday";
            conn = DriverManager.getConnection(url, "root", "admin123");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = getConnection();
        String sql = "insert into flink_student(id,name,age) values (?,?,?)";
        pstmt = connection.prepareStatement(sql);
    }

    //每条记录插入时调用一次,调用的时候为前面的sql赋值并执行，
    public void invoke(Students value, Context context) throws Exception {
        pstmt.setInt(1, value.getId());
        pstmt.setString(2, value.getName());
        pstmt.setInt(3, value.getAge());
        pstmt.executeUpdate();
    }
    // 在close文中释放资源

    @Override
    public void close() throws Exception {
        super.close();
        if (pstmt != null) pstmt.close();
        if (connection != null) connection.close();
    }
}
