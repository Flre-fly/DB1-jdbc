package hello.jdbc.respository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV0;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV1Test {

    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("11121asd", 10000);
        repositoryV0.save(member);

        //findById
        Member findMember = repositoryV0.findById(member.getMember_id());
        log.info("fiundMember = {}", findMember);
        log.info(" ==비교 결과: {}", findMember==member);
        log.info("isEqual비교결과:  {}", member.equals(findMember));

        //update: money: 10000 -> 20000
        repositoryV0.update(member.getMember_id(), 20000);
        Member updatedMember = repositoryV0.findById(member.getMember_id());

        repositoryV0.delete(member.getMember_id());

        Assertions.assertThatThrownBy(()-> repositoryV0.findById(member.getMember_id()))
                        .isInstanceOf(NoSuchElementException.class);
   }
}
