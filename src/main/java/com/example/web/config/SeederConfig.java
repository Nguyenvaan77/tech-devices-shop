package com.example.web.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.web.entity.*;
import com.example.web.repository.*;
import com.example.web.util.PermissionEnum;
import com.example.web.util.RoleEnum;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SeederConfig {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVideoRepository productVideoRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final SaleRepository saleRepository;
    private final ProductSaleRepository productSaleRepository;
    private final EmailLogRepository emailLogRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        

        return args -> {
            if (permissionRepository.count() == 0) {
                List<Permission> permissions = Arrays.stream(PermissionEnum.values())
                    .map(permissionEnum -> Permission.builder().permissionName(permissionEnum).build())
                    .toList();
                permissionRepository.saveAll(permissions);
            }

            if(roleRepository.count() == 0){
                roleRepository.save(new Role().builder().roleName(RoleEnum.ADMIN).build());
                roleRepository.save(new Role().builder().roleName(RoleEnum.BUSINESS).build());
                roleRepository.save(new Role().builder().roleName(RoleEnum.CUSTOMER).build());
            }

            Role admin = roleRepository.findByRoleName(RoleEnum.ADMIN).orElseThrow();
            Role business = roleRepository.findByRoleName(RoleEnum.BUSINESS).orElseThrow();
            Role customer = roleRepository.findByRoleName(RoleEnum.CUSTOMER).orElseThrow();

            // BUSINESS
            business.setPermissions(Set.of(
                    getPermission(PermissionEnum.PRODUCT_READ),
                    getPermission(PermissionEnum.PRODUCT_CREATE),
                    getPermission(PermissionEnum.PRODUCT_UPDATE),
                    getPermission(PermissionEnum.PRODUCT_DELETE),
                    getPermission(PermissionEnum.USER_CREATE),
                    getPermission(PermissionEnum.USER_UPDATE),
                    getPermission(PermissionEnum.ORDER_READ),
                    getPermission(PermissionEnum.ORDER_UPDATE),
                    getPermission(PermissionEnum.ORDER_CREATE),
                    getPermission(PermissionEnum.PAYMENT_CREATE),
                    getPermission(PermissionEnum.PAYMENT_READ)
            ));

            // CUSTOMER
            customer.setPermissions(Set.of(
                    getPermission(PermissionEnum.PRODUCT_READ),
                    getPermission(PermissionEnum.USER_CREATE),
                    getPermission(PermissionEnum.USER_UPDATE),
                    getPermission(PermissionEnum.ORDER_READ),
                    getPermission(PermissionEnum.ORDER_UPDATE),
                    getPermission(PermissionEnum.ORDER_CREATE),
                    getPermission(PermissionEnum.PAYMENT_CREATE),
                    getPermission(PermissionEnum.PAYMENT_READ)
            ));

            // ADMIN
            admin.setPermissions(Set.of(
                    getPermission(PermissionEnum.PRODUCT_READ),
                    getPermission(PermissionEnum.PRODUCT_DELETE),
                    getPermission(PermissionEnum.USER_CREATE),
                    getPermission(PermissionEnum.USER_DELETE),
                    getPermission(PermissionEnum.PAYMENT_DELETE),
                    getPermission(PermissionEnum.PAYMENT_READ),
                    getPermission(PermissionEnum.ORDER_READ)
            ));

            roleRepository.saveAll(List.of(admin, business, customer));

            if (userRepository.count() == 0) {
                List<User> users = new ArrayList<>();
                for (int i = 1; i <= 10; i++) {
                    User user = User.builder()
                            .email("user" + i + "@example.com")
                            .passwordHash(passwordEncoder.encode("123456")) // Mock hash
                            .fullName("Mock User " + i)
                            .phone("012345678" + i)
                            .roles(Set.of(i == 1 ? admin : (i % 2 == 0 ? business : customer)))
                            .createdAt(LocalDateTime.now())
                            .isDeleted(false)
                            .build();
                    users.add(userRepository.save(user));

                    Address address = Address.builder()
                            .receiverName("Receiver " + i)
                            .phone("012345678" + i)
                            .province("Hanoi")
                            .district("Cau Giay")
                            .ward("Dich Vong")
                            .detail("No " + i)
                            .user(user)
                            .build();
                    addressRepository.save(address);

                    user.setAddress(address);
                    userRepository.save(user);

                    Token token = Token.builder()
                            .user(user)
                            .refreshToken(UUID.randomUUID().toString())
                            .createdAt(LocalDateTime.now())
                            .expiredAt(LocalDateTime.now().plusDays(7))
                            .build();
                    tokenRepository.save(token);

                    OAuthAccount oauth = new OAuthAccount();
                    oauth.setUser(user);
                    oauth.setProvider("GOOGLE");
                    oauth.setProviderUserId("google_" + i);
                    oauth.setEmail(user.getEmail());
                    oAuthAccountRepository.save(oauth);

                    EmailLog log = EmailLog.builder()
                            .email(user.getEmail())
                            .subject("Welcome to Tech Shop " + i)
                            .status("SENT")
                            .sentAt(LocalDateTime.now())
                            .user(user)
                            .build();
                    emailLogRepository.save(log);

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    cartRepository.save(cart);
                }

                List<Category> categories = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    Category category = Category.builder()
                            .name("Category " + i)
                            .slug("category-" + i)
                            .isDeleted(false)
                            .build();
                    categories.add(categoryRepository.save(category));
                }

                List<Product> products = new ArrayList<>();
                for (int i = 1; i <= 10; i++) {
                    Product product = Product.builder()
                            .name("Tech Product " + i)
                            .businessOwner(users.get((i % 5) + 1)) // Business users
                            .description("Great product " + i)
                            .specifications("{\"color\": \"black\"}")
                            .status("ACTIVE")
                            .price(new BigDecimal("1500" + i))
                            .quantityInStock(100)
                            .ratingAvg(4.5)
                            .reviewCount(1)
                            .createdAt(LocalDateTime.now())
                            .isDeleted(false)
                            .category(categories.get(i % 5))
                            .build();
                    products.add(productRepository.save(product));

                    ProductImage img = ProductImage.builder()
                            .product(product)
                            .fileName("prod_img" + i + ".png")
                            .publicUrl("http://img.com/" + i)
                            .build();
                    productImageRepository.save(img);

                    ProductVideo vid = ProductVideo.builder()
                            .product(product)
                            .fileName("prod_vid" + i + ".mp4")
                            .publicUrl("http://vid.com/" + i)
                            .build();
                    productVideoRepository.save(vid);
                }

                List<Cart> carts = cartRepository.findAll();
                for (int i = 0; i < 10; i++) {
                    Cart cart = carts.get(i);
                    Product product = products.get((i + 1) % 10);
                    CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(2)
                            .build();
                    cartItemRepository.save(cartItem);
                }

                for (int i = 0; i < 10; i++) {
                    User user = users.get(i);
                    Order order = Order.builder()
                            .user(user)
                            .address(user.getAddress())
                            .totalPrice(new BigDecimal("3000" + i))
                            .status("PENDING")
                            .paymentMethod("COD")
                            .createdAt(LocalDateTime.now())
                            .build();
                    order = orderRepository.save(order);

                    Product product = products.get(i);
                    OrderItem orderItem = OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(1)
                            .price(product.getPrice())
                            .build();
                    orderItemRepository.save(orderItem);

                    Payment payment = Payment.builder()
                            .order(order)
                            .provider("VNPay")
                            .transactionId(UUID.randomUUID().toString())
                            .amount(order.getTotalPrice())
                            .status("SUCCESS")
                            .createdAt(LocalDateTime.now())
                            .build();
                    paymentRepository.save(payment);
                }

                for (int i = 0; i < 10; i++) {
                    Review review = Review.builder()
                            .product(products.get(i))
                            .user(users.get((i + 2) % 10))
                            .rating(5)
                            .comment("Awesome " + i)
                            .createdAt(LocalDateTime.now())
                            .build();
                    review = reviewRepository.save(review);

                    ReviewImage rImg = ReviewImage.builder()
                            .review(review)
                            .fileName("rev_img" + i + ".png")
                            .publicUrl("http://revimg.com/" + i)
                            .build();
                    reviewImageRepository.save(rImg);
                }

                Sale sale = Sale.builder().code("TECH2026").build();
                sale = saleRepository.save(sale);
                for (int i = 0; i < 5; i++) {
                    ProductSale ps = ProductSale.builder()
                            .sale(sale)
                            .product(products.get(i))
                            .value(15)
                            .isActive(true)
                            .build();
                    productSaleRepository.save(ps);
                }
            }
        };
    }

    private Permission getPermission(PermissionEnum permissionEnum) {
        return permissionRepository
                .findByPermissionName(permissionEnum)
                .orElseThrow(() ->
                        new RuntimeException("Permission not found: " + permissionEnum));
    }   
}
