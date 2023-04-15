package hello.jdbc.repository.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    //계좌이체 로직
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);//검증에 무언가 문제가 생김
        memberRepository.update(toId, fromMember.getMoney() + money);
    }
    //예외상황 테스트를 위한 method
    private void validation(Member member){
        if(member.getMember_id().equals("error")){
            throw new IllegalStateException("이체중 예외발생");
        }
    }
}
