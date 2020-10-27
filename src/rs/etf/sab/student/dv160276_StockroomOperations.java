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
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_StockroomOperations implements StockroomOperations {

    public boolean stockroomExsits(int stockroom) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM Warehouse WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, stockroom);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean stockroomEmpty(int stockroom) {

        Connection conn = DB.getInstance().getConnection();

        String query
                = "SELECT *\n"
                + "FROM Package\n"
                + "WHERE Status = 2 AND Location = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, stockroom);
            try (ResultSet rs = ps.executeQuery();) {
                return !rs.next();
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean delete(int idStockroom) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM Warehouse WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idStockroom);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getStockroom(int idCity) {

        dv160276_CityOperations city = new dv160276_CityOperations();
        if (!city.cityExist(idCity)) {
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Warehouse.IdAddress FROM Warehouse JOIN Address ON (Warehouse.IdAddress = Address.IdAddress) WHERE Address.IdCity = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return -1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -2;
    }

    @Override
    public int insertStockroom(int address) {

        dv160276_AddressOperations addr = new dv160276_AddressOperations();

        if (!addr.addressExsits(address)) {
            return -1;
        }

        int idCity = addr.getCity(address);
        if (idCity == -1) {
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();
        String query;

        query
                = "SELECT COUNT(Warehouse.IdAddress)\n"
                + "FROM Warehouse JOIN Address ON (Warehouse.IdAddress = Address.IdAddress)\n"
                + "WHERE Address.IdCity = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
            try (ResultSet rs = ps.executeQuery();) {
                if (!rs.next()) {
                    return -1;
                } else if (rs.getInt(1) != 0) {
                    return -1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        query = "INSERT INTO Warehouse (IdAddress) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, address);
            stmt.execute();
            return address;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    @Override
    public boolean deleteStockroom(int idStockroom) {

        dv160276_AddressOperations address = new dv160276_AddressOperations();
        if (!address.addressExsits(idStockroom)) {
            return false;
        }

        if (!stockroomExsits(idStockroom)) {
            return false;
        }

        if (!stockroomEmpty(idStockroom)) {
            return false;
        }

        return delete(idStockroom);

    }

    @Override
    public int deleteStockroomFromCity(int idCity) {

        int idStockroom = getStockroom(idCity);
        if (idStockroom == -1 || idStockroom == -2) {
            return -1;
        }

        if (!stockroomEmpty(idStockroom)) {
            return -1;
        }

        return delete(idStockroom) ? idStockroom : -1;

    }

    @Override
    public List<Integer> getAllStockrooms() {
        Connection conn = DB.getInstance().getConnection();
        String query = "select IdAddress from Warehouse";
        List<Integer> list = new LinkedList<>();
        try (
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

}
