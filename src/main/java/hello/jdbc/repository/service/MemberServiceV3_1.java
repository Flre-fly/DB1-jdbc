package hello.jdbc.repository.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//트랜잭션 - 트랜잭션 매니저
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {
    //아래코드는 JDBC에 의존하고 있다 service코드는 특정 기술에 의존적이면 안된다
    //private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜잭션 시작
        //트랜잭션 매니저는 datasource를 알고있기에 connection을 획득해서 트랜잭션 동기화 매니저에게 넘겨준다
        //트랜잭션 동기화 매니저는 커넥션을 스레드 로컬에 보관해서 한 스레드에서 커넥션을 요청할때마다 매번 이 커넥션을 반환해준다
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            //throw new IllegalStateException(); 이렇게 던지면 이 예외의 원인을 알 수 없음
            log.error("IllegalStateException: {}", e);
            throw new IllegalStateException(e);
        }
        //release는 tm이 알아서 해줘서 release코드가 필요없다

    }
    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, fromMember.getMoney() + money);

    }
    //예외상황 테스트를 위한 method
    private void validation(Member member){
        if(member.getMember_id().equals("error")){
            throw new IllegalStateException("이체중 예외발생");
        }
    }
}
