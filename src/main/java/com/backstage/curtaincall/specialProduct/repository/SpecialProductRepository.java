package com.backstage.curtaincall.specialProduct.repository;

import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SpecialProductRepository {

    @PersistenceContext
    EntityManager em;

    public List<SpecialProduct> findAll(){
        return em.createQuery("select sp from SpecialProduct sp join FETCH sp.product p where sp.deleted =false ", SpecialProduct.class)
                 .getResultList();
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
