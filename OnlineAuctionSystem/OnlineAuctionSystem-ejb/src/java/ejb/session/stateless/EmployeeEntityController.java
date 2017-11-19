/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import entity.CreditPackageEntity;
import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author User
 */
@Local(EmployeeEntityControllerLocal.class)
@Remote(EmployeeEntityControllerRemote.class)
@Stateless
public class EmployeeEntityController implements EmployeeEntityControllerRemote, EmployeeEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT s FROM EmployeeEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (EmployeeEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee Username " + username + " does not exist!");
        }
    }

    @Override
    public EmployeeEntity retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException {
        EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, employeeId);

        if (employeeEntity != null) {
            return employeeEntity;
        } else {
            throw new EmployeeNotFoundException("Employee ID: " + employeeId + " does not exist");
        }
    }

    @Override
    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            EmployeeEntity employeeEntity = retrieveEmployeeByUsername(username);

            if (employeeEntity.getPassword().equals(password)) {
                return employeeEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public void changePassword(EmployeeEntity employeeEntity) {
        em.merge(employeeEntity);
    }

    @Override
    public EmployeeEntity createEmployee(EmployeeEntity employeeEntity) {
        em.persist(employeeEntity);
        em.flush();
        em.refresh(employeeEntity);

        return employeeEntity;
    }

    @Override
    public Boolean checkUsername(String username) {
        Query query = em.createQuery("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }

    @Override
    public void updateEmployee(EmployeeEntity employeeEntity) {
        em.merge(employeeEntity);
    }

    @Override
    public List<EmployeeEntity> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM EmployeeEntity e");

        return query.getResultList();
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        try {
            EmployeeEntity employeeEntity = retrieveEmployeeById(employeeId);
            Query q1 = em.createQuery("SELECT c FROM CreditPackageEntity c WHERE c.employeeEntity = :inEmployee");
            q1.setParameter("inEmployee", employeeEntity);
            List<CreditPackageEntity> toRemove1 = q1.getResultList();
            for (CreditPackageEntity creditPackageEntity : toRemove1) {
                creditPackageEntity.setEmployeeEntity(null);
            }
            Query q2 = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.employeeEntity = :inEmployee");
            q2.setParameter("inEmployee", employeeEntity);
            List<AuctionListingEntity> toRemove2 = q2.getResultList();
            for (AuctionListingEntity auctionListingEntity : toRemove2) {
                auctionListingEntity.setEmployeeEntity(null);
            }
            em.remove(employeeEntity);
        } catch (EmployeeNotFoundException ex) {

        }
    }
}
