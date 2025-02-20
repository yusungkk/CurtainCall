package com.backstage.curtaincall.specialProduct.repository;

import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class SpecialProductRepository {

    @PersistenceContext
    EntityManager em;

    public List<SpecialProduct> findAll(){
        return em.createQuery("select sp from SpecialProduct sp join FETCH sp.product p where sp.deleted =false ", SpecialProduct.class)
                 .getResultList();
    }

    // 이름 검색 및 페이지네이션을 적용한 전체 조회
    public Page<SpecialProduct> findAll(String keyword, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("select sp from SpecialProduct sp join fetch sp.product p where sp.deleted = false ");
        StringBuilder countJpql = new StringBuilder("select count(sp) from SpecialProduct sp join sp.product p where sp.deleted = false ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append("and p.productName like :keyword ");
            countJpql.append("and p.productName like :keyword ");
        }

        TypedQuery<SpecialProduct> query = em.createQuery(jpql.toString(), SpecialProduct.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);

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

    //삭제된것만 전체조회
    public List<SpecialProduct> findAllDeleted(){
        return em.createQuery("select sp from SpecialProduct sp join FETCH sp.product p where sp.deleted =true ", SpecialProduct.class)
                .getResultList();
    }

    public Optional<SpecialProduct> findById(Long id) {
        return em.createQuery("select sp from SpecialProduct sp where sp.id = :id and sp.deleted = false", SpecialProduct.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public List<SpecialProduct> findAllByProductId(Long productId, LocalDate startDate, LocalDate endDate){
        return em.createQuery("SELECT sp from SpecialProduct sp "
                        + "where sp.product.id =: productId "
                        + "AND sp.deleted =false "
                        + "AND (sp.startDate <=: startDate AND  sp.endDate >=:startDate )"
                        + "AND (sp.startDate <=: endDate AND  sp.endDate >=:endDate )", SpecialProduct.class)
                .setParameter("productId", productId)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .getResultList();
    }


    public List<SpecialProduct> findAllOverlappingDates(Long productId, LocalDate newStartDate, LocalDate newEndDate) {
        return em.createQuery("SELECT sp FROM SpecialProduct sp "
                        + "WHERE sp.product.id = :productId "
                        + "AND sp.deleted = false "
                        + "AND ((sp.startDate BETWEEN :newStartDate AND :newEndDate) "
                        + "OR (sp.endDate BETWEEN :newStartDate AND :newEndDate) "
                        + "OR (sp.startDate <= :newStartDate AND sp.endDate >= :newEndDate) "
                        + "OR (sp.startDate >= :newStartDate AND sp.endDate <= :newEndDate))", SpecialProduct.class)
                .setParameter("productId", productId)
                .setParameter("newStartDate", newStartDate)
                .setParameter("newEndDate", newEndDate)
                .getResultList();
    }


    public Optional<SpecialProduct> findByIdWithProduct(Long id) {
        return em.createQuery(
                        "SELECT sp FROM SpecialProduct sp " +
                                "JOIN FETCH sp.product p " +
                                "WHERE sp.id = :id AND sp.deleted = false", SpecialProduct.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }


    public Optional<SpecialProduct> findByIdDeleted(Long id) {
        return em.createQuery("select sp from SpecialProduct sp where sp.id = :id and sp.deleted =true", SpecialProduct.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public void save(SpecialProduct specialProduct) {
        em.persist(specialProduct);
    }


    // 할인 종료 날짜가 오늘 이전인 상품 조회 (아직 삭제되지 않은 경우)
    public void deleteExpiredSpecialProducts(LocalDate today) {
        int updatedCount = em.createQuery(
                        "UPDATE SpecialProduct sp SET sp.deleted = true WHERE sp.endDate >:today AND sp.deleted = false",
                                    SpecialProduct.class)
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
