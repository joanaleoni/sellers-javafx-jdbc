package model.dao.impl;

import exception.DatabaseException;
import exception.DatabaseIntegrityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.dao.DepartmentDAO;
import model.database.Database;
import model.domain.Department;

/**
 *
 * @author joana
 */
public class DepartmentDAOJDBC implements DepartmentDAO {
    private Connection conn;

    public DepartmentDAOJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("SELECT * FROM department WHERE id=?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                Department dep = new Department();
                dep.setId(rs.getInt("id"));
                dep.setName(rs.getString("name"));
                return dep;
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Department> deps = new ArrayList<>();
        try {
            st = conn.prepareStatement(
                    "SELECT * FROM department ORDER BY name");
            rs = st.executeQuery();
            
            while (rs.next()) {
                Department dep = new Department();
                dep.setId(rs.getInt("id"));
                dep.setName(rs.getString("name"));
                deps.add(dep);
            }
            return deps;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }

    @Override
    public void insert(Department department) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "INSERT INTO department (name) "
                    + "VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, department.getName());

            int affectedRows = st.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    department.setId(id);
                }
            } else {
                throw new DatabaseException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE department "
                    + "SET name=? "
                    + "WHERE id=?");

            st.setString(1, department.getName());
            st.setInt(2, department.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM department WHERE id=?");
            st.setInt(1, id);
            int affectedRows = st.executeUpdate();
            
            if(affectedRows == 0){
                throw new DatabaseException("There is no department with this id. No rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseIntegrityException(e.getMessage());
        } finally {
            Database.closeStatement(st);
        }
    }
}