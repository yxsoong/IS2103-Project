/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadministrationpanel;

import ejb.session.stateless.EmployeeEntityControllerRemote;

/**
 *
 * @author User
 */
public class MainApp {
    private EmployeeEntityControllerRemote employeeEntityControllerRemote;

    public MainApp() {
    }

    public MainApp(EmployeeEntityControllerRemote employeeEntityControllerRemote) {
        this.employeeEntityControllerRemote = employeeEntityControllerRemote;
    }
    
    public void run(){
        employeeEntityControllerRemote.helloWorld();
        System.out.println("Hello World!");
        System.out.println("Hello World2!");
    }
}
