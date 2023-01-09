package model.dao;

import java.util.List;
import model.domain.Department;

/**
 *
 * @author joana
 */
public interface DepartmentDAO {
    void insert(Department department);
    void update(Department department);
    void deleteById(Integer id);
    Department findById(Integer id);
    List<Department> findAll();
}