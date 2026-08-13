package com.justjava.ecommerce.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") UUID customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByCustomerIdAndStatusOrderByCreatedAtDesc(@Param("customerId") UUID customerId, @Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.paymentReference = :ref")
    Optional<Order> findByPaymentReference(@Param("ref") String ref);

    @Query("SELECT o FROM Order o JOIN FETCH o.items ORDER BY o.createdAt DESC")
    Page<Order> findAllWithItems(Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId")
    Page<Order> findByVendorId(@Param("vendorId") UUID vendorId, Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status = :status",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status = :status")
    Page<Order> findByVendorIdAndStatus(@Param("vendorId") UUID vendorId, @Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.items i WHERE o.id = :id AND i.product.vendor.id = :vendorId")
    Optional<Order> findByIdAndVendorId(@Param("id") UUID id, @Param("vendorId") UUID vendorId);

    @Query("""
            SELECT o FROM Order o JOIN FETCH o.items
            WHERE o.id = :id
              AND EXISTS (SELECT 1 FROM OrderItem i2
                          WHERE i2.order.id = o.id
                            AND i2.product.vendor.id = :vendorId
                            AND i2.product.createdBy.id = :createdBy)
            """)
    Optional<Order> findByIdAndVendorIdAndCreator(@Param("id") UUID id,
                                                  @Param("vendorId") UUID vendorId,
                                                  @Param("createdBy") UUID createdBy);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy")
    Page<Order> findByVendorIdAndCreator(@Param("vendorId") UUID vendorId,
                                         @Param("createdBy") UUID createdBy,
                                         Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND o.status = :status",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND o.status = :status")
    Page<Order> findByVendorIdAndCreatorAndStatus(@Param("vendorId") UUID vendorId,
                                                  @Param("createdBy") UUID createdBy,
                                                  @Param("status") OrderStatus status,
                                                  Pageable pageable);

    // ── Dashboard stats ───────────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal sumPlatformRevenue();

    // ── Agent (warehouse-agent) queries ───────────────────────────────────────

    Page<Order> findByAssignedAgentIdOrderByCreatedAtDesc(UUID agentId, Pageable pageable);

    Page<Order> findByAssignedAgentIdAndStatusOrderByCreatedAtDesc(UUID agentId, OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndAssignedAgentId(UUID id, UUID agentId);

    long countByAssignedAgentIdAndStatus(UUID agentId, OrderStatus status);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId")
    long countByVendorId(@Param("vendorId") UUID vendorId);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy")
    long countByVendorIdAndCreator(@Param("vendorId") UUID vendorId, @Param("createdBy") UUID createdBy);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status = :status")
    long countByVendorIdAndStatus(@Param("vendorId") UUID vendorId, @Param("status") OrderStatus status);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND o.status = :status")
    long countByVendorIdAndCreatorAndStatus(@Param("vendorId") UUID vendorId,
                                            @Param("createdBy") UUID createdBy,
                                            @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status = 'PAID'")
    BigDecimal sumRevenueByVendorId(@Param("vendorId") UUID vendorId);

    /**
     * Sum of line totals (not order totals) for products created by {@code createdBy}
     * within the vendor company, restricted to PAID orders. Uses line totals so
     * multi-vendor orders don't double-count.
     */
    @Query("SELECT COALESCE(SUM(i.lineTotal), 0) FROM OrderItem i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND i.order.status = 'PAID'")
    BigDecimal sumRevenueByVendorIdAndCreator(@Param("vendorId") UUID vendorId, @Param("createdBy") UUID createdBy);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId AND o.status IN :statuses")
    long countByCustomerIdAndStatusIn(@Param("customerId") UUID customerId, @Param("statuses") Set<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.customer.id = :customerId AND o.status IN :statuses")
    BigDecimal sumSpendByCustomerIdAndStatusIn(@Param("customerId") UUID customerId, @Param("statuses") Set<OrderStatus> statuses);

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId ORDER BY o.createdAt DESC")
    List<Order> findRecentByVendorId(@Param("vendorId") UUID vendorId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy ORDER BY o.createdAt DESC")
    List<Order> findRecentByVendorIdAndCreator(@Param("vendorId") UUID vendorId,
                                               @Param("createdBy") UUID createdBy,
                                               Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Order> findRecentByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);

    // ── Vendor earnings ───────────────────────────────────────────────────────

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status IN :statuses ORDER BY o.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status IN :statuses")
    Page<Order> findByVendorIdAndStatusIn(@Param("vendorId") UUID vendorId, @Param("statuses") Set<OrderStatus> statuses, Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND o.status IN :statuses ORDER BY o.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND o.status IN :statuses")
    Page<Order> findByVendorIdAndCreatorAndStatusIn(@Param("vendorId") UUID vendorId,
                                                    @Param("createdBy") UUID createdBy,
                                                    @Param("statuses") Set<OrderStatus> statuses,
                                                    Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o JOIN o.items i WHERE i.product.vendor.id = :vendorId AND o.status IN :statuses")
    java.math.BigDecimal sumRevenueByVendorIdAndStatusIn(@Param("vendorId") UUID vendorId, @Param("statuses") Set<OrderStatus> statuses);

    @Query("SELECT COALESCE(SUM(i.lineTotal), 0) FROM OrderItem i WHERE i.product.vendor.id = :vendorId AND i.product.createdBy.id = :createdBy AND i.order.status IN :statuses")
    java.math.BigDecimal sumRevenueByVendorIdAndCreatorAndStatusIn(@Param("vendorId") UUID vendorId,
                                                                   @Param("createdBy") UUID createdBy,
                                                                   @Param("statuses") Set<OrderStatus> statuses);

    // ── Analytics ─────────────────────────────────────────────────────────────

    long countByStatus(OrderStatus status);

    @Query("""
            SELECT i.vendorName, SUM(i.lineTotal), COUNT(DISTINCT i.order.id)
            FROM OrderItem i
            WHERE i.order.status IN :statuses
            GROUP BY i.vendorName
            ORDER BY SUM(i.lineTotal) DESC
            """)
    List<Object[]> findTopVendorsByRevenue(@Param("statuses") Set<OrderStatus> statuses, Pageable pageable);

    @Query("""
            SELECT i.productName, SUM(i.quantity), SUM(i.lineTotal)
            FROM OrderItem i
            WHERE i.product.vendor.id = :vendorId
              AND i.order.status IN :statuses
            GROUP BY i.productName
            ORDER BY SUM(i.quantity) DESC
            """)
    List<Object[]> findTopProductsByVendor(
            @Param("vendorId") UUID vendorId,
            @Param("statuses") Set<OrderStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT i.productName, SUM(i.quantity), SUM(i.lineTotal)
            FROM OrderItem i
            WHERE i.product.vendor.id = :vendorId
              AND i.product.createdBy.id = :createdBy
              AND i.order.status IN :statuses
            GROUP BY i.productName
            ORDER BY SUM(i.quantity) DESC
            """)
    List<Object[]> findTopProductsByVendorAndCreator(
            @Param("vendorId") UUID vendorId,
            @Param("createdBy") UUID createdBy,
            @Param("statuses") Set<OrderStatus> statuses,
            Pageable pageable);
}
