# Pay
基于HotMapper+Config实现的支付模块，封装了微信支付和支付宝支付，在maven引入之后项目接入支付能力0成本。
充分运用了HotMapper的逆向工程能力，自主建立支付日志表和退款日志表。
开发者只需要在业务Service打上@PaySign注解以及实现PaymentManager接口，即可完成支付的开发。
对支付成功，支付失败，调起支付都做了回调处理，开发者可在对应代码块编写相应业务逻辑，扩张性强。
对余额支付预留了接口，实现BalancePay接口即可让HotPay拥有余额支付能力，跟具项目业务逻辑需要决定是否需要实现。