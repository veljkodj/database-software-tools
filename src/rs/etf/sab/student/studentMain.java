package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class studentMain {

    public static void main(String[] args) {

        AddressOperations addressOperations = new dv160276_AddressOperations();
        CityOperations cityOperations = new dv160276_CityOperations();
        CourierOperations courierOperations = new dv160276_CourierOperations();
        CourierRequestOperation courierRequestOperation = new dv160276_CourierRequestOperation();
        DriveOperation driveOperation = new dv160276_DriveOperation();
        GeneralOperations generalOperations = new dv160276_GeneralOperations();
        PackageOperations packageOperations = new dv160276_PackageOperations();
        StockroomOperations stockroomOperations = new dv160276_StockroomOperations();
        UserOperations userOperations = new dv160276_UserOperations();
        VehicleOperations vehicleOperations = new dv160276_VehicleOperations();

        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();

    }
}
