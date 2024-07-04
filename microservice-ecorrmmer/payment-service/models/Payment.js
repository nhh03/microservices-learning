const mongoose = require('mongoose');

const paymentSchema = new mongoose.Schema({
    orderid: { type: String, required: true },
    paymentMethod: { type: String, enum: ['COD', 'PAYPAL', 'VNPAY'], required: true },
    paymentStatus: { type: String, enum: ['PENDING', 'COMPLETED', 'CANCELLED'], required: true },
    vnpPayDate: {type:String},
    vnpTxnRef: {type:String},
    createdOn: { type: Date, default: Date.now },
    lastModifiedOn: { type: Date, default: Date.now }
})
const Payment = mongoose.model('Payment', paymentSchema);
module.exports = Payment;   