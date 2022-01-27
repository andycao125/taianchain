package com.reddate.ddc.service;

import com.reddate.ddc.config.ConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
public class ChargeServiceTest extends BaseServiceTest{

	String abi = ConfigCache.get().getChargeLogicABI();
	String bin = ConfigCache.get().getChargeLogicBIN();

	@Test
	public void recharge() throws Exception {
		ChargeService chargeService = getChargeService();
		
//		String to = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
		String to = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";
		BigInteger amount = new BigInteger("300000");
		
		String txHash = chargeService.recharge(consumerAddress,to, amount);
		log.info(txHash);
		log.info(analyzeRecepit(txHash,abi,bin));
		assertNotNull(txHash);

	}
	
	
	@Test
	public void balanceOf() throws Exception {
		ChargeService chargeService = getChargeService();
		
//		String accAddr = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
		String accAddr = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";

		BigInteger amont = chargeService.balanceOf(accAddr);
		log.info(amont.toString());
		assertNotNull(amont);
	}

	
	@Test
	public void queryFee() throws Exception {
		ChargeService chargeService = getChargeService();

		String ddcAddr = ConfigCache.get().getDdc721Address();

		String sig = "0xf6dda936";
		
		BigInteger fee = chargeService.queryFee(ddcAddr, sig);
		assertNotNull(fee);
		log.info("result  {} ",fee);
	}
	
}
