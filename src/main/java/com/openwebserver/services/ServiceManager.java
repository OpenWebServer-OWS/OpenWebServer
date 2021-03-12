package com.openwebserver.services;

import com.openwebserver.core.WebException;
import com.openwebserver.services.Objects.Service;

import java.util.HashMap;
import java.util.UUID;

public class ServiceManager extends HashMap<String, Service>{

    private static final ServiceManager manager = new ServiceManager();

    private ServiceManager() {}

    public static void register(Service service) {
        if(manager.containsKey(service.getName())){
            manager.put(service.getName()+ "@" + UUID.randomUUID().toString(), service);
        }else{
            manager.put(service.getName(), service);
        }
    }

    public static HashMap<String, Service> getServices(){
        System.out.println("WARNING: This is a cloned instance of the list");
        return (HashMap<String, Service>) manager.clone();
    }

    public static <T> T getService(Class<T> serviceClass) throws ServiceManagerException {
        for (String name : getInstance().keySet()) {
            Service s = getInstance().get(name);
            if(s.getClass().equals(serviceClass)){
                return (T) s;
            }
        }
        throw new ServiceManagerException.NotFoundException(serviceClass);
    }

    public static ServiceManager getInstance() {
        return manager;
    }

    public static class ServiceManagerException extends WebException {
        public ServiceManagerException(String message) {
            super(message);
        }

        public static class NotFoundException extends ServiceManagerException{
            public NotFoundException(Class<?> serviceClass){
                super("Can't find service with class '"+serviceClass.getSimpleName()+"'");
            }
        }
    }
}
