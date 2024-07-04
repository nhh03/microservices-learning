const config = require('config');
const moment = require('moment');
const request = require('request');

const paymentService = require('../services/Payment');

const querystring = require('qs');
const crypto = require('crypto');

exports.createPayment = async (req, res) => {
    process.env.TZ = 'Asia/Ho_Chi_Minh';

    let date = new Date();
    let createDate = moment(date).format('YYYYMMDDHHmmss');

    let ipAddr = req.headers['x-forwarded-for'] ||
        req.connection.remoteAddress ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;

    let tmnCode = config.get('vnp_TmnCode');
    let secretKey = config.get('vnp_HashSecret');
    let vnpUrl = config.get('vnp_Url');
    let returnUrl = config.get('vnp_ReturnUrl');
    let orderId = moment(date).format('DDHHmmss');

    // String paymentTime = request.getParameter("vnp_PayDate");
    // String vnp_TxnRef = request.getParameter("vnp_TxnRef"); 

    let amount = req.body.amount;
    let paymentMethod = req.body.paymentMethod;
    let orderid = req.body.orderid;
    let dataupdate = req.body.dataupdate;
    let bankCode = 'VNBANK';


    let locale = 'vn';
    if (locale === null || locale === '') {
        locale = 'vn';
    }
    let currCode = 'VND';



    let vnp_Params = {};
    if (bankCode !== null && bankCode !== '') {
        vnp_Params['vnp_BankCode'] = bankCode;
    }

    vnp_Params['vnp_OrderInfo'] = orderid + '_' + paymentMethod + '_' + dataupdate;
    vnp_Params['vnp_Version'] = '2.1.0';
    vnp_Params['vnp_Command'] = 'pay';
    vnp_Params['vnp_TmnCode'] = tmnCode;
    vnp_Params['vnp_Locale'] = locale;
    vnp_Params['vnp_CurrCode'] = currCode;
    vnp_Params['vnp_TxnRef'] = orderId;
    vnp_Params['vnp_OrderType'] = 'other';
    vnp_Params['vnp_Amount'] = amount * 100;
    vnp_Params['vnp_ReturnUrl'] = returnUrl;
    vnp_Params['vnp_IpAddr'] = ipAddr;
    vnp_Params['vnp_CreateDate'] = createDate;


    vnp_Params = sortObject(vnp_Params);


    let signData = querystring.stringify(vnp_Params, { encode: false });
    let hmac = crypto.createHmac("sha512", secretKey);
    let signed = hmac.update(new Buffer(signData, 'utf-8')).digest("hex");
    vnp_Params['vnp_SecureHash'] = signed;
    vnpUrl += '?' + querystring.stringify(vnp_Params, { encode: false });
    res.status(200).json(vnpUrl);
}


exports.returnPayment = async (req, res) => {

    console.log(req)
    let vnp_Params = req.query;

    let secureHash = vnp_Params['vnp_SecureHash'];

    delete vnp_Params['vnp_SecureHash'];
    delete vnp_Params['vnp_SecureHashType'];

    vnp_Params = sortObject(vnp_Params);

    let tmnCode = config.get('vnp_TmnCode');
    let secretKey = config.get('vnp_HashSecret');


    let signData = querystring.stringify(vnp_Params, { encode: false });
    let hmac = crypto.createHmac("sha512", secretKey);
    let signed = hmac.update(new Buffer(signData, 'utf-8')).digest("hex");

    if (secureHash === signed) {
        let responseCode = vnp_Params['vnp_ResponseCode'];
        let vnp_OrderInfo = vnp_Params['vnp_OrderInfo'];
        let vnp_PayDate = vnp_Params['vnp_PayDate'];
        let vnp_TxnRef = vnp_Params['vnp_TxnRef'];
        // console.log(responseCode)
        // console.log(vnp_OrderInfo)
        let data = vnp_OrderInfo.split('_');
        let orderid = parseInt(data[0].toString());
        let paymentMethod = data[1];
        let dataupdate = data[2];
        console.log('Payment successful:', orderid, paymentMethod, dataupdate);

        if (responseCode == '00') {
            // Giao dịch thành công, bạn có thể xử lý ở đây
            const payment = await paymentService.addPayment(orderid, 'VNPAY', 'COMPLETED', vnp_PayDate, vnp_TxnRef);
            // console.log('Payment successful:', payment);
            if (payment) {

                // Khởi tạo một instance của KafkaProducerService
                // const producerService = new KafkaProducerService();
                // producerService.connectProducer()
                //     .then(() => {
                //         // Gửi message tới topic 'payment-topic'
                //         const topic = 'payment-topic';
                //         console.log(dataupdate)
                //         producerService.sendMessage(topic, dataupdate);
                //     })
                //     .catch(error => {
                //         console.error('Error connecting to Kafka:', error);
                //     });

                // res.redirect(`http://localhost:3000/payment-result?vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_OrderInfo=${vnp_OrderInfo}`);
            }

        } else {

            // Giao dịch không thành công, chuyển hướng đến trang thông báo lỗi
            const payment = await paymentService.addPayment(orderid, 'VNPAY', 'CANCELLED', 'vnpay', 'vnpay');

            console.log('Payment CANCELLED:', payment);


            if (payment) {
                const producerService = new KafkaProducerService();
                let data1 = data[0].toString()
                producerService.connectProducer()
                    .then(() => {
                        // Gửi message tới topic 'payment-topic'
                        const topic = 'order_Cancel_topic';
                        producerService.sendMessage(topic, data1);
                    })
                    .catch(error => {
                        console.error('Error connecting to Kafka:', error);
                    });
                res.redirect(`http://localhost:3000/payment-result?vnp_ResponseCode=01&vnp_TransactionStatus=01&vnp_OrderInfo=${vnp_OrderInfo}`);
            }

        }
    } else {
        res.render('success', { code: '97' })
    }
}

