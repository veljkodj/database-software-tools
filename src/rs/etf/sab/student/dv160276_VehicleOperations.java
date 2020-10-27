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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_VehicleOperations implements VehicleOperations {

    public static final int gas = 0;
    public static final int diesel = 1;
    public static final int gasoline = 2;

    public boolean isInStockroom(int idVehicle) {

        Connection conn = DB.getInstance().getConnection();

        String query = "SELECT InWarehouse FROM Vehicle WHERE IdVehicle = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idVehicle);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next() && rs.getInt(1) == 1) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public int getVehicleId(String licensePlate) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdVehicle FROM Vehicle WHERE LicensePlate = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, licensePlate);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean parkInGarage(int idVehicle, int idStockroom) {

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT InWarehouse FROM Vehicle WHERE IdVehicle = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idVehicle);
            try (ResultSet rs = ps.executeQuery();) {
                if (!rs.next()) {
                    return false;
                } else if (rs.getInt(1) == 1) {
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

        query = "UPDATE Vehicle SET Warehouse = ?, InWarehouse = 1 WHERE IdVehicle = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idStockroom);
            ps.setInt(2, idVehicle);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public double getLoadCapacity(int idVehicle) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT LoadCapacity FROM Vehicle WHERE IdVehicle = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idVehicle);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getBigDecimal(1).doubleValue();
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion, BigDecimal capacity) {

        Connection conn = DB.getInstance().getConnection();

        String query = "SELECT * FROM Vehicle WHERE LicensePlate = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, licencePlateNumber);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        if (fuelType != gas && fuelType != diesel && fuelType != gasoline) {
            System.out.println("Please choose correct fuel type.");
            return false;
        }
        query = "INSERT INTO Vehicle (LicensePlate, FuelType, Consumption, LoadCapacity, Warehouse, InWarehouse) VALUES (?, ?, ?, ?, NULL, 0)";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, licencePlateNumber);
            stmt.setInt(2, fuelType);
            stmt.setBigDecimal(3, fuelConsumtion);
            stmt.setBigDecimal(4, capacity);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM Vehicle WHERE LicensePlate = ?";
        int cntrDeletedRows = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            for (String licencePlateNumber : licencePlateNumbers) {
                ps.setString(1, licencePlateNumber);
                cntrDeletedRows += ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cntrDeletedRows;
    }

    @Override
    public List<String> getAllVehichles() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT LicensePlate FROM Vehicle";
        List<String> list = new LinkedList<>();
        try (
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public boolean changeFuelType(String licensePlateNumber, int fuelType) {

        if (fuelType != gas && fuelType != diesel && fuelType != gasoline) {
            System.out.println("Please choose correct fuel type.");
            return false;
        }

        int idVehicle = getVehicleId(licensePlateNumber);
        if (idVehicle == -1) {
            return false;
        }

        if (!isInStockroom(idVehicle)) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Vehicle SET FuelType = ? WHERE LicensePlate = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, fuelType);
            ps.setString(2, licensePlateNumber);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeConsumption(String licensePlateNumber, BigDecimal fuelConsumption) {

        int idVehicle = getVehicleId(licensePlateNumber);
        if (idVehicle == -1) {
            return false;
        }

        if (!isInStockroom(idVehicle)) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Vehicle SET Consumption = ? WHERE LicensePlate = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setBigDecimal(1, fuelConsumption);
            ps.setString(2, licensePlateNumber);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeCapacity(String licensePlateNumber, BigDecimal capacity) {

        int idVehicle = getVehicleId(licensePlateNumber);
        if (idVehicle == -1) {
            return false;
        }

        if (!isInStockroom(idVehicle)) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Vehicle SET LoadCapacity = ? WHERE LicensePlate = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setBigDecimal(1, capacity);
            ps.setString(2, licensePlateNumber);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean parkVehicle(String licensePlateNumber, int idStockroom) {

        int idVehicle = getVehicleId(licensePlateNumber);
        if (idVehicle == -1) {
            return false;
        }

        dv160276_AddressOperations addr = new dv160276_AddressOperations();
        if (!addr.addressExsits(idStockroom)) {
            return false;
        }

        dv160276_StockroomOperations stockroom = new dv160276_StockroomOperations();
        if (!stockroom.stockroomExsits(idStockroom)) {
            return false;
        }

        return parkInGarage(idVehicle, idStockroom);

    }

}
