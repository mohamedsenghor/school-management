package com.bassine.soap.webservice.service;

import com.bassine.metier.dto.SectorsDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(name = "SectorsWebService", targetNamespace = "http://bassine.com/school-management")
public interface SectorsWebService {

    @WebMethod(operationName = "getSector")
    SectorsDto getSector(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "getAllSectors")
    List<SectorsDto> getAllSectors();

    @WebMethod(operationName = "saveSector")
    SectorsDto saveSector(@WebParam(name = "sector") SectorsDto sectorDto);

    @WebMethod(operationName = "updateSector")
    SectorsDto updateSector(@WebParam(name = "sector") SectorsDto sectorDto);

    @WebMethod(operationName = "deleteSector")
    boolean deleteSector(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "searchSectorsByName")
    List<SectorsDto> searchSectorsByName(@WebParam(name = "name") String name);

    @WebMethod(operationName = "getSectorWithClasses")
    SectorsDto getSectorWithClasses(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "existsSectorByName")
    boolean existsSectorByName(@WebParam(name = "name") String name);

    @WebMethod(operationName = "countClassesInSector")
    long countClassesInSector(@WebParam(name = "sectorId") Long sectorId);
}
