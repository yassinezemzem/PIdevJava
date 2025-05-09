package Crud;

import Entities.DemandeDonSang;
import Interfaces.IDemandeDonSangCrud;
import Services.CentreDeDonService;
import Services.DemandeDonSangService;
import Services.UserService;
import java.util.List;

public class DemandeDonSangCrud implements IDemandeDonSangCrud {
    private final DemandeDonSangService demandeService;
    private final UserService userService;
    
    public DemandeDonSangCrud() {
        // Create the dependency services
        this.demandeService = new DemandeDonSangService();
        this.userService = new UserService();
    }
    
    @Override
    public DemandeDonSang add(DemandeDonSang demande) {
        return demandeService.createDemande(
            demande.getUser().getId(),
            demande.getCentreDeDon().getId(),
            demande.getGroupeSanguin(),
            demande.getQuantite()
        );
    }
    
    @Override
    public DemandeDonSang getById(int id) {
        return demandeService.getDemandeById(id);
    }
    
    @Override
    public List<DemandeDonSang> getAll() {
        return demandeService.getAllDemandes();
    }
    
    @Override
    public DemandeDonSang update(DemandeDonSang demande) {
        return demandeService.updateDemande(
            demande.getId(),
            demande.getGroupeSanguin(),
            demande.getQuantite()
        );
    }
    
    @Override
    public boolean delete(int id) {
        return demandeService.deleteDemande(id);
    }
    
    @Override
    public List<DemandeDonSang> findByUser(int userId) {
        return demandeService.getDemandesByUserId(userId);
    }
    
    @Override
    public List<DemandeDonSang> findByCentre(int centreId) {
        return demandeService.getDemandesByCentre(centreId);
    }
    
    @Override
    public List<DemandeDonSang> findByStatus(String status) {
        return demandeService.getDemandesByStatus(status);
    }
    
    @Override
    public List<DemandeDonSang> findByBloodGroup(String groupeSanguin) {
        return demandeService.getDemandesByBloodGroup(groupeSanguin);
    }
    
    @Override
    public DemandeDonSang updateStatus(int id, String status) {
        return demandeService.updateDemandeStatus(id, status);
    }
    
    @Override
    public int countByStatus(String status) {
        return demandeService.countDemandesByStatus(status);
    }
    
    @Override
    public double getTotalQuantity() {
        return demandeService.getTotalDonationQuantity();
    }
    
    @Override
    public double getTotalQuantityByBloodGroup(String groupeSanguin) {
        return demandeService.getTotalDonationQuantityByBloodGroup(groupeSanguin);
    }
} 