package com.backstage.curtaincall.specialProduct.repository;

import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SpecialProductRepository {

    @PersistenceContext
    EntityManager em;

    public List<SpecialProduct> findAllWithProduct(){
        return em.createQuery("select sp from SpecialProduct sp join sp.product p where sp.deleted =false ", SpecialProduct.class)
                 .getResultList();
    }

    public Optional<SpecialProduct> findById(Long id) {
        return em.createQuery("select sp from SpecialProduct sp where sp.id = :id and sp.deleted = false", SpecialProduct.class)
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


}
