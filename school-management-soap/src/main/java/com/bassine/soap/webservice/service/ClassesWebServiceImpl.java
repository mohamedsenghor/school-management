package com.bassine.soap.webservice.service;

import com.bassine.metier.dto.ClassesDto;
import com.bassine.metier.service.IClassesService;
import com.bassine.metier.service.ClassesService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
import java.util.Optional;

@WebService(
    endpointInterface = "com.bassine.soap.webservice.service.ClassesWebService",
    serviceName = "ClassesWebService",
    portName = "ClassesWebServicePort",
    targetNamespace = "http://bassine.com/school-management"
)
public class ClassesWebServiceImpl implements ClassesWebService {

    private final IClassesService classesService = new ClassesService();

    @Override
    @WebMethod(operationName = "getClass")
    public ClassesDto getClass(@WebParam(name = "classId") Long classId) {
        try {
            if (classId == null) {
                return null;
            }
            return classesService.get(classId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "getAllClasses")
    public List<ClassesDto> getAllClasses() {
        try {
            return classesService.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "saveClass")
    public ClassesDto saveClass(@WebParam(name = "classe") ClassesDto classDto) {
        try {
            if (classDto == null) {
                return null;
            }
            
            boolean saved = classesService.save(classDto);
            if (saved) {
                // Récupérer la classe créée
                Optional<ClassesDto> createdClass = classesService.findByClassNameAndSector(
                    classDto.getClassName(), classDto.getSectorId());
                return createdClass.orElse(null);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "updateClass")
    public ClassesDto updateClass(@WebParam(name = "classe") ClassesDto classDto) {
        try {
            if (classDto == null || classDto.getId() == null) {
                return null;
            }
            
            boolean updated = classesService.update(classDto);
            if (updated) {
                return classesService.get(classDto.getId());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "deleteClass")
    public boolean deleteClass(@WebParam(name = "classId") Long classId) {
        try {
            if (classId == null) {
                return false;
            }
            return classesService.delete(classId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @WebMethod(operationName = "getClassesBySector")
    public List<ClassesDto> getClassesBySector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return List.of();
            }
            return classesService.getClassesBySector(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "searchClassesByName")
    public List<ClassesDto> searchClassesByName(@WebParam(name = "className") String className) {
        try {
            return classesService.searchByClassName(className);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "getClassesBySectorName")
    public List<ClassesDto> getClassesBySectorName(@WebParam(name = "sectorName") String sectorName) {
        try {
            return classesService.getClassesBySectorName(sectorName);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "existsClassInSector")
    public boolean existsClassInSector(@WebParam(name = "className") String className, 
                                     @WebParam(name = "sectorId") Long sectorId) {
        try {
            return classesService.existsByClassNameAndSector(className, sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
