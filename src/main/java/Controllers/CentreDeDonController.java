package Controllers;

import Crud.CentreDeDonCrud;
import Entities.CentreDeDon;
import java.util.Date;
import java.util.List;

public class CentreDeDonController {
    private CentreDeDonCrud centreCrud;
    
    public CentreDeDonController() {
        this.centreCrud = new CentreDeDonCrud();
    }
    
    // Create a new blood donation center
    public CentreDeDon createCentre(String name, String address, String telephone, String email, double latitude, double longitude) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telephone is required");
        }
        if (email != null && !email.isEmpty() && !isValidEmail(email)) {
            throw new IllegalArgumentException("Valid email is required");
        }
        
        // Create and add center
        CentreDeDon centre = new CentreDeDon(0, name, address, telephone, email, new Date(), latitude, longitude);
        return centreCrud.add(centre);
    }
    
    // CRUD operations
    public CentreDeDon getCentreById(int id) {
        return centreCrud.getById(id);
    }
    
    public List<CentreDeDon> getAllCentres() {
        return centreCrud.getAll();
    }
    
    public CentreDeDon updateCentre(CentreDeDon centre) {
        if (centre == null) {
            throw new IllegalArgumentException("Centre cannot be null");
        }
        return centreCrud.update(centre);
    }
    
    public boolean deleteCentre(int id) {
        return centreCrud.delete(id);
    }
    
    // Search operations
    public CentreDeDon findCentreByNameAndAddress(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        
        return centreCrud.findByNameAndAddress(name, address);
    }
    
    public List<CentreDeDon> searchCentresByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        
        return centreCrud.findByName(name);
    }
    
    // Geographic search
    public List<CentreDeDon> findNearestCentres(double latitude, double longitude, int limit) {
        if (limit <= 0) {
            limit = 5; // Default to 5 nearest centers
        }
        
        return centreCrud.findNearest(latitude, longitude, limit);
    }
    
    // Helper methods
    private boolean isValidEmail(String email) {
        // Basic email validation
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
} 