package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;



class ShopperOrderControllerTest {

    @InjectMocks
    private ShopperOrderController controller;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private com.awesomity.marketplace.marketplace_api.repository.OrderItemRepository orderItemRepository;

    @Mock
    private Authentication authentication;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);

        testOrder = new Order();
        testOrder.setUser(testUser);

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldPlaceOrder() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setTotalAmount(2000);

        when(orderService.createOrder(any(), any(), eq(testUser))).thenReturn(testOrder);

        ResponseEntity<ApiResponse<Order>> response = controller.placeOrder(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getData()).isEqualTo(testOrder);
    }

    @Test
    void shouldReturnOrderHistory() {
        when(orderService.findOrdersByUser(testUser)).thenReturn(Collections.singletonList(testOrder));

        ResponseEntity<ApiResponse<List<Order>>> response = controller.getOrderHistory();

        assertThat(response.getBody().getData()).contains(testOrder);
    }

    @Test
    void shouldTrackOrderSuccessfully() {
        when(orderService.findById(1L)).thenReturn(testOrder);

        ResponseEntity<ApiResponse<Order>> response = controller.trackOrder(1L);

        assertThat(response.getBody().getData()).isEqualTo(testOrder);
    }

    @Test
    void shouldThrowIfUserTracksOtherOrder() {
        User otherUser = new User();
        otherUser.setId(999L);
        testOrder.setUser(otherUser);

        when(orderService.findById(1L)).thenReturn(testOrder);

        assertThatThrownBy(() -> controller.trackOrder(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        when(orderService.cancelOrderForShopper(1L, testUser)).thenReturn(testOrder);

        ResponseEntity<ApiResponse<Order>> response = controller.cancelOrder(1L);

        assertThat(response.getBody().getData()).isEqualTo(testOrder);
    }
}
