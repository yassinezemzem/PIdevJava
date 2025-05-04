package org.example.Crud;

import org.example.entities.CentreDeDon;
import org.example.Interfaces.ICentreDeDonCrud;
import org.example.service.CentreDeDonService;
import java.util.List;

public class CentreDeDonCrud implements ICentreDeDonCrud {
    private CentreDeDonService centreService;
    
    public CentreDeDonCrud() {
        this.centreService = new CentreDeDonService();
    }
    

    public CentreDeDon add(CentreDeDon centre) {
        return centreService.createCentre(
            centre.getName(),
            centre.getAddress(),
            centre.getTelephone(),
            centre.getEmail(),
            centre.getLatitude(),
            centre.getLongitude()
        );
    }
    

    public CentreDeDon getById(int id) {
        return centreService.getCentreById(id);
    }
    

    public List<CentreDeDon> getAll() {
        return centreService.getAllCentres();
    }
    

    public CentreDeDon update(CentreDeDon centre) {
        return centreService.updateCentre(
            centre.getId(),
            centre.getName(),
            centre.getAddress(),
            centre.getTelephone(),
            centre.getEmail(),
            centre.getLatitude(),
            centre.getLongitude()
        );
    }
    

    public boolean delete(int id) {
        return centreService.deleteCentre(id);
    }
    
    @Override
    public CentreDeDon findByNameAndAddress(String name, String address) {
        return centreService.findCentreByNameAndAddress(name, address);
    }
    
    @Override
    public List<CentreDeDon> findByName(String name) {
        return centreService.findCentresByName(name);
    }
    
    @Override
    public List<CentreDeDon> findNearest(double latitude, double longitude, int limit) {
        return centreService.findNearestCentres(latitude, longitude, limit);
    }
} 