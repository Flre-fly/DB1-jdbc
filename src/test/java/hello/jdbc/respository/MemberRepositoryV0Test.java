package hello.jdbc.respository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV0;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class MemberRepositoryV0Test {

    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("a", 10000);
        repositoryV0.save(member);

        //findById
        Member findMember = repositoryV0.findById(member.getMember_id());
        log.info("fiundMember = {}", findMember);
        log.info(" ==비교 결과: {}", findMember==member);
        log.info("isEqual비교결과:  {}", member.equals(findMember));

        //인스턴스비교(==비교)가 아니라 equalto를 사용한 값 비교를 사용했기때문에 true가 나온다
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}
