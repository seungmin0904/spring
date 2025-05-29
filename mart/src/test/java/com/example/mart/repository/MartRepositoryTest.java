package com.example.mart.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.example.mart.entity.Category;
import com.example.mart.entity.CategoryItem;
import com.example.mart.entity.Delivery;
import com.example.mart.entity.Item;
import com.example.mart.entity.Member;
import com.example.mart.entity.Order;
import com.example.mart.entity.OrderItem;
import com.example.mart.entity.constant.DeliveryStatus;
import com.example.mart.entity.constant.OrderStatus;

import jakarta.transaction.Transactional;

@SpringBootTest
public class MartRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private CategoryItemRepository categoryItemRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testMemberInsert() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member member = Member.builder()
                    .name("user" + i)
                    .city("서울" + i)
                    .street("724-11" + i)
                    .zipcode("1650" + i)
                    .build();

            memberRepository.save(member);

        });
    }

    @Test
    public void testItemInsert() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Item item = Item.builder()

                    .name("티셔츠" + i)
                    .price(i * 20000)
                    .StockQuantity(i * 5)
                    .build();

            itemRepository.save(item);

        });
    }

    // 주문(Order+OrderItem insert)
    @Test
    public void testOrderInsert() {
        Order order = Order.builder()
                .member(Member.builder().id(1L).build())
                .oderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER)
                .build();

        orderRepository.save(order);

        // 주문과 관련 된 item은 orderitem 테이블에 삽임
        OrderItem orderItem = OrderItem.builder()
                .item(itemRepository.findById(2L).get())
                .order(order)
                .orderPrice(39000)
                .count(1)
                .build();
        orderItemRepository.save(orderItem);

        orderItem = OrderItem.builder()
                .item(itemRepository.findById(3L).get())
                .order(order)
                .orderPrice(69000)
                .count(1)
                .build();
        orderItemRepository.save(orderItem);
    }

    @Transactional
    @Test
    public void readTest1() {
        // 주문 조회(주문번호 이용)
        Order order = orderRepository.findById(1L).get();
        System.out.println(order);
        // 주문자 정보 조회
        System.out.println(order.getMember());
    }

    @Transactional
    @Test
    public void readTest2() {
        // 특정 회원의 주문 전체조회
        Member member = memberRepository.findById(1L).get();
        System.out.println(member.getOrders());
    }

    @Transactional
    @Test
    public void readTest3() {
        // 주문 상품의 정보조회
        OrderItem orderItem = orderItemRepository.findById(1L).get();
        System.out.println(orderItem);
        // 주문 상품의 상품명 조회
        System.out.println(orderItem.getItem().getName());
        // 주문 상품을 주문한 고객 정보 조회
        System.out.println(orderItem.getOrder().getMember());

    }

    @Transactional
    @Test
    public void readTest4() {
        // 주문에 들어있는 주문 아이템 전체조회
        Order order = orderRepository.findById(5L).get();
        System.out.println(order);
        order.getOrderItems().forEach(item -> System.out.println(item));
    }

    @Test
    public void testDelete1() {
        // 멤버 id로 주문 찾아오기
        orderItemRepository.deleteById(5L);
        // 주문 상품
        // 주문
        // 멤버제거
        memberRepository.deleteById(5L);
    }

    @Test
    public void testDelete2() {
        // 부모쪽에 cascade 작성
        // 주문제거 (주문상품 같이 제거)
        orderRepository.deleteById(6L);

    }

    @Commit
    @Transactional
    @Test
    public void testDelete3() {
        Order order = orderRepository.findById(6L).get();
        // order에 연결된 주문 상품을 조회
        System.out.println(order.getOrderItems());

        // 첫번째 자식 제거
        // orphanRemoval = true
        order.getOrderItems().remove(0);
        orderRepository.save(order);

    }

    @Test
    public void testOrderInsert2() {
        // order 저장 시 orderitem도 같이 저장
        // 저장 main(order)에 CascadeType.PERSIST 사용
        Order order = Order.builder()
                .member(Member.builder().id(1L).build())
                .oderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER)
                .build();

        // 주문과 관련 된 item은 orderitem 테이블에 삽임
        OrderItem orderItem = OrderItem.builder()
                .item(itemRepository.findById(2L).get())
                .order(order)
                .orderPrice(39000)
                .count(1)
                .build();

        // orderItemRepository.save(orderItem);
        // CascadeType.PERSIST
        order.getOrderItems().add(orderItem);
        orderRepository.save(order);
    }

    // 배송정보입력
    @Test
    public void testDeliveryInsert() {
        Delivery delivery = Delivery.builder()
                .zipcode("1511")
                .city("서울")
                .street("125-56")
                .deliveryStatus(DeliveryStatus.READY)
                .build();
        deliveryRepository.save(delivery);
        // 주문

        Order order = orderRepository.findById(2L).get();
        order.setDelivery(delivery);
        orderRepository.save(order);
    }

    @Transactional
    @Test
    public void testDeliveryRead() {
        // 배송 조회
        System.out.println(deliveryRepository.findById(1L).get());
        // 주문에 관련있는 배송 조회
        Order order = orderRepository.findById(2L).get();
        System.out.println(order.getDelivery().getDeliveryStatus());

    }

    @Transactional
    @Test
    public void testDeliveryRead2() {
        // 배송 조회
        Delivery delivery = deliveryRepository.findById(1L).get();
        // 배송과 관련있는 order 조회(X) => 양방향 열면 됨

        System.out.println("주문유무" + delivery.getOrder()); // 주문 유무 조회
        System.out.println("주문자" + delivery.getOrder().getMember()); // 주문자 조회
        System.out.println("주문한 물건" + delivery.getOrder().getOrderItems()); // 주문한 물건 조회

    }

    @Test
    public void testDeliveryInsert2() {
        Delivery delivery = Delivery.builder()
                .zipcode("1511")
                .city("서울")
                .street("125-56")
                .deliveryStatus(DeliveryStatus.READY)
                .build();

        // deliveryRepository.save(delivery);

        // 주문
        Order order = orderRepository.findById(3L).get();
        order.setDelivery(delivery);
        orderRepository.save(order);
    }

    @Test
    public void deleteTest() {
        // order 지우면서 배송정보,주문상품 제거
        orderRepository.deleteById(3L);
    }

    @Test
    public void testCategoryItemInsert() {
        Category category1 = Category.builder().name("가전제품").build();
        Category category2 = Category.builder().name("식품").build();
        Category category3 = Category.builder().name("생활용품").build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        Item item = Item.builder().name("TV").price(2500000).StockQuantity(15).build();
        itemRepository.save(item);

        CategoryItem categoryItem = CategoryItem.builder().category(category1).item(item).build();
        categoryItemRepository.save(categoryItem);

        item = Item.builder().name("콩나물").price(1200).StockQuantity(5).build();
        itemRepository.save(item);

        categoryItem = CategoryItem.builder().category(category2).item(item).build();
        categoryItemRepository.save(categoryItem);

        item = Item.builder().name("샴푸").price(12000).StockQuantity(7).build();
        itemRepository.save(item);

        categoryItem = CategoryItem.builder().category(category3).item(item).build();
        categoryItemRepository.save(categoryItem);
    }

    @Test
    public void JoinTest() {
        List<Object[]> list = orderRepository.joinTest();
        for (Object[] objects : list) {
            Order order = (Order) objects[0];
            Member member = (Member) objects[1];
            OrderItem orderItem = (OrderItem) objects[2];
            System.out.println(order);
            System.out.println(member);
            System.out.println(orderItem);
        }
    }

    @Test
    public void subQueryTest() {
        List<Object[]> list = orderRepository.subQueryTest();
        for (Object[] objects : list) {
            Order order = (Order) objects[0];
            Member member = (Member) objects[1];
            OrderItem orderItem = (OrderItem) objects[2];
            Long orderCnt = (Long) objects[3];
            Long orderSum = (Long) objects[4];
            System.out.println(order);
            System.out.println(member);
            System.out.println(orderItem);
            System.out.println(orderCnt);
            System.out.println(orderSum);
        }
    }
}