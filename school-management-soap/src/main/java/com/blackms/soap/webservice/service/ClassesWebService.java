package dev.blackms.soap.webservice.service;

import dev.blackms.metier.dto.ClassesDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(name = "ClassesWebService", targetNamespace = "http://blackms.dev/school-management")
public interface ClassesWebService {

    @WebMethod(operationName = "getClass")
    ClassesDto getClass(@WebParam(name = "classId") Long classId);

    @WebMethod(operationName = "getAllClasses")
    List<ClassesDto> getAllClasses();

    @WebMethod(operationName = "saveClass")
    ClassesDto saveClass(@WebParam(name = "classe") ClassesDto classDto);

    @WebMethod(operationName = "updateClass")
    ClassesDto updateClass(@WebParam(name = "classe") ClassesDto classDto);

    @WebMethod(operationName = "deleteClass")
    boolean deleteClass(@WebParam(name = "classId") Long classId);

    @WebMethod(operationName = "getClassesBySector")
    List<ClassesDto> getClassesBySector(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "searchClassesByName")
    List<ClassesDto> searchClassesByName(@WebParam(name = "className") String className);

    @WebMethod(operationName = "getClassesBySectorName")
    List<ClassesDto> getClassesBySectorName(@WebParam(name = "sectorName") String sectorName);

    @WebMethod(operationName = "existsClassInSector")
    boolean existsClassInSector(@WebParam(name = "className") String className,
            @WebParam(name = "sectorId") Long sectorId);
}
