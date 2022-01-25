## ddc-sdk-taian

本分支SDK中包含如下可调用的方法：
```

3.2.1BSN-DDC-权限管理
    3.2.1.1添加下级账户
    3.2.1.3查询账户
    3.2.1.4更新账户状态

3.2.2BSN-DDC-费用管理
    3.2.2.1充值
    3.2.2.2链账户余额查询
    3.2.2.3DDC计费规则查询

3.2.3BSN-DDC-721
    3.2.3.1生成
    3.2.3.2安全生成
    3.2.3.3DDC授权
    3.2.3.4DDC授权查询
    3.2.3.5账户授权
    3.2.3.6账户授权查询
    3.2.3.7安全转移
    3.2.3.8转移
    3.2.3.11销毁
    3.2.3.12查询数量
    3.2.3.13查询拥有者
    3.2.3.14获取名称
    3.2.3.15获取符号
    3.2.3.16获取DDCURI
    3.2.3.17URI设置

3.2.4BSN-DDC-1155
    3.2.4.1生成
    3.2.4.2批量生成
    3.2.4.3账户授权
    3.2.4.4账户授权查询
    3.2.4.5安全转移
    3.2.4.6批量安全转移
    3.2.4.9销毁
    3.2.4.10批量销毁
    3.2.4.11查询数量
    3.2.4.12批量查询数量
    3.2.4.13获取DDCURI
    3.2.3.14URI设置

3.2.5BSN-DDC-交易查询
    3.2.5.1查询交易信息
    3.2.5.2查询交易回执
    3.2.5.3查询交易状态

3.2.6BSN-DDC-区块查询
    3.2.6.1获取区块信息

3.2.7BSN-DDC-签名事件

3.2.8BSN-DDC-数据解析

    3.2.8.1权限数据
        3.2.8.1.1添加账户
        3.2.8.1.2更新账户状态

    3.2.8.2充值数据
        3.2.8.2.1充值
        3.2.8.2.2DDC业务费扣除
        3.2.8.2.3设置DDC计费规则
        3.2.8.2.4删除DDC计费规则
        3.2.8.2.5按合约删除DDC计费规则

    3.2.8.3BSN-DDC-721数据
        3.2.8.3.1生成
        3.2.8.3.2转移/安全转移
        3.2.8.3.3冻结
        3.2.8.3.4解冻
        3.2.8.3.5销毁

    3.2.8.4BSN-DDC-1155数据
        3.2.8.4.1生成
        3.2.8.4.2批量生成
        3.2.8.4.3安全转移
        3.2.8.4.4批量安全转移
        3.2.8.4.5冻结
        3.2.8.4.6解冻
        3.2.8.4.7销毁
        3.2.8.4.8批量销毁

```

### 要求

**Java 1.8 或 更高**

### 配置说明
配置信息硬编码到com.reddate.ddc.config.ConfigCache文件中，如需更换相关配置请修改该文件下的信息

### 调用示例

1. 创建链账户

```
    // 生成链账户
    public void generatePem() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Account account = PemUtil.createAccount();
        System.out.println(account.getPrivateKey());
        System.out.println(account.getPublicKey());
        System.out.println(account.getAddress());
    }

```

2. 初始化SDK实例 

```
    // 初始化SDK配置信息
    static DDCSdkClient ddcSdkClient;
    static {
        ddcSdkClient = new DDCSdkClient("https://opbtest.bsngate.com:18602/api/projectId/rpc");
        Secp256K1SignEventListener signEventListener = null;
        try {
            // 设置签名使用的公私钥
            signEventListener = new Secp256K1SignEventListener(ecPrivateKeyPem, ecPublicKeyPem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ddcSdkClient.registerSignListener(signEventListener);
    }
    
    // 每个合约的实例通过sdk获取，此处以获取721实例为例
    private DDC721Service getDDC721Service() {
        return sdk.getDDC721Service();
    }

```

3. 调用合约方法进行DDC的发行、流转
```
    //通过合约的实例进行该合约内方法的调用，此处以发行、查看ddcId、流转一个DDC721为例
    //发行、查看DDC
    void mint() throws Exception {
        String tx = getDDC721Service().mint(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        log.info(tx);
        assertNotNull(tx);
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionRecepit(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            BlockEventService blockEventService = new BlockEventService();
            ArrayList result = blockEventService.getBlockEvent(transactionRecepitBean.getBlockNumber());
            result.forEach(t -> {
                if (t instanceof DDC721TransferEventBean) {
                    log.info("{}:DDCID {}", t.getClass(), ((DDC721TransferEventBean) t).getDdcId());
                }
            });
            break;
        }

    }
    
    //流转DDC
     void transferFrom() throws Exception {
        String tx = getDDC721Service().transferFrom(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("1"));
        log.info(tx);
    }
    
   
    
```
