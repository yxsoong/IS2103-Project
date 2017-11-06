/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author User
 */
public class InsufficientCreditsException extends Exception{

    public InsufficientCreditsException() {
    }

    public InsufficientCreditsException(String message) {
        super(message);
    }
    
}
