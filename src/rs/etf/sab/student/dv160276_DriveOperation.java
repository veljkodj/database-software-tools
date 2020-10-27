/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DriveOperation;

/**
 *
 * @author Veljko
 */
public class dv160276_DriveOperation implements DriveOperation {

    public static final int pickup = 0;
    public static final int deliver = 1;
    public static final int return_to_stockroom = 2;

    public int getFirstAvailableVehicle(int idCity) {

        Connection conn = DB.getInstance().getConnection();

        String query
                = " SELECT IdVehicle\n"
                + " FROM Vehicle\n"
                + " WHERE\n"
                + "    InWarehouse = 1 AND\n"
                + "    Warehouse =\n"
                + "    (SELECT [Address].IdAddress\n"
                + "    FROM Warehouse JOIN [Address] ON (Warehouse.IdAddress = [Address].IdAddress)\n"
                + "    WHERE [Address].IdCity = ?) ";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    public boolean driveVehicleOut(int idVehicle) {
        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Vehicle SET InWarehouse = 0, Warehouse = NULL WHERE IdVehicle = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idVehicle);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getVehicle(int idDrives) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdVehicle FROM Drives WHERE IdDrives = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int addToDrives(int idCourier, int currentAddress, int idVehicle) {
        Connection conn = DB.getInstance().getConnection();
        String query = "INSERT INTO Drives (IdUser, Cost, Earnings, CurrentAddress, IdVehicle) VALUES (?, 0, 0, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCourier);
            ps.setInt(2, currentAddress);
            ps.setInt(3, idVehicle);
            ps.execute();
            query = "SELECT IdDrives FROM Drives WHERE IdUser = ? AND IdVehicle = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query);) {
                stmt.setInt(1, idCourier);
                stmt.setInt(2, idVehicle);
                try (ResultSet rs = stmt.executeQuery();) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public LinkedList<PackageInfo> getPackagesOutsideOfStockroom(int cityId) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "SELECT IdPackage, Weight , ReturnAddress, DeliveryAddress\n"
                + "FROM Package\n"
                + "WHERE\n"
                + "Status = 1 AND\n"
                + "ReturnAddress IN (SELECT IdAddress FROM Address WHERE IdCity = ?) AND\n"
                + "IdPackage NOT IN (SELECT DISTINCT IdPackage FROM [Plan])\n"
                + "ORDER BY RequestAccepted";
        LinkedList<PackageInfo> list = new LinkedList<>();
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, cityId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(new PackageInfo(rs.getInt(1), rs.getBigDecimal(2).doubleValue(), rs.getInt(3), rs.getInt(4), pickup));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public LinkedList<PackageInfo> getPackagesFromStockroom(int stockroomAddress) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "SELECT IdPackage, Weight, ReturnAddress, DeliveryAddress\n"
                + "FROM Package\n"
                + "WHERE \n"
                + "Status = 2 AND \n"
                + "Location = ? AND\n"
                + "IdPackage NOT IN (SELECT DISTINCT IdPackage FROM [Plan])\n"
                + "ORDER BY RequestAccepted";
        LinkedList<PackageInfo> list = new LinkedList<>();
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, stockroomAddress);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(new PackageInfo(rs.getInt(1), rs.getBigDecimal(2).doubleValue(), rs.getInt(3), rs.getInt(4), pickup));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public double distance(int idAddr0, int idAddr1) {
        int addr0X, addr0Y, addr1X, addr1Y;
        dv160276_AddressOperations address = new dv160276_AddressOperations();
        addr0X = address.getXCord(idAddr0);
        addr0Y = address.getYCord(idAddr0);
        addr1X = address.getXCord(idAddr1);
        addr1Y = address.getYCord(idAddr1);
        //if (addr0X == -1 || addr0Y == -1 || addr1X == -1 || addr1Y == -1) return -1;
        return distance(addr0X, addr0Y, addr1X, addr1Y);
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public int insertIntoPlan(int idDrives, int idPackage, int operation, int order) {
        Connection conn = DB.getInstance().getConnection();
        String query = "INSERT INTO [Plan] (IdPackage, Operation, IdDrives, [Order]) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, idPackage);
            ps.setInt(2, operation);
            ps.setInt(3, idDrives);
            ps.setInt(4, order);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int getDrivesId(int idCourier) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdDrives FROM Drives WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCourier);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean driving(int idCourier) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM Drives WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCourier);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public PlanInfo getNextDuty(int idDrives) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdPlan, IdPackage, Operation FROM [Plan] WHERE IdDrives = ? AND [Order] = (SELECT MIN([Order]) FROM [Plan] WHERE IdDrives = ?)";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            ps.setInt(2, idDrives);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new PlanInfo(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean changeLocation(int idDrives, int oldLocation, int newLocation) {

        Connection conn = DB.getInstance().getConnection();

        String query = "UPDATE Drives SET CurrentAddress = ? WHERE IdDrives = ?";

        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setInt(1, newLocation);
            ps0.setInt(2, idDrives);
            ps0.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query
                = "SELECT FuelType, Consumption\n"
                + "FROM Vehicle\n"
                + "WHERE IdVehicle = \n"
                + "	(\n"
                + "	SELECT IdVehicle\n"
                + "	FROM Drives\n"
                + "	WHERE IdDrives = ?\n"
                + "	)";

        int fuelType;
        double consumption;

        try (PreparedStatement ps1 = conn.prepareStatement(query);) {
            ps1.setInt(1, idDrives);
            try (ResultSet rs1 = ps1.executeQuery();) {
                if (rs1.next()) {
                    fuelType = rs1.getInt(1);
                    consumption = rs1.getBigDecimal(2).doubleValue();
                } else {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        double distance = distance(oldLocation, newLocation);
        if (distance == -1) {
            return false;
        }

        double cost = distance * consumption;
        switch (fuelType) {
            case dv160276_VehicleOperations.gas:
                cost *= 15;
                break;
            case dv160276_VehicleOperations.diesel:
                cost *= 32;
                break;
            case dv160276_VehicleOperations.gasoline:
                cost *= 36;
                break;
        }

        query = "UPDATE Drives SET Cost = Cost + ? WHERE IdDrives = ?";

        try (PreparedStatement ps2 = conn.prepareStatement(query);) {
            ps2.setBigDecimal(1, new BigDecimal(cost));
            ps2.setInt(2, idDrives);
            ps2.execute();
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query
                = "UPDATE Package\n"
                + "SET [Location] = ?\n"
                + "WHERE IdPackage IN \n"
                + "(\n"
                + "	SELECT IdPackage\n"
                + "	FROM Package\n"
                + "	WHERE [Status] = 2 AND IdPackage IN (SELECT IdPackage FROM Cargo WHERE IdDrives = ?)\n"
                + ")";

        try (PreparedStatement ps3 = conn.prepareStatement(query);) {
            ps3.setInt(1, newLocation);
            ps3.setInt(2, idDrives);
            ps3.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getCurrentLocation(int idDrives) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT CurrentAddress FROM Drives WHERE IdDrives = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean deleteDuty(int idPlan) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM [Plan] WHERE IdPlan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPlan);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean calculateProfit(int idDrives, int idCourier) {

        Connection conn = DB.getInstance().getConnection();

        String query = "SELECT Earnings-Cost FROM [Drives] WHERE IdDrives = ?";
        BigDecimal profit;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    profit = rs.getBigDecimal(1);
                } else {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "UPDATE Courier SET Profit = Profit + ? WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setBigDecimal(1, profit);
            ps.setInt(2, idCourier);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public boolean insertIntoDrove(int idVehicle, int idCourier) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "IF NOT EXISTS (SELECT * FROM Drove WHERE IdUser = ? AND IdVehicle = ?)\n"
                + "BEGIN INSERT INTO Drove(IdUser, IdVehicle) VALUES (?, ?) END";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCourier);
            ps.setInt(2, idVehicle);
            ps.setInt(3, idCourier);
            ps.setInt(4, idVehicle);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteDrives(int idDrives) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM Drives WHERE IdDrives = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertIntoCargo(int idDrives, int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "INSERT INTO Cargo (IdDrives, IdPackage)\n"
                + "VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            ps.setInt(2, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteFromCargo(int idDrives, int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "DELETE FROM Cargo\n"
                + "WHERE IdDrives = ? AND IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            ps.setInt(2, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean pickup(int idPackage, int pickupLocation) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "UPDATE Package\n"
                + "SET Status = 2, Location = ?\n"
                + "WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, pickupLocation);
            ps.setInt(2, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deliver(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "UPDATE Package\n"
                + "SET Status = 3\n"
                + "WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean incDeliveredPackages(int idCourier) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "UPDATE Courier\n"
                + "SET PackagesDelivered = PackagesDelivered + 1\n"
                + "WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCourier);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean addEarning(int idDrives, BigDecimal newEarning) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "UPDATE Drives\n"
                + "SET Earnings = Earnings + ?\n"
                + "WHERE IdDrives = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setBigDecimal(1, newEarning);
            ps.setInt(2, idDrives);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean emptyCargo(int idDrives) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "DELETE FROM Cargo\n"
                + "WHERE IdDrives = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idDrives);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public LinkedList<PackageInfo> optimize(LinkedList<PackageInfo> list) {

        LinkedList<PackageInfo> newList = new LinkedList<>();

        List<PackageInfo> found = new LinkedList<PackageInfo>();
        int startAddr = -1;

        while (!list.isEmpty()) {
            startAddr = list.peekFirst().getStartAddr();
            for (PackageInfo pi : list) {
                if (pi.getStartAddr() == startAddr) {
                    found.add(pi);
                }
            }
            newList.addAll(found);
            list.removeAll(found);
            found.clear();
        }

        return newList;
    }

    @Override
    public boolean planingDrive(String courierUsername) {

        dv160276_UserOperations userO = new dv160276_UserOperations();
        dv160276_CourierOperations courierO = new dv160276_CourierOperations();
        dv160276_StockroomOperations stockroomO = new dv160276_StockroomOperations();
        dv160276_AddressOperations addressO = new dv160276_AddressOperations();
        dv160276_VehicleOperations vehicleO = new dv160276_VehicleOperations();

        int courierId = userO.getUserId(courierUsername);
        if (courierId == -1) {
            return false;
        }

        if (!courierO.isCourier(courierId)) {
            return false;
        }

        if (driving(courierId)) {
            return false;
        }

        int myCity = userO.getCity(courierId);
        if (myCity == -1) {
            return false;
        }

        int vehicleId = getFirstAvailableVehicle(myCity);
        if (vehicleId == -1) {
            return false;
        }

        int myStockroomAddress = stockroomO.getStockroom(myCity);
        if (myStockroomAddress == -1) {
            return false;
        }
        int currentAddress = myStockroomAddress;
        int currentCity = myCity;

        double loadCapacity = vehicleO.getLoadCapacity(vehicleId);
        if (loadCapacity == -1) {
            return false;
        }

        double availableCapacity = loadCapacity;

        // plan
        LinkedList<PackageInfo> plan = new LinkedList<>();
        // helper list
        LinkedList<PackageInfo> list;

        list = getPackagesOutsideOfStockroom(currentCity);

        PackageInfo temp = null;
        while (!list.isEmpty()) {
            temp = list.pollFirst();
            if (temp.getWeight() <= availableCapacity) {
                plan.add(temp);
                availableCapacity -= temp.getWeight();
            }
        }
        plan = optimize(plan);
        if (!plan.isEmpty()) {
            currentAddress = plan.getLast().getStartAddr();
        }

        list = getPackagesFromStockroom(myStockroomAddress);
        while (!list.isEmpty()) {
            temp = list.pollFirst();
            if (temp.getWeight() <= availableCapacity) {
                currentAddress = myStockroomAddress;
                plan.add(temp);
                availableCapacity -= temp.getWeight();
            }
        }

        LinkedList<PackageInfo> planClone = (LinkedList<PackageInfo>) plan.clone();
        double shortest;
        temp = null;

        while (!planClone.isEmpty()) {

            shortest = Double.POSITIVE_INFINITY;
            for (int i = 0; i < planClone.size(); i++) {
                double distance = distance(currentAddress, planClone.get(i).getEndAddr());
                if (distance < shortest) {
                    shortest = distance;
                    temp = planClone.get(i);
                }
            }

            plan.add(new PackageInfo(temp.getId(), temp.getWeight(), temp.getStartAddr(), temp.getEndAddr(), deliver));

            availableCapacity += temp.getWeight();

            currentAddress = temp.getEndAddr();
            currentCity = addressO.getCity(currentAddress);
            if (currentCity == -1) {
                return false;
            }

            planClone.remove(temp);

            list = getPackagesOutsideOfStockroom(currentCity);
            temp = null;
            LinkedList<PackageInfo> tempList = new LinkedList<>();
            while (!list.isEmpty()) {

                temp = list.pollFirst();

                boolean flag0 = true;
                for (int i = 0; i < plan.size(); i++) {
                    if (plan.get(i).getId() == temp.getId()) {
                        flag0 = false;
                        break;
                    }
                }

                if (temp.getWeight() <= availableCapacity && flag0) {
                    tempList.add(temp);
                    availableCapacity -= temp.getWeight();
                }

            }
            tempList = optimize(tempList);
            plan.addAll(tempList);
            if (!tempList.isEmpty()) {
                currentAddress = plan.getLast().getStartAddr();
            }
            tempList.clear();

            currentCity = addressO.getCity(currentAddress);

            int stockroomAddress = stockroomO.getStockroom(currentCity);
            if (stockroomAddress == -2) {
                return false;
            }
            if (stockroomAddress != -1) {

                list = getPackagesFromStockroom(stockroomAddress);
                while (!list.isEmpty()) {

                    temp = list.pollFirst();

                    boolean flag1 = true;
                    for (int i = 0; i < plan.size(); i++) {
                        if (plan.get(i).getId() == temp.getId()) {
                            flag1 = false;
                            break;
                        }
                    }

                    if (temp.getWeight() <= availableCapacity && flag1) {
                        currentAddress = stockroomAddress;
                        plan.add(temp);
                        availableCapacity -= temp.getWeight();
                    }

                }

            }

        }

        if (!plan.isEmpty()) {

            // reserve vehicle
            if (!driveVehicleOut(vehicleId)) {
                return false;
            }

            // add to drives entity
            int idDrives = addToDrives(courierId, myStockroomAddress, vehicleId);
            if (idDrives == -1) {
                return false;
            }

            temp = null;
            int i;
            for (i = 0; i < plan.size(); i++) {
                temp = plan.get(i);
                if (insertIntoPlan(idDrives, temp.getId(), temp.getOperation(), i + 1) == -1) {
                    return false;
                }
            }

            if (insertIntoPlan(idDrives, temp.getId(), return_to_stockroom, i + 1) == -1) {
                return false;
            }

            // change courier status 
            dv160276_CourierOperations cour = new dv160276_CourierOperations();
            if (cour.changeStatus(courierId, dv160276_CourierOperations.drive) == -1) {
                return false;
            }

            return true;

        }

        return false;

    }

    @Override
    public int nextStop(String courierUsername) {

        int errorReturnValue = -3;

        dv160276_UserOperations userO = new dv160276_UserOperations();
        dv160276_CourierOperations courierO = new dv160276_CourierOperations();
        dv160276_PackageOperations packageO = new dv160276_PackageOperations();
        dv160276_StockroomOperations stockroomO = new dv160276_StockroomOperations();
        dv160276_VehicleOperations vehicleO = new dv160276_VehicleOperations();

        int courierId = userO.getUserId(courierUsername);
        if (courierId == -1) {
            return errorReturnValue;
        }

        if (!courierO.isCourier(courierId)) {
            return errorReturnValue;
        }

        // same as checking if courier_status == 1
        if (!driving(courierId)) {
            return errorReturnValue;
        }

        int idDrives = getDrivesId(courierId);
        if (idDrives == -1) {
            return errorReturnValue;
        }

        int currentLocation = getCurrentLocation(idDrives);
        if (currentLocation == -1) {
            return errorReturnValue;
        }

        PlanInfo pi = getNextDuty(idDrives);
        if (pi == null) {
            return errorReturnValue;
        }

        int ret = errorReturnValue;
        int gotoAddr = -1;

        switch (pi.getOperation()) {
            case deliver:

                gotoAddr = packageO.getEndAddress(pi.getIdPackage());
                if (gotoAddr == -1) {
                    return errorReturnValue;
                }

                if (!changeLocation(idDrives, currentLocation, gotoAddr)) {
                    return errorReturnValue;
                }

                do {

                    if (!deliver(pi.getIdPackage())) {
                        return errorReturnValue;
                    }

                    if (!deleteFromCargo(idDrives, pi.getIdPackage())) {
                        return errorReturnValue;
                    }

                    if (!incDeliveredPackages(courierId)) {
                        return errorReturnValue;
                    }

                    BigDecimal price = packageO.getPrice(pi.getIdPackage());
                    if (price == null) {
                        return errorReturnValue;
                    }

                    if (!addEarning(idDrives, price)) {
                        return errorReturnValue;
                    }

                    ret = pi.getIdPackage();

                    if (!deleteDuty(pi.getIdPlan())) {
                        return errorReturnValue;
                    }

                    pi = getNextDuty(idDrives);
                    if (pi == null) {
                        return errorReturnValue;
                    }

                } while ((pi.getOperation() == deliver) && (packageO.getEndAddress(pi.getIdPackage()) == gotoAddr));

                break;

            case pickup:

                int packageStatus = packageO.getStatus(pi.getIdPackage());
                if (packageStatus == -1) {
                    return errorReturnValue;
                }

                switch (packageStatus) {

                    case dv160276_PackageOperations.status_request_accepted:

                        gotoAddr = packageO.getStartAddress(pi.getIdPackage());
                        if (gotoAddr == -1) {
                            return errorReturnValue;
                        }

                        if (!changeLocation(idDrives, currentLocation, gotoAddr)) {
                            return errorReturnValue;
                        }

                        do {

                            if (!pickup(pi.getIdPackage(), gotoAddr)) {
                                return errorReturnValue;
                            }

                            if (!insertIntoCargo(idDrives, pi.getIdPackage())) {
                                return errorReturnValue;
                            }

                            if (!deleteDuty(pi.getIdPlan())) {
                                return errorReturnValue;
                            }

                            ret = -2;

                            pi = getNextDuty(idDrives);
                            if (pi == null) {
                                return errorReturnValue;
                            }

                        } while ((pi.getOperation() == pickup) && (packageO.getStartAddress(pi.getIdPackage()) == gotoAddr));

                        break;

                    case dv160276_PackageOperations.status_pickedup:

                        gotoAddr = packageO.getLocation(pi.getIdPackage());
                        if (gotoAddr == -1) {
                            return errorReturnValue;
                        }

                        if (!changeLocation(idDrives, currentLocation, gotoAddr)) {
                            return errorReturnValue;
                        }

                        do {

                            if (!insertIntoCargo(idDrives, pi.getIdPackage())) {
                                return errorReturnValue;
                            }

                            if (!deleteDuty(pi.getIdPlan())) {
                                return errorReturnValue;
                            }

                            ret = -2;

                            pi = getNextDuty(idDrives);
                            if (pi == null) {
                                return errorReturnValue;
                            }

                        } while ((pi.getOperation() == pickup) && (packageO.getLocation(pi.getIdPackage()) == gotoAddr));

                        break;

                }

                break;

            case return_to_stockroom:

                int idCity = userO.getCity(courierId);
                if (idCity == -1) {
                    return errorReturnValue;
                }

                int stockroomAddress = stockroomO.getStockroom(idCity);
                if (stockroomAddress == -2) {
                    return errorReturnValue;
                }

                if (!changeLocation(idDrives, currentLocation, stockroomAddress)) {
                    return errorReturnValue;
                }

                if (!calculateProfit(idDrives, courierId)) {
                    return errorReturnValue;
                }

                int vehicleId = getVehicle(idDrives);
                if (vehicleId == -1) {
                    return errorReturnValue;
                }

                if (courierO.changeStatus(courierId, dv160276_CourierOperations.relax) == -1) {
                    return errorReturnValue;
                }

                if (!insertIntoDrove(vehicleId, courierId)) {
                    return errorReturnValue;
                }

                if (!emptyCargo(idDrives)) {
                    return errorReturnValue;
                }

                if (!vehicleO.parkInGarage(vehicleId, stockroomAddress)) {
                    return errorReturnValue;
                }

                if (!deleteDuty(pi.getIdPlan())) {
                    return errorReturnValue;
                }

                if (!deleteDrives(idDrives)) {
                    return errorReturnValue;
                }

                ret = -1;

                break;
            default:
                break;
        }

        return ret;

    }

    @Override
    public List<Integer> getPackagesInVehicle(String courierUsername) {

        // courier id
        dv160276_UserOperations user = new dv160276_UserOperations();
        int courierId = user.getUserId(courierUsername);
        if (courierId == -1) {
            return null;
        }

        int drivesId = getDrivesId(courierId);
        if (drivesId == -1) {
            return null;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdPackage FROM Cargo WHERE IdDrives = ?";
        List<Integer> list = new LinkedList<>();

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, drivesId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;

    }

}
