package com.reddate.ddc.service;

import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.constant.AuthorityFunctions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountRole;
import com.reddate.ddc.dto.ddc.AccountState;
import com.reddate.ddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.util.AddressUtils;
import com.reddate.ddc.util.HexUtils;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.abi.datatypes.*;
import org.fisco.bcos.web3j.abi.datatypes.generated.Bytes4;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.tx.txdecode.ResultEntity;
import org.fisco.bcos.web3j.utils.Strings;

import java.util.*;

@Slf4j
public class AuthorityService extends BaseService {

    public AuthorityService(SignEventListener signEventListener) {
        super.signEventListener = signEventListener;
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息的查询，上级角色可进行下级角色账户的操作。
     *
     * @param account DDC用户链账户地址
     * @return 返回DDC账户信息
     * @throws Exception
     */
    public AccountInfo getAccount(String account) throws Exception {

        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(ZeroAddress, AuthorityFunctions.GetAccount, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        InputAndOutputResult inputAndOutputResult = analyzeTransactionRecepitOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), (String) respJsonRpcBean.getResult());
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountDID(String.valueOf(inputAndOutputResult.getResult().get(0).getData()));
        accountInfo.setAccountName(String.valueOf(inputAndOutputResult.getResult().get(1).getData()));
        accountInfo.setLeaderDID(String.valueOf(inputAndOutputResult.getResult().get(3).getData()));
        accountInfo.setField(String.valueOf(inputAndOutputResult.getResult().get(6).getData()));
        String accountRole = String.valueOf(inputAndOutputResult.getResult().get(2).getData());
        if (accountRole != null && !accountRole.trim().isEmpty()) {
            accountInfo.setAccountRole(AccountRole.getByVal(Integer.parseInt(accountRole)));
        }
        String platformState = String.valueOf(inputAndOutputResult.getResult().get(4).getData());
        if (platformState != null && !platformState.trim().isEmpty()) {
            accountInfo.setPlatformState(AccountState.getByVal(Integer.parseInt(platformState)));
        }

        String operatorState = String.valueOf(inputAndOutputResult.getResult().get(5).getData());
        if (operatorState != null && !operatorState.trim().isEmpty()) {
            accountInfo.setOperatorState(AccountState.getByVal(Integer.parseInt(operatorState)));
        }

        return accountInfo;
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息状态的更改。
     *
     * @param sender 调用者地址
     * @param account DDC用户链账户地址
     * @param state   状态 ：Frozen - 冻结状态 ； Active - 活跃状态
     * @return 返回交易哈希
     * @throws Exception
     */
    public String updateAccState(String sender, String account, AccountState state, boolean changePlatformState) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        if (state == null) {
            throw new DDCException(ErrorMessage.ACCOUNT_STASTUS_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        // arrayList.add(new Int(new BigInteger(state.getStatus().toString())));
        arrayList.add(state.getStatus());
        arrayList.add(changePlatformState);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender,AuthorityFunctions.UpdateAccountState, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询合约中角色具有调用权限的方法列表
     *
     * @param ddcAddr 合约地址
     * @param role    角色
     * @return
     * @throws Exception
     */
    public ArrayList<String> getFunctions(String ddcAddr, AccountRole role) throws Exception {

        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }

        if (role == null) {
            throw new DDCException(ErrorMessage.ROLE_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(role.getRole());
        arrayList.add(new Address(ddcAddr));

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(ZeroAddress, AuthorityFunctions.GetFunction, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        InputAndOutputResult inputAndOutputResult = analyzeTransactionRecepitOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), (String) respJsonRpcBean.getResult());

        ArrayList<String> list = new ArrayList<>();
        List<ResultEntity> functionList = inputAndOutputResult.getResult();
        if (functionList != null) {
            for (ResultEntity resultEntity : functionList) {

                ArrayList<Bytes4> dataList = (ArrayList<Bytes4>) resultEntity.getTypeObject().getValue();
                dataList.forEach(data -> {
                    list.add("0x"+Hex.toHexString(data.getValue()));
                });

            }
        }
        return list;
    }

    private ReqJsonRpcBean assembleAuthorityTransaction(String sender,String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(sender,getBlockNumber(), ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicAddress(), functionName, params);
    }

}
