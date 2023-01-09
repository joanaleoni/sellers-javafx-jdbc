package model.dao;

import model.dao.impl.DepartmentDAOJDBC;
import model.dao.impl.SellerDAOJDBC;
import model.database.Database;

/**
 *
 * @author joana
 */
public class DAOFactory {
    public static SellerDAO createSellerDAO(){
        return new SellerDAOJDBC(Database.getConnection());
    }
    
    public static DepartmentDAO createDepartmentDAO(){
        return new DepartmentDAOJDBC(Database.getConnection());
    }
}