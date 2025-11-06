package com.mycompany.dao;

import com.mycompany.model.Supplier;
import com.mycompany.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SupplierDAO {

    public List<Supplier> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery("SELECT s FROM Supplier s ORDER BY s.name", Supplier.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Supplier findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Supplier.class, id);
        } finally {
            em.close();
        }
    }

    public boolean insert(Supplier s) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(s);
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

    public boolean update(Supplier s) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(s);
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
            Supplier s = em.find(Supplier.class, id);
            if (s != null) em.remove(s);
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

    public Supplier findByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery("SELECT s FROM Supplier s WHERE s.name = :n", Supplier.class);
            q.setParameter("n", name);
            return q.getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
}