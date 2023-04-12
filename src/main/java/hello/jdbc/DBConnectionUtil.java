package hello.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            //class를 출력하는 이유는 Connection이 인터페이스이기때문에 사용된 class를 출력하기 위해서임
            log.info("get connection: {}, class: {}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            //runtime exception으로 바꿔서 throw. 나중에 설명
            throw new IllegalStateException();
        }
    }
}
