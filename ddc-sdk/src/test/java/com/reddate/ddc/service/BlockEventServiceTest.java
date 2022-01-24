package com.reddate.ddc.service;

import com.reddate.ddc.DDCSdkClient;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

@Slf4j
class BlockEventServiceTest {
    static {
        new DDCSdkClient("https://opbtest.bsngate.com:18602/api/Fiscorpc/rpc");
    }

    BlockEventService blockEventService = new BlockEventService();

    @Test
    void getBlockEvent() throws BaseException, IOException, InterruptedException {
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 588094; i < 588095; i++) {
            result.addAll(blockEventService.getBlockEvent(String.valueOf(i)));
        }
        log.info("");
        log.info("");
        log.info("");

        result.forEach( t -> {
            log.info("{}:{}",t.getClass(),t);
        });

    }
}