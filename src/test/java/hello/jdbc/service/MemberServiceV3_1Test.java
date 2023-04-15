package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import hello.jdbc.repository.service.MemberServiceV3_1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

//트랜잭션이 없어서 문제가 발생한다
@Slf4j
public class MemberServiceV3_1Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String ERROR_MEMBER = "error";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void init() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        memberService = new MemberServiceV3_1(new DataSourceTransactionManager(dataSource), memberRepository);
    }

    @AfterEach
    void clear() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(ERROR_MEMBER);
    }
    @Test
    @DisplayName("정상 이체 테스트")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        log.info("start");
        memberService.accountTransfer(memberA.getMember_id(), memberB.getMember_id(), 2000);
        log.info("end");
        //then
        Member findMemberA = memberRepository.findById(memberA.getMember_id());
        Member findMemberB = memberRepository.findById(memberB.getMember_id());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외가 발생한 케이스")
    void accountTransferException() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(ERROR_MEMBER, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);
        //when
        Assertions.assertThatThrownBy(
                ()-> memberService.accountTransfer(memberA.getMember_id(), memberEx.getMember_id(), 2000))
                .isInstanceOf(IllegalStateException.class);
        //then
        Member findMemberA = memberRepository.findById(memberA.getMember_id());
        Member findMemberB = memberRepository.findById(memberEx.getMember_id());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000);// exception 터져서 롤백됨
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
;}
