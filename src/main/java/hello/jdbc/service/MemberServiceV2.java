package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//트랜잭션 - 파라미터 연동, 풀을 고려한 종료 -> 무슨말이지
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {
    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    //계좌이체 로직
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false);
            //비즈니스로직
            bizLogic(con, fromId, toId, money);
            con.commit();//커밋!
        }catch(Exception e){
            con.rollback();//실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            if(con!=null){
                release(con);
            }
        }
    }
    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con,fromId);
        Member toMember = memberRepository.findById(con,toId);

        memberRepository.update(con,fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, fromMember.getMoney() + money);

    }
    private void release(Connection con){
        try{
            //보통 자동커밋이 default니까 이를 고려하여 해준다
            con.setAutoCommit(true);
            con.close();//돌려준다
        }catch(Exception e){
            log.info("error : {}", e);
        }
    }
    //예외상황 테스트를 위한 method
    private void validation(Member member){
        if(member.getMember_id().equals("error")){
            throw new IllegalStateException("이체중 예외발생");
        }
    }
}
