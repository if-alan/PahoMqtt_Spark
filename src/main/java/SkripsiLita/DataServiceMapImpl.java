/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkripsiLita;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author if_alan
 */
public class DataServiceMapImpl implements DataService {
    private HashMap<String, Data> userMap;

    public DataServiceMapImpl() {
        userMap = new HashMap<>();
    }

    @Override
    public void addService(Data user) {
        userMap.put(user.getVoltage(), user);
    }

    @Override
    public Collection<Data> getDataCollection() {
        return userMap.values();
    }

    @Override
    public Data getData(String id) {
        return userMap.get(id);
    }

    @Override
    public Data editData(Data forEdit) throws DataException {
        try {
            if (forEdit.getVoltage() == null)
                throw new DataException("ID cannot be blank");

            Data toEdit = userMap.get(forEdit.getVoltage());

            if (toEdit == null)
                throw new DataException("User not found");

            if (forEdit.getKwh() != null) {
                toEdit.setKwh(forEdit.getKwh());
            }
            if (forEdit.getCurrent() != null) {
                toEdit.setCurrent(forEdit.getCurrent());
            }
            if (forEdit.getPower() != null) {
                toEdit.setPower(forEdit.getPower());
            }
            if (forEdit.getVoltage() != null) {
                toEdit.setVoltage(forEdit.getVoltage());
            }

            return toEdit;
        } catch (Exception ex) {
            throw new DataException(ex.getMessage());
        }
    }

    @Override
    public void deleteData(String id) {
        userMap.remove(id);
    }

    @Override
    public boolean dataExist(String id) {
        return userMap.containsKey(id);
    }
}