package model.service;

import java.util.List;
import model.dao.DAOFactory;
import model.dao.SellerDAO;
import model.domain.Seller;

/**
 *
 * @author joana
 */
public class SellerService {
    private SellerDAO sellerDAO = DAOFactory.createSellerDAO();
    
    public List<Seller> findAll(){
        return sellerDAO.findAll();
    }
    
    public void saveOrUpdate(Seller obj) {
        if (obj.getId() == null) {
            sellerDAO.insert(obj);
        }
        else {
            sellerDAO.update(obj);
        }
    }
    
    public void remove(Seller obj){
        sellerDAO.deleteById(obj.getId());
    }
}