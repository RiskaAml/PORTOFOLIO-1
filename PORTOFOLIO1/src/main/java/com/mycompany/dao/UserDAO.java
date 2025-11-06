package com.mycompany.dao;

import com.mycompany.model.User;
import com.mycompany.util.JPAUtil;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class UserDAO {

    public User findByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :un", User.class);
            q.setParameter("un", username);
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean insert(String username, String plainPassword, String fullName, String role) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User u = new User();
            u.setUsername(username);
            u.setHashedPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
            u.setFullName(fullName);
            u.setRole(role);
            em.persist(u);
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

    public boolean authenticate(String username, String plainPassword) {
        User u = findByUsername(username);
        if (u == null) return false;
        return BCrypt.checkpw(plainPassword, u.getHashedPassword());
    }
}
