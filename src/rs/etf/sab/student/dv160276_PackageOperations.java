/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_PackageOperations implements PackageOperations {

    public static final int small_package = 0;
    public static final int standard_package = 1;
    public static final int nonstandard_package = 2;
    public static final int fragile_package = 3;

    public static final int status_created = 0;
    public static final int status_request_accepted = 1;
    public static final int status_pickedup = 2;
    public static final int status_delivered = 3;
    public static final int status_request_rejected = 4;

    public static final int starting_price_small = 115;
    public static final int starting_price_standard = 175;
    public static final int starting_price_nonstandard = 250;
    public static final int starting_price_fragile = 350;

    public static final int per_kg_standard = 100;
    public static final int per_kg_nonstandard = 100;
    public static final int per_kg_fragile = 500;

    public boolean packageExist(int idPackage) {

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM Package WHERE IdPackage = ?";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public boolean delete(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getStatus(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Status FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
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

    public int getStartAddress(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT ReturnAddress FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
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

    public int getEndAddress(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT DeliveryAddress FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
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

    public int getLocation(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Location FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
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

    public BigDecimal getPrice(int idPackage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Price FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idPackage);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int insertPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {

        dv160276_AddressOperations address = new dv160276_AddressOperations();
        int addressFromX, addressFromY, addressToX, addressToY;
        addressFromX = address.getXCord(addressFrom);
        addressFromY = address.getYCord(addressFrom);
        addressToX = address.getXCord(addressTo);
        addressToY = address.getYCord(addressTo);
        //if (addressFromX == -1 || addressFromY == -1 || addressToX == -1 || addressToY == -1) return -1;

        dv160276_UserOperations user = new dv160276_UserOperations();
        int userId = user.getUserId(userName);
        if (userId == -1) {
            return -1;
        }

        if (packageType != small_package
                && packageType != standard_package
                && packageType != nonstandard_package
                && packageType != fragile_package) {
            return -1;
        }

        if (weight == null) {
            weight = new BigDecimal(10);
        }

        Connection conn = DB.getInstance().getConnection();
        String query
                = "INSERT INTO Package ([Type], [Weight], [Status], ReturnAddress, DeliveryAddress, Price, RequestCreated, RequestAccepted, [Location], IdUser) "
                + "VALUES (?, ?, 0, ?, ?, 0, GETDATE(), NULL, NULL, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, packageType);
            ps.setBigDecimal(2, weight);
            ps.setInt(3, addressFrom);
            ps.setInt(4, addressTo);
            ps.setInt(5, userId);
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

    @Override
    public boolean acceptAnOffer(int packageId) {

        if (!packageExist(packageId)) {
            return false;
        }

        if (getDeliveryStatus(packageId) == -1) {
            return false;
        }
        if (getDeliveryStatus(packageId) != status_created) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Package SET RequestAccepted = GETDATE(), Status=? WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, status_request_accepted);
            ps.setInt(2, packageId);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean rejectAnOffer(int packageId) {

        if (!packageExist(packageId)) {
            return false;
        }

        if (getDeliveryStatus(packageId) == -1) {
            return false;
        }
        if (getDeliveryStatus(packageId) != status_created) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Package SET Status=? WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, status_request_rejected);
            ps.setInt(2, packageId);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public List<Integer> getAllPackages() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdPackage FROM Package";
        List<Integer> list = new LinkedList<>();
        try (
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {

        List<Integer> list = new LinkedList<>();

        if (type != small_package
                && type != standard_package
                && type != nonstandard_package
                && type != fragile_package) {
            return list;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdPackage FROM Package WHERE Type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, type);
            try (ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdPackage FROM Package WHERE Status IN (?, ?)";
        List<Integer> list = new LinkedList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, status_request_accepted);
            stmt.setInt(2, status_pickedup);
            try (ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int cityId) {

        List<Integer> list = new LinkedList<>();

        dv160276_CityOperations city = new dv160276_CityOperations();
        if (!city.cityExist(cityId)) {
            return list;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdPackage FROM Package WHERE ReturnAddress IN (SELECT IdAddress FROM Address WHERE IdCity = ?) AND Status IN (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, cityId);
            stmt.setInt(2, status_request_accepted);
            stmt.setInt(3, status_pickedup);
            try (ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;

    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int idCity) {

        List<Integer> list = new LinkedList<>();

        dv160276_CityOperations city = new dv160276_CityOperations();
        if (!city.cityExist(idCity)) {
            return list;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query
                = "SELECT Package.IdPackage\n"
                + "FROM Package JOIN Address ON (Package.ReturnAddress = Address.IdAddress)\n"
                + "WHERE Status = 1 AND Address.IdCity = ?";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query
                = "SELECT Package.IdPackage\n"
                + "FROM Package JOIN Address ON (Package.DeliveryAddress = Address.IdAddress)\n"
                + "WHERE Status = 3 AND Address.IdCity = ?";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        dv160276_StockroomOperations stockroom = new dv160276_StockroomOperations();
        int idStockroom = stockroom.getStockroom(idCity);
        if (idStockroom == -1 || idStockroom == -2) {
            return list;
        }

        query
                = "SELECT IdPackage\n"
                + "FROM Package\n"
                + "WHERE Status = 2 AND IdPackage NOT IN (SELECT IdPackage FROM Cargo) AND Location = ?";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idStockroom);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;

    }

    @Override
    public boolean deletePackage(int idPackage) {

        if (!packageExist(idPackage)) {
            return false;
        }

        int packageStatus = getDeliveryStatus(idPackage);
        if (packageStatus == -1) {
            return false;
        }
        if (packageStatus != status_created && packageStatus != status_request_rejected) {
            return false;
        }

        return delete(idPackage);
    }

    @Override
    public boolean changeWeight(int idPackage, BigDecimal newWeight) {

        if (!packageExist(idPackage)) {
            return false;
        }

        int packageStatus = getDeliveryStatus(idPackage);
        if (packageStatus == -1) {
            return false;
        }
        if (packageStatus != status_created) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Package SET Weight = ? WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setBigDecimal(1, newWeight);
            ps.setInt(2, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeType(int idPackage, int newType) {

        if (!packageExist(idPackage)) {
            return false;
        }

        int packageStatus = getDeliveryStatus(idPackage);
        if (packageStatus == -1) {
            return false;
        }
        if (packageStatus != status_created) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Package SET Type = ? WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, newType);
            ps.setInt(2, idPackage);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public int getDeliveryStatus(int packageId) {

        if (!packageExist(packageId)) {
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Status FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    @Override
    public BigDecimal getPriceOfDelivery(int packageId) {

        if (!packageExist(packageId)) {
            return new BigDecimal(-1);
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Price FROM Package WHERE IdPackage = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(-1);

    }

    @Override
    public int getCurrentLocationOfPackage(int packageId) {

        int errorReturnValue = -2;

        if (!packageExist(packageId)) {
            return errorReturnValue;
        }

        int packageStatus = getDeliveryStatus(packageId);
        if (packageStatus == -1) {
            return errorReturnValue;
        }
        if (packageStatus != status_request_accepted
                && packageStatus != status_delivered
                && packageStatus != status_pickedup) {
            return errorReturnValue;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query
                = "SELECT *\n"
                + "FROM Cargo\n"
                + "WHERE IdPackage = (SELECT IdPackage FROM Package WHERE Status = 2 AND IdPackage = ?)";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return -1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return errorReturnValue;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return errorReturnValue;
        }

        switch (packageStatus) {
            case status_request_accepted:
                query
                        = "SELECT IdCity\n"
                        + "FROM Address\n"
                        + "Where IdAddress = (SELECT ReturnAddress FROM Package WHERE IdPackage = ?)";
                break;
            case status_delivered:
                query
                        = "SELECT IdCity\n"
                        + "FROM Address\n"
                        + "Where IdAddress = (SELECT DeliveryAddress FROM Package WHERE IdPackage = ?)";
                break;
            case status_pickedup:
                query
                        = "SELECT IdCity\n"
                        + "FROM Address\n"
                        + "Where IdAddress = (SELECT Location FROM Package WHERE IdPackage = ?)";
                break;
        }

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return errorReturnValue;

    }

    @Override
    public Date getAcceptanceTime(int packageId) {

        if (!packageExist(packageId)) {
            return null;
        }

        int packageStatus = getDeliveryStatus(packageId);
        if (packageStatus == -1) {
            return null;
        }
        if (packageStatus != status_request_accepted) {
            return null;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT RequestAccepted FROM Package WHERE IdPackage = ?";
        java.sql.Date date = null;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    date = rs.getDate(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;

    }

}
