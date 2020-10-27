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
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_CourierOperations implements CourierOperations {

    public static final int relax = 0;
    public static final int drive = 1;

    public boolean isCourier(int idCourier) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM Courier WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCourier);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int changeStatus(int idCourier, int newStatus) {

        if (newStatus != drive && newStatus != relax) {
            return -1;
        }

        if (!isCourier(idCourier)) {
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "UPDATE Courier SET Status = ? WHERE IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, newStatus);
            ps.setInt(2, idCourier);
            ps.execute();
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public boolean insertCourier(String courierUserName, String licencePlateNumber) {

        Connection conn = DB.getInstance().getConnection();
        String query;

        dv160276_UserOperations user = new dv160276_UserOperations();
        int courierId = user.getUserId(courierUserName);
        if (courierId == -1) {
            return false;
        }

        query = "SELECT * FROM Courier WHERE IdUser = ? OR LicenseNumber = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setInt(1, courierId);
            ps0.setString(2, licencePlateNumber);
            try (ResultSet rs = ps0.executeQuery();) {
                if (rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "INSERT INTO Courier (IdUser, PackagesDelivered, Profit, [Status], LicenseNumber) VALUES (?, 0, 0.000, 0, ?)";
        try (PreparedStatement ps1 = conn.prepareStatement(query);) {
            ps1.setInt(1, courierId);
            ps1.setString(2, licencePlateNumber);
            ps1.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean deleteCourier(String courierUserName) {

        dv160276_UserOperations user = new dv160276_UserOperations();
        int courierId = user.getUserId(courierUserName);
        if (courierId == -1) {
            return false;
        }

        if (!isCourier(courierId)) {
            return false;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "DELETE FROM Courier WHERE IdUser = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, courierId);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int statusOfCourier) {
        if (statusOfCourier < 0 || statusOfCourier > 1) {
            return null;
        }
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT [User].Username FROM Courier JOIN [User] ON (Courier.IdUser=[User].IdUser) WHERE [Courier].Status = ?";
        List<String> list = new LinkedList<>();
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, statusOfCourier);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<String> getAllCouriers() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT [User].Username FROM Courier JOIN [User] ON (Courier.IdUser=[User].IdUser) ORDER BY Profit DESC";
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
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        Connection conn = DB.getInstance().getConnection();
        String query;
        BigDecimal ret;
        if (numberOfDeliveries == -1) {
            query = "SELECT AVG(Profit) FROM [Courier]";
            try (
                    PreparedStatement ps = conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getBigDecimal(1);
                    if (ret != null) {
                        return ret;
                    } else {
                        return new BigDecimal(0);
                    }
                } else {
                    return new BigDecimal(0);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            query = "SELECT AVG(Profit) FROM [Courier] WHERE PackagesDelivered = ?";
            try (PreparedStatement ps = conn.prepareStatement(query);) {
                ps.setInt(1, numberOfDeliveries);
                try (ResultSet rs = ps.executeQuery();) {
                    if (rs.next()) {
                        ret = rs.getBigDecimal(1);
                        if (ret != null) {
                            return ret;
                        } else {
                            return new BigDecimal(0);
                        }
                    } else {
                        return new BigDecimal(0);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

}
