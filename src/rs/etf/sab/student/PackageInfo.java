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
public class PackageInfo {

    private int id;
    private double weight;
    private int startAddr;
    private int endAddr;
    private int operation;

    public PackageInfo(int id, double weight, int startAddr, int endAddr, int operation) {
        this.id = id;
        this.weight = weight;
        this.startAddr = startAddr;
        this.endAddr = endAddr;
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(int startAddr) {
        this.startAddr = startAddr;
    }

    public int getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(int endAddr) {
        this.endAddr = endAddr;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

}
