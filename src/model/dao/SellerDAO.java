package model.dao;

import java.util.List;
import model.domain.Department;
import model.domain.Seller;

/**
 *
 * @author joana
 */
public interface SellerDAO {
    void insert(Seller seller);
    void update(Seller seller);
    void deleteById(Integer id);
    Seller findById(Integer id);
    List<Seller> findAll();
    List<Seller> findByDepartment(Department department);
}