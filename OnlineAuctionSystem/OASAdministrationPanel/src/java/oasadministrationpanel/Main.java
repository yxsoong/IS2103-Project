/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadministrationpanel;

import ejb.session.stateless.EmployeeEntityControllerRemote;
import javax.ejb.EJB;

/**
 *
 * @author User
 */
public class Main {

    @EJB
    private static EmployeeEntityControllerRemote employeeEntityControllerRemote;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(employeeEntityControllerRemote);
        mainApp.runApp();
    }
    
}
