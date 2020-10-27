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
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_AddressOperations implements AddressOperations {

    public int getXCord(int id) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select x from Address where IdAddress = ?";
        int ret = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public int getYCord(int id) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select y from Address where IdAddress = ?";
        int ret = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public int getCity(int idAddress) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdCity FROM Address WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idAddress);
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

    public boolean addressExsits(int address) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM Address WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, address);
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
    
    @Override
    public int insertAddress(String street, int number, int cityId, int xCord, int yCord) {

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM City WHERE IdCity = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setInt(1, cityId);
            try (ResultSet rs = ps0.executeQuery();) {
                if (!rs.next()) {
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

        query = "INSERT INTO Address (Street, Number, x, y, IdCity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, street);
            ps.setInt(2, number);
            ps.setInt(3, xCord);
            ps.setInt(4, yCord);
            ps.setInt(5, cityId);
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
    public int deleteAddresses(String name, int number) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM Address WHERE Street = ? AND Number = ?";
        int cntrDeletedRows = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, name);
            ps.setInt(2, number);
            cntrDeletedRows = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cntrDeletedRows;
    }

    @Override
    public boolean deleteAdress(int idAddress) {

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM Address WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idAddress);
            try (ResultSet rs = ps.executeQuery();) {
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

        query = "DELETE FROM Address WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idAddress);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public int deleteAllAddressesFromCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM Address WHERE IdAddress IN (SELECT Address.IdAddress FROM City JOIN Address ON City.IdCity = Address.IdCity WHERE City.IdCity = ?)";
        int cntrDeletedRows = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idCity);
            cntrDeletedRows = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cntrDeletedRows;
    }

    @Override
    public List<Integer> getAllAddresses() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdAddress FROM Address";
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
    public List<Integer> getAllAddressesFromCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT * FROM City WHERE IdCity = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCity);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() == false) {
                    return null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "SELECT IdAddress FROM Address WHERE IdCity = ?";
        List<Integer> list = new LinkedList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, idCity);
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

}