exports.refund = async (req, res) => {
    process.env.TZ = 'Asia/Ho_Chi_Minh';
    let date = new Date();

    let config = require('config');
    let crypto = require("crypto");

    let vnp_TmnCode = config.get('vnp_TmnCode');
    let secretKey = config.get('vnp_HashSecret');
    let vnp_Api = config.get('vnp_Api');

    let vnp_CreateBy = req.body.user;
    let vnp_TxnRef = req.body.orderId;
    let vnp_TransactionDate = req.body.transDate;
    let vnp_Amount = req.body.amount * 100;
    let vnp_TransactionType = '03';

    let currCode = 'VND';

    let vnp_RequestId = moment(date).format('HHmmss');
    let vnp_Version = '2.1.0';
    let vnp_Command = 'refund';
    let vnp_OrderInfo = 'Hoan tien GD ma:' + vnp_TxnRef;

    let vnp_IpAddr = req.headers['x-forwarded-for'] ||
        req.connection.remoteAddress ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;


    let vnp_CreateDate = moment(date).format('YYYYMMDDHHmmss');

    let vnp_TransactionNo = 'other';

    let data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" + vnp_TransactionType + "|" + vnp_TxnRef + "|" + vnp_Amount + "|" + vnp_TransactionNo + "|" + vnp_TransactionDate + "|" + vnp_CreateBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;
    let hmac = crypto.createHmac("sha512", secretKey);
    let vnp_SecureHash = hmac.update(new Buffer(data, 'utf-8')).digest("hex");

    let dataObj = {
        'vnp_RequestId': vnp_RequestId,
        'vnp_Version': vnp_Version,
        'vnp_Command': vnp_Command,
        'vnp_TmnCode': vnp_TmnCode,
        'vnp_TransactionType': vnp_TransactionType,
        'vnp_TxnRef': vnp_TxnRef,
        'vnp_Amount': vnp_Amount,
        'vnp_TransactionNo': vnp_TransactionNo,
        'vnp_CreateBy': vnp_CreateBy,
        'vnp_OrderInfo': vnp_OrderInfo,
        'vnp_TransactionDate': vnp_TransactionDate,
        'vnp_CreateDate': vnp_CreateDate,
        'vnp_IpAddr': '127.0.0.1',
        'vnp_SecureHash': vnp_SecureHash
    };

    // Make the HTTP request using request library

    console.log('Refund request:', dataObj);
    request({
        url: vnp_Api,
        method: "POST",
        json: true,
        body: dataObj
    }, function (error, response, body) {
        console.log(response);
    });
};

function sortObject(obj) {
    let sorted = {};
    let str = [];
    let key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) {
            str.push(encodeURIComponent(key));
        }
    }
    str.sort();
    for (key = 0; key < str.length; key++) {
        sorted[str[key]] = encodeURIComponent(obj[str[key]]).replace(/%20/g, "+");
    }
    return sorted;
}