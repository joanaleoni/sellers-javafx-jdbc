package model.service;

import java.util.List;
import model.dao.DAOFactory;
import model.dao.DepartmentDAO;
import model.domain.Department;

/**
 *
 * @author joana
 */
public class DepartmentService {
    private DepartmentDAO departmentDAO = DAOFactory.createDepartmentDAO();
    
    public List<Department> findAll(){
        return departmentDAO.findAll();
    }
    
    public void saveOrUpdate(Department obj) {
        if (obj.getId() == null) {
            departmentDAO.insert(obj);
        }
        else {
            departmentDAO.update(obj);
        }
    }
}