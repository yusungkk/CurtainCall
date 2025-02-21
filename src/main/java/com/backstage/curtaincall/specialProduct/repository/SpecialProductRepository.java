package com.backstage.curtaincall.specialProduct.repository;

import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SpecialProductRepository {

    @PersistenceContext
    EntityManager em;

    public List<SpecialProduct> findAll() {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp JOIN FETCH sp.product p " +
                                "WHERE sp.status != :deletedStatus", SpecialProduct.class)
                .setParameter("deletedStatus", SpecialProductStatus.DELETED)
                .getResultList();
    }

    // 활성 (ACTIVE) 상태의 특가상품 조회
    public List<SpecialProduct> findAllActive() {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp JOIN FETCH sp.product p " +
                                "LEFT JOIN FETCH p.productImage pi " +
                                "WHERE sp.status = :activeStatus", SpecialProduct.class)
                .setParameter("activeStatus", SpecialProductStatus.ACTIVE)
                .getResultList();
    }



    // 이름 검색 및 페이지네이션을 적용한 전체 조회
    public Page<SpecialProduct> findAll(String keyword, Pageable pageable) {
        StringBuilder jpql = new StringBuilder(
                "SELECT sp FROM SpecialProduct sp " +
                        "JOIN FETCH sp.product p " +
                        "LEFT JOIN FETCH p.productImage pi " +
                        "WHERE sp.status != :deletedStatus ");
        StringBuilder countJpql = new StringBuilder(
                "SELECT COUNT(sp) FROM SpecialProduct sp JOIN sp.product p " +
                        "WHERE sp.status != :deletedStatus ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append("AND p.productName LIKE :keyword ");
            countJpql.append("AND p.productName LIKE :keyword ");
        }

        TypedQuery<SpecialProduct> query = em.createQuery(jpql.toString(), SpecialProduct.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);

        query.setParameter("deletedStatus", SpecialProductStatus.DELETED);
        countQuery.setParameter("deletedStatus", SpecialProductStatus.DELETED);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<SpecialProduct> content = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    // 삭제된 것만 전체 조회
    public List<SpecialProduct> findAllDeleted() {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp JOIN FETCH sp.product p " +
                                "LEFT JOIN FETCH p.productImage pi " +
                                "WHERE sp.status = :deletedStatus", SpecialProduct.class)
                .setParameter("deletedStatus", SpecialProductStatus.DELETED)
                .getResultList();
    }

    public Optional<SpecialProduct> findById(Long id) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp " +
                                "WHERE sp.id = :id AND sp.status != :deletedStatus", SpecialProduct.class)
                .setParameter("id", id)
                .setParameter("deletedStatus", SpecialProductStatus.DELETED)
                .getResultStream()
                .findFirst();
    }

    public List<SpecialProduct> findAllOverlappingDates(Long productId, LocalDate newStartDate, LocalDate newEndDate) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp " +
                                "WHERE sp.product.id = :productId " +
                                "AND sp.status != :deletedStatus " +
                                "AND ((sp.startDate BETWEEN :newStartDate AND :newEndDate) " +
                                "OR (sp.endDate BETWEEN :newStartDate AND :newEndDate) " +
                                "OR (sp.startDate <= :newStartDate AND sp.endDate >= :newEndDate) " +
                                "OR (sp.startDate >= :newStartDate AND sp.endDate <= :newEndDate))", SpecialProduct.class)
                .setParameter("productId", productId)
                .setParameter("newStartDate", newStartDate)
                .setParameter("newEndDate", newEndDate)
                .setParameter("deletedStatus", SpecialProductStatus.DELETED)
                .getResultList();
    }

    public Optional<SpecialProduct> findByIdWithProduct(Long id) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp " +
                                "JOIN FETCH sp.product p " +
                                "WHERE sp.id = :id AND sp.status <> :deleted", SpecialProduct.class)
                .setParameter("id", id)
                .setParameter("deleted", SpecialProductStatus.DELETED)
                .getResultStream()
                .findFirst();
    }


    public Optional<SpecialProduct> findByIdUpcoming(Long id) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp " +
                                "WHERE sp.id = :id AND sp.status = :upcoming", SpecialProduct.class)
                .setParameter("id", id)
                .setParameter("upcoming", SpecialProductStatus.UPCOMING)
                .getResultStream()
                .findFirst();
    }

    public Optional<SpecialProduct> findByIdActive(Long id) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp " +
                                "WHERE sp.id = :id AND sp.status = :active", SpecialProduct.class)
                .setParameter("id", id)
                .setParameter("active", SpecialProductStatus.ACTIVE)
                .getResultStream()
                .findFirst();
    }

    public void save(SpecialProduct specialProduct) {
        em.persist(specialProduct);
    }

    // 할인 종료 날짜가 오늘 이전인 상품을 자동으로 만료 처리 (DELETED 상태로 변경)
    public void deleteExpiredSpecialProducts(LocalDate today) {
        int updatedCount = em.createQuery(
                        "UPDATE SpecialProduct sp SET sp.status = :deletedStatus " +
                                "WHERE sp.endDate < :today AND sp.status != :deletedStatus")
                .setParameter("deletedStatus", SpecialProductStatus.DELETED)
                .setParameter("today", today)
                .executeUpdate();
        em.clear();
    }

    // 할인 시작 날짜가 오늘인 상품 조회
    public List<SpecialProduct> findAllStartingSpecialProducts(LocalDate today) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp WHERE sp.startDate = :today",
                        SpecialProduct.class)
                .setParameter("today", today)
                .getResultList();
    }


}
