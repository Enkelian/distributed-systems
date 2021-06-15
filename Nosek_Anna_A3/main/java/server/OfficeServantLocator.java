package server;

import TheOffice.DocumentResponse;
import TheOffice.ResidentRegistrationResponse;
import com.zeroc.Ice.*;
import com.zeroc.Ice.Object;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class OfficeServantLocator implements ServantLocator {

    private final ObjectAdapter adapter;

    public OfficeServantLocator(ObjectAdapter adapter){
        this.adapter = adapter;
    }


    @Override
    public LocateResult locate(Current current) throws UserException {

        String category = current.id.category;
        String name = current.id.name;

        OfficeI<?> servant =
                switch (name){
                    case "document" -> new OfficeI<DocumentResponse>(new ConcurrentSkipListSet<>(), new OfficeExecutor<>(10, new ConcurrentHashMap<>()));
                    case "resident" -> new OfficeI<ResidentRegistrationResponse>(new ConcurrentSkipListSet<>(), new OfficeExecutor<>(10, new ConcurrentHashMap<>()));
                    default -> throw new IllegalStateException("Unexpected value: " + name);
                };

        adapter.add(servant, new Identity(name, category));

        return new LocateResult(servant, null);
    }

    @Override
    public void finished(Current current, Object object, java.lang.Object o) throws UserException {

    }

    @Override
    public void deactivate(String s) {

    }

}
