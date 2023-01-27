package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entity.User;

import javax.persistence.*;
import java.util.List;

@Repository
public class UserDaoImp implements UserDao {

   @PersistenceContext
   private EntityManager em;

   @Override
   public List<User> getUsersList() {
      Query query = em.createQuery("SELECT u FROM User u");
      List<User> entities = query.getResultList();
      return entities;
   }

   @Override
   public void addUser(User user) {
      em.persist(user);
   }

   @Override
   public void removeUser(Long id) {
//      Query query = em.createQuery("delete from User u where u.id=:id")
//              .setParameter("id", id);
//      query.executeUpdate();
   }

   @Override
   public User findUserById(Long id) {
      return em.find(User.class, id);
   }

   @Override
   public void updateUser(User user) {
      em.merge(user);
   }
}
