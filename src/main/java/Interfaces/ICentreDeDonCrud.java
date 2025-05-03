package Interfaces;

import Entities.CentreDeDon;
import java.util.List;

public interface ICentreDeDonCrud extends ICrud<CentreDeDon> {
    CentreDeDon findByNameAndAddress(String name, String address);
    List<CentreDeDon> findByName(String name);
    List<CentreDeDon> findNearest(double latitude, double longitude, int limit);
} 