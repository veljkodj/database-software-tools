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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_CityOperations implements CityOperations {

    public boolean cityExist(int idCity) {

        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM City WHERE IdCity = ?";

        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
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

    @Override
    public int insertCity(String name, String postalCode) {

        Connection conn = DB.getInstance().getConnection();

        String query = "SELECT * FROM City WHERE PostalCode = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setString(1, postalCode);
            try (ResultSet rs = ps0.executeQuery();) {
                if (rs.next()) {
                    return -1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        query = "INSERT INTO City (Name, PostalCode) VALUES (?, ?)";
        try (PreparedStatement ps1 = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps1.setString(1, name);
            ps1.setString(2, postalCode);
            ps1.executeUpdate();
            ResultSet rs = ps1.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    @Override
    public int deleteCity(String... names) {

        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM City WHERE Name = ?";
        int cntrDeletedRows = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            for (String name : names) {
                ps.setString(1, name);
                cntrDeletedRows += ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cntrDeletedRows;

    }

    @Override
    public boolean deleteCity(int idCity) {

        Connection conn = DB.getInstance().getConnection();

        String query = "SELECT * FROM City WHERE IdCity = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setInt(1, idCity);
            try (ResultSet rs = ps0.executeQuery();) {
                if (!rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        query = "DELETE FROM City WHERE IdCity = ?";
        try (PreparedStatement ps1 = conn.prepareStatement(query);) {
            ps1.setInt(1, idCity);
            return ps1.executeUpdate() == 1;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public List<Integer> getAllCities() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdCity FROM City";
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

}
