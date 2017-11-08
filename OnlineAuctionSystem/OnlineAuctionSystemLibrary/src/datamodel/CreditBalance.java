/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.math.BigDecimal;

/**
 *
 * @author User
 */
public class CreditBalance {
    private BigDecimal creditBalance;
    private BigDecimal holdingBalance;
    private BigDecimal availableBalance;

    public CreditBalance() {
    }

    public CreditBalance(BigDecimal creditBalance, BigDecimal holdingBalance, BigDecimal availableBalance) {
        this.creditBalance = creditBalance;
        this.holdingBalance = holdingBalance;
        this.availableBalance = availableBalance;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
    }

    public BigDecimal getHoldingBalance() {
        return holdingBalance;
    }

    public void setHoldingBalance(BigDecimal holdingBalance) {
        this.holdingBalance = holdingBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

}
