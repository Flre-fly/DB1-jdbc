package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
// JDBC - DriverManager 사용해보기
public class MemberRepositoryV1 {

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        log.info("sql: {}", sql);

        try{
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
            //반환값 = 영향받은 row수
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(connection, pstmt, null);
        }
    }
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        log.info("sql: {}", sql);
        try{
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();//sql을 커넥션을 통해 db에 전달
            log.info("resultSize = {}", resultSize);
            //반환값 = 영향받은 row수
        }catch (SQLException e){
            log.error("db error", e);
            throw e;//예외 다시 던지기
        }finally {
            //쿼리실행 후 자원정리 필요, finally에 작성해야함
            close(connection, pstmt, null);
        }
    }
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            //단건조회이기 때문에 0건아니면 1건 조회되니까 rs.next를 1회호출하였다
            if (rs.next()) {
                Member member = new Member();
                member.setMember_id(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found! memberid: {}"+ memberId);
            }
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?,?)";
        Connection connection = null;
        PreparedStatement pstmt = null;
        log.info("sql: {}", sql);
        try{
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, member.getMember_id());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();//sql을 커넥션을 통해 db에 전달
            //반환값 = 영향받은 row수
            return member;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;//예외 다시 던지기
        }finally {
            //쿼리실행 후 자원정리 필요, finally에 작성해야함
            close(connection, pstmt, null);
        }



    }
    private void close(Connection connection, Statement stmt, ResultSet rs){
        if(rs != null){
            try{
                rs.close();
            }catch (SQLException e){
                log.info("error: ", e);
            }
        }

        //stmt exception이 터져도 try-catch로 잡기 때문에 connetion 을 닫을 수 있다
        if(stmt != null){
            try{
                stmt.close();
            }catch (SQLException e){
                log.info("error: ", e);
            }
        }
        if(connection != null){
            try{
                connection.close();
            }catch (SQLException e){
                log.info("error: ", e);
            }
        }
    }
    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }
}
