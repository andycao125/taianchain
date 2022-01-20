package com.reddate.ddc.service;

import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountRole;
import com.reddate.ddc.dto.ddc.AccountState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class AuthorityServiceTest extends BaseServiceTest{

    String abi = ConfigCache.get().getAuthorityLogicABI();
    String bin = ConfigCache.get().getAuthorityLogicBIN();

    @Test
    public void getAccount() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        //AccountInfo accountInfo = authorityService.getAccount("0x179319b482320c74bE043bf0fb3F00411ca12F8d");
//		AccountInfo accountInfo = authorityService.getAccount("4bab66900062c2b13604324f572fafed28234f0a");
        AccountInfo accountInfo = authorityService.getAccount("0x81072375a506581CADBd90734Bd00A20CdDbE48b");
//		AccountInfo accountInfo = authorityService.getAccount("0x522bc3e4e29276A13f7b7BE9D404961826a82b11");
//		AccountInfo accountInfo = authorityService.getAccount("4bab66900062c2b13604324f572fafed28234f0a");
        // AccountInfo accountInfo = authorityService.getAccount("39db18cb303bce407bded5b0c082c3f193321374");
        System.out.println(accountInfo);
        assertNotNull(accountInfo);
    }


    @Test
    public void updateAccState() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        // String account = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
        String account = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";

        String txHash = authorityService.updateAccState(consumerAddress,account, AccountState.Active, false);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

}
