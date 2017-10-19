/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;


public interface EmployeeEntityControllerRemote {

    public EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;

    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    public void changePassword(EmployeeEntity employeeEntity);

    public EmployeeEntity createEmployee(EmployeeEntity employeeEntity);

    public Boolean checkUsername(String username);

    public EmployeeEntity retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException;

    public void updateEmployee(EmployeeEntity employeeEntity);

    public List<EmployeeEntity> retrieveAllEmployees();

    public void deleteEmployee(Long employeeId);
    
}
