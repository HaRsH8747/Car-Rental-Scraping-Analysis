package model;

public class CarInfo {
    private String name;
    private double price;
    private int passengerCapacity;
    private String carGroup;
    private String transmissionType;
    private int largeBag;
    private int smallBag;

    public CarInfo(String name, double price, int passengerCapacity, String carGroup, String transmissionType, int largeBag, int smallBag) {
        this.name = name;
        this.price = price;
        this.passengerCapacity = passengerCapacity;
        this.carGroup = carGroup;
        this.transmissionType = transmissionType;
        this.largeBag = largeBag;
        this.smallBag = smallBag;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public String getCarGroup() {
        return carGroup;
    }

    public int getLargeBag() {
        return largeBag;
    }

    public int getSmallBag() {
        return smallBag;
    }
}
