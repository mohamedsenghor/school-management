package com.bassine.soap.webservice.service;

import com.bassine.metier.dto.SectorsDto;
import com.bassine.metier.service.ISectorsService;
import com.bassine.metier.service.SectorsService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
import java.util.Optional;

@WebService(
    endpointInterface = "com.bassine.soap.webservice.service.SectorsWebService",
    serviceName = "SectorsWebService",
    portName = "SectorsWebServicePort",
    targetNamespace = "http://bassine.com/school-management"
)
public class SectorsWebServiceImpl implements SectorsWebService {

    private final ISectorsService sectorsService = new SectorsService();

    @Override
    @WebMethod(operationName = "getSector")
    public SectorsDto getSector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return null;
            }
            return sectorsService.get(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "getAllSectors")
    public List<SectorsDto> getAllSectors() {
        try {
            return sectorsService.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "saveSector")
    public SectorsDto saveSector(@WebParam(name = "sector") SectorsDto sectorDto) {
        try {
            if (sectorDto == null) {
                return null;
            }
            
            boolean saved = sectorsService.save(sectorDto);
            if (saved) {
                // Récupérer le secteur créé par son nom
                Optional<SectorsDto> createdSector = sectorsService.findByName(sectorDto.getName());
                return createdSector.orElse(null);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "updateSector")
    public SectorsDto updateSector(@WebParam(name = "sector") SectorsDto sectorDto) {
        try {
            if (sectorDto == null || sectorDto.getId() == null) {
                return null;
            }
            
            boolean updated = sectorsService.update(sectorDto);
            if (updated) {
                return sectorsService.get(sectorDto.getId());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "deleteSector")
    public boolean deleteSector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return false;
            }
            return sectorsService.delete(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @WebMethod(operationName = "searchSectorsByName")
    public List<SectorsDto> searchSectorsByName(@WebParam(name = "name") String name) {
        try {
            return sectorsService.searchByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "getSectorWithClasses")
    public SectorsDto getSectorWithClasses(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return null;
            }
            return sectorsService.getSectorWithClasses(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "existsSectorByName")
    public boolean existsSectorByName(@WebParam(name = "name") String name) {
        try {
            return sectorsService.existsByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @WebMethod(operationName = "countClassesInSector")
    public long countClassesInSector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            return sectorsService.countClasses(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
