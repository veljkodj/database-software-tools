/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Veljko
 */
public class dv160276_GeneralOperations implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
        String query = "EXEC sys.sp_msforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL' \n"
                + "EXEC sys.sp_msforeachtable 'DELETE FROM ?'\n"
                + "EXEC sys.sp_MSForEachTable 'ALTER TABLE ? CHECK CONSTRAINT ALL' ";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.execute();
        } catch (SQLException ex) {
            Logger.getLogger(dv160276_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
