package cart.cart.service;

import cart.cart.dao.CartDao;
import cart.cart.domain.Cart;
import cart.cart.dto.CartResponse;
import cart.config.TestConfig;
import cart.member.dto.MemberRequest;
import cart.member.dto.MemberResponse;
import cart.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.only;

@SuppressWarnings("NonAsciiCharacters")
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(CartMemoryService.class)
class CartMemoryServiceTest {
    @Autowired
    private CartService cartService;
    
    @MockBean
    private CartDao cartDao;
    @MockBean
    private MemberService memberService;
    
    private InOrder inOrder;
    
    @BeforeEach
    void setUp() {
        inOrder = inOrder(cartDao, memberService);
    }
    
    @Test
    void 장바구니를_추가한다() {
        // given
        final MemberRequest memberRequest = new MemberRequest(1L, "a@a.com", "password1");
        given(cartDao.save(anyLong(), anyLong())).willReturn(1L);
        
        // when
        final Long cartId = cartService.addCart(1L, memberRequest);
        
        assertAll(
                () -> assertThat(cartId).isOne(),
                () -> then(cartDao).should(inOrder).findByMemberId(anyLong()),
                () -> then(cartDao).should(inOrder).save(anyLong(), anyLong())
        );
    }
    
    @Test
    void MemberRequest를_전달하면_장바구니에_담긴_상품들을_가져온다() {
        // given
        final String email = "b@b.com";
        final String password = "password";
        final Long memberId = 2L;
        final Cart firstCart = new Cart(1L, memberId, 3L);
        final Cart secondCart = new Cart(2L, memberId, 3L);
        final Cart thirdCart = new Cart(4L, memberId, 3L);
        
        given(memberService.findByEmailAndPassword(email, password)).willReturn(new MemberResponse(memberId, email, password));
        given(cartDao.findByMemberId(memberId)).willReturn(List.of(firstCart, secondCart, thirdCart));
        
        // when
        final MemberRequest memberRequest = new MemberRequest(memberId, email, password);
        final List<CartResponse> carts = cartService.findByMemberRequest(memberRequest);
        
        // then
        final CartResponse firstCartResponse = new CartResponse(firstCart);
        final CartResponse secondCartResponse = new CartResponse(secondCart);
        final CartResponse thirdCartResponse = new CartResponse(thirdCart);
        
        assertAll(
                () -> assertThat(carts).containsExactly(firstCartResponse, secondCartResponse, thirdCartResponse),
                () -> then(memberService).should(inOrder).findByEmailAndPassword(anyString(), anyString()),
                () -> then(cartDao).should(inOrder).findByMemberId(anyLong())
        );
    }
    
    @Test
    void carId와_memberId를_전달하면_해당_카트_품목을_삭제한다() {
        // when
        cartService.deleteByCartIdAndMemberId(1L, 1L);
        
        // then
        then(cartDao).should(only()).deleteByCartIdAndMemberId(1L, 1L);
    }
    
    @Test
    void productId를_전달하면_해당_카트_품목을_삭제한다() {
        // when
        cartService.deleteByProductId(1L);
        
        // then
        then(cartDao).should(only()).deleteByProductId(1L);
    }
}