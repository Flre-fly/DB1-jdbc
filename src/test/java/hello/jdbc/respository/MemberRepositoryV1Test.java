package hello.jdbc.respository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV0;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class MemberRepositoryV1Test {

    MemberRepositoryV1 repositoryV1;

    @BeforeEach
    void init(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repositoryV1 = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("11121asd", 10000);
        repositoryV1.save(member);

        //findById
        Member findMember = repositoryV1.findById(member.getMember_id());
        log.info("fiundMember = {}", findMember);
        log.info(" ==비교 결과: {}", findMember==member);
        log.info("isEqual비교결과:  {}", member.equals(findMember));

        //update: money: 10000 -> 20000
        repositoryV1.update(member.getMember_id(), 20000);
        Member updatedMember = repositoryV1.findById(member.getMember_id());

        repositoryV1.delete(member.getMember_id());

        Assertions.assertThatThrownBy(()-> repositoryV1.findById(member.getMember_id()))
                        .isInstanceOf(NoSuchElementException.class);
   }
}
