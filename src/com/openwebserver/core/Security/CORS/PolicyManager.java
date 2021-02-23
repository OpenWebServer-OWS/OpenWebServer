package com.openwebserver.core.Security.CORS;

import com.openwebserver.core.WebException;

import java.util.HashMap;

public class PolicyManager {

    private static final PolicyManager manager = new PolicyManager();
    private final HashMap<String, Policy> policies = new HashMap<>();

    static {
        PolicyManager.Register(new Policy("none"));
    }

    public static Policy getPolicy(String name) throws PolicyException.NotFound {
        Policy p = getInstance().policies.get(name);
        if(p != null){
            return p;
        }else{
            throw new PolicyException.NotFound(name);
        }
    }

    public static PolicyManager getInstance(){
        return manager;
    }

    public static void Register(Policy policy){
        getInstance().policies.put(policy.getName(), policy);
    }

    public static class PolicyException extends WebException {
        public PolicyException(String message){
            super(message);
        }

        public static class NotFound extends PolicyException{

            public NotFound(String name) {
                super("Policy not found with name '"+name+"'");
            }
        }
    }
}
