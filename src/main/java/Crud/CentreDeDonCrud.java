package Crud;

import Entities.CentreDeDon;
import Interfaces.ICentreDeDonCrud;
import Services.CentreDeDonService;
import java.util.List;

public class CentreDeDonCrud implements ICentreDeDonCrud {
    private CentreDeDonService centreService;
    
    public CentreDeDonCrud() {
        this.centreService = new CentreDeDonService();
    }
    
    @Override
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
    
    @Override
    public CentreDeDon getById(int id) {
        return centreService.getCentreById(id);
    }
    
    @Override
    public List<CentreDeDon> getAll() {
        return centreService.getAllCentres();
    }
    
    @Override
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
    
    @Override
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