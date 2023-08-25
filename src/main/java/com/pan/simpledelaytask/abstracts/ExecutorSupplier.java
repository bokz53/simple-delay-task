package com.pan.simpledelaytask.abstracts;

import java.util.concurrent.ExecutorService;

public interface ExecutorSupplier {
    
    public ExecutorService getExecutor();

}
