package com.metric.receiver.dto;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class TemperatureDTO {
    private String sensorUuid;
    @NotNull
    private double temperature;

    private Date at = new Date();

    public TemperatureDTO() {
    }

    public TemperatureDTO(String sensorUuid, double temperature, Date at) {
        this.sensorUuid = sensorUuid;
        this.at = at;
        this.temperature = temperature;
    }

    public void setSensorUuid(String sensorUuid) {
        this.sensorUuid = sensorUuid;
    }

    public String getSensorUuid() {
        return sensorUuid;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    @Override
    public String toString() {
        return "TemperatureDTO{" +
                "sensorUuid='" + sensorUuid + '\'' +
                ", temperature=" + temperature +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemperatureDTO that = (TemperatureDTO) o;

        if (Double.compare(that.temperature, temperature) != 0) return false;
        if (sensorUuid != null ? !sensorUuid.equals(that.sensorUuid) : that.sensorUuid != null) return false;
        return at != null ? at.equals(that.at) : that.at == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = sensorUuid != null ? sensorUuid.hashCode() : 0;
        temp = Double.doubleToLongBits(temperature);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
