package cart.member.service;

import cart.config.TestConfig;
import cart.member.dao.MemberDao;
import cart.member.domain.Member;
import cart.member.dto.MemberResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SuppressWarnings("NonAsciiCharacters")
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(MemberMemoryService.class)
class MemberMemoryServiceTest {
    @Autowired
    private MemberService memberService;
    
    @MockBean
    private MemberDao memberDao;
    
    @Test
    void 모든_회원_정보를_가져온다() {
        // given
        final Member firstMember = new Member("a@a.com", "password1");
        final Member secondMember = new Member("b@b.com", "password2");
        given(memberDao.findAll()).willReturn(List.of(firstMember, secondMember));
        
        // when
        final List<MemberResponse> members = memberService.findAll();
        
        // then
        assertThat(members).containsExactly(MemberResponse.from(firstMember), MemberResponse.from(secondMember));
    }
    
    @Test
    void email과_password를_전달하면_해당_회원정보를_가져온다() {
        // given
        final String email = "b@b.com";
        final String password = "password2";
        final Member findMemberByEmailAndPassword = new Member(2L, email, password);
        given(memberDao.findByEmailAndPassword(email, password)).willReturn(findMemberByEmailAndPassword);
        
        // when
        final MemberResponse member = memberService.findByEmailAndPassword(email, password);
        
        // then
        assertThat(member).isEqualTo(new MemberResponse(2L, email, password));
    }
}