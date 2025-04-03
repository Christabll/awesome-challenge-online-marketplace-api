package com.awesomity.marketplace.marketplace_api.serviceImpl;

import com.awesomity.marketplace.marketplace_api.dto.OrderItemDto;
import com.awesomity.marketplace.marketplace_api.entity.*;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.OrderItemRepository;
import com.awesomity.marketplace.marketplace_api.repository.OrderRepository;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import com.awesomity.marketplace.marketplace_api.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductService productService;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderWithItems() {
        User user = new User(); user.setId(1L);
        Product product = new Product(); product.setId(2L);
        Order order = new Order();
        OrderItemDto itemDto = new OrderItemDto(2L, 2);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(productService.findById(2L)).thenReturn(product);

        Order result = orderService.createOrder(order, List.of(itemDto), user);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PLACED);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void shouldUpdateOrderStatusAndSendEmail() {
        User user = new User(); user.setEmail("test@mail.com"); user.setFirstName("Christa");
        Order order = new Order(); order.setId(1L); order.setUser(user);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L, "processing");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldFindOrderById() {
        Order order = new Order(); order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThat(orderService.findById(1L)).isEqualTo(order);
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order(), new Order()));
        assertThat(orderService.findAll()).hasSize(2);
    }

    @Test
    void shouldDeleteOrderAndItems() {
        Order order = new Order();
        order.setId(1L);
        OrderItem item = new OrderItem();
        order.setOrderItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderItemRepository).deleteAll(anyList());
        verify(orderRepository).delete(order);
    }

    @Test
    void shouldReturnOrdersForUser() {
        User user = new User(); user.setId(1L);
        when(orderRepository.findByUser(user)).thenReturn(List.of(new Order()));
        assertThat(orderService.findOrdersByUser(user)).hasSize(1);
    }

    @Test
    void shouldCancelOrderIfUserOwnsAndStatusIsValid() {
        User user = new User(); user.setId(1L);
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.cancelOrderForShopper(1L, user);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void shouldThrowIfUserDoesNotOwnOrder() {
        User user = new User(); user.setId(1L);
        User other = new User(); other.setId(2L);
        Order order = new Order(); order.setId(1L); order.setUser(other);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrderForShopper(1L, user))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldThrowIfOrderStatusIsInvalidForCancel() {
        User user = new User(); user.setId(1L);
        Order order = new Order(); order.setId(1L); order.setUser(user);
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrderForShopper(1L, user))
                .isInstanceOf(BadRequestException.class);
    }
}