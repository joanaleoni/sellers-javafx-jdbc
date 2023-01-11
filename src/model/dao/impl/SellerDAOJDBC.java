package model.dao.impl;

import exception.DatabaseException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.dao.SellerDAO;
import model.database.Database;
import model.domain.Department;
import model.domain.Seller;

/**
 *
 * @author joana
 */
public class SellerDAOJDBC implements SellerDAO {

    private Connection conn;

    public SellerDAOJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller seller) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "INSERT INTO seller " +
                    "(name, email, birth_date, base_salary, department_id) " +
                    "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail());
            st.setDate(3, new Date(seller.getBirthDate().getTime()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());
            
            int affectedRows = st.executeUpdate();
            if(affectedRows > 0){
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    seller.setId(id);
                }
                Database.closeResultSet(rs);
            } else {
                throw new DatabaseException("Unexpected error! No rows affected.");
            }
        } catch(SQLException e){
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
        }
    }

    @Override
    public void update(Seller seller) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE seller " +
                    "SET name=?, email=?, birth_date=?, base_salary=?, department_id=? " +
                    "WHERE id=?");
            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail());
            st.setDate(3, new Date(seller.getBirthDate().getTime()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());
            st.setInt(6, seller.getId());
            st.executeUpdate();   
        } catch(SQLException e){
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM seller WHERE id=?");
            st.setInt(1, id);
            int affectedRows = st.executeUpdate();
            
            if(affectedRows == 0){
                throw new DatabaseException("There is no seller with this id. No rows affected.");
            }
        } catch(SQLException e){
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.name as dep_name "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.department_id=department.id "
                    + "WHERE seller.id=?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                Department dep = populateVODepartment(rs);
                Seller seller = populateVOSeller(rs, dep);
                return seller;
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
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();
        Map<Integer, Department> map = new HashMap<>();
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.name as dep_name " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.department_id = department.id " +
                    "ORDER BY name");
            rs = st.executeQuery();
            while (rs.next()) {
                Department dep = map.get(rs.getInt("department_id"));
                if(dep == null){
                    dep = populateVODepartment(rs);
                    map.put(rs.getInt("department_id"), dep);
                }
                Seller seller = populateVOSeller(rs, dep);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();
        Map<Integer, Department> map = new HashMap<>();
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.name as dep_name " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.department_id = department.id " +
                    "WHERE department_id = ? " +
                    "ORDER BY name");
            st.setInt(1, department.getId());
            rs = st.executeQuery();
            while (rs.next()) {
                Department dep = map.get(rs.getInt("department_id"));
                if(dep == null){
                    dep = populateVODepartment(rs);
                    map.put(rs.getInt("department_id"), dep);
                }
                Seller seller = populateVOSeller(rs, dep);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }

    private Department populateVODepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("department_id"));
        dep.setName(rs.getString("dep_name"));
        return dep;
    }
    
    private Seller populateVOSeller(ResultSet rs, Department dep) throws SQLException{
        Seller seller = new Seller();
        seller.setId(rs.getInt("id"));
        seller.setName(rs.getString("name"));
        seller.setEmail(rs.getString("email"));
        seller.setBaseSalary(rs.getDouble("base_salary"));
        seller.setBirthDate(new java.util.Date(rs.getTimestamp("birth_date").getTime())); 
        seller.setDepartment(dep);
        return seller;
    }
}