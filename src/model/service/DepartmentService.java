package model.service;

import java.util.ArrayList;
import java.util.List;
import model.domain.Department;

/**
 *
 * @author joana
 */
public class DepartmentService {
    public List<Department> findAll(){
        List<Department> deps = new ArrayList<>();
        deps.add(new Department(1, "Books"));
        deps.add(new Department(2, "Computers"));
        deps.add(new Department(3, "Eletronics"));
        return deps;
    }
}
