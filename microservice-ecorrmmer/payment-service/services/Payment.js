const Payment = require('../models/Payment');

exports.createPayment = async(orderid, paymentMethod, paymentStatus, vnpPayDate, vnpTxnRef ) => {
    try{
        const payment = new Payment({
            orderid,
            paymentMethod,
            paymentStatus ,
            vnpPayDate,
            vnpTxnRef
        });

        await payment.save();   
        return payment;

    }
    catch(err){
        console.error('Error saving payment:', error);
        return null
    }

}

// Controller to update payment status
exports.updatePaymentStatus = async (id, paymentStatus) => {
    try {
        // Call the service to update payment status by ID
        const payment = await Payment.findByIdAndUpdate(id, { paymentStatus }, { new: true });

        if (!payment) {
            return null;
        }

        // Return success response
        return payment
    } catch (error) {
        return null;
    }
};



// Service to get payment by order ID
exports.getPaymentByOrderId = async (orderId) => {
    try {
        // Call the Payment model to find payment by order ID
        const payment = await Payment.findOne({ orderid: orderId });
        
        // Return the payment object if found
        return payment;
    } catch (error) {
        console.error('Error fetching payment by order ID:', error);
        return null;
    }
};

