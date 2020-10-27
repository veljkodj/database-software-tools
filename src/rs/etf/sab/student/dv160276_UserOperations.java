/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.util.regex.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_UserOperations implements UserOperations {

    public int getUserId(String username) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select IdUser from [user] where Username = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, username);
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

    public String getUsername(int idUser) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select Username from [user] where IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getCity(int idUser) {
        Connection conn = DB.getInstance().getConnection();
        String query
                = "SELECT [Address].IdCity\n"
                + "FROM [User] JOIN [Address] ON ([User].IdAddress = [Address].IdAddress)\n"
                + "WHERE [User].IdUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idUser);
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

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int idAddress) {

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM [User] WHERE Username = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, userName);
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

        if (Character.isLowerCase(firstName.charAt(0)) || Character.isLowerCase(lastName.charAt(0))) {
            return false;
        }

        query = "SELECT * FROM Address WHERE IdAddress = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, idAddress);
            try (ResultSet rs = ps.executeQuery();) {
                if (!rs.next()) {
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

        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._])[A-Za-z\\d@$!%*?&._]{8,}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);
        if (!m.find()) {
            return false;
        }
        query = "INSERT INTO [User] (FirstName, LastName, Username, [Password], [IdAddress]) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, userName);
            stmt.setString(4, password);
            stmt.setInt(5, idAddress);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean declareAdmin(String userName) {

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM [User] JOIN [Administrator] ON [User].IdUser = [Administrator].IdUser WHERE [User].Username = ?";
        try (PreparedStatement ps0 = conn.prepareStatement(query);) {
            ps0.setString(1, userName);
            try (ResultSet rs = ps0.executeQuery();) {
                if (rs.next()) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        int userId = getUserId(userName);
        if (userId == -1) {
            return false;
        }

        query = "INSERT INTO Administrator (IdUser) VALUES (?)";
        try (PreparedStatement ps1 = conn.prepareStatement(query);) {
            ps1.setInt(1, userId);
            ps1.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int getSentPackages(String... userNames) {

        int retErrorValue = -2;

        Connection conn = DB.getInstance().getConnection();
        String query;

        query = "SELECT * FROM [User] WHERE Username = ?";
        boolean userExist = false;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            for (String userName : userNames) {
                ps.setString(1, userName);
                try (ResultSet rs = ps.executeQuery();) {
                    if (rs.next()) {
                        userExist = true;
                        break;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                    return retErrorValue;
                }
            }
            if (!userExist) {
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return retErrorValue;
        }

        int cntrPackages = 0;
        query
                = "SELECT COUNT(Package.IdPackage)\n"
                + "FROM Package JOIN [User] ON (Package.IdUser = [User].IdUser)\n"
                + "WHERE [Package].Status = 3 AND [User].Username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            for (String userName : userNames) {
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery();) {
                    if (rs.next()) {
                        cntrPackages += rs.getInt(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cntrPackages;

    }

    @Override
    public int deleteUsers(String... usernames) {
        Connection conn = DB.getInstance().getConnection();
        String query = "DELETE FROM [User] WHERE Username = ?";
        int cntrDeletedRows = 0;
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            for (String username : usernames) {
                ps.setString(1, username);
                cntrDeletedRows += ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return cntrDeletedRows;
    }

    @Override
    public List<String> getAllUsers() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT Username FROM [User]";
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

}
