/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author Veljko
 */
public class dv160276_CourierRequestOperation implements CourierRequestOperation {

    @Override
    public boolean insertCourierRequest(String userName, String driverLicenceNumber) {

        dv160276_UserOperations user = new dv160276_UserOperations();
        int userId = user.getUserId(userName);
        if (userId == -1) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM CourierRequest WHERE LicenseNumber = ? OR IdUser = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setString(1, driverLicenceNumber);
            ps0.setInt(2, userId);
            try (ResultSet rs = ps0.executeQuery();) {
                if (rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "SELECT * FROM Courier WHERE IdUser = ?";
        try (PreparedStatement ps1 = conn.prepareStatement(query);) {
            ps1.setInt(1, userId);
            try (ResultSet rs = ps1.executeQuery();) {
                if (rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "INSERT INTO CourierRequest (IdUser, LicenseNumber) VALUES (?,?)";
        try (PreparedStatement ps2 = conn.prepareStatement(query);) {
            ps2.setInt(1, userId);
            ps2.setString(2, driverLicenceNumber);
            ps2.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean deleteCourierRequest(String userName) {

        dv160276_UserOperations user = new dv160276_UserOperations();
        int userId = user.getUserId(userName);
        if (userId == -1) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM CourierRequest WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery();) {
                if (!rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "DELETE FROM CourierRequest WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, userId);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String userName, String driverLicenceNumber) {

        dv160276_UserOperations user = new dv160276_UserOperations();
        int userId = user.getUserId(userName);
        if (userId == -1) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM CourierRequest WHERE IdUser = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setInt(1, userId);
            try (ResultSet rs = ps0.executeQuery();) {
                if (!rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "UPDATE CourierRequest SET LicenseNumber = ? WHERE IdUser = ?";
        try (PreparedStatement ps3 = conn.prepareStatement(query);) {
            ps3.setString(1, driverLicenceNumber);
            ps3.setInt(2, userId);
            ps3.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public boolean changeVehicleInCourierRequest(String userName, String licencePlateNumber) {
        dv160276_UserOperations user = new dv160276_UserOperations();
        int userId = user.getUserId(userName);
        if (userId == -1) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE CourierRequest SET LicenseNumber = ? WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, licencePlateNumber);
            ps.setInt(2, userId);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdUser FROM CourierRequest";
        List<String> list = new LinkedList<>();
        dv160276_UserOperations user = new dv160276_UserOperations();
        int userId;
        String username;
        try (
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                username = user.getUsername(rs.getInt(1));
                if (username == null) {
                    return null;
                }
                list.add(username);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public boolean grantRequest(String username) {

        dv160276_UserOperations user = new dv160276_UserOperations();
        String licenseNumber;
        int userId = user.getUserId(username);
        if (userId == -1) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM CourierRequest WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery();) {
                if (!rs.next()) {
                    return false;
                } else {
                    licenseNumber = rs.getString(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "DELETE FROM CourierRequest WHERE IdUser = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, userId);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        dv160276_CourierOperations courier = new dv160276_CourierOperations();
        return courier.insertCourier(username, licenseNumber);

    }

}
