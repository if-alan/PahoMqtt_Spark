/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkripsiLita;

import java.util.Collection;

/**
 *
 * @author if_alan
 */
public interface DataService {

    public void addService (Data user);
     
    public Collection<Data> getDataCollection ();
    
    public Data getData (String id);
     
    public Data editData (Data user) throws DataException;
     
    public void deleteData (String id);
     
    public boolean dataExist (String id);
}