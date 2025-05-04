package org.example.Interfaces;

import org.example.entities.DemandeDonSang;
import java.util.List;

public interface IDemandeDonSangCrud extends ICrud<DemandeDonSang> {
    List<DemandeDonSang> findByUser(int userId);
    List<DemandeDonSang> findByCentre(int centreId);
    List<DemandeDonSang> findByStatus(String status);
    List<DemandeDonSang> findByBloodGroup(String groupeSanguin);
    DemandeDonSang updateStatus(int id, String status);
    int countByStatus(String status);
    double getTotalQuantity();
    double getTotalQuantityByBloodGroup(String groupeSanguin);
} 