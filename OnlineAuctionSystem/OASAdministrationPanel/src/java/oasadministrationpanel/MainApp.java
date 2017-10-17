/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadministrationpanel;

import ejb.session.stateless.EmployeeEntityControllerRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author User
 */
public class MainApp {
    private EmployeeEntityControllerRemote employeeEntityControllerRemote;
    
    private EmployeeEntity currentEmployeeEntity;
    public MainApp() {
    }

    public MainApp(EmployeeEntityControllerRemote employeeEntityControllerRemote) {
        this.employeeEntityControllerRemote = employeeEntityControllerRemote;
    }
    
    public void runApp(){
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Retail-Core-Banking-System (RCBS) System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {

                    try {
                        if (doLogin()) {
                            mainMenu();
                        }
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential \n");
                    }
                } else if (response == 2) {
                    break;

                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
        
    }
    
    private void mainMenu(){
        
    }
    
    private boolean doLogin() throws InvalidLoginCredentialException{
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** OAS Administration System :: Login ***\n");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        if (username.length() > 0 && password.length() > 0) {
            try {
                currentEmployeeEntity = employeeEntityControllerRemote.employeeLogin(username, password);
                System.out.println("Login successful!\n");
                return true;
            } catch (InvalidLoginCredentialException ex) {
                throw new InvalidLoginCredentialException();
            }
        } else {
            System.out.println("Invalid login credential!");
            return false;
        }
    }
}
