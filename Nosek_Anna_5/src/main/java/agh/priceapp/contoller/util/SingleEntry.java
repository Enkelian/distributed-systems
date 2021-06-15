package agh.priceapp.contoller.util;

;
import agh.priceapp.model.DealInfo;
import agh.priceapp.model.Store;

public class SingleEntry {

    private DealInfo dealInfo;
    private Store store;

    public SingleEntry(DealInfo DealInfo, Store store){
        this.dealInfo = DealInfo;
        this.store = store;
    }

    public DealInfo getDealInfo() {
        return dealInfo;
    }

    public void setDeal(DealInfo DealInfo) {
        this.dealInfo = DealInfo;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }


}
