/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

/**
 *
 * @author Veljko
 */
public class PlanInfo {

    private int idPlan;
    private int idPackage;
    private int operation;

    public PlanInfo(int idPlan, int idPackage, int operation) {
        this.idPlan = idPlan;
        this.idPackage = idPackage;
        this.operation = operation;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public int getIdPackage() {
        return idPackage;
    }

    public void setIdPackage(int idPackage) {
        this.idPackage = idPackage;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    

}
