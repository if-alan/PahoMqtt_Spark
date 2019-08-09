/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkripsiLita;

/**
 *
 * @author if_alan
 */
public class Data {
    private String voltage;
    private String current;
    private String power;
    private String kwh;

    public Data(String voltage, String current, String power, String kwh) {
        super();
        this.voltage = voltage;
        this.current = current;
        this.power = power;
        this.kwh = kwh;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getKwh() {
        return kwh;
    }

    public void setKwh(String feedback) {
        this.kwh = feedback;
    }

    @Override
    public String toString() {
        return new StringBuffer().append(getKwh()).toString();
    }
}
