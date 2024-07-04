const paymentService = require('../services/Payment');


exports.addPayment = async (req, res) => {
    try {
        const { amount, orderid, paymentMethod, paymentStatus } = req.body;
        const payment = await paymentService.createPayment(orderid, paymentMethod, paymentStatus, '', '');
        res.status(201).json({ success: true, message: 'Payment added successfully', data: payment });
    }
    catch (error) {
        console.error('Error adding payment:', error);
        res.status(500).json({ success: false, message: 'Failed to add payment', error: error.message });
    }
}

exports.updatePaymentStatus = async (req, res) => {
    try {
        const { id } = req.params;
        const { paymentStatus } = req.body;

        const payment = await paymentService.updatePaymentStatus(id, paymentStatus);
        if (!payment) {
            res.status(404).json({ success: false, message: 'Payment not found' });
        }
        res.status(200).json({ success: true, message: 'Payment updated successfully', data: payment });

        
    } catch (error) {
        console.error('Error upadte payment:', error);
        res.status(500).json({ success: false, message: 'Failed to add payment', error: error.message });
    }

}

exports.getPaymentByOrderId = async (req, res) =>{
    try {
        const { orderId } = req.params;
        const payment = await paymentService.getPaymentByOrderId(orderId);
        if (!payment) {
            res.status(404).json({ success: false, message: 'Payment not found' });
        }
        res.status(200).json({ success: true, message: 'Payment found', data: payment });
    } catch (error) {
        console.error('Error fetching payment:', error);
        res.status(500).json({ success: false, message: 'Failed to fetch payment', error: error.message });
    }
}