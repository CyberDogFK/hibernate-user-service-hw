package mate.academy.dao.impl;

import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mate.academy.dao.UserDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class UserDaoImpl implements UserDao {
    @Override
    public User add(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert user " + user, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = HibernateUtil.getSessionFactory()
                    .getCriteriaBuilder();
            CriteriaQuery<User> userQuery = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = userQuery.from(User.class);
            CriteriaBuilder.In<String> emailPredicate = criteriaBuilder.in(userRoot.get("email"));
            emailPredicate.value(email);
            userQuery.where(emailPredicate);
            return session.createQuery(userQuery).uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Error of founding user from DB " + email, e);
        }
    }
}