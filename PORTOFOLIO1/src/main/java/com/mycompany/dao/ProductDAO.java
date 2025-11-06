package com.mycompany.dao;

import com.mycompany.model.Product;
import com.mycompany.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProductDAO {

    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> q = em.createQuery("SELECT p FROM Product p ORDER BY p.name", Product.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Product findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public boolean insert(Product p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean update(Product p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Product p = em.find(Product.class, id);
            if (p != null) em.remove(p);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}