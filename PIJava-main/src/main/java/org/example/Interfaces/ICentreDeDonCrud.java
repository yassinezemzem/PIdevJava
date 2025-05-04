package org.example.Interfaces;

import org.example.entities.CentreDeDon;
import java.util.List;

public interface ICentreDeDonCrud extends org.example.Interfaces.ICrud<CentreDeDon> {
    CentreDeDon findByNameAndAddress(String name, String address);
    List<CentreDeDon> findByName(String name);
    List<CentreDeDon> findNearest(double latitude, double longitude, int limit);
} 